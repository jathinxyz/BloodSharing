package com.bloodmatcher.auth;

import com.bloodmatcher.auth.dto.*;
import com.bloodmatcher.common.Role;
import com.bloodmatcher.common.exception.BadRequestException;
import com.bloodmatcher.common.exception.ConflictException;
import com.bloodmatcher.common.exception.UnauthorizedException;
import com.bloodmatcher.donor.DonorProfile;
import com.bloodmatcher.donor.DonorProfileRepository;
import com.bloodmatcher.security.JwtTokenProvider;
import com.bloodmatcher.security.UserPrincipal;
import com.bloodmatcher.user.User;
import com.bloodmatcher.user.UserRepository;
import com.bloodmatcher.user.dto.UserDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository,
                       DonorProfileRepository donorProfileRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.donorProfileRepository = donorProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        // 1. Check duplicate email
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("An account already exists with email: " + normalizedEmail);
        }

        // 2. Reject public ADMIN registration
        if (request.getRole() == Role.ADMIN) {
            throw new BadRequestException("Public registration cannot assign the ADMIN role");
        }

        // 3. Determine role (default to DONOR if not specified)
        Role role = request.getRole() != null ? request.getRole() : Role.DONOR;

        // 4. Create and persist user
        User user = new User(
                request.getFullName().trim(),
                normalizedEmail,
                passwordEncoder.encode(request.getPassword()),
                role,
                true
        );
        User savedUser = userRepository.save(user);

        // 5. If registering as DONOR, create DonorProfile if blood group provided
        if (role == Role.DONOR && request.getBloodGroup() != null) {
            DonorProfile profile = new DonorProfile(
                    savedUser,
                    request.getBloodGroup(),
                    request.getPhone() != null ? request.getPhone().trim() : "Pending",
                    request.getAddress() != null ? request.getAddress().trim() : "Pending",
                    request.getLatitude(),
                    request.getLongitude(),
                    true
            );
            donorProfileRepository.save(profile);
        }

        // 6. Generate tokens
        String jwt = tokenProvider.generateTokenFromUser(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getFullName()
        );
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());

        return new AuthResponse(
                jwt,
                tokenProvider.getExpirationMs(),
                refreshToken.getToken(),
                UserDto.from(savedUser)
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        if (!userPrincipal.isEnabled()) {
            throw new UnauthorizedException("User account is disabled. Please contact support.");
        }

        String jwt = tokenProvider.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userPrincipal.getId());

        User user = userRepository.findById(userPrincipal.getId()).orElseThrow();

        return new AuthResponse(
                jwt,
                tokenProvider.getExpirationMs(),
                refreshToken.getToken(),
                UserDto.from(user)
        );
    }

    @Transactional
    public TokenRefreshResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyExpiration(request.getRefreshToken());
        User user = refreshToken.getUser();

        if (!user.isEnabled()) {
            throw new UnauthorizedException("User account has been disabled");
        }

        String token = tokenProvider.generateTokenFromUser(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                user.getFullName()
        );

        return new TokenRefreshResponse(
                token,
                refreshToken.getToken(),
                tokenProvider.getExpirationMs()
        );
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenService.deleteByUserId(userId);
        SecurityContextHolder.clearContext();
    }
}

package com.bloodmatcher.auth;

import com.bloodmatcher.auth.dto.AuthResponse;
import com.bloodmatcher.auth.dto.LoginRequest;
import com.bloodmatcher.auth.dto.RegisterRequest;
import com.bloodmatcher.common.BloodGroup;
import com.bloodmatcher.common.Role;
import com.bloodmatcher.common.exception.BadRequestException;
import com.bloodmatcher.common.exception.ConflictException;
import com.bloodmatcher.donor.DonorProfileRepository;
import com.bloodmatcher.security.JwtTokenProvider;
import com.bloodmatcher.security.UserPrincipal;
import com.bloodmatcher.user.User;
import com.bloodmatcher.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private DonorProfileRepository donorProfileRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                donorProfileRepository,
                passwordEncoder,
                authenticationManager,
                tokenProvider,
                refreshTokenService
        );
    }

    @Test
    @DisplayName("Public registration cannot assign ADMIN role and throws BadRequestException")
    void testPublicRegistrationRejectsAdminRole() {
        RegisterRequest request = new RegisterRequest("Admin Wannabe", "wannabe@admin.com", "Secret123!", Role.ADMIN);
        when(userRepository.existsByEmailIgnoreCase("wannabe@admin.com")).thenReturn(false);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> authService.register(request));
        assertTrue(ex.getMessage().contains("ADMIN"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Registration normalizes email and detects duplicates")
    void testRegistrationRejectsDuplicateEmail() {
        RegisterRequest request = new RegisterRequest("Jane Doe", " Jane.Doe@Example.COM ", "Secret123!", Role.DONOR);
        when(userRepository.existsByEmailIgnoreCase("jane.doe@example.com")).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class, () -> authService.register(request));
        assertTrue(ex.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Registration hashes password with BCrypt and creates tokens")
    void testSuccessfulRegistration() {
        RegisterRequest request = new RegisterRequest("Jane Donor", "jane@example.com", "PlainPassword123!", Role.DONOR);
        request.setBloodGroup(BloodGroup.O_POS);
        request.setPhone("555-0199");
        request.setAddress("123 Maple St");

        when(userRepository.existsByEmailIgnoreCase("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("PlainPassword123!")).thenReturn("$2a$10$hashedPasswordHere");

        User savedUser = new User("Jane Donor", "jane@example.com", "$2a$10$hashedPasswordHere", Role.DONOR, true);
        savedUser.setId(10L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(tokenProvider.generateTokenFromUser(any(), any(), any(), any())).thenReturn("mock.jwt.token");
        when(tokenProvider.getExpirationMs()).thenReturn(86400000L);

        RefreshToken mockRefreshToken = new RefreshToken(savedUser, "mock-refresh-token", null);
        when(refreshTokenService.createRefreshToken(10L)).thenReturn(mockRefreshToken);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getAccessToken());
        assertEquals("mock-refresh-token", response.getRefreshToken());
        assertEquals("jane@example.com", response.getUser().getEmail());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("$2a$10$hashedPasswordHere", userCaptor.getValue().getPasswordHash());
    }

    @Test
    @DisplayName("Login with invalid credentials throws BadCredentialsException")
    void testLoginInvalidCredentials() {
        LoginRequest request = new LoginRequest("user@example.com", "WrongPassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Login with valid credentials returns AuthResponse")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("user@example.com", "ValidPassword");
        User user = new User("John Doe", "user@example.com", "hash", Role.RECIPIENT, true);
        user.setId(20L);

        UserPrincipal principal = UserPrincipal.create(user);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authToken);
        when(tokenProvider.generateToken(authToken)).thenReturn("valid.jwt.token");
        when(tokenProvider.getExpirationMs()).thenReturn(86400000L);
        when(refreshTokenService.createRefreshToken(20L)).thenReturn(new RefreshToken(user, "valid-refresh-token", null));
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));

        AuthResponse response = authService.login(request);
        assertNotNull(response);
        assertEquals("valid.jwt.token", response.getAccessToken());
        assertEquals("valid-refresh-token", response.getRefreshToken());
        assertEquals(20L, response.getUser().getId());
    }
}

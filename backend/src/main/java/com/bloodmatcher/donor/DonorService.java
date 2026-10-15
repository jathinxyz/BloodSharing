package com.bloodmatcher.donor;

import com.bloodmatcher.common.exception.ResourceNotFoundException;
import com.bloodmatcher.donor.dto.DonorProfileDto;
import com.bloodmatcher.donor.dto.UpdateDonorProfileRequest;
import com.bloodmatcher.user.User;
import com.bloodmatcher.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonorService {

    private final DonorProfileRepository donorProfileRepository;
    private final UserRepository userRepository;

    public DonorService(DonorProfileRepository donorProfileRepository, UserRepository userRepository) {
        this.donorProfileRepository = donorProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public DonorProfile getDonorProfileEntityByUserId(Long userId) {
        return donorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Donor profile not found for user id: " + userId));
    }

    @Transactional(readOnly = true)
    public DonorProfileDto getProfileByUserId(Long userId) {
        return DonorProfileDto.from(getDonorProfileEntityByUserId(userId));
    }

    @Transactional
    public DonorProfileDto createOrUpdateProfile(Long userId, UpdateDonorProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        DonorProfile profile = donorProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    DonorProfile newProfile = new DonorProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        profile.setBloodGroup(request.getBloodGroup());
        profile.setPhone(request.getPhone().trim());
        profile.setAddress(request.getAddress().trim());
        profile.setLatitude(request.getLatitude());
        profile.setLongitude(request.getLongitude());

        if (request.getAvailable() != null) {
            profile.setAvailable(request.getAvailable());
        }
        if (request.getLastDonationDate() != null) {
            profile.setLastDonationDate(request.getLastDonationDate());
        }

        DonorProfile saved = donorProfileRepository.save(profile);
        return DonorProfileDto.from(saved);
    }

    @Transactional
    public DonorProfileDto updateAvailability(Long userId, boolean available) {
        DonorProfile profile = getDonorProfileEntityByUserId(userId);
        profile.setAvailable(available);
        DonorProfile saved = donorProfileRepository.save(profile);
        return DonorProfileDto.from(saved);
    }
}

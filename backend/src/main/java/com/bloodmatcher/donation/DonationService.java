package com.bloodmatcher.donation;

import com.bloodmatcher.common.NotificationType;
import com.bloodmatcher.common.exception.ResourceNotFoundException;
import com.bloodmatcher.donation.dto.DonationRecordDto;
import com.bloodmatcher.donation.dto.RecordDonationRequest;
import com.bloodmatcher.donor.DonorProfile;
import com.bloodmatcher.donor.DonorProfileRepository;
import com.bloodmatcher.notification.NotificationService;
import com.bloodmatcher.request.BloodRequest;
import com.bloodmatcher.request.BloodRequestRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class DonationService {

    private final DonationRecordRepository donationRecordRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private final NotificationService notificationService;

    public DonationService(DonationRecordRepository donationRecordRepository,
                           DonorProfileRepository donorProfileRepository,
                           BloodRequestRepository bloodRequestRepository,
                           NotificationService notificationService) {
        this.donationRecordRepository = donationRecordRepository;
        this.donorProfileRepository = donorProfileRepository;
        this.bloodRequestRepository = bloodRequestRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public DonationRecordDto recordDonation(DonorProfile donor, RecordDonationRequest request) {
        BloodRequest bloodRequest = null;
        if (request.getRequestId() != null) {
            bloodRequest = bloodRequestRepository.findById(request.getRequestId()).orElse(null);
        }

        LocalDate donationDate = request.getDonationDate() != null ? request.getDonationDate() : LocalDate.now();

        DonationRecord record = new DonationRecord(
                bloodRequest,
                donor,
                donationDate,
                "COMPLETED",
                request.getNotes()
        );

        DonationRecord saved = donationRecordRepository.save(record);

        // Update donor's last donation date to maintain cooldown tracking
        donor.setLastDonationDate(donationDate);
        donorProfileRepository.save(donor);

        // Notify donor of recorded donation
        notificationService.notifyUser(
                donor.getUser(),
                NotificationType.DONATION_CONFIRMED,
                "Donation Confirmed",
                "Thank you for your life-saving blood donation on " + donationDate + "! Your profile has been updated.",
                request.getRequestId(),
                true
        );

        return DonationRecordDto.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<DonationRecordDto> getDonationsForDonor(Long donorId, Pageable pageable) {
        return donationRecordRepository.findByDonorId(donorId, pageable)
                .map(DonationRecordDto::from);
    }

    @Transactional(readOnly = true)
    public Page<DonationRecordDto> getDonationsForDonorUser(Long userId, Pageable pageable) {
        DonorProfile donor = donorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Donor profile not found for user id: " + userId));
        return getDonationsForDonor(donor.getId(), pageable);
    }
}

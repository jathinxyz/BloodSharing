package com.bloodmatcher.admin;

import com.bloodmatcher.admin.dto.AdminDashboardDto;
import com.bloodmatcher.admin.dto.AdminReportDto;
import com.bloodmatcher.common.BloodGroup;
import com.bloodmatcher.common.InvitationStatus;
import com.bloodmatcher.common.RequestStatus;
import com.bloodmatcher.common.Role;
import com.bloodmatcher.donation.DonationRecordRepository;
import com.bloodmatcher.donor.DonorProfileRepository;
import com.bloodmatcher.invitation.DonorInvitationRepository;
import com.bloodmatcher.request.BloodRequest;
import com.bloodmatcher.request.BloodRequestRepository;
import com.bloodmatcher.request.dto.BloodRequestDto;
import com.bloodmatcher.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private final DonorInvitationRepository invitationRepository;
    private final DonationRecordRepository donationRecordRepository;

    public AdminService(UserRepository userRepository,
                        DonorProfileRepository donorProfileRepository,
                        BloodRequestRepository bloodRequestRepository,
                        DonorInvitationRepository invitationRepository,
                        DonationRecordRepository donationRecordRepository) {
        this.userRepository = userRepository;
        this.donorProfileRepository = donorProfileRepository;
        this.bloodRequestRepository = bloodRequestRepository;
        this.invitationRepository = invitationRepository;
        this.donationRecordRepository = donationRecordRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardDto getDashboardStats() {
        AdminDashboardDto dto = new AdminDashboardDto();

        dto.setTotalUsers(userRepository.count());
        dto.setTotalDonors(userRepository.countByRole(Role.DONOR));
        dto.setTotalRecipients(userRepository.countByRole(Role.RECIPIENT));
        dto.setAvailableDonors(donorProfileRepository.countByAvailable(true));

        dto.setTotalRequests(bloodRequestRepository.count());
        dto.setOpenRequests(bloodRequestRepository.countByStatus(RequestStatus.OPEN));
        dto.setInProgressRequests(bloodRequestRepository.countByStatus(RequestStatus.IN_PROGRESS)
                + bloodRequestRepository.countByStatus(RequestStatus.DONORS_NOTIFIED));
        dto.setFulfilledRequests(bloodRequestRepository.countByStatus(RequestStatus.FULFILLED));
        dto.setCancelledRequests(bloodRequestRepository.countByStatus(RequestStatus.CANCELLED));

        dto.setTotalDonationsCompleted(donationRecordRepository.count());

        long totalInvites = invitationRepository.count();
        long acceptedInvites = invitationRepository.countByStatus(InvitationStatus.ACCEPTED);
        long pendingInvites = invitationRepository.countByStatus(InvitationStatus.PENDING);
        long rejectedInvites = invitationRepository.countByStatus(InvitationStatus.REJECTED);

        dto.setTotalInvitationsSent(totalInvites);
        dto.setInvitationsAccepted(acceptedInvites);
        dto.setInvitationsPending(pendingInvites);
        dto.setInvitationsRejected(rejectedInvites);

        return dto;
    }

    @Transactional(readOnly = true)
    public AdminReportDto getReports() {
        AdminReportDto report = new AdminReportDto();

        Map<BloodGroup, Long> donorCounts = new EnumMap<>(BloodGroup.class);
        Map<BloodGroup, Long> requestCounts = new EnumMap<>(BloodGroup.class);

        for (BloodGroup bg : BloodGroup.values()) {
            donorCounts.put(bg, donorProfileRepository.countByBloodGroup(bg));
            requestCounts.put(bg, bloodRequestRepository.countByBloodGroup(bg));
        }

        report.setDonorCountByBloodGroup(donorCounts);
        report.setRequestCountByBloodGroup(requestCounts);

        long totalInvites = invitationRepository.count();
        long acceptedInvites = invitationRepository.countByStatus(InvitationStatus.ACCEPTED);
        double acceptanceRate = totalInvites > 0 ? ((double) acceptedInvites / totalInvites) * 100.0 : 0.0;
        report.setInvitationAcceptanceRate(Math.round(acceptanceRate * 100.0) / 100.0);

        long totalRequests = bloodRequestRepository.count();
        long fulfilledRequests = bloodRequestRepository.countByStatus(RequestStatus.FULFILLED);
        double fulfillmentRate = totalRequests > 0 ? ((double) fulfilledRequests / totalRequests) * 100.0 : 0.0;
        report.setRequestFulfillmentRate(Math.round(fulfillmentRate * 100.0) / 100.0);

        return report;
    }

    @Transactional(readOnly = true)
    public Page<BloodRequestDto> getAllRequests(RequestStatus status, Pageable pageable) {
        Page<BloodRequest> page = (status != null)
                ? bloodRequestRepository.findByStatus(status, pageable)
                : bloodRequestRepository.findAll(pageable);
        return page.map(BloodRequestDto::from);
    }
}

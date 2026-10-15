package com.bloodmatcher.invitation;

import com.bloodmatcher.common.InvitationStatus;
import com.bloodmatcher.common.NotificationType;
import com.bloodmatcher.common.RequestStatus;
import com.bloodmatcher.common.Role;
import com.bloodmatcher.common.exception.ConflictException;
import com.bloodmatcher.common.exception.ForbiddenException;
import com.bloodmatcher.common.exception.ResourceNotFoundException;
import com.bloodmatcher.donor.DonorProfile;
import com.bloodmatcher.donor.DonorProfileRepository;
import com.bloodmatcher.invitation.dto.InvitationDto;
import com.bloodmatcher.notification.NotificationService;
import com.bloodmatcher.request.BloodRequest;
import com.bloodmatcher.request.BloodRequestRepository;
import com.bloodmatcher.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class InvitationService {

    private static final Logger log = LoggerFactory.getLogger(InvitationService.class);

    private final DonorInvitationRepository invitationRepository;
    private final DonorProfileRepository donorProfileRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private final NotificationService notificationService;

    public InvitationService(DonorInvitationRepository invitationRepository,
                             DonorProfileRepository donorProfileRepository,
                             BloodRequestRepository bloodRequestRepository,
                             NotificationService notificationService) {
        this.invitationRepository = invitationRepository;
        this.donorProfileRepository = donorProfileRepository;
        this.bloodRequestRepository = bloodRequestRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public DonorInvitation createInvitation(BloodRequest request, DonorProfile donor, String compatibilityOutcome, Double distanceKm) {
        if (invitationRepository.existsByRequestIdAndDonorId(request.getId(), donor.getId())) {
            throw new ConflictException("Donor has already been invited for this request");
        }

        DonorInvitation invitation = new DonorInvitation(request, donor, compatibilityOutcome, distanceKm);
        DonorInvitation saved = invitationRepository.save(invitation);

        // Notify donor via in-app notification and email
        String title = "Urgent: Blood Donation Request";
        String message = String.format("A patient requires %s units of %s blood at %s (%s). Please review the request and respond if you are available.",
                request.getUnitsRequested(),
                request.getBloodGroup().getDisplayValue(),
                request.getHospitalName(),
                request.getHospitalLocation());

        notificationService.notifyUser(
                donor.getUser(),
                NotificationType.INVITATION_RECEIVED,
                title,
                message,
                request.getId(),
                true
        );

        return saved;
    }

    @Transactional
    public InvitationDto respondToInvitation(Long invitationId, Long userId, boolean accept) {
        DonorInvitation invitation = invitationRepository.findByIdWithDetails(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("DonorInvitation", "id", invitationId));

        // Enforce ownership: only the invited donor can respond
        if (!invitation.getDonor().getUser().getId().equals(userId)) {
            throw new ForbiddenException("You cannot respond to an invitation that was not addressed to you");
        }

        // Enforce valid state transition: must be PENDING
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new ConflictException("Invitation has already been responded to with status: " + invitation.getStatus());
        }

        BloodRequest request = invitation.getRequest();
        if (request.getStatus().isTerminal()) {
            throw new ConflictException("Cannot respond to an invitation for a closed or fulfilled request");
        }

        invitation.setStatus(accept ? InvitationStatus.ACCEPTED : InvitationStatus.REJECTED);
        invitation.setRespondedAt(Instant.now());
        DonorInvitation saved = invitationRepository.save(invitation);

        User requester = request.getRequester();
        String donorName = invitation.getDonor().getUser().getFullName();

        if (accept) {
            // Update request status to IN_PROGRESS if currently OPEN or DONORS_NOTIFIED
            if (request.getStatus() == RequestStatus.OPEN || request.getStatus() == RequestStatus.DONORS_NOTIFIED || request.getStatus() == RequestStatus.MATCHING) {
                request.setStatus(RequestStatus.IN_PROGRESS);
                bloodRequestRepository.save(request);
            }

            // Notify recipient that a donor has accepted
            notificationService.notifyUser(
                    requester,
                    NotificationType.INVITATION_ACCEPTED,
                    "Donor Accepted Your Request",
                    String.format("Donor %s (%s) has accepted your blood request for %s at %s. Contact details are now available.",
                            donorName,
                            invitation.getDonor().getBloodGroup().getDisplayValue(),
                            request.getBloodGroup().getDisplayValue(),
                            request.getHospitalName()),
                    request.getId(),
                    true
            );
        } else {
            // Notify recipient that a donor declined
            notificationService.notifyUser(
                    requester,
                    NotificationType.INVITATION_REJECTED,
                    "Donor Declined Invitation",
                    String.format("A notified donor was unable to accept your request for %s at %s.",
                            request.getBloodGroup().getDisplayValue(),
                            request.getHospitalName()),
                    request.getId(),
                    false
            );
        }

        return InvitationDto.from(saved, true);
    }

    @Transactional(readOnly = true)
    public Page<InvitationDto> getInvitationsForDonorUser(Long userId, Pageable pageable) {
        DonorProfile donor = donorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Donor profile not found for user"));
        return invitationRepository.findByDonorId(donor.getId(), pageable)
                .map(inv -> InvitationDto.from(inv, true));
    }

    @Transactional(readOnly = true)
    public List<InvitationDto> getInvitationsForRequest(Long requestId, User currentUser) {
        BloodRequest request = bloodRequestRepository.findByIdWithRequester(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", "id", requestId));

        // Enforce ownership: must be the requester or an administrator
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = request.getRequester().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new ForbiddenException("You are not authorized to view matches or invitations for this request");
        }

        return invitationRepository.findByRequestId(requestId).stream()
                .map(inv -> InvitationDto.from(inv, true))
                .toList();
    }
}

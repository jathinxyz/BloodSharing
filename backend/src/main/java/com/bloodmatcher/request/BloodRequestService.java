package com.bloodmatcher.request;

import com.bloodmatcher.common.RequestStatus;
import com.bloodmatcher.common.Role;
import com.bloodmatcher.common.exception.ConflictException;
import com.bloodmatcher.common.exception.ForbiddenException;
import com.bloodmatcher.common.exception.ResourceNotFoundException;
import com.bloodmatcher.donation.DonationService;
import com.bloodmatcher.donation.dto.RecordDonationRequest;
import com.bloodmatcher.donor.DonorProfile;
import com.bloodmatcher.donor.DonorProfileRepository;
import com.bloodmatcher.invitation.InvitationService;
import com.bloodmatcher.matching.MatchingEngineService;
import com.bloodmatcher.matching.dto.MatchResultDto;
import com.bloodmatcher.request.dto.BloodRequestDto;
import com.bloodmatcher.request.dto.CreateBloodRequestDto;
import com.bloodmatcher.request.dto.FulfillRequestDto;
import com.bloodmatcher.user.User;
import com.bloodmatcher.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BloodRequestService {

    private static final Logger log = LoggerFactory.getLogger(BloodRequestService.class);

    private final BloodRequestRepository bloodRequestRepository;
    private final UserRepository userRepository;
    private final MatchingEngineService matchingEngineService;
    private final InvitationService invitationService;
    private final DonorProfileRepository donorProfileRepository;
    private final DonationService donationService;

    public BloodRequestService(BloodRequestRepository bloodRequestRepository,
                               UserRepository userRepository,
                               MatchingEngineService matchingEngineService,
                               InvitationService invitationService,
                               DonorProfileRepository donorProfileRepository,
                               DonationService donationService) {
        this.bloodRequestRepository = bloodRequestRepository;
        this.userRepository = userRepository;
        this.matchingEngineService = matchingEngineService;
        this.invitationService = invitationService;
        this.donorProfileRepository = donorProfileRepository;
        this.donationService = donationService;
    }

    @Transactional
    public BloodRequestDto createRequest(Long userId, CreateBloodRequestDto dto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        BloodRequest request = new BloodRequest(
                requester,
                dto.getBloodGroup(),
                dto.getUnitsRequested(),
                dto.getHospitalName().trim(),
                dto.getHospitalLocation().trim(),
                dto.getLatitude(),
                dto.getLongitude(),
                dto.getUrgency(),
                dto.getRequiredDate(),
                dto.getNotes()
        );

        BloodRequest saved = bloodRequestRepository.save(request);
        return BloodRequestDto.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<BloodRequestDto> getMyRequests(Long userId, Pageable pageable) {
        return bloodRequestRepository.findByRequesterId(userId, pageable)
                .map(BloodRequestDto::from);
    }

    @Transactional(readOnly = true)
    public BloodRequestDto getRequestById(Long requestId, User currentUser) {
        BloodRequest request = bloodRequestRepository.findByIdWithRequester(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", "id", requestId));

        // Ownership enforcement: only owner or administrator can view request details directly
        boolean isOwner = request.getRequester().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You are not authorized to view this private blood request");
        }

        return BloodRequestDto.from(request);
    }

    @Transactional
    public BloodRequestDto cancelRequest(Long requestId, Long userId) {
        BloodRequest request = bloodRequestRepository.findByIdWithRequester(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", "id", requestId));

        User requester = request.getRequester();
        if (!requester.getId().equals(userId)) {
            throw new ForbiddenException("You can only cancel your own blood requests");
        }

        if (request.getStatus().isTerminal()) {
            throw new ConflictException("Cannot cancel request already in terminal state: " + request.getStatus());
        }

        request.setStatus(RequestStatus.CANCELLED);
        BloodRequest saved = bloodRequestRepository.save(request);
        return BloodRequestDto.from(saved);
    }

    @Transactional
    public BloodRequestDto fulfillRequest(Long requestId, Long userId, FulfillRequestDto dto) {
        BloodRequest request = bloodRequestRepository.findByIdWithRequester(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", "id", requestId));

        if (!request.getRequester().getId().equals(userId)) {
            User user = userRepository.findById(userId).orElseThrow();
            if (user.getRole() != Role.ADMIN) {
                throw new ForbiddenException("You are not authorized to fulfill this request");
            }
        }

        if (request.getStatus() == RequestStatus.FULFILLED || request.getStatus() == RequestStatus.CANCELLED) {
            throw new ConflictException("Cannot fulfill request already in status: " + request.getStatus());
        }

        request.setStatus(RequestStatus.FULFILLED);
        BloodRequest saved = bloodRequestRepository.save(request);

        // Record donation if donor is specified
        if (dto != null && dto.getDonorId() != null) {
            DonorProfile donor = donorProfileRepository.findById(dto.getDonorId()).orElse(null);
            if (donor != null) {
                RecordDonationRequest recordReq = new RecordDonationRequest(
                        request.getId(),
                        LocalDate.now(),
                        dto.getNotes() != null ? dto.getNotes() : "Fulfilled request #" + request.getId()
                );
                donationService.recordDonation(donor, recordReq);
            }
        }

        return BloodRequestDto.from(saved);
    }

    @Transactional(readOnly = true)
    public List<MatchResultDto> previewMatches(Long requestId, User currentUser, int limit) {
        BloodRequest request = bloodRequestRepository.findByIdWithRequester(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", "id", requestId));

        boolean isOwner = request.getRequester().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You are not authorized to view matches for this request");
        }

        return matchingEngineService.findCandidateMatches(request, limit);
    }

    @Transactional
    public List<MatchResultDto> triggerMatchAndNotify(Long requestId, User currentUser, int limit) {
        BloodRequest request = bloodRequestRepository.findByIdWithRequester(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("BloodRequest", "id", requestId));

        boolean isOwner = request.getRequester().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You are not authorized to trigger matches for this request");
        }

        if (request.getStatus().isTerminal()) {
            throw new ConflictException("Cannot trigger matching for a request in status: " + request.getStatus());
        }

        List<MatchResultDto> candidates = matchingEngineService.findCandidateMatches(request, limit);
        List<MatchResultDto> invited = new ArrayList<>();

        for (MatchResultDto candidate : candidates) {
            if (!candidate.isAlreadyInvited() && candidate.isEligible()) {
                DonorProfile donor = donorProfileRepository.findById(candidate.getDonorId()).orElse(null);
                if (donor != null) {
                    invitationService.createInvitation(
                            request,
                            donor,
                            candidate.getCompatibilityOutcome(),
                            candidate.getDistanceKm()
                    );
                    candidate.setAlreadyInvited(true);
                    invited.add(candidate);
                }
            }
        }

        if (!invited.isEmpty() && request.getStatus() == RequestStatus.OPEN) {
            request.setStatus(RequestStatus.DONORS_NOTIFIED);
            bloodRequestRepository.save(request);
        }

        return invited;
    }
}

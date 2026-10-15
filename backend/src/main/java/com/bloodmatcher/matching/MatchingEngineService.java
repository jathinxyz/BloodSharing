package com.bloodmatcher.matching;

import com.bloodmatcher.common.BloodGroup;
import com.bloodmatcher.common.Constants;
import com.bloodmatcher.donor.DonorProfile;
import com.bloodmatcher.donor.DonorProfileRepository;
import com.bloodmatcher.invitation.DonorInvitation;
import com.bloodmatcher.invitation.DonorInvitationRepository;
import com.bloodmatcher.matching.dto.MatchResultDto;
import com.bloodmatcher.request.BloodRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingEngineService {

    private static final Logger log = LoggerFactory.getLogger(MatchingEngineService.class);

    private final BloodCompatibilityService compatibilityService;
    private final DistanceCalculatorService distanceCalculatorService;
    private final DonorProfileRepository donorProfileRepository;
    private final DonorInvitationRepository invitationRepository;

    public MatchingEngineService(BloodCompatibilityService compatibilityService,
                                 DistanceCalculatorService distanceCalculatorService,
                                 DonorProfileRepository donorProfileRepository,
                                 DonorInvitationRepository invitationRepository) {
        this.compatibilityService = compatibilityService;
        this.distanceCalculatorService = distanceCalculatorService;
        this.donorProfileRepository = donorProfileRepository;
        this.invitationRepository = invitationRepository;
    }

    /**
     * Finds and ranks candidate donors for a blood request without persisting invitations.
     * Respects privacy: does NOT expose private donor telephone numbers or exact coordinates.
     */
    @Transactional(readOnly = true)
    public List<MatchResultDto> findCandidateMatches(BloodRequest request, int limit) {
        if (request == null) {
            throw new IllegalArgumentException("Blood request cannot be null");
        }

        // 1. Get compatible donor blood groups
        Set<BloodGroup> compatibleGroups = compatibilityService.getCompatibleDonorsForRecipient(request.getBloodGroup());
        if (compatibleGroups.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Fetch candidate donors from database
        List<DonorProfile> candidateProfiles = donorProfileRepository.findCandidateDonors(compatibleGroups);

        // 3. Exclude requester's own donor profile if any
        Long requesterUserId = request.getRequester().getId();
        candidateProfiles = candidateProfiles.stream()
                .filter(profile -> !profile.getUser().getId().equals(requesterUserId))
                .collect(Collectors.toList());

        // 4. Fetch already invited donor IDs for this request
        Set<Long> alreadyInvitedDonorIds = invitationRepository.findByRequestId(request.getId()).stream()
                .map(invitation -> invitation.getDonor().getId())
                .collect(Collectors.toSet());

        LocalDate now = LocalDate.now();

        // 5. Build explainable match results
        List<MatchResultDto> results = new ArrayList<>();
        for (DonorProfile donor : candidateProfiles) {
            MatchResultDto dto = new MatchResultDto();
            dto.setDonorId(donor.getId());
            dto.setDonorName(donor.getUser().getFullName());
            dto.setBloodGroup(donor.getBloodGroup());
            dto.setGeneralLocation(donor.getAddress());
            dto.setAlreadyInvited(alreadyInvitedDonorIds.contains(donor.getId()));

            // Cooldown check (56 days)
            boolean cooldownEligible = true;
            int daysRemaining = 0;
            if (donor.getLastDonationDate() != null) {
                long daysSince = ChronoUnit.DAYS.between(donor.getLastDonationDate(), now);
                if (daysSince < Constants.DONATION_COOLDOWN_DAYS) {
                    cooldownEligible = false;
                    daysRemaining = (int) (Constants.DONATION_COOLDOWN_DAYS - daysSince);
                }
            }
            dto.setCooldownEligible(cooldownEligible);
            dto.setCooldownDaysRemaining(daysRemaining);

            // Distance calculation
            Double distance = null;
            if (request.hasValidCoordinates() && donor.hasValidCoordinates()) {
                distance = distanceCalculatorService.calculateDistanceKm(
                        request.getLatitude(), request.getLongitude(),
                        donor.getLatitude(), donor.getLongitude()
                );
            }
            dto.setDistanceKm(distance);
            if (distance != null) {
                dto.setDistanceDescription(distance + " km away");
            } else {
                dto.setDistanceDescription("Distance unavailable (coordinates missing)");
            }

            dto.setCompatibilityOutcome(compatibilityService.getCompatibilityExplanation(
                    donor.getBloodGroup(), request.getBloodGroup()
            ));

            // Overall eligibility for invitation
            boolean overallEligible = cooldownEligible && donor.isAvailable() && donor.getUser().isEnabled();
            dto.setEligible(overallEligible);

            results.add(dto);
        }

        // 6. Sort results:
        // Priority 1: Uninvited before already invited
        // Priority 2: Cooldown eligible before ineligible
        // Priority 3: Known distance (ascending) before unknown distance
        // Priority 4: Donor profile ID
        results.sort((a, b) -> {
            if (a.isAlreadyInvited() != b.isAlreadyInvited()) {
                return a.isAlreadyInvited() ? 1 : -1;
            }
            if (a.isCooldownEligible() != b.isCooldownEligible()) {
                return a.isCooldownEligible() ? -1 : 1;
            }
            if (a.getDistanceKm() != null && b.getDistanceKm() != null) {
                return Double.compare(a.getDistanceKm(), b.getDistanceKm());
            }
            if (a.getDistanceKm() != null && b.getDistanceKm() == null) {
                return -1; // known distance prioritized
            }
            if (a.getDistanceKm() == null && b.getDistanceKm() != null) {
                return 1;
            }
            return Long.compare(a.getDonorId(), b.getDonorId());
        });

        int maxCount = limit > 0 ? Math.min(limit, results.size()) : results.size();
        return results.subList(0, maxCount);
    }
}

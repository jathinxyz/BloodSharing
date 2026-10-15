package com.bloodmatcher.matching;

import com.bloodmatcher.common.BloodGroup;
import com.bloodmatcher.common.Role;
import com.bloodmatcher.donor.DonorProfile;
import com.bloodmatcher.donor.DonorProfileRepository;
import com.bloodmatcher.invitation.DonorInvitationRepository;
import com.bloodmatcher.matching.dto.MatchResultDto;
import com.bloodmatcher.request.BloodRequest;
import com.bloodmatcher.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchingEngineServiceTest {

    @Mock
    private BloodCompatibilityService compatibilityService;
    @Mock
    private DistanceCalculatorService distanceCalculator;
    @Mock
    private DonorProfileRepository donorProfileRepository;
    @Mock
    private DonorInvitationRepository invitationRepository;

    private MatchingEngineService matchingEngineService;

    @BeforeEach
    void setUp() {
        matchingEngineService = new MatchingEngineService(
                compatibilityService,
                distanceCalculator,
                donorProfileRepository,
                invitationRepository
        );
    }

    @Test
    @DisplayName("Matching engine excludes requester's own profile and prioritizes distance")
    void testMatchingEngineExclusionsAndDistanceOrdering() {
        User requesterUser = new User("Recipient User", "recip@test.com", "hash", Role.RECIPIENT, true);
        requesterUser.setId(1L);

        BloodRequest request = new BloodRequest(
                requesterUser, BloodGroup.A_POS, 2, "City Hospital", "Central Ave",
                40.7128, -74.0060, null, LocalDate.now().plusDays(2), "Urgent"
        );
        request.setId(100L);

        // Donor 1: Nearby (5 km)
        User user1 = new User("Nearby Donor", "donor1@test.com", "hash", Role.DONOR, true);
        user1.setId(2L);
        DonorProfile donor1 = new DonorProfile(user1, BloodGroup.O_NEG, "555-1", "Downtown", 40.7300, -73.9950, true);
        donor1.setId(10L);

        // Donor 2: Distant (50 km)
        User user2 = new User("Distant Donor", "donor2@test.com", "hash", Role.DONOR, true);
        user2.setId(3L);
        DonorProfile donor2 = new DonorProfile(user2, BloodGroup.A_POS, "555-2", "Suburbs", 41.1000, -73.8000, true);
        donor2.setId(20L);

        // Donor 3: Requester themselves registered as a donor (should be excluded)
        DonorProfile donorSelf = new DonorProfile(requesterUser, BloodGroup.A_POS, "555-self", "Central", 40.7128, -74.0060, true);
        donorSelf.setId(30L);

        // Donor 4: Cooldown ineligible (donated 10 days ago)
        User user4 = new User("Cooldown Donor", "donor4@test.com", "hash", Role.DONOR, true);
        user4.setId(4L);
        DonorProfile donor4 = new DonorProfile(user4, BloodGroup.O_POS, "555-4", "Midtown", 40.7500, -73.9800, true);
        donor4.setId(40L);
        donor4.setLastDonationDate(LocalDate.now().minusDays(10));

        when(compatibilityService.getCompatibleDonorsForRecipient(BloodGroup.A_POS))
                .thenReturn(Set.of(BloodGroup.O_NEG, BloodGroup.O_POS, BloodGroup.A_NEG, BloodGroup.A_POS));

        when(donorProfileRepository.findCandidateDonors(any()))
                .thenReturn(List.of(donor1, donor2, donorSelf, donor4));

        when(invitationRepository.findByRequestId(100L)).thenReturn(Collections.emptyList());

        when(distanceCalculator.calculateDistanceKm(40.7128, -74.0060, 40.7300, -73.9950)).thenReturn(5.2);
        when(distanceCalculator.calculateDistanceKm(40.7128, -74.0060, 41.1000, -73.8000)).thenReturn(51.4);
        when(distanceCalculator.calculateDistanceKm(40.7128, -74.0060, 40.7500, -73.9800)).thenReturn(8.1);

        when(compatibilityService.getCompatibilityExplanation(any(), any())).thenReturn("Compatible RBC match");

        List<MatchResultDto> results = matchingEngineService.findCandidateMatches(request, 10);

        // Requester self (ID 30) must be excluded!
        assertFalse(results.stream().anyMatch(r -> r.getDonorId().equals(30L)), "Self-donor must be excluded");

        // Exactly 3 candidates returned
        assertEquals(3, results.size());

        // Eligible candidates must come before cooldown-ineligible candidates
        // Among eligible candidates, closer donor (5.2 km) must be first!
        assertEquals(10L, results.get(0).getDonorId(), "Nearby donor should be first");
        assertEquals(5.2, results.get(0).getDistanceKm());
        assertTrue(results.get(0).isCooldownEligible());

        assertEquals(20L, results.get(1).getDonorId(), "Distant donor should be second");
        assertEquals(51.4, results.get(1).getDistanceKm());
        assertTrue(results.get(1).isCooldownEligible());

        assertEquals(40L, results.get(2).getDonorId(), "Cooldown-ineligible donor should be last");
        assertFalse(results.get(2).isCooldownEligible());
        assertTrue(results.get(2).getCooldownDaysRemaining() > 0);
    }
}

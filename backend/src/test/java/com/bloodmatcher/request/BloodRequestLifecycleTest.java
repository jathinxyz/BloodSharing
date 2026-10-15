package com.bloodmatcher.request;

import com.bloodmatcher.common.BloodGroup;
import com.bloodmatcher.common.RequestStatus;
import com.bloodmatcher.common.Role;
import com.bloodmatcher.common.UrgencyLevel;
import com.bloodmatcher.common.exception.ConflictException;
import com.bloodmatcher.common.exception.ForbiddenException;
import com.bloodmatcher.donation.DonationService;
import com.bloodmatcher.donor.DonorProfile;
import com.bloodmatcher.donor.DonorProfileRepository;
import com.bloodmatcher.invitation.InvitationService;
import com.bloodmatcher.matching.MatchingEngineService;
import com.bloodmatcher.request.dto.BloodRequestDto;
import com.bloodmatcher.request.dto.CreateBloodRequestDto;
import com.bloodmatcher.request.dto.FulfillRequestDto;
import com.bloodmatcher.user.User;
import com.bloodmatcher.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BloodRequestLifecycleTest {

    @Mock
    private BloodRequestRepository bloodRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MatchingEngineService matchingEngineService;
    @Mock
    private InvitationService invitationService;
    @Mock
    private DonorProfileRepository donorProfileRepository;
    @Mock
    private DonationService donationService;

    private BloodRequestService bloodRequestService;

    private User requester;
    private BloodRequest sampleRequest;

    @BeforeEach
    void setUp() {
        bloodRequestService = new BloodRequestService(
                bloodRequestRepository,
                userRepository,
                matchingEngineService,
                invitationService,
                donorProfileRepository,
                donationService
        );

        requester = new User("Alice Requester", "alice@test.com", "hash", Role.RECIPIENT, true);
        requester.setId(10L);

        sampleRequest = new BloodRequest(
                requester, BloodGroup.B_POS, 3, "General Hospital", "Downtown",
                40.71, -74.00, UrgencyLevel.HIGH, LocalDate.now().plusDays(3), "Urgent need"
        );
        sampleRequest.setId(100L);
    }

    @Test
    @DisplayName("Create blood request initializes status to OPEN")
    void testCreateBloodRequest() {
        CreateBloodRequestDto dto = new CreateBloodRequestDto();
        dto.setBloodGroup(BloodGroup.B_POS);
        dto.setUnitsRequested(3);
        dto.setHospitalName("General Hospital");
        dto.setHospitalLocation("Downtown");
        dto.setUrgency(UrgencyLevel.HIGH);

        when(userRepository.findById(10L)).thenReturn(Optional.of(requester));
        when(bloodRequestRepository.save(any(BloodRequest.class))).thenAnswer(invocation -> {
            BloodRequest req = invocation.getArgument(0);
            req.setId(101L);
            return req;
        });

        BloodRequestDto created = bloodRequestService.createRequest(10L, dto);
        assertNotNull(created);
        assertEquals(RequestStatus.OPEN, created.getStatus());
        assertEquals(3, created.getUnitsRequested());
    }

    @Test
    @DisplayName("Owner can cancel their open blood request")
    void testOwnerCanCancelRequest() {
        when(bloodRequestRepository.findByIdWithRequester(100L)).thenReturn(Optional.of(sampleRequest));
        when(bloodRequestRepository.save(any(BloodRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BloodRequestDto cancelled = bloodRequestService.cancelRequest(100L, 10L);
        assertEquals(RequestStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    @DisplayName("Another user cannot cancel someone else's blood request")
    void testNonOwnerCannotCancelRequest() {
        when(bloodRequestRepository.findByIdWithRequester(100L)).thenReturn(Optional.of(sampleRequest));

        // User ID 99 attempts to cancel user 10's request
        assertThrows(ForbiddenException.class, () -> bloodRequestService.cancelRequest(100L, 99L));
        verify(bloodRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cannot cancel a request already in terminal state")
    void testCannotCancelTerminalRequest() {
        sampleRequest.setStatus(RequestStatus.FULFILLED);
        when(bloodRequestRepository.findByIdWithRequester(100L)).thenReturn(Optional.of(sampleRequest));

        assertThrows(ConflictException.class, () -> bloodRequestService.cancelRequest(100L, 10L));
    }

    @Test
    @DisplayName("Fulfill request workflow updates status and records donation")
    void testFulfillRequest() {
        sampleRequest.setStatus(RequestStatus.IN_PROGRESS);
        when(bloodRequestRepository.findByIdWithRequester(100L)).thenReturn(Optional.of(sampleRequest));
        when(bloodRequestRepository.save(any(BloodRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DonorProfile donor = new DonorProfile();
        donor.setId(55L);
        when(donorProfileRepository.findById(55L)).thenReturn(Optional.of(donor));

        FulfillRequestDto fulfillDto = new FulfillRequestDto(55L, "Successfully donated units");
        BloodRequestDto fulfilled = bloodRequestService.fulfillRequest(100L, 10L, fulfillDto);

        assertEquals(RequestStatus.FULFILLED, fulfilled.getStatus());
        verify(donationService).recordDonation(eq(donor), any());
    }
}

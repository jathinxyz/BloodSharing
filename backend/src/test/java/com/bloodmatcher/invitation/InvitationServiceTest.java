package com.bloodmatcher.invitation;

import com.bloodmatcher.common.BloodGroup;
import com.bloodmatcher.common.InvitationStatus;
import com.bloodmatcher.common.NotificationType;
import com.bloodmatcher.common.RequestStatus;
import com.bloodmatcher.common.Role;
import com.bloodmatcher.common.exception.ConflictException;
import com.bloodmatcher.common.exception.ForbiddenException;
import com.bloodmatcher.donor.DonorProfile;
import com.bloodmatcher.donor.DonorProfileRepository;
import com.bloodmatcher.invitation.dto.InvitationDto;
import com.bloodmatcher.notification.NotificationService;
import com.bloodmatcher.request.BloodRequest;
import com.bloodmatcher.request.BloodRequestRepository;
import com.bloodmatcher.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvitationServiceTest {

    @Mock
    private DonorInvitationRepository invitationRepository;
    @Mock
    private DonorProfileRepository donorProfileRepository;
    @Mock
    private BloodRequestRepository bloodRequestRepository;
    @Mock
    private NotificationService notificationService;

    private InvitationService invitationService;

    private User recipientUser;
    private User donorUser;
    private DonorProfile donorProfile;
    private BloodRequest request;
    private DonorInvitation invitation;

    @BeforeEach
    void setUp() {
        invitationService = new InvitationService(
                invitationRepository,
                donorProfileRepository,
                bloodRequestRepository,
                notificationService
        );

        recipientUser = new User("Recipient", "recip@test.com", "hash", Role.RECIPIENT, true);
        recipientUser.setId(1L);

        donorUser = new User("Donor Dan", "dan@test.com", "hash", Role.DONOR, true);
        donorUser.setId(2L);

        donorProfile = new DonorProfile(donorUser, BloodGroup.O_NEG, "555-1234", "100 Main St", 40.7, -74.0, true);
        donorProfile.setId(10L);

        request = new BloodRequest(recipientUser, BloodGroup.O_POS, 2, "Mercy Hospital", "Uptown", 40.8, -73.9, null, null, null);
        request.setId(20L);
        request.setStatus(RequestStatus.DONORS_NOTIFIED);

        invitation = new DonorInvitation(request, donorProfile, "Compatible RBC match", 12.5);
        invitation.setId(30L);
        invitation.setStatus(InvitationStatus.PENDING);
    }

    @Test
    @DisplayName("Duplicate invitations for the same donor and request are prevented")
    void testDuplicateInvitationPrevented() {
        when(invitationRepository.existsByRequestIdAndDonorId(20L, 10L)).thenReturn(true);

        assertThrows(ConflictException.class, () ->
                invitationService.createInvitation(request, donorProfile, "Compatible", 5.0));
        verify(invitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Donor accepts invitation, transitioning request to IN_PROGRESS and notifying recipient")
    void testDonorAcceptsInvitation() {
        when(invitationRepository.findByIdWithDetails(30L)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(any(DonorInvitation.class))).thenAnswer(i -> i.getArgument(0));

        InvitationDto response = invitationService.respondToInvitation(30L, 2L, true);

        assertEquals(InvitationStatus.ACCEPTED, response.getStatus());
        assertNotNull(response.getRespondedAt());
        // Since accepted, phone number is disclosed in DTO
        assertEquals("555-1234", response.getContactPhone());

        // Request status updated to IN_PROGRESS
        assertEquals(RequestStatus.IN_PROGRESS, request.getStatus());
        verify(bloodRequestRepository).save(request);

        // Recipient was notified
        verify(notificationService).notifyUser(
                eq(recipientUser),
                eq(NotificationType.INVITATION_ACCEPTED),
                anyString(),
                anyString(),
                eq(20L),
                eq(true)
        );
    }

    @Test
    @DisplayName("Donor cannot respond to another donor's invitation")
    void testCannotRespondToAnotherDonorsInvitation() {
        when(invitationRepository.findByIdWithDetails(30L)).thenReturn(Optional.of(invitation));

        // User ID 99 (different user) attempts to accept Dan's invitation
        assertThrows(ForbiddenException.class, () ->
                invitationService.respondToInvitation(30L, 99L, true));

        verify(invitationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Duplicate response to already responded invitation is rejected")
    void testDuplicateResponseRejected() {
        invitation.setStatus(InvitationStatus.ACCEPTED);
        when(invitationRepository.findByIdWithDetails(30L)).thenReturn(Optional.of(invitation));

        assertThrows(ConflictException.class, () ->
                invitationService.respondToInvitation(30L, 2L, false));
    }
}

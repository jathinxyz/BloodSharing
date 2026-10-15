package com.bloodmatcher.donor;

import com.bloodmatcher.common.ApiResponse;
import com.bloodmatcher.common.PagedResponse;
import com.bloodmatcher.donation.DonationService;
import com.bloodmatcher.donation.dto.DonationRecordDto;
import com.bloodmatcher.donor.dto.DonorProfileDto;
import com.bloodmatcher.donor.dto.UpdateAvailabilityRequest;
import com.bloodmatcher.donor.dto.UpdateDonorProfileRequest;
import com.bloodmatcher.invitation.InvitationService;
import com.bloodmatcher.invitation.dto.InvitationDto;
import com.bloodmatcher.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/donors")
@Tag(name = "Donor Operations", description = "Donor profile management, invitations, and donation history")
public class DonorController {

    private final DonorService donorService;
    private final InvitationService invitationService;
    private final DonationService donationService;

    public DonorController(DonorService donorService,
                           InvitationService invitationService,
                           DonationService donationService) {
        this.donorService = donorService;
        this.invitationService = invitationService;
        this.donationService = donationService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated donor's profile")
    public ResponseEntity<ApiResponse<DonorProfileDto>> getMyProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        DonorProfileDto profile = donorService.getProfileByUserId(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PutMapping("/me")
    @Operation(summary = "Create or update current authenticated donor's profile")
    public ResponseEntity<ApiResponse<DonorProfileDto>> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateDonorProfileRequest request) {
        DonorProfileDto profile = donorService.createOrUpdateProfile(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Donor profile updated successfully", profile));
    }

    @PatchMapping("/me/availability")
    @Operation(summary = "Update current authenticated donor's availability status")
    public ResponseEntity<ApiResponse<DonorProfileDto>> updateAvailability(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateAvailabilityRequest request) {
        DonorProfileDto profile = donorService.updateAvailability(userPrincipal.getId(), request.getAvailable());
        return ResponseEntity.ok(ApiResponse.ok("Availability updated successfully", profile));
    }

    @GetMapping("/me/invitations")
    @Operation(summary = "Get invitations received by current authenticated donor")
    public ResponseEntity<ApiResponse<PagedResponse<InvitationDto>>> getMyInvitations(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 10, sort = "invitedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var page = invitationService.getInvitationsForDonorUser(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.from(page)));
    }

    @GetMapping("/me/donations")
    @Operation(summary = "Get completed donation history for current authenticated donor")
    public ResponseEntity<ApiResponse<PagedResponse<DonationRecordDto>>> getMyDonations(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 10, sort = "donationDate", direction = Sort.Direction.DESC) Pageable pageable) {
        var page = donationService.getDonationsForDonorUser(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.from(page)));
    }

    @PostMapping("/me/invitations/{invitationId}/accept")
    @Operation(summary = "Accept an invitation to donate blood")
    public ResponseEntity<ApiResponse<InvitationDto>> acceptInvitation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long invitationId) {
        InvitationDto response = invitationService.respondToInvitation(invitationId, userPrincipal.getId(), true);
        return ResponseEntity.ok(ApiResponse.ok("Invitation accepted successfully. Contact details shared with recipient.", response));
    }

    @PostMapping("/me/invitations/{invitationId}/reject")
    @Operation(summary = "Decline an invitation to donate blood")
    public ResponseEntity<ApiResponse<InvitationDto>> rejectInvitation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long invitationId) {
        InvitationDto response = invitationService.respondToInvitation(invitationId, userPrincipal.getId(), false);
        return ResponseEntity.ok(ApiResponse.ok("Invitation declined", response));
    }
}

package com.bloodmatcher.request;

import com.bloodmatcher.common.ApiResponse;
import com.bloodmatcher.common.PagedResponse;
import com.bloodmatcher.invitation.InvitationService;
import com.bloodmatcher.invitation.dto.InvitationDto;
import com.bloodmatcher.matching.dto.MatchResultDto;
import com.bloodmatcher.request.dto.BloodRequestDto;
import com.bloodmatcher.request.dto.CreateBloodRequestDto;
import com.bloodmatcher.request.dto.FulfillRequestDto;
import com.bloodmatcher.request.dto.TriggerMatchRequest;
import com.bloodmatcher.security.UserPrincipal;
import com.bloodmatcher.user.User;
import com.bloodmatcher.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@Tag(name = "Blood Requests", description = "Blood request lifecycle, donor matching, invitations, and fulfillment")
public class BloodRequestController {

    private final BloodRequestService bloodRequestService;
    private final UserService userService;
    private final InvitationService invitationService;

    public BloodRequestController(BloodRequestService bloodRequestService,
                                  UserService userService,
                                  InvitationService invitationService) {
        this.bloodRequestService = bloodRequestService;
        this.userService = userService;
        this.invitationService = invitationService;
    }

    @PostMapping
    @Operation(summary = "Create a new blood request")
    public ResponseEntity<ApiResponse<BloodRequestDto>> createRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreateBloodRequestDto requestDto) {
        BloodRequestDto created = bloodRequestService.createRequest(userPrincipal.getId(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Blood request created successfully", created));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated recipient's blood requests")
    public ResponseEntity<ApiResponse<PagedResponse<BloodRequestDto>>> getMyRequests(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var page = bloodRequestService.getMyRequests(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.from(page)));
    }

    @GetMapping("/{requestId}")
    @Operation(summary = "Get request details (restricted to requester or administrator)")
    public ResponseEntity<ApiResponse<BloodRequestDto>> getRequestById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long requestId) {
        User currentUser = userService.getUserById(userPrincipal.getId());
        BloodRequestDto request = bloodRequestService.getRequestById(requestId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(request));
    }

    @PostMapping("/{requestId}/cancel")
    @Operation(summary = "Cancel an open blood request")
    public ResponseEntity<ApiResponse<BloodRequestDto>> cancelRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long requestId) {
        BloodRequestDto cancelled = bloodRequestService.cancelRequest(requestId, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Blood request cancelled successfully", cancelled));
    }

    @PostMapping("/{requestId}/fulfill")
    @Operation(summary = "Fulfill a blood request and optionally record donation")
    public ResponseEntity<ApiResponse<BloodRequestDto>> fulfillRequest(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long requestId,
            @Valid @RequestBody(required = false) FulfillRequestDto fulfillDto) {
        BloodRequestDto fulfilled = bloodRequestService.fulfillRequest(requestId, userPrincipal.getId(), fulfillDto);
        return ResponseEntity.ok(ApiResponse.ok("Blood request marked as fulfilled", fulfilled));
    }

    @GetMapping("/{requestId}/matches")
    @Operation(summary = "Preview candidate matches for request (restricted to requester or admin)")
    public ResponseEntity<ApiResponse<List<MatchResultDto>>> getMatches(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long requestId,
            @RequestParam(defaultValue = "10") int limit) {
        User currentUser = userService.getUserById(userPrincipal.getId());
        List<MatchResultDto> matches = bloodRequestService.previewMatches(requestId, currentUser, limit);
        return ResponseEntity.ok(ApiResponse.ok(matches));
    }

    @PostMapping("/{requestId}/match")
    @Operation(summary = "Trigger matching engine and send invitations to eligible donors (restricted to requester or admin)")
    public ResponseEntity<ApiResponse<List<MatchResultDto>>> triggerMatch(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long requestId,
            @Valid @RequestBody(required = false) TriggerMatchRequest triggerRequest) {
        User currentUser = userService.getUserById(userPrincipal.getId());
        int limit = (triggerRequest != null && triggerRequest.getLimit() != null) ? triggerRequest.getLimit() : 10;
        List<MatchResultDto> invited = bloodRequestService.triggerMatchAndNotify(requestId, currentUser, limit);
        return ResponseEntity.ok(ApiResponse.ok("Matching engine executed. " + invited.size() + " donor(s) notified.", invited));
    }

    @GetMapping("/{requestId}/invitations")
    @Operation(summary = "Get all donor invitations and responses for request (restricted to requester or admin)")
    public ResponseEntity<ApiResponse<List<InvitationDto>>> getRequestInvitations(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long requestId) {
        User currentUser = userService.getUserById(userPrincipal.getId());
        List<InvitationDto> invitations = invitationService.getInvitationsForRequest(requestId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(invitations));
    }
}

package com.bloodmatcher.notification;

import com.bloodmatcher.common.ApiResponse;
import com.bloodmatcher.common.PagedResponse;
import com.bloodmatcher.notification.dto.NotificationDto;
import com.bloodmatcher.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "User notification retrieval and read status endpoints")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user notifications with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationDto>>> getMyNotifications(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var page = notificationService.getUserNotifications(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.from(page)));
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long notificationId) {
        NotificationDto dto = notificationService.markAsRead(notificationId, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", dto));
    }

    @PatchMapping("/me/read-all")
    @Operation(summary = "Mark all notifications as read for current user")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAllAsRead(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        int updated = notificationService.markAllAsRead(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Notifications marked as read", Map.of("updatedCount", updated)));
    }
}

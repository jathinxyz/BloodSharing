package com.bloodmatcher.admin;

import com.bloodmatcher.admin.dto.AdminDashboardDto;
import com.bloodmatcher.admin.dto.AdminReportDto;
import com.bloodmatcher.common.ApiResponse;
import com.bloodmatcher.common.PagedResponse;
import com.bloodmatcher.common.RequestStatus;
import com.bloodmatcher.common.Role;
import com.bloodmatcher.request.dto.BloodRequestDto;
import com.bloodmatcher.user.UserService;
import com.bloodmatcher.user.dto.UpdateUserStatusRequest;
import com.bloodmatcher.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Administrator", description = "System metrics, user management, request oversight, and reports")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get system-wide metrics and real operational counts")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getDashboardStats() {
        AdminDashboardDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/users")
    @Operation(summary = "List all registered users with optional role filtering and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<UserDto>>> getAllUsers(
            @RequestParam(required = false) Role role,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var page = userService.getAllUsers(role, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.from(page)));
    }

    @PatchMapping("/users/{userId}/status")
    @Operation(summary = "Enable or disable a user account")
    public ResponseEntity<ApiResponse<UserDto>> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        UserDto updated = userService.updateUserStatus(userId, request.getEnabled());
        return ResponseEntity.ok(ApiResponse.ok("User status updated successfully", updated));
    }

    @GetMapping("/requests")
    @Operation(summary = "List all blood requests across the system with optional status filtering")
    public ResponseEntity<ApiResponse<PagedResponse<BloodRequestDto>>> getAllRequests(
            @RequestParam(required = false) RequestStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var page = adminService.getAllRequests(status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.from(page)));
    }

    @GetMapping("/reports")
    @Operation(summary = "Get blood group distribution and matching performance reports")
    public ResponseEntity<ApiResponse<AdminReportDto>> getReports() {
        AdminReportDto reports = adminService.getReports();
        return ResponseEntity.ok(ApiResponse.ok(reports));
    }
}

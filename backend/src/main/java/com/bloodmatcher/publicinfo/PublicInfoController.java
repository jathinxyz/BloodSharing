package com.bloodmatcher.publicinfo;

import com.bloodmatcher.common.ApiResponse;
import com.bloodmatcher.common.BloodGroup;
import com.bloodmatcher.common.Constants;
import com.bloodmatcher.matching.BloodCompatibilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Public Information", description = "Public health disclaimers, system details, and blood compatibility rules")
public class PublicInfoController {

    private final BloodCompatibilityService compatibilityService;

    public PublicInfoController(BloodCompatibilityService compatibilityService) {
        this.compatibilityService = compatibilityService;
    }

    @GetMapping("/info")
    @Operation(summary = "Get public system info, mission, and version")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("name", "Blood Matcher");
        info.put("description", "Smart Blood Donor and Recipient Matching System");
        info.put("version", "1.0.0");
        info.put("cooldownDays", Constants.DONATION_COOLDOWN_DAYS);
        info.put("disclaimer", Constants.HEALTH_DISCLAIMER);
        return ResponseEntity.ok(ApiResponse.ok(info));
    }

    @GetMapping("/disclaimers")
    @Operation(summary = "Get medical and legal health screening disclaimers")
    public ResponseEntity<ApiResponse<Map<String, String>>> getDisclaimers() {
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "screeningDisclaimer", Constants.HEALTH_DISCLAIMER,
                "clinicalNotice", "Transfusion safety requires certified laboratory typing, antibody screening, and cross-matching before transfusion. Blood Matcher does not replace clinical evaluation."
        )));
    }

    @GetMapping("/compatibility-matrix")
    @Operation(summary = "Get the standard preliminary RBC ABO/Rh compatibility matrix")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getCompatibilityMatrix() {
        Map<String, List<String>> matrix = new LinkedHashMap<>();
        for (BloodGroup recipient : BloodGroup.values()) {
            Set<BloodGroup> compatible = compatibilityService.getCompatibleDonorsForRecipient(recipient);
            matrix.put(recipient.getDisplayValue(), compatible.stream().map(BloodGroup::getDisplayValue).sorted().toList());
        }
        return ResponseEntity.ok(ApiResponse.ok(matrix));
    }
}

package com.bloodmatcher;

import com.bloodmatcher.auth.dto.LoginRequest;
import com.bloodmatcher.auth.dto.RegisterRequest;
import com.bloodmatcher.common.BloodGroup;
import com.bloodmatcher.common.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BloodMatcherIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Actuator health endpoint is publicly accessible and returns UP")
    void testActuatorHealth() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    @DisplayName("Public health disclaimers endpoint returns transparent medical screening notice")
    void testPublicDisclaimers() throws Exception {
        mockMvc.perform(get("/api/public/disclaimers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.screeningDisclaimer", containsString("preliminary software coordination aid")));
    }

    @Test
    @DisplayName("Public compatibility matrix endpoint returns ABO/Rh compatibility mapping")
    void testPublicCompatibilityMatrix() throws Exception {
        mockMvc.perform(get("/api/public/compatibility-matrix"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data['O-']", hasItem("O-")))
                .andExpect(jsonPath("$.data['AB+']", hasSize(8)));
    }

    @Test
    @DisplayName("Public registration rejects self-assigned ADMIN role with 400 Bad Request")
    void testPublicRegistrationRejectsAdminRole() throws Exception {
        RegisterRequest req = new RegisterRequest("Malicious Admin", "malicious@test.com", "Password123!", Role.ADMIN);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", containsString("ADMIN")));
    }

    @Test
    @DisplayName("Unauthenticated request to protected endpoint returns 401 JSON error response")
    void testUnauthenticatedAccessReturns401() throws Exception {
        mockMvc.perform(get("/api/donors/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")));
    }

    @Test
    @DisplayName("Full workflow: Register donor, login, obtain JWT, access profile, and verify forbidden admin endpoints")
    void testFullRegistrationAndAuthFlow() throws Exception {
        // 1. Register Donor
        RegisterRequest registerReq = new RegisterRequest("Sarah Connor", "sarah@resistance.org", "Terminator2024!", Role.DONOR);
        registerReq.setBloodGroup(BloodGroup.O_NEG);
        registerReq.setPhone("555-9000");
        registerReq.setAddress("456 Resistance Way");
        registerReq.setLatitude(34.0522);
        registerReq.setLongitude(-118.2437);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.user.email", is("sarah@resistance.org")))
                .andExpect(jsonPath("$.data.user.role", is("DONOR")));

        // 2. Login
        LoginRequest loginReq = new LoginRequest("sarah@resistance.org", "Terminator2024!");
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).path("data").path("accessToken").asText();

        // 3. Access Donor Profile with JWT
        mockMvc.perform(get("/api/donors/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bloodGroup", is("O-")))
                .andExpect(jsonPath("$.data.phone", is("555-9000")))
                .andExpect(jsonPath("$.data.available", is(true)));

        // 4. Regular donor attempts to access Admin dashboard -> 403 Forbidden
        mockMvc.perform(get("/api/admin/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.error", is("Forbidden")));

        // 5. Access authenticated /api/auth/me
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email", is("sarah@resistance.org")));
    }
}

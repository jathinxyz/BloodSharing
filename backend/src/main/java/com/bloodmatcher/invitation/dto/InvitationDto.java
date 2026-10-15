package com.bloodmatcher.invitation.dto;

import com.bloodmatcher.common.BloodGroup;
import com.bloodmatcher.common.InvitationStatus;
import com.bloodmatcher.common.UrgencyLevel;
import com.bloodmatcher.invitation.DonorInvitation;

import java.time.Instant;

public class InvitationDto {
    private Long id;
    private Long requestId;
    private BloodGroup requiredBloodGroup;
    private Integer unitsRequested;
    private String hospitalName;
    private String hospitalLocation;
    private UrgencyLevel urgency;
    private String compatibilityOutcome;
    private Double distanceKm;
    private InvitationStatus status;
    private Instant invitedAt;
    private Instant respondedAt;
    private String recipientName;
    private String donorName;
    private BloodGroup donorBloodGroup;
    private String contactPhone; // Shared conditionally upon acceptance

    public InvitationDto() {
    }

    public static InvitationDto from(DonorInvitation invitation, boolean includeContactDetails) {
        InvitationDto dto = new InvitationDto();
        dto.setId(invitation.getId());
        dto.setRequestId(invitation.getRequest().getId());
        dto.setRequiredBloodGroup(invitation.getRequest().getBloodGroup());
        dto.setUnitsRequested(invitation.getRequest().getUnitsRequested());
        dto.setHospitalName(invitation.getRequest().getHospitalName());
        dto.setHospitalLocation(invitation.getRequest().getHospitalLocation());
        dto.setUrgency(invitation.getRequest().getUrgency());
        dto.setCompatibilityOutcome(invitation.getCompatibilityOutcome());
        dto.setDistanceKm(invitation.getDistanceKm());
        dto.setStatus(invitation.getStatus());
        dto.setInvitedAt(invitation.getInvitedAt());
        dto.setRespondedAt(invitation.getRespondedAt());
        dto.setRecipientName(invitation.getRequest().getRequester().getFullName());
        dto.setDonorName(invitation.getDonor().getUser().getFullName());
        dto.setDonorBloodGroup(invitation.getDonor().getBloodGroup());

        if (includeContactDetails && invitation.getStatus() == InvitationStatus.ACCEPTED) {
            dto.setContactPhone(invitation.getDonor().getPhone());
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public BloodGroup getRequiredBloodGroup() {
        return requiredBloodGroup;
    }

    public void setRequiredBloodGroup(BloodGroup requiredBloodGroup) {
        this.requiredBloodGroup = requiredBloodGroup;
    }

    public Integer getUnitsRequested() {
        return unitsRequested;
    }

    public void setUnitsRequested(Integer unitsRequested) {
        this.unitsRequested = unitsRequested;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalLocation() {
        return hospitalLocation;
    }

    public void setHospitalLocation(String hospitalLocation) {
        this.hospitalLocation = hospitalLocation;
    }

    public UrgencyLevel getUrgency() {
        return urgency;
    }

    public void setUrgency(UrgencyLevel urgency) {
        this.urgency = urgency;
    }

    public String getCompatibilityOutcome() {
        return compatibilityOutcome;
    }

    public void setCompatibilityOutcome(String compatibilityOutcome) {
        this.compatibilityOutcome = compatibilityOutcome;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public Instant getInvitedAt() {
        return invitedAt;
    }

    public void setInvitedAt(Instant invitedAt) {
        this.invitedAt = invitedAt;
    }

    public Instant getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(Instant respondedAt) {
        this.respondedAt = respondedAt;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public BloodGroup getDonorBloodGroup() {
        return donorBloodGroup;
    }

    public void setDonorBloodGroup(BloodGroup donorBloodGroup) {
        this.donorBloodGroup = donorBloodGroup;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}

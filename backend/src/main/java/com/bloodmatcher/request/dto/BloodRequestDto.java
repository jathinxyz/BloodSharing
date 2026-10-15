package com.bloodmatcher.request.dto;

import com.bloodmatcher.common.BloodGroup;
import com.bloodmatcher.common.RequestStatus;
import com.bloodmatcher.common.UrgencyLevel;
import com.bloodmatcher.request.BloodRequest;

import java.time.Instant;
import java.time.LocalDate;

public class BloodRequestDto {
    private Long id;
    private Long requesterId;
    private String requesterName;
    private BloodGroup bloodGroup;
    private Integer unitsRequested;
    private String hospitalName;
    private String hospitalLocation;
    private Double latitude;
    private Double longitude;
    private UrgencyLevel urgency;
    private LocalDate requiredDate;
    private String notes;
    private RequestStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public BloodRequestDto() {
    }

    public static BloodRequestDto from(BloodRequest request) {
        BloodRequestDto dto = new BloodRequestDto();
        dto.setId(request.getId());
        dto.setRequesterId(request.getRequester().getId());
        dto.setRequesterName(request.getRequester().getFullName());
        dto.setBloodGroup(request.getBloodGroup());
        dto.setUnitsRequested(request.getUnitsRequested());
        dto.setHospitalName(request.getHospitalName());
        dto.setHospitalLocation(request.getHospitalLocation());
        dto.setLatitude(request.getLatitude());
        dto.setLongitude(request.getLongitude());
        dto.setUrgency(request.getUrgency());
        dto.setRequiredDate(request.getRequiredDate());
        dto.setNotes(request.getNotes());
        dto.setStatus(request.getStatus());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public UrgencyLevel getUrgency() {
        return urgency;
    }

    public void setUrgency(UrgencyLevel urgency) {
        this.urgency = urgency;
    }

    public LocalDate getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(LocalDate requiredDate) {
        this.requiredDate = requiredDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

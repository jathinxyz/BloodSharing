package com.bloodmatcher.request.dto;

import com.bloodmatcher.common.BloodGroup;
import com.bloodmatcher.common.UrgencyLevel;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class CreateBloodRequestDto {

    @NotNull(message = "Blood group is required")
    private BloodGroup bloodGroup;

    @NotNull(message = "Units requested is required")
    @Min(value = 1, message = "At least 1 unit must be requested")
    @Max(value = 20, message = "Units requested cannot exceed 20 units")
    private Integer unitsRequested;

    @NotBlank(message = "Hospital name is required")
    @Size(max = 150, message = "Hospital name cannot exceed 150 characters")
    private String hospitalName;

    @NotBlank(message = "Hospital location/address is required")
    @Size(max = 255, message = "Hospital location cannot exceed 255 characters")
    private String hospitalLocation;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    private Double latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    private Double longitude;

    private UrgencyLevel urgency = UrgencyLevel.MEDIUM;

    @FutureOrPresent(message = "Required date cannot be in the past")
    private LocalDate requiredDate;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public CreateBloodRequestDto() {
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
}

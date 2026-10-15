package com.bloodmatcher.donor.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateAvailabilityRequest {

    @NotNull(message = "Availability status must not be null")
    private Boolean available;

    public UpdateAvailabilityRequest() {
    }

    public UpdateAvailabilityRequest(Boolean available) {
        this.available = available;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}

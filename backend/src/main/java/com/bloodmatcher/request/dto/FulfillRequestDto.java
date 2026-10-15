package com.bloodmatcher.request.dto;

import jakarta.validation.constraints.Size;

public class FulfillRequestDto {

    private Long donorId;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public FulfillRequestDto() {
    }

    public FulfillRequestDto(Long donorId, String notes) {
        this.donorId = donorId;
        this.notes = notes;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

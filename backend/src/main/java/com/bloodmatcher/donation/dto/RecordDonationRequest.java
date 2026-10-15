package com.bloodmatcher.donation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class RecordDonationRequest {

    private Long requestId;

    @NotNull(message = "Donation date is required")
    @PastOrPresent(message = "Donation date cannot be in the future")
    private LocalDate donationDate;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    public RecordDonationRequest() {
    }

    public RecordDonationRequest(Long requestId, LocalDate donationDate, String notes) {
        this.requestId = requestId;
        this.donationDate = donationDate;
        this.notes = notes;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public LocalDate getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

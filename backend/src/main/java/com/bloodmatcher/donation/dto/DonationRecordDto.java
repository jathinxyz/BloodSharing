package com.bloodmatcher.donation.dto;

import com.bloodmatcher.donation.DonationRecord;

import java.time.Instant;
import java.time.LocalDate;

public class DonationRecordDto {
    private Long id;
    private Long requestId;
    private Long donorId;
    private String donorName;
    private LocalDate donationDate;
    private String status;
    private String notes;
    private Instant createdAt;

    public DonationRecordDto() {
    }

    public static DonationRecordDto from(DonationRecord record) {
        DonationRecordDto dto = new DonationRecordDto();
        dto.setId(record.getId());
        dto.setRequestId(record.getRequest() != null ? record.getRequest().getId() : null);
        dto.setDonorId(record.getDonor().getId());
        dto.setDonorName(record.getDonor().getUser().getFullName());
        dto.setDonationDate(record.getDonationDate());
        dto.setStatus(record.getStatus());
        dto.setNotes(record.getNotes());
        dto.setCreatedAt(record.getCreatedAt());
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

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public LocalDate getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

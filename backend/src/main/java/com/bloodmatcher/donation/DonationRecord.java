package com.bloodmatcher.donation;

import com.bloodmatcher.donor.DonorProfile;
import com.bloodmatcher.request.BloodRequest;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "donation_records", indexes = {
        @Index(name = "idx_donations_donor_id", columnList = "donor_id"),
        @Index(name = "idx_donations_request_id", columnList = "request_id")
})
public class DonationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private BloodRequest request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "donor_id", nullable = false)
    private DonorProfile donor;

    @Column(name = "donation_date", nullable = false)
    private LocalDate donationDate;

    @Column(name = "status", nullable = false, length = 50)
    private String status = "COMPLETED";

    @Column(name = "notes", length = 500)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public DonationRecord() {
    }

    public DonationRecord(BloodRequest request, DonorProfile donor, LocalDate donationDate, String status, String notes) {
        this.request = request;
        this.donor = donor;
        this.donationDate = donationDate != null ? donationDate : LocalDate.now();
        this.status = status != null ? status : "COMPLETED";
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BloodRequest getRequest() {
        return request;
    }

    public void setRequest(BloodRequest request) {
        this.request = request;
    }

    public DonorProfile getDonor() {
        return donor;
    }

    public void setDonor(DonorProfile donor) {
        this.donor = donor;
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

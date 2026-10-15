package com.bloodmatcher.invitation;

import com.bloodmatcher.common.InvitationStatus;
import com.bloodmatcher.donor.DonorProfile;
import com.bloodmatcher.request.BloodRequest;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "donor_invitations", uniqueConstraints = {
        @UniqueConstraint(name = "uk_invitation_request_donor", columnNames = {"request_id", "donor_id"})
}, indexes = {
        @Index(name = "idx_invitations_request_id", columnList = "request_id"),
        @Index(name = "idx_invitations_donor_id", columnList = "donor_id"),
        @Index(name = "idx_invitations_status", columnList = "status")
})
public class DonorInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "request_id", nullable = false)
    private BloodRequest request;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "donor_id", nullable = false)
    private DonorProfile donor;

    @Column(name = "compatibility_outcome", nullable = false, length = 150)
    private String compatibilityOutcome;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvitationStatus status = InvitationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "invited_at", nullable = false, updatable = false)
    private Instant invitedAt;

    @Column(name = "responded_at")
    private Instant respondedAt;

    public DonorInvitation() {
    }

    public DonorInvitation(BloodRequest request, DonorProfile donor, String compatibilityOutcome, Double distanceKm) {
        this.request = request;
        this.donor = donor;
        this.compatibilityOutcome = compatibilityOutcome;
        this.distanceKm = distanceKm;
        this.status = InvitationStatus.PENDING;
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
}

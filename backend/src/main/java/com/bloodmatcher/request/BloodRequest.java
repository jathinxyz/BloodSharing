package com.bloodmatcher.request;

import com.bloodmatcher.common.BloodGroup;
import com.bloodmatcher.common.RequestStatus;
import com.bloodmatcher.common.UrgencyLevel;
import com.bloodmatcher.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "blood_requests", indexes = {
        @Index(name = "idx_requests_user_id", columnList = "user_id"),
        @Index(name = "idx_requests_status", columnList = "status"),
        @Index(name = "idx_requests_blood_group", columnList = "blood_group"),
        @Index(name = "idx_requests_urgency", columnList = "urgency")
})
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", nullable = false, length = 10)
    private BloodGroup bloodGroup;

    @Column(name = "units_requested", nullable = false)
    private Integer unitsRequested;

    @Column(name = "hospital_name", nullable = false, length = 150)
    private String hospitalName;

    @Column(name = "hospital_location", nullable = false, length = 255)
    private String hospitalLocation;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency", nullable = false, length = 20)
    private UrgencyLevel urgency = UrgencyLevel.MEDIUM;

    @Column(name = "required_date")
    private LocalDate requiredDate;

    @Column(name = "notes", length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 25)
    private RequestStatus status = RequestStatus.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public BloodRequest() {
    }

    public BloodRequest(User requester, BloodGroup bloodGroup, Integer unitsRequested, String hospitalName,
                        String hospitalLocation, Double latitude, Double longitude, UrgencyLevel urgency,
                        LocalDate requiredDate, String notes) {
        this.requester = requester;
        this.bloodGroup = bloodGroup;
        this.unitsRequested = unitsRequested;
        this.hospitalName = hospitalName;
        this.hospitalLocation = hospitalLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.urgency = urgency != null ? urgency : UrgencyLevel.MEDIUM;
        this.requiredDate = requiredDate;
        this.notes = notes;
        this.status = RequestStatus.OPEN;
    }

    public boolean hasValidCoordinates() {
        return latitude != null && longitude != null
                && latitude >= -90.0 && latitude <= 90.0
                && longitude >= -180.0 && longitude <= 180.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
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

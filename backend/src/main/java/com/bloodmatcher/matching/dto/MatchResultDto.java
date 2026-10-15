package com.bloodmatcher.matching.dto;

import com.bloodmatcher.common.BloodGroup;

public class MatchResultDto {
    private Long donorId;
    private String donorName;
    private BloodGroup bloodGroup;
    private String generalLocation;
    private Double distanceKm;
    private String distanceDescription;
    private String compatibilityOutcome;
    private boolean eligible;
    private boolean cooldownEligible;
    private Integer cooldownDaysRemaining;
    private boolean alreadyInvited;

    public MatchResultDto() {
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

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getGeneralLocation() {
        return generalLocation;
    }

    public void setGeneralLocation(String generalLocation) {
        this.generalLocation = generalLocation;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public String getDistanceDescription() {
        return distanceDescription;
    }

    public void setDistanceDescription(String distanceDescription) {
        this.distanceDescription = distanceDescription;
    }

    public String getCompatibilityOutcome() {
        return compatibilityOutcome;
    }

    public void setCompatibilityOutcome(String compatibilityOutcome) {
        this.compatibilityOutcome = compatibilityOutcome;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public boolean isCooldownEligible() {
        return cooldownEligible;
    }

    public void setCooldownEligible(boolean cooldownEligible) {
        this.cooldownEligible = cooldownEligible;
    }

    public Integer getCooldownDaysRemaining() {
        return cooldownDaysRemaining;
    }

    public void setCooldownDaysRemaining(Integer cooldownDaysRemaining) {
        this.cooldownDaysRemaining = cooldownDaysRemaining;
    }

    public boolean isAlreadyInvited() {
        return alreadyInvited;
    }

    public void setAlreadyInvited(boolean alreadyInvited) {
        this.alreadyInvited = alreadyInvited;
    }
}

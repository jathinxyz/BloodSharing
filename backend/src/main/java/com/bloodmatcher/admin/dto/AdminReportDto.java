package com.bloodmatcher.admin.dto;

import com.bloodmatcher.common.BloodGroup;

import java.util.Map;

public class AdminReportDto {
    private Map<BloodGroup, Long> donorCountByBloodGroup;
    private Map<BloodGroup, Long> requestCountByBloodGroup;
    private double invitationAcceptanceRate;
    private double requestFulfillmentRate;

    public AdminReportDto() {
    }

    public Map<BloodGroup, Long> getDonorCountByBloodGroup() {
        return donorCountByBloodGroup;
    }

    public void setDonorCountByBloodGroup(Map<BloodGroup, Long> donorCountByBloodGroup) {
        this.donorCountByBloodGroup = donorCountByBloodGroup;
    }

    public Map<BloodGroup, Long> getRequestCountByBloodGroup() {
        return requestCountByBloodGroup;
    }

    public void setRequestCountByBloodGroup(Map<BloodGroup, Long> requestCountByBloodGroup) {
        this.requestCountByBloodGroup = requestCountByBloodGroup;
    }

    public double getInvitationAcceptanceRate() {
        return invitationAcceptanceRate;
    }

    public void setInvitationAcceptanceRate(double invitationAcceptanceRate) {
        this.invitationAcceptanceRate = invitationAcceptanceRate;
    }

    public double getRequestFulfillmentRate() {
        return requestFulfillmentRate;
    }

    public void setRequestFulfillmentRate(double requestFulfillmentRate) {
        this.requestFulfillmentRate = requestFulfillmentRate;
    }
}

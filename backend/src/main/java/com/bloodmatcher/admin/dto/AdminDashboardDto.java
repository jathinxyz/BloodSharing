package com.bloodmatcher.admin.dto;

public class AdminDashboardDto {
    private long totalUsers;
    private long totalDonors;
    private long totalRecipients;
    private long availableDonors;
    private long totalRequests;
    private long openRequests;
    private long inProgressRequests;
    private long fulfilledRequests;
    private long cancelledRequests;
    private long totalDonationsCompleted;
    private long totalInvitationsSent;
    private long invitationsAccepted;
    private long invitationsPending;
    private long invitationsRejected;

    public AdminDashboardDto() {
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalDonors() {
        return totalDonors;
    }

    public void setTotalDonors(long totalDonors) {
        this.totalDonors = totalDonors;
    }

    public long getTotalRecipients() {
        return totalRecipients;
    }

    public void setTotalRecipients(long totalRecipients) {
        this.totalRecipients = totalRecipients;
    }

    public long getAvailableDonors() {
        return availableDonors;
    }

    public void setAvailableDonors(long availableDonors) {
        this.availableDonors = availableDonors;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getOpenRequests() {
        return openRequests;
    }

    public void setOpenRequests(long openRequests) {
        this.openRequests = openRequests;
    }

    public long getInProgressRequests() {
        return inProgressRequests;
    }

    public void setInProgressRequests(long inProgressRequests) {
        this.inProgressRequests = inProgressRequests;
    }

    public long getFulfilledRequests() {
        return fulfilledRequests;
    }

    public void setFulfilledRequests(long fulfilledRequests) {
        this.fulfilledRequests = fulfilledRequests;
    }

    public long getCancelledRequests() {
        return cancelledRequests;
    }

    public void setCancelledRequests(long cancelledRequests) {
        this.cancelledRequests = cancelledRequests;
    }

    public long getTotalDonationsCompleted() {
        return totalDonationsCompleted;
    }

    public void setTotalDonationsCompleted(long totalDonationsCompleted) {
        this.totalDonationsCompleted = totalDonationsCompleted;
    }

    public long getTotalInvitationsSent() {
        return totalInvitationsSent;
    }

    public void setTotalInvitationsSent(long totalInvitationsSent) {
        this.totalInvitationsSent = totalInvitationsSent;
    }

    public long getInvitationsAccepted() {
        return invitationsAccepted;
    }

    public void setInvitationsAccepted(long invitationsAccepted) {
        this.invitationsAccepted = invitationsAccepted;
    }

    public long getInvitationsPending() {
        return invitationsPending;
    }

    public void setInvitationsPending(long invitationsPending) {
        this.invitationsPending = invitationsPending;
    }

    public long getInvitationsRejected() {
        return invitationsRejected;
    }

    public void setInvitationsRejected(long invitationsRejected) {
        this.invitationsRejected = invitationsRejected;
    }
}

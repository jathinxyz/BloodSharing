package com.bloodmatcher.common;

public enum RequestStatus {
    OPEN,
    MATCHING,
    DONORS_NOTIFIED,
    IN_PROGRESS,
    FULFILLED,
    CANCELLED,
    EXPIRED;

    public boolean isTerminal() {
        return this == FULFILLED || this == CANCELLED || this == EXPIRED;
    }
}

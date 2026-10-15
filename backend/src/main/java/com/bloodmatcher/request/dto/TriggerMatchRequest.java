package com.bloodmatcher.request.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class TriggerMatchRequest {

    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 50, message = "Limit cannot exceed 50")
    private Integer limit = 10;

    public TriggerMatchRequest() {
    }

    public TriggerMatchRequest(Integer limit) {
        this.limit = limit;
    }

    public Integer getLimit() {
        return limit != null ? limit : 10;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}

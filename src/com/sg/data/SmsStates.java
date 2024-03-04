package com.sg.data;

public enum SmsStates {

    DRAFT("Drafted"),
    WAITING("Waiting"),
    APPROVED("Approved"),
    REJECTED("Rejected");

    private final String status;

    SmsStates(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

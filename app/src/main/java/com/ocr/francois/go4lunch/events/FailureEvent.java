package com.ocr.francois.go4lunch.events;

public class FailureEvent {

    private String failureMessage;

    public FailureEvent(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public String getFailureMessage() {
        return failureMessage;
    }
}

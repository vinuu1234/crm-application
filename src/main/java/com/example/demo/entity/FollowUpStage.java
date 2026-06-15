package com.example.demo.entity;

public enum FollowUpStage {
    S2A("Stage 2A - Initial Interest"),
    S2C("Stage 2C - Negotiation"),
    CW("Closed Won"),
    DND("Do Not Disturb");
    
    private final String description;
    
    FollowUpStage(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}
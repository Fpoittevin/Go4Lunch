package com.ocr.francois.go4lunch.models;

import com.google.firebase.Timestamp;

public class Lunch {

    private String userId;
    private String restaurantId;
    private String restaurantName;
    private Timestamp timestamp;

    public Lunch(){}

    public Lunch(String userId, String restaurantId, String restaurantName) {
        this.userId = userId;
        this.timestamp = timestamp;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

   public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }
}
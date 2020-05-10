package com.ocr.francois.go4lunch.models;

public class Like {

    private String restaurantPlaceId;
    private String userId;

    public Like() {}

    public Like(String restaurantPlaceId, String userId) {
        this.restaurantPlaceId = restaurantPlaceId;
        this.userId = userId;
    }

    public String getRestaurantPlaceId() {
        return restaurantPlaceId;
    }

    public void setRestaurantPlaceId(String restaurantPlaceId) {
        this.restaurantPlaceId = restaurantPlaceId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

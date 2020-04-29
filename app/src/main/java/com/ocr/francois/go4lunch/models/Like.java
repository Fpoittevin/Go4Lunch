package com.ocr.francois.go4lunch.models;

public class Like {

    private String RestaurantPlaceId;
    private String UserId;

    public Like() {}

    public Like(String restaurantPlaceId, String userId) {
        RestaurantPlaceId = restaurantPlaceId;
        UserId = userId;
    }

    public String getRestaurantPlaceId() {
        return RestaurantPlaceId;
    }

    public void setRestaurantPlaceId(String restaurantPlaceId) {
        RestaurantPlaceId = restaurantPlaceId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}

package com.ocr.francois.go4lunch.models;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;
import com.ocr.francois.go4lunch.utils.DateTool;

public class User {

    private String id;
    private String userName;
    @Nullable
    private String urlPicture;

    @Nullable
    private String lunchRestaurantPlaceId;
    @Nullable
    private String lunchRestaurantName;
    @Nullable
    private Timestamp lunchTimestamp;

    public User() {
    }

    public User(String id, String userName, @Nullable String urlPicture) {
        this.id = id;
        this.userName = userName;
        this.urlPicture = urlPicture;
    }

    public User(String id, String userName, @Nullable String urlPicture, @Nullable String lunchRestaurantPlaceId, @Nullable String lunchRestaurantName, @Nullable Timestamp lunchTimestamp) {
        this.id = id;
        this.userName = userName;
        this.urlPicture = urlPicture;
        this.lunchRestaurantPlaceId = lunchRestaurantPlaceId;
        this.lunchRestaurantName = lunchRestaurantName;
        this.lunchTimestamp = lunchTimestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    @Nullable
    public String getLunchRestaurantPlaceId() {
        return lunchRestaurantPlaceId;
    }

    @Nullable
    public String getLunchRestaurantName() {
        return lunchRestaurantName;
    }

    @Nullable
    public Timestamp getLunchTimestamp() {
        return lunchTimestamp;
    }

    public boolean choseARestaurant() {
        return lunchTimestamp != null && DateTool.isToday(lunchTimestamp) && lunchRestaurantPlaceId != null && lunchRestaurantName != null;
    }
}
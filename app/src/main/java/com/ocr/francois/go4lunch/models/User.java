package com.ocr.francois.go4lunch.models;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

import java.util.List;

public class User {

    private String id;
    private String userName;
    @Nullable private String urlPicture;

    @Nullable private String lunchRestaurantPlaceId;
    @Nullable private String lunchRestaurantName;
    @Nullable private Timestamp lunchTimestamp;

    public User() {}

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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    @Nullable
    public String getLunchRestaurantPlaceId() {
        return lunchRestaurantPlaceId;
    }

    public void setLunchRestaurantPlaceId(@Nullable String lunchRestaurantPlaceId) {
        this.lunchRestaurantPlaceId = lunchRestaurantPlaceId;
    }

    @Nullable
    public String getLunchRestaurantName() {
        return lunchRestaurantName;
    }

    public void setLunchRestaurantName(@Nullable String lunchRestaurantName) {
        this.lunchRestaurantName = lunchRestaurantName;
    }

    @Nullable
    public Timestamp getLunchTimestamp() {
        return lunchTimestamp;
    }

    public void setLunchTimestamp(@Nullable Timestamp lunchTimestamp) {
        this.lunchTimestamp = lunchTimestamp;
    }
}
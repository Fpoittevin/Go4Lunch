package com.ocr.francois.go4lunch.models;

import androidx.annotation.Nullable;

import java.util.List;

public class User {

    private String id;
    private String userName;
    @Nullable private String urlPicture;

    public User() {}

    public User(String id, String userName, String urlPicture) {
        this.id = id;
        this.userName = userName;
        this.urlPicture = urlPicture;
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

    public boolean choseARestaurant(List<Lunch> lunches) {

        for(int i = 0; i < lunches.size(); i++) {
            if(lunches.get(i).getUserId().equals(getId())) {
                return true;
            }

        }
        return false;
    }
}

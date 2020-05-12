package com.ocr.francois.go4lunch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Restaurant {

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("vicinity")
    @Expose
    private String vicinity;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos = null;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("international_phone_number")
    @Expose
    private String internationalPhoneNumber;

    private List<User> participants = new ArrayList<>();
    private int numberOfParticipants = 0;

    private int note = 0;
    private double distance = 0;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public void setInternationalPhoneNumber(String internationalPhoneNumber) {
        this.internationalPhoneNumber = internationalPhoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }


    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public static class RestaurantDistanceComparator implements Comparator<Restaurant> {
        @Override
        public int compare(Restaurant restaurantA, Restaurant restaurantB) {
            return Double.compare(restaurantA.distance, restaurantB.distance);
        }
    }

    public static class RestaurantParticipantsComparator implements Comparator<Restaurant> {
        @Override
        public int compare(Restaurant restaurantA, Restaurant restaurantB) {
            return Integer.compare(restaurantB.getNumberOfParticipants(), restaurantA.getNumberOfParticipants());
        }
    }

    public static class RestaurantNotesComparator implements Comparator<Restaurant> {
        @Override
        public int compare(Restaurant restaurantA, Restaurant restaurantB) {
            return Integer.compare(restaurantB.getNote(), restaurantA.getNote());
        }
    }
}
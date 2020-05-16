package com.ocr.francois.go4lunch.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
    private List<Photo> photos;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("international_phone_number")
    @Expose
    private String internationalPhoneNumber;

    private List<User> participants;
    private int numberOfParticipants;

    private int note;
    private double distance;

    public Restaurant(Geometry geometry, String name, String placeId, String vicinity, List<Photo> photos, String website, String internationalPhoneNumber, List<User> participants, int numberOfParticipants, int note, double distance) {
        this.geometry = geometry;
        this.name = name;
        this.placeId = placeId;
        this.vicinity = vicinity;
        this.photos = photos;
        this.website = website;
        this.internationalPhoneNumber = internationalPhoneNumber;
        this.participants = participants;
        this.numberOfParticipants = numberOfParticipants;
        this.note = note;
        this.distance = distance;
    }

    public Restaurant() { }

    public Geometry getGeometry() {
        return geometry;
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

    public List<Photo> getPhotos() {
        return photos;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public String getWebsite() {
        return website;
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
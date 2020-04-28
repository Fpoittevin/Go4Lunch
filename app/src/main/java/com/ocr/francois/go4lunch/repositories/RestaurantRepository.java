package com.ocr.francois.go4lunch.repositories;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.ocr.francois.go4lunch.api.PlacesService;
import com.ocr.francois.go4lunch.models.GoogleDetailResult;
import com.ocr.francois.go4lunch.models.GoogleSearchResults;
import com.ocr.francois.go4lunch.models.Restaurant;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private MutableLiveData<List<Restaurant>> restaurants;
    private PlacesService placesService;

    public RestaurantRepository() {
        this.placesService = PlacesService.retrofit.create(PlacesService.class);
    }

    public MutableLiveData<List<Restaurant>> getRestaurants(Location location, int radius) {

        if (restaurants == null) {
            restaurants = new MutableLiveData<>();
        }

        Call<GoogleSearchResults> call = placesService.getNearbyPlaces(location.getLatitude() + "," + location.getLongitude(), radius);
        call.enqueue(new Callback<GoogleSearchResults>() {
            @Override
            public void onResponse(Call<GoogleSearchResults> call, Response<GoogleSearchResults> response) {

                if (response.body() != null) {
                    List<Restaurant> restaurantsList = response.body().getRestaurants();
                    for (Restaurant restaurant : restaurantsList) {
                        Location restaurantLocation = new Location("restaurant location");
                        restaurantLocation.setLatitude(restaurant.getGeometry().getLocation().getLat());
                        restaurantLocation.setLongitude(restaurant.getGeometry().getLocation().getLng());

                        restaurant.setDistance(location.distanceTo(restaurantLocation));
                    }

                    restaurants.setValue(restaurantsList);
                }
            }

            @Override
            public void onFailure(Call<GoogleSearchResults> call, Throwable t) {

                Log.d("repo", "onFailure: ");
            }
        });
        return restaurants;
    }

    public MutableLiveData<Restaurant> getRestaurant(String placeId) {

        MutableLiveData<Restaurant> restaurant = new MutableLiveData<>();
        Call<GoogleDetailResult> call = placesService.getPlaceDetails(placeId);

        call.enqueue(new Callback<GoogleDetailResult>() {
            @Override
            public void onResponse(Call<GoogleDetailResult> call, Response<GoogleDetailResult> response) {

                if (response.body() != null) {
                    Log.d("repo", "onResponse: " + response);
                    restaurant.setValue(response.body().getRestaurant());
                }
            }

            @Override
            public void onFailure(Call<GoogleDetailResult> call, Throwable t) {

                Log.d("repo", "onFailure: ");
            }
        });

        return restaurant;
    }
}

package com.ocr.francois.go4lunch.repositories;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.ocr.francois.go4lunch.api.PlacesService;
import com.ocr.francois.go4lunch.models.GoogleSearchResults;
import com.ocr.francois.go4lunch.models.Restaurant;

import java.util.List;

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
                    restaurants.setValue(response.body().getRestaurants());
                }
            }

            @Override
            public void onFailure(Call<GoogleSearchResults> call, Throwable t) {

                Log.d("repo", "onFailure: ");
            }
        });
        return restaurants;
    }
}

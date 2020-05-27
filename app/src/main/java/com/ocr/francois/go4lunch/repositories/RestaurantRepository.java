package com.ocr.francois.go4lunch.repositories;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.ocr.francois.go4lunch.api.PlacesService;
import com.ocr.francois.go4lunch.events.FailureEvent;
import com.ocr.francois.go4lunch.models.GoogleDetailResult;
import com.ocr.francois.go4lunch.models.GoogleSearchResults;
import com.ocr.francois.go4lunch.models.Restaurant;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private MutableLiveData<List<Restaurant>> restaurants;
    private final PlacesService placesService;

    public RestaurantRepository() {
        this.placesService = PlacesService.retrofit.create(PlacesService.class);
    }

    public MutableLiveData<List<Restaurant>> getRestaurants(Location location, int radius) {

        if (restaurants == null) restaurants = new MutableLiveData<>();

        Call<GoogleSearchResults> call = placesService.getNearbyPlaces(location.getLatitude() + "," + location.getLongitude(), radius);
        call.enqueue(new Callback<GoogleSearchResults>() {
            @Override
            public void onResponse(@NonNull Call<GoogleSearchResults> call, @NonNull Response<GoogleSearchResults> response) {

                if (response.isSuccessful() && response.body() != null) {
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
            public void onFailure(@NonNull Call<GoogleSearchResults> call, @NonNull Throwable t) {
                EventBus.getDefault().post(new FailureEvent(t.getMessage()));
            }
        });
        return restaurants;
    }

    public MutableLiveData<Restaurant> getRestaurant(String placeId) {

        MutableLiveData<Restaurant> restaurant = new MutableLiveData<>();
        Call<GoogleDetailResult> call = placesService.getPlaceDetails(placeId);

        call.enqueue(new Callback<GoogleDetailResult>() {
            @Override
            public void onResponse(@NonNull Call<GoogleDetailResult> call, @NonNull Response<GoogleDetailResult> response) {

                if (response.isSuccessful() && response.body() != null) {
                    restaurant.setValue(response.body().getRestaurant());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GoogleDetailResult> call, @NonNull Throwable t) {
                EventBus.getDefault().post(new FailureEvent(t.getMessage()));
            }
        });

        return restaurant;
    }
}

package com.ocr.francois.go4lunch.ui.viewmodels;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.repositories.RestaurantRepository;

import java.util.List;

public class RestaurantViewModel extends ViewModel {

    private final RestaurantRepository restaurantRepository;

    public RestaurantViewModel(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public MutableLiveData<List<Restaurant>> getRestaurants(Location location, int radius) {
        return restaurantRepository.getRestaurants(location, radius);
    }
}

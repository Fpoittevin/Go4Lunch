package com.ocr.francois.go4lunch.ui.viewmodels;

import android.location.Location;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.repositories.RestaurantRepository;
import com.ocr.francois.go4lunch.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class LunchViewModel extends ViewModel {

    private RestaurantRepository restaurantRepository;
    private UserRepository userRepository;

    public LunchViewModel(RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    // RESTAURANTS

    public MutableLiveData<List<Restaurant>> getRestaurants(Location location, int radius) {
        return restaurantRepository.getRestaurants(location, radius);
    }

    public MutableLiveData<Restaurant> getRestaurantDetails(String placeId) {
        return restaurantRepository.getRestaurant(placeId);
    }

    // USERS

    public MutableLiveData<List<User>> getUsers() {
        return userRepository.getUsers();
    }

    protected int getNumbersOfUsersByRestaurant(String placeId, List<User> users) {

        return getWorkmatesByRestaurant(placeId, users, false, null).size();
    }

    protected List<User> getWorkmatesByRestaurant(String placeId, List<User> users, Boolean includeCurrentUser, @Nullable String currentUserId) {

        List<User> workmatesByRestaurant = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {

            User user = users.get(i);

            if (user.getLunchRestaurantPlaceId() != null && user.getLunchRestaurantPlaceId().equals(placeId)) {

                if (!includeCurrentUser && user.getId().equals(currentUserId)) {
                    continue;
                }
                workmatesByRestaurant.add(user);
            }
        }

        return workmatesByRestaurant;
    }
}

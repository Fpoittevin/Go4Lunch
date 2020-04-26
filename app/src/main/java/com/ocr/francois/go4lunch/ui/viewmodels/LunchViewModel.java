package com.ocr.francois.go4lunch.ui.viewmodels;

import android.location.Location;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.repositories.RestaurantRepository;
import com.ocr.francois.go4lunch.repositories.UserRepository;
import com.ocr.francois.go4lunch.utils.DateTool;

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

    public MutableLiveData<Restaurant> getRestaurant(String placeId) {
        return restaurantRepository.getRestaurant(placeId);
    }

    // USERS

    public MutableLiveData<List<User>> getUsers() {
        return userRepository.getUsers();
    }

    public MutableLiveData<User> getCurrentUserInFirestore(String id) {
        return userRepository.getUser(id);
    }

    public int getNumbersOfUsersByRestaurant(Restaurant restaurant, List<User> users) {

        return getWorkmatesByRestaurant(restaurant, users, false, null).size();
    }

    public List<User> getWorkmatesByRestaurant(Restaurant restaurant, List<User> users, Boolean includeCurrentUser, @Nullable String currentUserId) {

        List<User> workmatesByRestaurant = new ArrayList<>();

        if (users != null && !users.isEmpty()) {
            for (User user : users) {

                if (user.getLunchTimestamp() != null && user.getLunchRestaurantPlaceId() != null) {
                    if (DateTool.isToday(user.getLunchTimestamp()) && user.getLunchRestaurantPlaceId().equals(restaurant.getPlaceId())) {

                        if (!includeCurrentUser && user.getId().equals(currentUserId)) {
                            continue;
                        }
                        workmatesByRestaurant.add(user);
                    }
                }
            }
        }
        return workmatesByRestaurant;
    }

    public void addParticipantInRestaurant(Restaurant restaurant, List<User> users) {
        restaurant.getParticipants().clear();
        restaurant.setParticipants(getWorkmatesByRestaurant(restaurant, users, true, null));
    }

    public void addParticipantsInAllRestaurants(List<Restaurant> restaurants, List<User> users) {
        if (restaurants != null && !restaurants.isEmpty()) {
            for (Restaurant restaurant : restaurants) {
                addParticipantInRestaurant(restaurant, users);
            }
        }
    }

    public void saveLunch(String userId, String lunchRestaurantPlaceId, String lunchRestaurantName) {
        userRepository.saveLunch(userId, lunchRestaurantPlaceId, lunchRestaurantName);
    }
}

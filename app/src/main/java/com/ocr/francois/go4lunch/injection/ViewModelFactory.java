package com.ocr.francois.go4lunch.injection;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.ocr.francois.go4lunch.repositories.LikeRepository;
import com.ocr.francois.go4lunch.repositories.RestaurantRepository;
import com.ocr.francois.go4lunch.repositories.UserRepository;
import com.ocr.francois.go4lunch.ui.viewmodels.LunchViewModel;
import com.ocr.francois.go4lunch.ui.viewmodels.RestaurantViewModel;
import com.ocr.francois.go4lunch.ui.viewmodels.UserViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public ViewModelFactory(RestaurantRepository restaurantRepository, UserRepository userRepository, LikeRepository likeRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RestaurantViewModel.class)) {
            return (T) new RestaurantViewModel(restaurantRepository);
        }
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(userRepository);
        }
        if (modelClass.isAssignableFrom(LunchViewModel.class)) {
            return (T) new LunchViewModel(restaurantRepository, userRepository, likeRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
package com.ocr.francois.go4lunch.injection;

import com.ocr.francois.go4lunch.repositories.LikeRepository;
import com.ocr.francois.go4lunch.repositories.RestaurantRepository;
import com.ocr.francois.go4lunch.repositories.UserRepository;

public class Injection {

    private static RestaurantRepository provideRestaurantRepository() {
        return new RestaurantRepository();
    }

    private static UserRepository provideUserRepository() {
        return new UserRepository();
    }

    private static LikeRepository provideLikeRepository() {
        return new LikeRepository();
    }

    public static ViewModelFactory provideViewModelFactory() {
        RestaurantRepository restaurantRepository = provideRestaurantRepository();
        UserRepository userRepository = provideUserRepository();
        LikeRepository likeRepository = provideLikeRepository();

        return new ViewModelFactory(restaurantRepository, userRepository, likeRepository);
    }
}

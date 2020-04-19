package com.ocr.francois.go4lunch.injection;

import android.content.Context;

import com.ocr.francois.go4lunch.repositories.RestaurantRepository;
import com.ocr.francois.go4lunch.repositories.UserRepository;

public class Injection {

    public static RestaurantRepository provideRestaurantRepository(Context context) {
        return new RestaurantRepository();
    }

    public static UserRepository provideUserRepository(Context context) {
        return new UserRepository();
    }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        RestaurantRepository restaurantRepository = provideRestaurantRepository(context);
        UserRepository userRepository = provideUserRepository(context);

        return new ViewModelFactory(restaurantRepository, userRepository);
    }
}

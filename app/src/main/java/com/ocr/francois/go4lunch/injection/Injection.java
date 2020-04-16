package com.ocr.francois.go4lunch.injection;

import android.content.Context;

import com.ocr.francois.go4lunch.repositories.RestaurantRepository;

public class Injection {

    public static RestaurantRepository provideRestaurantRepository(Context context) {
        return new RestaurantRepository();
    }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        RestaurantRepository restaurantRepository = provideRestaurantRepository(context);

        return new ViewModelFactory(restaurantRepository);
    }
}

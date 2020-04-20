package com.ocr.francois.go4lunch.ui.restaurantDetails;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ocr.francois.go4lunch.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantDetailsFragment extends Fragment {

    public RestaurantDetailsFragment() {
        // Required empty public constructor
    }

    public static RestaurantDetailsFragment newInstance() {
        return new RestaurantDetailsFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_details, container, false);
    }
}

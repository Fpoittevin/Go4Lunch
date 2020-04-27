package com.ocr.francois.go4lunch.ui.base;

import android.location.Location;
import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ocr.francois.go4lunch.injection.Injection;
import com.ocr.francois.go4lunch.injection.ViewModelFactory;
import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.ui.viewmodels.LunchViewModel;
import com.ocr.francois.go4lunch.utils.LocationTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BaseFragment extends Fragment {

    protected LunchViewModel lunchViewModel;
    protected LocationTracker locationTracker;
    protected Location currentLocation = null;

    protected List<Restaurant> restaurants = new ArrayList<>();
    protected List<User> users = new ArrayList<>();

    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected void configureLunchViewModel() {

        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        lunchViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()), viewModelFactory).get(LunchViewModel.class);
    }

    protected void configureLocationTracker() {

        this.locationTracker = new LocationTracker(getContext());
    }

    protected void setRestaurants(List<Restaurant> restaurants) {

        this.restaurants.clear();
        this.restaurants.addAll(restaurants);
    }

    protected void setUsers(List<User> users) {
        this.users.clear();
        this.users.addAll(users);
    }

    protected void showProgressBar(int resId) {
        ProgressBar progressBar = Objects.requireNonNull(this.getActivity()).findViewById(resId);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgressBar(int resId) {
        ProgressBar progressBar = Objects.requireNonNull(this.getActivity()).findViewById(resId);
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
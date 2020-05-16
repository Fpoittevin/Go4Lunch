package com.ocr.francois.go4lunch.ui.base;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ocr.francois.go4lunch.injection.Injection;
import com.ocr.francois.go4lunch.injection.ViewModelFactory;
import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.ui.settings.SettingsFragment;
import com.ocr.francois.go4lunch.ui.viewmodels.LunchViewModel;
import com.ocr.francois.go4lunch.utils.LocationTracker;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    protected LocationTracker locationTracker;
    protected Location currentLocation = null;
    protected SharedPreferences sharedPreferences;
    protected List<Restaurant> restaurants = new ArrayList<>();
    protected List<User> users = new ArrayList<>();
    private LunchViewModel lunchViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        showProgressBar();

        return view;
    }

    protected abstract int getLayoutId();

    protected abstract int getProgressBarId();

    protected abstract void updateUiWhenDataChange();

    protected FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected void configureLunchViewModel() {

        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        lunchViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(LunchViewModel.class);
    }

    protected void configureLocationTracker() {
        this.locationTracker = new LocationTracker(getContext());
    }

    protected void getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        }
    }

    protected void getRestaurants() {
        getSharedPreferences();
        int radius = sharedPreferences.getInt(SettingsFragment.SEARCH_RADIUS_KEY_PREFERENCES, SettingsFragment.DEFAULT_SEARCH_RADIUS);
        if (restaurants == null) {
            restaurants = new ArrayList<>();
        }

        if (currentLocation != null) {
            lunchViewModel.getRestaurants(currentLocation, radius).observe(this, restaurantsList -> {
                setRestaurants(restaurantsList);
                updateUiWhenDataChange();
            });
        }
    }

    protected void getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        lunchViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            setUsers(users);
            updateUiWhenDataChange();
        });
    }

    private void setRestaurants(List<Restaurant> restaurantsList) {
        this.restaurants.clear();
        this.restaurants.addAll(restaurantsList);
    }

    private void setUsers(List<User> users) {
        this.users.clear();
        this.users.addAll(users);
    }

    private void showProgressBar() {
        ProgressBar progressBar = this.requireActivity().findViewById(getProgressBarId());
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgressBar() {
        ProgressBar progressBar = this.requireActivity().findViewById(getProgressBarId());
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
package com.ocr.francois.go4lunch.ui.base;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.SphericalUtil;
import com.ocr.francois.go4lunch.BuildConfig;
import com.ocr.francois.go4lunch.R;
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
    protected final List<Restaurant> restaurants = new ArrayList<>();
    protected final List<Restaurant> restaurantsSearchResult = new ArrayList<>();
    protected final List<User> users = new ArrayList<>();
    protected Menu menu;
    private LunchViewModel lunchViewModel;
    private PlacesClient placesClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        showProgressBar(true);

        return view;
    }

    protected abstract int getLayoutId();

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

        if (currentLocation != null) {
            lunchViewModel.getRestaurants(currentLocation, radius).observe(this, restaurantsList -> {
                setRestaurants(restaurantsList);
                updateUiWhenDataChange();
            });
        }
    }

    protected void getUsers() {
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

    protected void showProgressBar(Boolean visible) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).showProgressBar(visible);
        }
    }

    protected void configureSearchPlaces() {

        Places.initialize(requireContext(), BuildConfig.GOOGLE_MAPS_API_KEY);
        placesClient = Places.createClient(requireContext());

        MenuItem searchItem = menu.findItem(R.id.search_toolbar_menu);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    updateUiWhenDataChange();
                } else {

                    AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                    int searchRadius = sharedPreferences.getInt(SettingsFragment.SEARCH_RADIUS_KEY_PREFERENCES, SettingsFragment.DEFAULT_SEARCH_RADIUS);

                    LatLng latLng1 = SphericalUtil.computeOffset(latLng, searchRadius, 45);
                    LatLng latLng2 = SphericalUtil.computeOffset(latLng, searchRadius, 225);
                    RectangularBounds bounds = RectangularBounds.newInstance(latLng2, latLng1);
                    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(newText)
                            .setOrigin(latLng)
                            .setLocationRestriction(bounds)
                            .setTypeFilter(TypeFilter.ESTABLISHMENT)
                            .setSessionToken(token)
                            .build();
                    placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                        restaurantsSearchResult.clear();
                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            if (prediction.getPlaceTypes().contains(Place.Type.RESTAURANT)) {
                                for (Restaurant restaurant : restaurants) {
                                    if (restaurant.getPlaceId().equals(prediction.getPlaceId())) {
                                        restaurantsSearchResult.add(restaurant);
                                    }
                                }
                            }
                        }
                        onSearchResults();
                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e("GOOGLE AUTO COMPLETE", "Place not found: " + apiException.getStatusCode());
                        }
                    });
                }
                return true;
            }
        });
    }

    protected abstract void onSearchResults();
}
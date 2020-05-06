package com.ocr.francois.go4lunch.ui.mapView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.SphericalUtil;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.ui.base.BaseFragment;
import com.ocr.francois.go4lunch.ui.settings.SettingsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO : put map zoom in preferences

public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    private int mapZoom;
    private MarkerClickCallback markerClickCallback;
    private PlacesClient placesClient;

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    public void setMarkerClickCallback(MarkerClickCallback callback) {
        this.markerClickCallback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        configureLunchViewModel();
        configureMap();
        configureLocationTracker();
        showProgressBar(R.id.fragment_map_view_progress_bar);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getSharedPreferences();
        mapZoom = sharedPreferences.getInt(SettingsFragment.MAP_ZOOM_KEY_PREFERENCES, SettingsFragment.DEFAULT_MAP_ZOOM);
        locationTracker.startLocationUpdates();
        observeLocation();
        //TODO: change place of api key
        Places.initialize(requireContext(), "AIzaSyAwcLs-t_e1sfK1Fjkfwo3Ndr2AeJBu7JE");
        placesClient = Places.createClient(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        locationTracker.stopLocationUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        locationTracker.stopLocationUpdates();
    }

    private void observeLocation() {
        locationTracker.getLocation().observe(this, newLocation -> {
            if (newLocation != null) {
                currentLocation = newLocation;
            }
            LatLng latLngLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(latLngLocation));
            map.moveCamera(CameraUpdateFactory.zoomTo(mapZoom));

            hideProgressBar(R.id.fragment_map_view_progress_bar);
            getRestaurants();
        });
    }

    protected void updateUiWhenDataChange() {
        map.clear();
        if (!restaurants.isEmpty()) {
            addMarkers(restaurants);
        }
    }

    private void addMarkers(List<Restaurant> restaurantsList) {
        for (Restaurant restaurant : restaurantsList) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng());
            markerOptions.position(latLng);
            if (restaurant.getNumberOfParticipants() == 0) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_orange));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green));
            }
            Marker marker = map.addMarker(markerOptions);

            marker.setTag(restaurant.getPlaceId());
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    private void configureMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map_view_map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sort_and_search_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

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


                    LatLng latLng1 = SphericalUtil.computeOffset(latLng, 1600, 45);
                    LatLng latLng2 = SphericalUtil.computeOffset(latLng, 1600, 225);
                    RectangularBounds bounds = RectangularBounds.newInstance(latLng2, latLng1);
                    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(newText)
                            .setOrigin(latLng)
                            .setLocationRestriction(bounds)
                            .setTypeFilter(TypeFilter.ESTABLISHMENT)
                            .setSessionToken(token)
                            .build();
                    placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                        List<Restaurant> restaurantsSuggestions = new ArrayList<>();
                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            if (prediction.getPlaceTypes().contains(Place.Type.RESTAURANT)) {
                                for (Restaurant restaurant : restaurants) {
                                    if (restaurant.getPlaceId().equals(prediction.getPlaceId())) {
                                        restaurantsSuggestions.add(restaurant);
                                    }
                                }
                            }
                        }
                        map.clear();
                        addMarkers(restaurantsSuggestions);
                    }).addOnFailureListener((exception) -> {
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e("GOOGLE AUTO COMPLETE", "Place not found: " + apiException.getStatusCode());
                        }
                    });
                }
                return false;
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        markerClickCallback.onMarkerClickCallback((String) marker.getTag());
        return false;
    }

    public interface MarkerClickCallback {
        void onMarkerClickCallback(String placeId);
    }
}

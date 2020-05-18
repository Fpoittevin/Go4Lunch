package com.ocr.francois.go4lunch.ui.mapView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.ui.base.BaseFragment;
import com.ocr.francois.go4lunch.ui.settings.SettingsFragment;

import java.util.List;

public class MapViewFragment extends BaseFragment
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, BaseFragment.OnSearchResultsListener {

    private GoogleMap map;
    private int mapZoom;
    private MarkerClickCallback markerClickCallback;

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    public void setMarkerClickCallback(MarkerClickCallback callback) {
        this.markerClickCallback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        configureLunchViewModel();
        configureMap();
        configureLocationTracker();
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireActivity().setTitle(R.string.i_am_hungry_title);
    }

    @Override
    public void onStart() {
        super.onStart();
        getSharedPreferences();
        mapZoom = sharedPreferences.getInt(SettingsFragment.MAP_ZOOM_KEY_PREFERENCES, SettingsFragment.DEFAULT_MAP_ZOOM);
        locationTracker.startLocationUpdates();
        observeLocation();
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
                LatLng latLngLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLng(latLngLocation));
                map.moveCamera(CameraUpdateFactory.zoomTo(mapZoom));
                getRestaurants();
            }
        });
    }

    protected void updateUiWhenDataChange() {
        hideProgressBar();
        map.clear();
        if (!restaurants.isEmpty()) {
            addMarkers(restaurants);
        }
    }

    private void configureMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragment_map_view_map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void addMarkers(List<Restaurant> restaurantsList) {
        for (Restaurant restaurant : restaurantsList) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(
                    restaurant.getGeometry().getLocation().getLat(),
                    restaurant.getGeometry().getLocation().getLng());

            markerOptions.position(latLng);
            if (restaurant.getNumberOfParticipants() == 0) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_orange));
            } else {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green));
            }
            Marker marker = map.addMarker(markerOptions);

            marker.setTag(restaurant.getPlaceId());
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_toolbar_menu, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
        configureSearchPlaces(this);
    }

    @Override
    public void onSearchResults(List<Restaurant> restaurantsSearchResult) {
        hideProgressBar();
        map.clear();
        if (!restaurantsSearchResult.isEmpty()) {
            addMarkers(restaurantsSearchResult);
        }
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

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map_view;
    }

    @Override
    protected int getProgressBarId() {
        return R.id.fragment_map_view_progress_bar;
    }

    public interface MarkerClickCallback {
        void onMarkerClickCallback(String placeId);
    }
}

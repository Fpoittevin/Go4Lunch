package com.ocr.francois.go4lunch.ui.mapView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

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
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.ui.base.BaseFragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        configureLunchViewModel();
        configureMap();
        configureLocationTracker();
        showProgressBar(R.id.fragment_map_view_progress_bar);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        observeLocation();
        getUsers();
    }

    @Override
    public void onPause() {
        super.onPause();
        locationTracker.stopLocationUpdates();
    }


    private void observeLocation() {

        locationTracker.getLocation().observe(this, newLocation -> {
            if (newLocation != null) {
                if (currentLocation == null || Math.round(newLocation.distanceTo(currentLocation)) < 500) {

                    currentLocation = newLocation;
                    LatLng latLngLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    map.moveCamera(CameraUpdateFactory.newLatLng(latLngLocation));
                    map.animateCamera(CameraUpdateFactory.zoomTo(12));
                    hideProgressBar(R.id.fragment_map_view_progress_bar);
                    getRestaurants();
                }
            }
        });
    }

    private void getRestaurants() {
        lunchViewModel.getRestaurants(currentLocation, 2000).observe(this, restaurants -> {
            if (restaurants != null) {
                setRestaurants(restaurants);
                addMarkers();
            }
        });
    }

    private void getUsers() {
        lunchViewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                setUsers(users);
                lunchViewModel.addParticipantsInAllRestaurants(restaurants, users);
                addMarkers();
            }
        });
    }

    private void configureMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map_view_map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void addMarkers() {
        map.clear();
        for (Restaurant restaurant : restaurants) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng());
            markerOptions.position(latLng);
            if (lunchViewModel.getNumbersOfUsersByRestaurant(restaurant, users) != 0) {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }
            Marker marker = map.addMarker(markerOptions);

            marker.setTag(restaurant.getPlaceId());
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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

    public interface MarkerClickCallback {
        void onMarkerClickCallback(String placeId);
    }
}

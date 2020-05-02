package com.ocr.francois.go4lunch.ui.mapView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

// TODO : put map zoom in preferences

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
            }
            LatLng latLngLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLng(latLngLocation));
            map.moveCamera(CameraUpdateFactory.zoomTo(12));

            hideProgressBar(R.id.fragment_map_view_progress_bar);
            getRestaurants();
        });
    }

    protected void updateUiWhenDataChange() {
        map.clear();
        if (!restaurants.isEmpty()) {
            addMarkers();
        }
    }

    private void addMarkers() {
        for (Restaurant restaurant : restaurants) {
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

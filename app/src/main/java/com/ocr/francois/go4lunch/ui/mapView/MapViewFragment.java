package com.ocr.francois.go4lunch.ui.mapView;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.injection.Injection;
import com.ocr.francois.go4lunch.injection.ViewModelFactory;
import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.utils.LocationTracker;
import com.ocr.francois.go4lunch.ui.viewmodels.RestaurantViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private RestaurantViewModel restaurantViewModel;
    private GoogleMap map;
    private Location currentLocation = null;
    private LocationTracker locationTracker;
    private MarkerClickCallback markerClickCallback;

    public MapViewFragment() {
        // Required empty public constructor
    }

    public static MapViewFragment newInstance() {
        return new MapViewFragment();
    }

    public void setMarkerClickCallback(MarkerClickCallback callback) {
        this.markerClickCallback = callback;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        configureViewModel();
        configureMap();
        configureLocationTracker();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        observeLocation();
        Log.d("FRAGMENT", "ON START !!!!!!!");
    }
    @Override
    public void onPause() {
        super.onPause();
        locationTracker.stopLocationUpdates();
        Log.d("FRAGMENT", "ON PAUSE !!!!!!!");
    }

    private void configureMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map_view_map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void configureViewModel() {

        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        restaurantViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(RestaurantViewModel.class);
    }

    private void configureLocationTracker() {
        locationTracker = new LocationTracker(getContext());
    }

    private void observeLocation() {

        locationTracker.getLocation().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location newLocation) {
                if (newLocation != null) {
                    if(currentLocation == null || Math.round(newLocation.distanceTo(currentLocation)) < 100) {

                        currentLocation = newLocation;
                        LatLng latLngLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        map.moveCamera(CameraUpdateFactory.newLatLng(latLngLocation));
                        map.animateCamera(CameraUpdateFactory.zoomTo(16));

                        getRestaurants();
                    }
                }
            }
        });
    }

    private void getRestaurants() {
        restaurantViewModel.getRestaurants(currentLocation, 2000).observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                if (restaurants != null) {

                    addMarkers(restaurants);
                }
            }
        });
    }

    private void addMarkers(List<Restaurant> restaurants) {
        for (Restaurant restaurant : restaurants) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(restaurant.getGeometry().getLocation().getLat(), restaurant.getGeometry().getLocation().getLng());
            markerOptions.position(latLng);
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
        markerClickCallback.onMarkerClickCallback((String)marker.getTag());
        return false;
    }

    public interface MarkerClickCallback {
        void onMarkerClickCallback(String placeId);
    }
}

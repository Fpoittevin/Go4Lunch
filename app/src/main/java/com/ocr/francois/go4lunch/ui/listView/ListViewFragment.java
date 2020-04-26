package com.ocr.francois.go4lunch.ui.listView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ListViewFragment extends BaseFragment {

    static int AUTOCOMPLETE_REQUEST_CODE = 123;
    @BindView(R.id.fragment_list_view_recycler_view)
    RecyclerView recyclerView;
    private RestaurantAdapter restaurantAdapter;
    private PlacesClient placesClient;

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);

        ButterKnife.bind(this, view);
        configureLunchViewModel();
        configureLocationTracker();
        configureRecyclerView();

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Places.initialize(getContext(), "AIzaSyAwcLs-t_e1sfK1Fjkfwo3Ndr2AeJBu7JE");
        placesClient = Places.createClient(getContext());
        observeLocation();
        getUsers();
    }

    @Override
    public void onPause() {
        super.onPause();
        locationTracker.stopLocationUpdates();
    }

    private void configureRecyclerView() {
        restaurants = new ArrayList<>();
        restaurantAdapter = new RestaurantAdapter(restaurants, (RestaurantAdapter.RestaurantItemClickCallback) getActivity(), currentLocation);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(restaurantAdapter);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void observeLocation() {

        locationTracker.getLocation().observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location newLocation) {
                if (newLocation != null) {
                    if (currentLocation == null || Math.round(newLocation.distanceTo(currentLocation)) < 100) {
                        currentLocation = newLocation;
                        restaurantAdapter.setCurrentLocation(currentLocation);
                        getRestaurants();
                    }
                }
            }
        });
    }

    private void getRestaurants() {
        lunchViewModel.getRestaurants(currentLocation, 2000).observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                if (restaurants != null) {
                    setRestaurants(restaurants);
                    restaurantAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void getUsers() {
        lunchViewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                setUsers(users);
                lunchViewModel.addParticipantsInAllRestaurants(restaurants, users);
                restaurantAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchItem = menu.findItem(R.id.menu_activity_main_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("TEXTE CHANGE !!!!!", newText);

                AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(newText)
                        .setOrigin(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                        .setSessionToken(token)
                        .build();
                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {

                    for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                        Log.i("GOOGLE AUTO COMPLETE", prediction.getPlaceId());
                        Log.i("GOOGLE AUTO COMPLETE", prediction.getPrimaryText(null).toString());
                    }
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e("GOOGLE AUTO COMPLETE", "Place not found: " + apiException.getStatusCode());
                    }
                });
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_activity_main_search) {

        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
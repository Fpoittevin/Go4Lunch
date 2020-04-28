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

import androidx.annotation.NonNull;
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
import com.ocr.francois.go4lunch.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ListViewFragment extends BaseFragment {

    static int AUTOCOMPLETE_REQUEST_CODE = 123;
    @BindView(R.id.fragment_list_view_recycler_view)
    RecyclerView recyclerView;
    private RestaurantAdapter restaurantAdapter;
    private PlacesClient placesClient;

    private SortMethod sortMethod = SortMethod.DISTANCE;

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        //TODO: change place of api key
        Places.initialize(Objects.requireNonNull(getContext()), "AIzaSyAwcLs-t_e1sfK1Fjkfwo3Ndr2AeJBu7JE");
        placesClient = Places.createClient(getContext());
        observeLocation();
        getUsers();
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

    private void configureRecyclerView() {
        restaurantAdapter = new RestaurantAdapter(
                new ArrayList<>(),
                (RestaurantAdapter.RestaurantItemClickCallback) getActivity());
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
                    currentLocation = newLocation;
                    getRestaurants();
                }
            }
        });
    }

    protected void updateUiWhenDataChange() {
        lunchViewModel.addParticipantsInAllRestaurants(restaurants, users);

        switch (sortMethod) {
            case DISTANCE:
                Collections.sort(restaurants, new Restaurant.RestaurantDistanceComparator());
                break;
            case PARTICIPANTS:
                Collections.sort(restaurants, new Restaurant.RestaurantParticipantsComparator());
                break;
            case LIKE:
                //TODO: create like comparator
        }

        restaurantAdapter.updateRestaurants(restaurants);
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
        switch (item.getItemId()) {
            case R.id.sort_by_distance:
                sortMethod = SortMethod.DISTANCE;
                break;
            case R.id.sort_by_participants:
                sortMethod = SortMethod.PARTICIPANTS;
                break;
            case (R.id.sort_by_likes):
                sortMethod = SortMethod.LIKE;
                break;
        }
        getRestaurants();
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    private enum SortMethod {
        DISTANCE,
        PARTICIPANTS,
        LIKE
    }
}
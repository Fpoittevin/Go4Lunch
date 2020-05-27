package com.ocr.francois.go4lunch.ui.listView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public class ListViewFragment extends BaseFragment implements
        BaseFragment.OnSearchResultsListener {

    @BindView(R.id.fragment_list_view_recycler_view)
    RecyclerView recyclerView;
    private RestaurantAdapter restaurantAdapter;

    private SortMethod sortMethod = SortMethod.DISTANCE;

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        configureLunchViewModel();
        configureRecyclerView();
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
                getRestaurants();
            }
        });
    }

    protected void updateUiWhenDataChange() {
        showProgressBar(false);
        switch (sortMethod) {
            case DISTANCE:
                Collections.sort(restaurants, new Restaurant.RestaurantDistanceComparator());
                break;
            case PARTICIPANTS:
                Collections.sort(restaurants, new Restaurant.RestaurantParticipantsComparator());
                break;
            case LIKE:
                Collections.sort(restaurants, new Restaurant.RestaurantNotesComparator());
        }
        restaurantAdapter.updateRestaurants(restaurants);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sort_and_search_toolbar_menu, menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
        configureSearchPlaces(this);
    }

    @Override
    public void onSearchResults(List<Restaurant> restaurantsSearchResult) {
        restaurantAdapter.updateRestaurants(restaurantsSearchResult);
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
    protected int getLayoutId() {
        return R.layout.fragment_list_view;
    }

    private enum SortMethod {
        DISTANCE,
        PARTICIPANTS,
        LIKE
    }
}
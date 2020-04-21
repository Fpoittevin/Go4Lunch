package com.ocr.francois.go4lunch.ui.listView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.injection.Injection;
import com.ocr.francois.go4lunch.injection.ViewModelFactory;
import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.ui.viewmodels.RestaurantViewModel;
import com.ocr.francois.go4lunch.utils.LocationTracker;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ListViewFragment extends Fragment {

    @BindView(R.id.fragment_list_view_recycler_view)
    RecyclerView recyclerView;
    private RestaurantViewModel restaurantViewModel;
    private Location currentLocation = null;
    private LocationTracker locationTracker;
    private RestaurantAdapter restaurantAdapter;
    private List<Restaurant> restaurants;

    static int AUTOCOMPLETE_REQUEST_CODE = 123;

    public ListViewFragment() {
        // Required empty public constructor
    }

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);

        ButterKnife.bind(this, view);
        configureViewModel();
        configureLocationTracker();
        configureRecyclerView();

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        observeLocation();
    }
    @Override
    public void onPause() {
        super.onPause();
        locationTracker.stopLocationUpdates();
    }

    private void configureViewModel() {

        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        restaurantViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(RestaurantViewModel.class);
    }

    private void configureLocationTracker() {
        locationTracker = new LocationTracker(getContext());
    }

    private void configureRecyclerView() {
        restaurants = new ArrayList<>();
        restaurantAdapter = new RestaurantAdapter(getContext(), restaurants, (RestaurantAdapter.RestaurantItemClickCallback) getActivity());
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
                    if(currentLocation == null || Math.round(newLocation.distanceTo(currentLocation)) < 100) {

                        currentLocation = newLocation;

                        getRestaurants();
                    }
                }
            }
        });
    }

    private void getRestaurants() {
        restaurantViewModel.getRestaurants(currentLocation, 2000).observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurantsList) {
                if (restaurants != null) {
                    restaurants.addAll(restaurantsList);
                    restaurantAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
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

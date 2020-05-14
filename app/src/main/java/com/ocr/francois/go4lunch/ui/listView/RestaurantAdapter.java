package com.ocr.francois.go4lunch.ui.listView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.models.Restaurant;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private List<Restaurant> restaurants;
    private RestaurantItemClickCallback restaurantItemClickCallback;

    RestaurantAdapter(List<Restaurant> restaurants, RestaurantItemClickCallback restaurantItemClickCallback) {
        this.restaurants = restaurants;
        this.restaurantItemClickCallback = restaurantItemClickCallback;
    }

    void updateRestaurants(List<Restaurant> restaurantsList) {
        this.restaurants.clear();
        this.restaurants.addAll(restaurantsList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_restaurant_item, parent, false);

        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {

        Restaurant restaurant = restaurants.get(position);
        holder.updateUi(restaurant);
        holder.view.setOnClickListener(v -> restaurantItemClickCallback.onRestaurantItemClick(restaurant.getPlaceId()));
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public interface RestaurantItemClickCallback {
        void onRestaurantItemClick(String placeId);
    }
}
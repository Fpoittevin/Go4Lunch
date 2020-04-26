package com.ocr.francois.go4lunch.ui.listView;

import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.models.Photo;
import com.ocr.francois.go4lunch.models.Restaurant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<Restaurant> restaurants;
    private RestaurantItemClickCallback restaurantItemClickCallback;
    private Location currentLocation;

    public RestaurantAdapter(List<Restaurant> restaurants, RestaurantItemClickCallback restaurantItemClickCallback, Location currentLocation) {

        this.restaurants = restaurants;
        this.restaurantItemClickCallback = restaurantItemClickCallback;
        this.currentLocation = currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_list_view_restaurant_item, parent, false);

        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {

        Restaurant restaurant = restaurants.get(position);
        holder.updateUi(restaurant, currentLocation);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                restaurantItemClickCallback.onRestaurantItemClick(restaurant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public interface RestaurantItemClickCallback {
        void onRestaurantItemClick(Restaurant restaurant);
    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        protected View view;
        @BindView(R.id.fragment_list_view_restaurant_item_name_text_view)
        TextView nameTextView;
        @BindView(R.id.fragment_list_view_restaurant_item_address_text_view)
        TextView addressTextView;
        @BindView(R.id.fragment_list_view_restaurant_item_distance)
        TextView distanceTextView;
        @BindView(R.id.fragment_list_view_restaurant_item_participants_number_text_view)
        TextView participantsNumberTextView;
        @BindView(R.id.fragment_list_view_restaurant_item_photo_image_view)
        ImageView photoImageView;

        RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, view);
        }

        void updateUi(Restaurant restaurant, Location currentLocation) {
            nameTextView.setText(restaurant.getName());
            addressTextView.setText(restaurant.getVicinity().replaceAll(", ", "\n"));

            if (currentLocation != null) {
                String distanceText = getDistance(restaurant, currentLocation) + " m";
                distanceTextView.setText(distanceText);
            }
            participantsNumberTextView.setText(String.valueOf(restaurant.getParticipants().size()));

            if (restaurant.getPhotos() != null) {
                if (!restaurant.getPhotos().isEmpty()) {
                    Photo photo = restaurant.getPhotos().get(0);
                    String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&key=AIzaSyAwcLs-t_e1sfK1Fjkfwo3Ndr2AeJBu7JE&photoreference=" + photo.getPhotoReference();
                    Glide
                            .with(view)
                            .load(photoUrl)
                            .apply(RequestOptions.centerCropTransform())
                            .into(photoImageView);
                }
            }
        }

        private int getDistance(Restaurant restaurant, Location currentLocation) {
            Location restaurantLocation = new Location("restaurant location");
            restaurantLocation.setLatitude(restaurant.getGeometry().getLocation().getLat());
            restaurantLocation.setLongitude(restaurant.getGeometry().getLocation().getLng());

            return Math.round(currentLocation.distanceTo(restaurantLocation));
        }
    }
}
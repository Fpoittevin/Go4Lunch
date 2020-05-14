package com.ocr.francois.go4lunch.ui.listView;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.models.Photo;
import com.ocr.francois.go4lunch.models.Restaurant;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {
    protected View view;
    @BindView(R.id.fragment_list_view_restaurant_item_name_text_view)
    TextView nameTextView;
    @BindView(R.id.fragment_list_view_restaurant_item_address_text_view)
    TextView addressTextView;
    @BindView(R.id.fragment_list_view_restaurant_item_distance)
    TextView distanceTextView;
    @BindView(R.id.fragment_list_view_restaurant_item_participants_number_text_view)
    TextView participantsNumberTextView;
    @BindView(R.id.fragment_list_view_restaurant_item_note_rating_bar)
    RatingBar noteRatingBar;
    @BindView(R.id.fragment_list_view_restaurant_item_photo_image_view)
    ImageView photoImageView;

    RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        ButterKnife.bind(this, view);
    }

    void updateUi(Restaurant restaurant) {
        if (restaurant.getName() != null) {
            nameTextView.setText(restaurant.getName());
        }
        if (restaurant.getVicinity() != null) {
            addressTextView.setText(restaurant.getVicinity().replaceAll(", ", "\n"));
        }
        String distanceText = Math.round(restaurant.getDistance()) + " m";
        distanceTextView.setText(distanceText);

        noteRatingBar.setVisibility(View.INVISIBLE);
        if (restaurant.getNote() > 0) {
            noteRatingBar.setVisibility(View.VISIBLE);
            noteRatingBar.setIsIndicator(true);
            noteRatingBar.setNumStars(restaurant.getNote());
            noteRatingBar.setRating((float) restaurant.getNote());
        }

        String participantText = "(" + restaurant.getNumberOfParticipants() + ")";
        participantsNumberTextView.setText(participantText);

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
}

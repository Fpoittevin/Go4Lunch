package com.ocr.francois.go4lunch.ui.restaurantDetails;

import android.Manifest;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.injection.Injection;
import com.ocr.francois.go4lunch.injection.ViewModelFactory;
import com.ocr.francois.go4lunch.models.Like;
import com.ocr.francois.go4lunch.models.Photo;
import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.ui.base.BaseActivity;
import com.ocr.francois.go4lunch.ui.viewmodels.LunchViewModel;
import com.ocr.francois.go4lunch.ui.workmates.WorkmatesAdapter;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import pub.devrel.easypermissions.EasyPermissions;

public class RestaurantDetailsActivity extends BaseActivity {

    @BindView(R.id.activity_restaurant_details_frame_layout)
    FrameLayout frameLayout;

    @BindView(R.id.activity_restaurant_details_picture_image_view)
    ImageView restaurantPictureImageView;
    @BindView(R.id.activity_restaurant_details_name_text_view)
    TextView restaurantNameTextView;
    @BindView(R.id.activity_restaurant_details_note_rating_bar)
    RatingBar noteRatingBar;
    @BindView(R.id.activity_restaurant_details_infos_text_view)
    TextView restaurantInfosTextView;
    @BindView(R.id.activity_restaurant_details_call_button)
    Button callButton;
    @BindView(R.id.activity_restaurant_details_like_button)
    Button likeButton;
    @BindView(R.id.activity_restaurant_details_website_button)
    Button websiteButton;
    @BindView(R.id.activity_restaurant_details_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.activity_restaurant_details_floating_action_button)
    FloatingActionButton fab;

    private WorkmatesAdapter workmatesAdapter;
    private Restaurant restaurant;

    private LunchViewModel lunchViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String placeId = getIntent().getStringExtra("placeId");

        configureLunchViewModel();
        configureRecyclerView();
        getRestaurant(placeId);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_restaurant_details;
    }

    protected void configureLunchViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        lunchViewModel = ViewModelProviders.of(this, viewModelFactory).get(LunchViewModel.class);
    }

    private void configureRecyclerView() {
        workmatesAdapter = new WorkmatesAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(workmatesAdapter);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void getRestaurant(String placeId) {
        lunchViewModel.getRestaurant(placeId).observe(this, restaurantChange -> {
            restaurant = restaurantChange;
            updateUi();
        });
    }

    private void updateUi() {
        restaurantNameTextView.setText(restaurant.getName());

        if (restaurant.getPhotos() != null && !restaurant.getPhotos().isEmpty()) {
            Photo photo = restaurant.getPhotos().get(0);
            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&key=AIzaSyAwcLs-t_e1sfK1Fjkfwo3Ndr2AeJBu7JE&photoreference=" + photo.getPhotoReference();
            Glide
                    .with(this)
                    .load(photoUrl)
                    .into(restaurantPictureImageView);
        }

        if (restaurant.getName() != null) {
            restaurantNameTextView.setText(restaurant.getName());
        }
        if (restaurant.getVicinity() != null) {
            restaurantInfosTextView.setText(restaurant.getVicinity());
        }

        configureCallButton();
        configureWebsiteButton();
        configureFab();
        configureLikeButton();

        workmatesAdapter.updatesWorkmates(restaurant.getParticipants(), Objects.requireNonNull(getCurrentUser()).getUid());

        noteRatingBar.setVisibility(View.INVISIBLE);
        if (restaurant.getNote() > 0) {
            noteRatingBar.setVisibility(View.VISIBLE);
            noteRatingBar.setIsIndicator(true);
            noteRatingBar.setNumStars(restaurant.getNote());
            noteRatingBar.setRating((float) restaurant.getNote());
        }
    }

    private void configureCallButton() {
        String phoneNumber = restaurant.getInternationalPhoneNumber();
        if (phoneNumber != null) {
            callButton.setOnClickListener(v -> {

                if (EasyPermissions.hasPermissions(this, Manifest.permission.CALL_PHONE)) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                    startActivity(callIntent);
                } else {
                    EasyPermissions.requestPermissions(this, getResources().getString(R.string.need_location_permissions_message), 124, Manifest.permission.CALL_PHONE);
                }
            });
        } else {
            callButton.setClickable(false);
        }
    }

    private void configureLikeButton() {
        Drawable star = getResources().getDrawable(R.drawable.ic_star_orange_24dp);
        Drawable starBorder = getResources().getDrawable(R.drawable.ic_star_border_orange_24dp);

        lunchViewModel.getLikesByRestaurant(restaurant.getPlaceId()).observe(this, likes -> {
            boolean currentUserLikeRestaurant = false;
            for (Like like : likes) {
                if (like.getUserId().equals(Objects.requireNonNull(getCurrentUser()).getUid())) {
                    currentUserLikeRestaurant = true;
                    break;
                }
            }
            if (currentUserLikeRestaurant) {
                likeButton.setOnClickListener(v -> lunchViewModel.deleteLike(getCurrentUser().getUid(), restaurant.getPlaceId()));
                likeButton.setCompoundDrawablesWithIntrinsicBounds(null, star, null, null);
            } else {
                likeButton.setOnClickListener(v -> lunchViewModel.createLike(restaurant.getPlaceId(), Objects.requireNonNull(getCurrentUser()).getUid()));
                likeButton.setCompoundDrawablesWithIntrinsicBounds(null, starBorder, null, null);
            }
        });
    }

    private void configureWebsiteButton() {
        String website = restaurant.getWebsite();
        if (website != null) {
            websiteButton.setOnClickListener(v -> {
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                startActivity(websiteIntent);
            });
        } else {
            websiteButton.setClickable(false);
        }
    }

    private void configureFab() {
        fab.setImageResource(R.drawable.ic_check_white_24dp);

        boolean currentUserIsParticipant = false;

        for (User participant : restaurant.getParticipants()) {
            if (participant.getId().equals(Objects.requireNonNull(getCurrentUser()).getUid())) {
                currentUserIsParticipant = true;
                break;
            }
        }

        if (currentUserIsParticipant) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            fab.setOnClickListener(v -> lunchViewModel.deleteLunch(getCurrentUser().getUid()));
        } else {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
            fab.setOnClickListener(v -> lunchViewModel.saveLunch(Objects.requireNonNull(getCurrentUser()).getUid(), restaurant.getPlaceId(), restaurant.getName()));
        }
    }
}

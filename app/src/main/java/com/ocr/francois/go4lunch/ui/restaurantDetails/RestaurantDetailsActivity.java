package com.ocr.francois.go4lunch.ui.restaurantDetails;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
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

public class RestaurantDetailsActivity extends BaseActivity implements View.OnClickListener {

    private static final int PROGRESS_BAR_ID = R.id.activity_restaurant_details_progress_bar;
    @BindView(R.id.activity_restaurant_details_frame_layout)
    FrameLayout frameLayout;
    @BindView(R.id.activity_restaurant_details_toolbar)
    MaterialToolbar toolbar;
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
    private boolean currentUserLikeRestaurant;
    private boolean currentUserIsParticipant = false;
    private LunchViewModel lunchViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String placeId = getIntent().getStringExtra("placeId");

        configureToolBar();
        configureLunchViewModel();
        configureRecyclerView();
        getRestaurant(placeId);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_restaurant_details;
    }

    private void configureToolBar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
    }

    private void getRestaurant(String placeId) {
        lunchViewModel.getRestaurant(placeId).observe(this, restaurantChange -> {
            restaurant = restaurantChange;
            updateUi();
        });
    }

    private void updateUi() {
        hideProgressBar(PROGRESS_BAR_ID);

        getSupportActionBar().setTitle(restaurant.getName());
        restaurantNameTextView.setText(restaurant.getName());

        if (restaurant.getPhotos() != null && !restaurant.getPhotos().isEmpty()) {
            Photo photo = restaurant.getPhotos().get(0);
            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=500&key=AIzaSyAwcLs-t_e1sfK1Fjkfwo3Ndr2AeJBu7JE&photoreference=" + photo.getPhotoReference();
            Glide
                    .with(this)
                    .load(photoUrl)
                    .centerCrop()
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
        if (restaurant.getInternationalPhoneNumber() != null) {
            callButton.setOnClickListener(this);
        } else {
            callButton.setClickable(false);
        }
    }

    private void configureLikeButton() {
        Drawable star = getResources().getDrawable(R.drawable.ic_star_orange_24dp);
        Drawable starBorder = getResources().getDrawable(R.drawable.ic_star_border_orange_24dp);

        lunchViewModel.getLikesByRestaurant(restaurant.getPlaceId()).observe(this, likes -> {
            hideProgressBar(PROGRESS_BAR_ID);
            likeButton.setClickable(true);
            currentUserLikeRestaurant = false;
            for (Like like : likes) {
                if (like.getUserId().equals(Objects.requireNonNull(getCurrentUser()).getUid())) {
                    currentUserLikeRestaurant = true;
                    break;
                }
            }
            if (currentUserLikeRestaurant) {
                likeButton.setCompoundDrawablesWithIntrinsicBounds(null, star, null, null);
            } else {
                likeButton.setCompoundDrawablesWithIntrinsicBounds(null, starBorder, null, null);
            }
            likeButton.setOnClickListener(this);
        });
    }

    private void configureWebsiteButton() {
        if (restaurant.getWebsite() != null) {
            websiteButton.setOnClickListener(this);
        } else {
            websiteButton.setClickable(false);
        }
    }

    private void configureFab() {
        fab.setImageResource(R.drawable.ic_check_white_24dp);
        currentUserIsParticipant = false;
        fab.setClickable(true);
        for (User participant : restaurant.getParticipants()) {
            if (participant.getId().equals(Objects.requireNonNull(getCurrentUser()).getUid())) {
                currentUserIsParticipant = true;
                break;
            }
        }

        if (currentUserIsParticipant) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        } else {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
        }
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.activity_restaurant_details_call_button:
                if (EasyPermissions.hasPermissions(this, Manifest.permission.CALL_PHONE)) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + restaurant.getInternationalPhoneNumber()));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);
                } else {
                    EasyPermissions.requestPermissions(this, getResources().getString(R.string.need_location_permissions_message), 124, Manifest.permission.CALL_PHONE);
                }
                break;

            case R.id.activity_restaurant_details_like_button:
                showProgressBar(PROGRESS_BAR_ID);
                likeButton.setClickable(false);
                if (currentUserLikeRestaurant) {
                    lunchViewModel.deleteLike(restaurant.getPlaceId(), Objects.requireNonNull(getCurrentUser()).getUid());
                } else {
                    lunchViewModel.createLike(restaurant.getPlaceId(), Objects.requireNonNull(getCurrentUser()).getUid());
                }
                break;
            case R.id.activity_restaurant_details_website_button:
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(restaurant.getWebsite()));
                startActivity(websiteIntent);
                break;

            case R.id.activity_restaurant_details_floating_action_button:
                showProgressBar(PROGRESS_BAR_ID);
                fab.setClickable(false);
                if (currentUserIsParticipant) {
                    lunchViewModel.deleteLunch(Objects.requireNonNull(getCurrentUser()).getUid());
                } else {
                    lunchViewModel.saveLunch(Objects.requireNonNull(getCurrentUser()).getUid(), restaurant.getPlaceId(), restaurant.getName());
                }

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
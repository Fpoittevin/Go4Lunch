package com.ocr.francois.go4lunch.ui.restaurantDetails;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.models.Like;
import com.ocr.francois.go4lunch.models.Photo;
import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantDetailsFragment extends BaseFragment {

    //TODO: add text when the list of workmates is empty
    //TODO: change color of the FAB
    //TODO: use UserRepository.getUsersByLunch for the list of participants

    @BindView(R.id.fragment_restaurant_details_picture_image_view)
    ImageView restaurantPictureImageView;
    @BindView(R.id.fragment_restaurant_details_name_text_view)
    TextView restaurantNameTextView;
    @BindView(R.id.fragment_restaurant_details_note_rating_bar)
    RatingBar noteRatingBar;
    @BindView(R.id.fragment_restaurant_details_infos_text_view)
    TextView restaurantInfosTextView;
    @BindView(R.id.fragment_restaurant_details_call_button)
    Button callButton;
    @BindView(R.id.fragment_restaurant_details_like_button)
    Button likeButton;
    @BindView(R.id.fragment_restaurant_details_website_button)
    Button websiteButton;
    @BindView(R.id.fragment_restaurant_details_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_restaurant_details_floating_action_button)
    FloatingActionButton fab;

    private WorkmatesAdapter workmatesAdapter;
    private String placeId;
    private Restaurant restaurant;

    static RestaurantDetailsFragment newInstance(String placeId) {
        RestaurantDetailsFragment restaurantDetailsFragment = new RestaurantDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("placeId", placeId);
        restaurantDetailsFragment.setArguments(bundle);


        return restaurantDetailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restaurant_details, container, false);
        ButterKnife.bind(this, view);

        this.placeId = this.getArguments().getString("placeId");
        configureLunchViewModel();
        configureRecyclerView();
        getRestaurant();

        return view;
    }

    private void getRestaurant() {
        lunchViewModel.getRestaurant(placeId).observe(this, new Observer<Restaurant>() {
            @Override
            public void onChanged(Restaurant restaurantChange) {
                restaurant = restaurantChange;
                updateUi();
            }
        });
    }
/*
   private void getNote() {
        lunchViewModel.getLikesByRestaurant(restaurant.getPlaceId()).observe(this, new Observer<List<Like>>() {
            @Override
            public void onChanged(List<Like> likes) {
            }
        });
   }

    private void getParticipants() {
        lunchViewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                //TODO: remove current user from list
                List<User> participants = lunchViewModel.getWorkmatesByRestaurant(restaurant, users, false, null);
                if(!participants.isEmpty()) {
                    workmatesAdapter.updatesWorkmates(participants);
                }
            }
        });
    }
*/
    @Override
    protected void updateUiWhenDataChange() {
        workmatesAdapter.notifyDataSetChanged();
    }

    private void updateUi() {
        restaurantNameTextView.setText(restaurant.getName());

        if (restaurant.getPhotos() != null && !restaurant.getPhotos().isEmpty()) {
            Photo photo = restaurant.getPhotos().get(0);
            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&key=AIzaSyAwcLs-t_e1sfK1Fjkfwo3Ndr2AeJBu7JE&photoreference=" + photo.getPhotoReference();
            Glide
                    .with(getView())
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
        //getParticipants();
    }

    private void configureCallButton() {
        String phoneNumber = restaurant.getInternationalPhoneNumber();
        if (phoneNumber != null) {
            callButton.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onClick(View v) {

                    if (EasyPermissions.hasPermissions(Objects.requireNonNull(getContext()), Manifest.permission.CALL_PHONE)) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                        Objects.requireNonNull(getActivity()).startActivity(callIntent);
                    } else {
                        EasyPermissions.requestPermissions(Objects.requireNonNull(getActivity()), getContext().getResources().getString(R.string.need_location_permissions_message), 124, Manifest.permission.CALL_PHONE);
                    }
                }
            });
        } else {
            callButton.setClickable(false);
        }
    }

    private void configureLikeButton() {
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lunchViewModel.createLike(placeId, getCurrentUser().getUid());
            }
        });
    }

    private void configureWebsiteButton() {
        String website = restaurant.getWebsite();
        if (website != null) {
            websiteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
                    startActivity(websiteIntent);
                }
            });
        } else {
            websiteButton.setClickable(false);
        }
    }

    private void configureRecyclerView() {
        workmatesAdapter = new WorkmatesAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(workmatesAdapter);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void configureFab() {
        fab.setOnClickListener(v -> lunchViewModel.saveLunch(getCurrentUser().getUid(), restaurant.getPlaceId(), restaurant.getName()));
    }
}
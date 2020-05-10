package com.ocr.francois.go4lunch.ui.restaurantDetails;

import android.Manifest;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
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
import com.ocr.francois.go4lunch.ui.workmates.WorkmatesAdapter;

import java.util.ArrayList;
import java.util.List;

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
        lunchViewModel.getRestaurant(placeId).observe(getViewLifecycleOwner(), restaurantChange -> {
            restaurant = restaurantChange;
            updateUi();
        });
    }

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
                    .with(requireView())
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

        workmatesAdapter.updatesWorkmates(restaurant.getParticipants(), getCurrentUser().getUid());

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

                if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CALL_PHONE)) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                    requireActivity().startActivity(callIntent);
                } else {
                    EasyPermissions.requestPermissions(requireActivity(), requireContext().getResources().getString(R.string.need_location_permissions_message), 124, Manifest.permission.CALL_PHONE);
                }
            });
        } else {
            callButton.setClickable(false);
        }
    }

    private void configureLikeButton() {
        Drawable star = getResources().getDrawable(R.drawable.ic_star_orange_24dp);
        Drawable starBorder = getResources().getDrawable(R.drawable.ic_star_border_orange_24dp);

        lunchViewModel.getLikesByRestaurant(restaurant.getPlaceId()).observe(getViewLifecycleOwner(), new Observer<List<Like>>() {
            @Override
            public void onChanged(List<Like> likes) {
                boolean currentUserLikeRestaurant = false;
                for (Like like : likes) {
                    if (like.getUserId().equals(getCurrentUser().getUid())) {
                        currentUserLikeRestaurant = true;
                        break;
                    }
                }
                if (currentUserLikeRestaurant) {
                    likeButton.setOnClickListener(v -> lunchViewModel.deleteLike(getCurrentUser().getUid(), restaurant.getPlaceId()));
                    likeButton.setCompoundDrawablesWithIntrinsicBounds(null, star, null, null);
                } else {
                    likeButton.setOnClickListener(v -> lunchViewModel.createLike(placeId, getCurrentUser().getUid()));
                    likeButton.setCompoundDrawablesWithIntrinsicBounds(null, starBorder, null, null);
                }
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

    private void configureRecyclerView() {
        workmatesAdapter = new WorkmatesAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setAdapter(workmatesAdapter);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void configureFab() {
        fab.setImageResource(R.drawable.ic_check_white_24dp);

        boolean currentUserIsParticipant = false;

        for (User participant : restaurant.getParticipants()) {
            if (participant.getId().equals(getCurrentUser().getUid())) {
                currentUserIsParticipant = true;
                break;
            }
        }

        if (currentUserIsParticipant) {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            fab.setOnClickListener(v -> lunchViewModel.deleteLunch(getCurrentUser().getUid()));
        } else {
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
            fab.setOnClickListener(v -> lunchViewModel.saveLunch(getCurrentUser().getUid(), restaurant.getPlaceId(), restaurant.getName()));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_restaurant_details;
    }
}
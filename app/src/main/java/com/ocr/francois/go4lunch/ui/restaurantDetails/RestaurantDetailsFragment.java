package com.ocr.francois.go4lunch.ui.restaurantDetails;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.injection.Injection;
import com.ocr.francois.go4lunch.injection.ViewModelFactory;
import com.ocr.francois.go4lunch.models.Photo;
import com.ocr.francois.go4lunch.models.Restaurant;
import com.ocr.francois.go4lunch.ui.viewmodels.RestaurantViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantDetailsFragment extends Fragment {

    @BindView(R.id.fragment_restaurant_details_picture_image_view)
    ImageView restaurantPictureImageView;
    @BindView(R.id.fragment_restaurant_details_name_text_view)
    TextView restaurantNameTextView;
    @BindView(R.id.fragment_restaurant_details_infos_text_view)
    TextView restaurantInfosTextView;
    @BindView(R.id.fragment_restaurant_details_call_button)
    Button callButton;
    @BindView(R.id.fragment_restaurant_details_like_button)
    Button likeButton;
    @BindView(R.id.fragment_restaurant_details_website_button)
    Button websiteButton;

    String placeId;
    RestaurantViewModel restaurantViewModel;
    Restaurant restaurant;

    public RestaurantDetailsFragment() {
        // Required empty public constructor
    }

    public static RestaurantDetailsFragment newInstance(String placeId) {
        RestaurantDetailsFragment restaurantDetailsFragment = new RestaurantDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putString("placeId", placeId);
        restaurantDetailsFragment.setArguments(bundle);

        return restaurantDetailsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restaurant_details, container, false);
        ButterKnife.bind(this, view);

        this.placeId = this.getArguments().getString("placeId");
        configureViewModel();

        return view;
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        restaurantViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(RestaurantViewModel.class);
        restaurantViewModel.getRestaurant(placeId).observe(this, new Observer<Restaurant>() {
            @Override
            public void onChanged(Restaurant restaurantChange) {

                restaurant = restaurantChange;
                updateUi();
            }
        });
    }

    private void updateUi() {
        restaurantNameTextView.setText(restaurant.getName());

        if (!restaurant.getPhotos().isEmpty()) {
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
    }

    private void configureCallButton() {
        String phoneNumber = restaurant.getInternationalPhoneNumber();
        if (phoneNumber != null) {
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.CALL_PHONE)) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                        getActivity().startActivity(callIntent);
                    } else {
                        EasyPermissions.requestPermissions(getActivity(), getContext().getResources().getString(R.string.need_location_permissions_message), 124, Manifest.permission.CALL_PHONE);
                    }
                }
            });
        } else {
            callButton.setClickable(false);
        }
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
}
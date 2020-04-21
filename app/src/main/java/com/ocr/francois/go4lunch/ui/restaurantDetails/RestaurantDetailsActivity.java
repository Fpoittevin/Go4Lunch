package com.ocr.francois.go4lunch.ui.restaurantDetails;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.ui.base.BaseActivity;

import butterknife.BindView;

public class RestaurantDetailsActivity extends BaseActivity {

    @BindView(R.id.activity_restaurant_details_frame_layout)
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String placeId = getIntent().getStringExtra("placeId");

        Bundle bundle = new Bundle();
        bundle.putString("placeId", placeId);

        displayFragment(R.id.activity_restaurant_details_frame_layout, RestaurantDetailsFragment.newInstance(placeId));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_restaurant_details;
    }
}

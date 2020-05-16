package com.ocr.francois.go4lunch.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.injection.Injection;
import com.ocr.francois.go4lunch.injection.ViewModelFactory;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.notifications.AlarmNotifications;
import com.ocr.francois.go4lunch.ui.base.BaseActivity;
import com.ocr.francois.go4lunch.ui.listView.ListViewFragment;
import com.ocr.francois.go4lunch.ui.listView.RestaurantAdapter;
import com.ocr.francois.go4lunch.ui.mapView.MapViewFragment;
import com.ocr.francois.go4lunch.ui.restaurantDetails.RestaurantDetailsActivity;
import com.ocr.francois.go4lunch.ui.settings.SettingsActivity;
import com.ocr.francois.go4lunch.ui.settings.SettingsFragment;
import com.ocr.francois.go4lunch.ui.viewmodels.LunchViewModel;
import com.ocr.francois.go4lunch.ui.workmates.WorkmatesAdapter;
import com.ocr.francois.go4lunch.ui.workmates.WorkmatesFragment;
import com.ocr.francois.go4lunch.utils.DateTool;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements RestaurantAdapter.RestaurantItemClickCallback, MapViewFragment.MarkerClickCallback, WorkmatesAdapter.WorkmateItemClickCallback {

    @BindView(R.id.activity_main_toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.activity_main_bottom_navigation_view)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.activity_main_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.activity_main_navigation_view)
    NavigationView navigationView;

    View navigationViewHeader;
    ImageView userPictureImageView;
    TextView userNameTextView;
    TextView userEmailTextView;

    private MapViewFragment mapViewFragment;
    private ListViewFragment listViewFragment;
    private WorkmatesFragment workmatesFragment;
    private int frameLayoutId;
    private User currentUser;

    private LunchViewModel lunchViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (isCurrentUserLogged()) {
            //UI
            configureBottomNavigationView();
            configureToolBar();
            configureDrawerLayout();
            configureNavigationView();

            configureLunchViewModel();
            getCurrentUserInFirestore();
            frameLayoutId = R.id.activity_main_frame_layout;

            mapViewFragment = MapViewFragment.newInstance();
            mapViewFragment.setMarkerClickCallback(this);
            displayFragment(frameLayoutId, mapViewFragment);

            askForNotifications();
        } else {
            startSignInActivity();
        }
    }

    private void configureBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.bottom_navigation_menu_map_button:
                    if (mapViewFragment == null) {
                        mapViewFragment = MapViewFragment.newInstance();
                    }
                    return displayFragment(frameLayoutId, mapViewFragment);
                case R.id.bottom_navigation_menu_list_button:
                    if (listViewFragment == null) {
                        listViewFragment = ListViewFragment.newInstance();
                    }
                    return displayFragment(frameLayoutId, listViewFragment);
                case R.id.bottom_navigation_menu_workmates_button:
                    if (workmatesFragment == null) {
                        workmatesFragment = WorkmatesFragment.newInstance();
                    }
                    return displayFragment(frameLayoutId, workmatesFragment);
            }
            return false;
        });
    }

    private void configureToolBar() {
        setSupportActionBar(toolbar);
    }

    private void configureDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        navigationViewHeader = navigationView.inflateHeaderView(R.layout.navigation_view_header);
        userPictureImageView = navigationViewHeader.findViewById(R.id.navigation_view_header_user_picture_image_view);
        userNameTextView = navigationViewHeader.findViewById(R.id.navigation_view_header_user_name_text_view);
        userEmailTextView = navigationViewHeader.findViewById(R.id.navigation_view_header_user_email_text_view);
    }

    private void configureLunchViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        lunchViewModel = ViewModelProviders.of(this, viewModelFactory).get(LunchViewModel.class);
    }

    private void getCurrentUserInFirestore() {
        lunchViewModel.getCurrentUserInFirestore(Objects.requireNonNull(getCurrentUser()).getUid()).observe(this, user -> {
            currentUser = user;
            updateUi();
        });
    }

    private void updateUi() {

        if (currentUser.getUrlPicture() != null) {
            Glide.with(navigationViewHeader)
                    .load(currentUser.getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(userPictureImageView);
        }
        if (currentUser.getUserName() != null) {
            userNameTextView.setText(currentUser.getUserName());
        }
        if (Objects.requireNonNull(getCurrentUser()).getEmail() != null) {
            userEmailTextView.setText(getCurrentUser().getEmail());
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.drawer_menu_lunch_button:
                    if (currentUser.getLunchTimestamp() != null && DateTool.isToday(currentUser.getLunchTimestamp()) && currentUser.getLunchRestaurantPlaceId() != null) {
                        startRestaurantDetailActivity(currentUser.getLunchRestaurantPlaceId());
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_restaurant_message, Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.drawer_menu_settings_button:
                    startSettingsActivity();
                    break;
                case R.id.drawer_menu_logout_button:
                    logOut();
                    break;
            }
            return true;
        });
    }

    private void askForNotifications() {
        getSharedPreferences();
        if (!sharedPreferences.contains("enableNotifications")) {

            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
            dialogBuilder.setMessage(getString(R.string.alert_dialog_notifications_message))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        sharedPreferencesEditor.putBoolean(SettingsFragment.ENABLE_NOTIFICATIONS_KEY_PREFERENCES, true).apply();
                        AlarmNotifications alarmNotifications = new AlarmNotifications(getApplicationContext());
                        alarmNotifications.start();
                    })
                    .setNegativeButton(getString(R.string.no), (dialog, which) -> sharedPreferencesEditor.putBoolean(SettingsFragment.ENABLE_NOTIFICATIONS_KEY_PREFERENCES, false).apply());
            dialogBuilder.create().show();
        }
    }

    private void startRestaurantDetailActivity(String placeId) {
        Intent restaurantDetailsIntent = new Intent(MainActivity.this, RestaurantDetailsActivity.class);
        restaurantDetailsIntent.putExtra("placeId", placeId);
        startActivity(restaurantDetailsIntent);
    }

    private void startSettingsActivity() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void logOut() {
        List<? extends UserInfo> userInfos = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getProviderData();
        FirebaseAuth.getInstance().signOut();

        for (UserInfo userInfo : userInfos) {
            if (userInfo != null) {
                switch (userInfo.getProviderId()) {
                    case "google.com":
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(getString(R.string.default_web_client_id))
                                .requestEmail()
                                .build();

                        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
                        googleSignInClient.signOut().addOnCompleteListener(this,
                                task -> startSignInActivity());
                        break;
                    case "facebook.com":
                        LoginManager.getInstance().logOut();
                        startSignInActivity();
                        break;
                }
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onRestaurantItemClick(String placeId) {
        startRestaurantDetailActivity(placeId);
    }

    @Override
    public void onMarkerClickCallback(String placeId) {
        startRestaurantDetailActivity(placeId);
    }

    @Override
    public void onWorkmateItemClick(String placeId) {
        startRestaurantDetailActivity(placeId);
    }
}
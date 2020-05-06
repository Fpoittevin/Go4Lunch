package com.ocr.francois.go4lunch.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.injection.Injection;
import com.ocr.francois.go4lunch.injection.ViewModelFactory;
import com.ocr.francois.go4lunch.models.User;
import com.ocr.francois.go4lunch.ui.base.BaseActivity;
import com.ocr.francois.go4lunch.ui.listView.ListViewFragment;
import com.ocr.francois.go4lunch.ui.listView.RestaurantAdapter;
import com.ocr.francois.go4lunch.ui.mapView.MapViewFragment;
import com.ocr.francois.go4lunch.ui.restaurantDetails.RestaurantDetailsActivity;
import com.ocr.francois.go4lunch.ui.settings.SettingsActivity;
import com.ocr.francois.go4lunch.ui.settings.SettingsFragment;
import com.ocr.francois.go4lunch.ui.signin.SignInActivity;
import com.ocr.francois.go4lunch.ui.viewmodels.LunchViewModel;
import com.ocr.francois.go4lunch.ui.workmates.WorkmatesAdapter;
import com.ocr.francois.go4lunch.ui.workmates.WorkmatesFragment;
import com.ocr.francois.go4lunch.utils.AlarmNotifications;

import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements LocationListener, SearchView.OnQueryTextListener, RestaurantAdapter.RestaurantItemClickCallback, MapViewFragment.MarkerClickCallback, WorkmatesAdapter.WorkmateItemClickCallback {

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

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = super.onCreateView(parent, name, context, attrs);
        return view;
    }

    private void askForNotifications() {
        getSharedPreferences();
        if (!sharedPreferences.contains("enableNotifications")) {

            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setMessage("souhaitez vous recevoir une notification tous les jours à midi afin de connaitre les collègues qui mangeront avec vous ?")
                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferencesEditor.putBoolean(SettingsFragment.ENABLE_NOTIFICATIONS_KEY_PREFERENCES, true);
                            AlarmNotifications alarmNotifications = new AlarmNotifications(getApplicationContext());
                            alarmNotifications.start();
                        }
                    })
                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferencesEditor.putBoolean(SettingsFragment.ENABLE_NOTIFICATIONS_KEY_PREFERENCES, false);
                        }
                    });
            dialogBuilder.create().show();
        }
    }

    private void configureLunchViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        lunchViewModel = ViewModelProviders.of(this, viewModelFactory).get(LunchViewModel.class);
    }

    private void getCurrentUserInFirestore() {
        lunchViewModel.getCurrentUserInFirestore(getCurrentUser().getUid()).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                currentUser = user;
                updateUi();
            }
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
        if (getCurrentUser().getEmail() != null) {
            userEmailTextView.setText(getCurrentUser().getEmail());
        }
    }

    private void configureBottomNavigationView() {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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
            }
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

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.drawer_menu_lunch_button:
                        if (currentUser.getLunchRestaurantPlaceId() != null) {
                            startRestaurantDetailActivity(currentUser.getLunchRestaurantPlaceId());
                        } else {
                            Toast.makeText(getApplicationContext(), "vous n'avez choisi aucun restaurant pour l'instant", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case R.id.drawer_menu_settings_button:
                        startSettingsActivity();
                        break;
                    case R.id.drawer_menu_logout_button:
                        logOut();
                        break;
                }
                return false;
            }
        });
    }

    private void logOut() {
        List<? extends UserInfo> userInfos = FirebaseAuth.getInstance().getCurrentUser().getProviderData();
        FirebaseAuth.getInstance().signOut();

        for (UserInfo userInfo : userInfos) {

            switch (userInfo.getProviderId()) {
                case "google.com":
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();

                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
                    googleSignInClient.signOut().addOnCompleteListener(this,
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    startSignInActivity();
                                }
                            });
            }
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("LE TEXTE ::::::::", query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("LE TEXTE ::::::::", newText);
        return false;
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

    private void startRestaurantDetailActivity(String placeId) {
        Intent restaurantDetailsIntent = new Intent(MainActivity.this, RestaurantDetailsActivity.class);
        restaurantDetailsIntent.putExtra("placeId", placeId);
        startActivity(restaurantDetailsIntent);
    }

    private void startSignInActivity() {
        Intent signInIntent = new Intent(this, SignInActivity.class);
        startActivity(signInIntent);
    }

    private void startSettingsActivity() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
}
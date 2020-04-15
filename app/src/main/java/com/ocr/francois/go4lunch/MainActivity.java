package com.ocr.francois.go4lunch;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

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
import com.ocr.francois.go4lunch.ui.base.BaseActivity;
import com.ocr.francois.go4lunch.ui.listView.ListViewFragment;
import com.ocr.francois.go4lunch.ui.mapView.MapViewFragment;
import com.ocr.francois.go4lunch.ui.workmates.WorkmatesFragment;

import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.activity_main_toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.activity_main_bottom_navigation_view)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.activity_main_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.activity_main_navigation_view)
    NavigationView navigationView;

    private MapViewFragment mapViewFragment;
    private ListViewFragment listViewFragment;
    private WorkmatesFragment workmatesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authVerification();

        //UI
        configureBottomNavigationView();
        configureToolBar();
        configureDrawerLayout();
        configureNavigationView();

        mapViewFragment = MapViewFragment.newInstance();
        displayFragment(mapViewFragment);
    }

    private void configureBottomNavigationView() {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragmentToDisplay;

                switch (item.getItemId()) {
                    case R.id.bottom_navigation_menu_map_button:
                        if(mapViewFragment == null) {
                            mapViewFragment = MapViewFragment.newInstance();
                        }
                        return displayFragment(mapViewFragment);
                    case R.id.bottom_navigation_menu_list_button:
                        if(listViewFragment == null) {
                            listViewFragment = ListViewFragment.newInstance();
                        }
                        return displayFragment(listViewFragment);
                    case R.id.bottom_navigation_menu_workmates_button:
                        if(workmatesFragment == null) {
                            workmatesFragment = WorkmatesFragment.newInstance();
                        }
                        return displayFragment(workmatesFragment);
                }

                return false;
            }
        });
    }

    private boolean displayFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_frame_layout, fragment)
                .commit();
        return true;
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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.drawer_menu_lunch_button:

                        break;
                    case R.id.drawer_menu_settings_button:

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
                                    authVerification();
                                }
                            });
            }
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }
}
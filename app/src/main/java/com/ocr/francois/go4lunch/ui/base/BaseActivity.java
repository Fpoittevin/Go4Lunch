package com.ocr.francois.go4lunch.ui.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.common.eventbus.Subscribe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.events.FailureEvent;
import com.ocr.francois.go4lunch.ui.signin.SignInActivity;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public abstract class BaseActivity extends AppCompatActivity {

    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getLayoutId());
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(FailureEvent failureEvent) {
        Log.e("ERROR", "onFailure: " + failureEvent.getFailureMessage());
        Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
    }

    protected void getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
    }

    protected abstract int getLayoutId();

    @Nullable
    protected FirebaseUser getCurrentUser() {

        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    public void showProgressBar(Boolean visible) {
        FrameLayout progressBarContainer = this.findViewById(R.id.progress_bar_container);
        if (progressBarContainer != null) {

            ViewGroup.LayoutParams layoutParams = progressBarContainer.getLayoutParams();
            if (visible) {
                progressBarContainer.setVisibility(View.VISIBLE);
            } else {
                progressBarContainer.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void displayFragment(int layout, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(layout, fragment)
                .commit();
    }

    protected void startSignInActivity() {
        Intent signInIntent = new Intent(this, SignInActivity.class);
        startActivity(signInIntent);
    }
}

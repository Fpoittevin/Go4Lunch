package com.ocr.francois.go4lunch.ui.base;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getLayoutId());
        ButterKnife.bind(this);
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

    protected void showProgressBar(int resId) {
        ProgressBar progressBar = findViewById(resId);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgressBar(int resId) {
        ProgressBar progressBar = findViewById(resId);
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }


    protected boolean displayFragment(int layout, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(layout, fragment)
                .commit();
        return true;
    }
}

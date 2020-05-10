package com.ocr.francois.go4lunch.ui.settings;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.appbar.MaterialToolbar;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.ui.base.BaseActivity;

import java.util.Objects;

import butterknife.BindView;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.activity_settings_toolbar)
    MaterialToolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_settings_frame_layout, new SettingsFragment())
                .commit();

        configureToolBar();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    private void configureToolBar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }
}
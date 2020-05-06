package com.ocr.francois.go4lunch.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.ui.base.BaseActivity;

import butterknife.BindView;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class SettingsActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_settings_frame_layout, new SettingsFragment())
                .commit();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_settings;
    }
/*
    @BindView(R.id.activity_settings_user_name_edit_text)
    EditText userNameEditText;

    @BindView(R.id.activity_settings_zoom_text_view)
    TextView zoomTextView;
    @BindView(R.id.activity_settings_zoom_seek_bar)
    SeekBar zoomSeekBar;

    @BindView(R.id.activity_settings_radius_text_view)
    TextView radiusTextView;
    @BindView(R.id.activity_settings_radius_seek_bar)
    SeekBar radiusSeekBar;

    @BindView(R.id.activity_settings_notifications_switch)
    SwitchMaterial notificationsSwitch;

    SharedPreferences sharedPreferences;

    private int mapZoom;
    private int searchRadius;
    private int enableNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getDefaultSharedPreferences(this);

        configureUserNameEditText();
        configureZoomSeekBar();
        configureRadiusSeekBar();
        configureNotificationsSwitch();
    }

    private void configureUserNameEditText() {
        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("EDIT TEXT !!!", "onTextChanged: " + s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void configureZoomSeekBar() {
        mapZoom = sharedPreferences.getInt("mapZoom", 10);
        zoomSeekBar.setProgress(mapZoom);

        zoomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                zoomTextView.setText(String.valueOf(progress + 10));
                setMapZoom(progress + 10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("PROGRESS !!!", "onProgressChanged: ");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sharedPreferences
                        .edit()
                        .putInt("mapZoom", mapZoom)
                        .apply();
            }
        });
    }

    public void setMapZoom(int mapZoom) {
        this.mapZoom = mapZoom;
    }

    private void configureRadiusSeekBar() {
        searchRadius = sharedPreferences.getInt("searchRadius", 10);
        radiusSeekBar.setProgress(searchRadius);

        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiusTextView.setText(String.valueOf(progress + 10));
                setSearchRadius(progress + 10);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sharedPreferences
                        .edit()
                        .putInt("searchRadius", searchRadius)
                        .apply();
            }
        });
    }

    public void setSearchRadius(int searchRadius) {
        this.searchRadius = searchRadius;
    }


    private void configureNotificationsSwitch() {
        enableNotifications = sharedPreferences.getInt("enableNotifications", 1);

        if (enableNotifications == 0) {
            notificationsSwitch.setActivated(false);
        } else {
            notificationsSwitch.setActivated(true);
        }

        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setEnableNotifications(isChecked);
            sharedPreferences
                    .edit()
                    .putInt("enableNotifications", enableNotifications)
                    .apply();
        });
    }

    public void setEnableNotifications(Boolean isChecked) {
        if (isChecked) {
            this.enableNotifications = 1;
        } else {
            this.enableNotifications = 0;
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_settings;
    }
*/
}


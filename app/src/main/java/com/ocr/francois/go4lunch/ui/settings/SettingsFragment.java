package com.ocr.francois.go4lunch.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.ocr.francois.go4lunch.R;
import com.ocr.francois.go4lunch.notifications.AlarmNotifications;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final int DEFAULT_MAP_ZOOM = 15;
    public static final int DEFAULT_SEARCH_RADIUS = 500;
    public static final String MAP_ZOOM_KEY_PREFERENCES = "mapZoom";
    public static final String SEARCH_RADIUS_KEY_PREFERENCES = "searchRadius";
    public static final String ENABLE_NOTIFICATIONS_KEY_PREFERENCES = "enableNotifications";

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        configureSearchRadiusSeekBar();
        configureMapZoomSeekBar();
        configureEnableNotificationsSwitch();
    }

    private void configureMapZoomSeekBar() {

        SeekBarPreference mapZoomSeekBar = findPreference(MAP_ZOOM_KEY_PREFERENCES);
        if (mapZoomSeekBar != null) {
            mapZoomSeekBar.setSummary("x " + sharedPreferences.getInt(MAP_ZOOM_KEY_PREFERENCES, DEFAULT_MAP_ZOOM));
            mapZoomSeekBar.setValue(sharedPreferences.getInt(MAP_ZOOM_KEY_PREFERENCES, DEFAULT_MAP_ZOOM));

            mapZoomSeekBar.setOnPreferenceChangeListener((preference, newValue) -> {
                mapZoomSeekBar.setSummary("x " + newValue);
                return true;
            });
        }
    }

    private void configureSearchRadiusSeekBar() {

        SeekBarPreference searchRadiusSeekBar = findPreference(SEARCH_RADIUS_KEY_PREFERENCES);
        if (searchRadiusSeekBar != null) {
            searchRadiusSeekBar.setSummary(sharedPreferences.getInt(SEARCH_RADIUS_KEY_PREFERENCES, DEFAULT_SEARCH_RADIUS) + " m");
            searchRadiusSeekBar.setValue(sharedPreferences.getInt(SEARCH_RADIUS_KEY_PREFERENCES, DEFAULT_SEARCH_RADIUS));

            searchRadiusSeekBar.setOnPreferenceChangeListener((preference, newValue) -> {
                searchRadiusSeekBar.setSummary(newValue + " m");
                return true;
            });
        }
    }

    private void configureEnableNotificationsSwitch() {
        AlarmNotifications alarmNotifications = new AlarmNotifications(requireContext());
        SwitchPreferenceCompat enableNotificationsSwitch = findPreference(ENABLE_NOTIFICATIONS_KEY_PREFERENCES);
        if (enableNotificationsSwitch != null) {

            enableNotificationsSwitch.setChecked(sharedPreferences.getBoolean(ENABLE_NOTIFICATIONS_KEY_PREFERENCES, false));
            enableNotificationsSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                if ((Boolean) newValue) {
                    alarmNotifications.start();
                } else {
                    alarmNotifications.stop();
                }
                return true;
            });
        }
    }
}
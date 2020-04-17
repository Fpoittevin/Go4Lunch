package com.ocr.francois.go4lunch.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ocr.francois.go4lunch.R;

import pub.devrel.easypermissions.EasyPermissions;

public class LocationTracker implements OnSuccessListener<Location> {

    private Context context;
    private MutableLiveData<Location> location;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    public LocationTracker(Context context) {
        this.context = context;
        this.location = new MutableLiveData<>();
        this.locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(30 * 1000)
                .setFastestInterval(2 * 1000);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    setLocation(locationResult.getLastLocation());
                }
            }
        };
    }

    public MutableLiveData<Location> getLocation() {

        if (hasLocationPermissions()) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener((Activity) context, loc -> {
                        startLocationUpdates();
                        location.setValue(loc);
                    });
        } else {
            EasyPermissions.requestPermissions((Activity) context, context.getResources().getString(R.string.need_location_permissions_message), 124, Manifest.permission.ACCESS_FINE_LOCATION);
        }
        return location;
    }

    private void setLocation(Location location) {
        this.location.setValue(location);
    }

    private Boolean hasLocationPermissions() {

        return EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public void onSuccess(Location location) {

        setLocation(location);
    }

    public void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
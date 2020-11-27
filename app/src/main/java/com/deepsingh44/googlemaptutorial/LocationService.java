package com.deepsingh44.googlemaptutorial;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class LocationService extends Service implements LocationListener {
    private LocationManager locationmanager;

    @SuppressLint("MissingPermission")
    @Override

    public void onCreate() {
        super.onCreate();
        locationmanager = (LocationManager) getApplicationContext().
                getSystemService(Context.LOCATION_SERVICE);
        locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
        locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Intent in = new Intent("com.example.parkingapp.listener.cutomlocation");
        in.putExtra("lat", location.getLatitude());
        in.putExtra("lon", location.getLongitude());
        sendBroadcast(in);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}
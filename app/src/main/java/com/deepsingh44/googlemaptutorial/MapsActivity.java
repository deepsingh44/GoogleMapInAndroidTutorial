package com.deepsingh44.googlemaptutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private BroadcastReceiver broadcastReceiver;
    SupportMapFragment mapFragment;
    double lat, lon;
    private FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        client = LocationServices.getFusedLocationProviderClient(this);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                lat = intent.getExtras().getDouble("lat");
                lon = intent.getExtras().getDouble("lon");
                Toast.makeText(context, "Current Location : " + lat + "\n" + lon, Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("com.example.parkingapp.listener.cutomlocation"));
        startService(new Intent(this, LocationService.class));

        registerReceiver(broadcastReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        }
    }

    private void getCurrentLocation() {
        if (lat == 0.0 && lon == 0.0) {
            @SuppressLint("MissingPermission")
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(final Location location) {
                    if (location != null) {
                        mapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                mMap.addCircle(new CircleOptions()
                                        .center(new LatLng(location.getLatitude(), location.getLongitude()))
                                        .radius(500)
                                        .strokeColor(Color.RED)
                                        );
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Hello Here");
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                                googleMap.addMarker(markerOptions);
                            }
                        });
                    }
                }
            });
        } else {
            mMap.clear();
            LatLng latLng = new LatLng(lat, lon);
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Hello Here");
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(14f).tilt(70).build();
          /*  mMap.addCircle(new CircleOptions()
                    .center(new LatLng(lat, lon))
                    .radius(100)
                    .strokeColor(Color.RED)
                    .fillColor(Color.BLUE));*/
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            /* mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));*/
            mMap.addMarker(markerOptions);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission is activated
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Please permission allow", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            }
        }
    }

   /* @Override
    protected void onDestroy() {
        super.onDestroy();
        if (((SingleTask) getApplication()).getLocationBrodcast() != null) {
            unregisterReceiver(((SingleTask) getApplication()).getLocationBrodcast());
        }
        Log.e("error", "unregister broadcastreceiver loation");
    }*/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //getCurrentLocation();
    }

    /*public void updateMap(LatLng latLng) {
        //Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.default_product);
        // Add a marker in Sydney and move the camera
        //LatLng ducat = new LatLng(28.6773, 77.3882);
        MarkerOptions mo = new MarkerOptions().position(latLng).title("Marker in Ducat").snippet("This is an Institute");
        //mo.draggable(true);
        //mMap.getUiSettings().setZoomControlsEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(14f).tilt(70).build();
        mMap.addMarker(mo);
        //mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }*/
}
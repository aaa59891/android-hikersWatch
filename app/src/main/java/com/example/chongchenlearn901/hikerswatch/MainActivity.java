package com.example.chongchenlearn901.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView tvLat;
    private TextView tvLng;
    private TextView tvAccuracy;
    private TextView tvAltitude;
    private TextView tvAddress;

    private final String latStr = "Latitude: %.2f";
    private final String lngStr = "Longitude: %.2f";
    private final String accuracyStr = "Accuracy: %.1f";
    private final String altitudeStr = "Altitude: %.1f";
    private final String addressStr = "Address: \n%s";

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.tvLat = findViewById(R.id.tvLat);
        this.tvLng = findViewById(R.id.tvLng);
        this.tvAccuracy = findViewById(R.id.tvAccuracy);
        this.tvAltitude = findViewById(R.id.tvAltitude);
        this.tvAddress = findViewById(R.id.tvAddress);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            setLocationListener();
            setTextOnCreate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setLocationListener();
                setTextOnCreate();
            }
        }
    }

    private void setTextOnCreate() {
        if (this.locationManager == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        setAllTextView(location);
    }

    private void setLocationListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new MyLocationListener());
    }

    private void setAllTextView(Location location) {
        double lat = 0;
        double lng = 0;
        double accuracy = 0;
        double altitude = 0;
        String addressStr = "";

        if (location == null) {
            Toast.makeText(this, "Could not get location...", Toast.LENGTH_SHORT).show();
        } else {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            lat = location.getLatitude();
            lng = location.getLongitude();
            accuracy = location.getAccuracy();
            altitude = location.getAltitude();
            try {
                List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addressList != null && !addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    addressStr = getAddress(address);
                }
            } catch (Exception e) {
                addressStr = "could not get address...";
                Log.e(TAG, "onLocationChanged: ", e);
            }
        }

        this.tvLat.setText(String.format(this.latStr, lat));
        this.tvLng.setText(String.format(this.lngStr, lng));
        this.tvAccuracy.setText(String.format(this.accuracyStr, accuracy));
        this.tvAltitude.setText(String.format(this.altitudeStr, altitude));
        this.tvAddress.setText(String.format(this.addressStr, addressStr));
    }

    private String getAddress(Address address) {
        ArrayList<String> data = new ArrayList<>();
        addStringIfNotEmpty(data, address.getCountryName());
        addStringIfNotEmpty(data, address.getAdminArea());
        addStringIfNotEmpty(data, address.getLocality());
        return TextUtils.join(" ", data);
    }

    private ArrayList<String> addStringIfNotEmpty(ArrayList<String> data, String s) {
        if (!TextUtils.isEmpty(s)) {
            data.add(s);
        }
        return data;
    }

    private class MyLocationListener implements LocationListener {
        private static final String TAG = "MyLocationListener";

        @Override
        public void onLocationChanged(Location location) {
            setAllTextView(location);
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
    }
}

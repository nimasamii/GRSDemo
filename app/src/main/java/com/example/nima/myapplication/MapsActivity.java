package com.example.nima.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private List<LocationInfo> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        loadLocations();
    }

    private void loadLocations() {
        XmlResourceParser res_parser = getResources().getXml(R.xml.locations);
        LocationInfoParser loc_parser = new LocationInfoParser();
        try {
            locations = loc_parser.parse(res_parser);
        } catch (XmlPullParserException e) {
            Log.e("debug", e.getMessage());
            e.printStackTrace();
            locations = new ArrayList<>();
        } catch (IOException e) {
            Log.e("debug", e.getMessage());
            e.printStackTrace();
            locations = new ArrayList<>();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        enableMyLocation();

        for (LocationInfo location : locations) {
            mMap.addMarker(new MarkerOptions()
                    .position(location.getCoordinates())
                    .title(location.getName()));
        }

        LatLng grs_cologne = new LatLng(50.938873, 6.951560);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(grs_cologne));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13.0f));
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != 1) {
            return;
        }

        if (permissions.length == 1 &&
                permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            Log.e("debug", "Location permissions not granted");
        }
    }
}

package com.example.nima.myapplication;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by nima on 4/19/16.
 */
public class LocationInfo {
    private final int id;
    private static int next_id = 0;
    private final String name;
    private final LatLng coordinates;

    LocationInfo(String n, LatLng coords) {
        id = ++next_id;
        name = n;
        coordinates = coords;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public LatLng getCoordinates() { return coordinates;}
}

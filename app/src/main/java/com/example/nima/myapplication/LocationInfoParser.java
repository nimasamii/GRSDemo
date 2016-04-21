package com.example.nima.myapplication;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nima on 4/21/16.
 */
public class LocationInfoParser {
    private static final String ns = null;

    private static final String latitude_tag = "latitude";
    private static final String longitude_tag = "longitude";

    public List<LocationInfo> parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        //parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.next();
        parser.next();
        return readLocations(parser);
    }

    private List<LocationInfo> readLocations(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<LocationInfo> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "locations");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("location")) {
                entries.add(readLocation(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private LocationInfo readLocation(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "location");
        String location_name = null;
        LatLng location_pos = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.v("LIP::readLocation", "parsing tag " + name);
            if (name.equals("name")) {
                location_name = readName(parser);
            } else if (name.equals("coordinates")) {
                location_pos = readCoordinates(parser);
            } else {
                skip(parser);
            }
        }
        Log.v("LIP::readLocation", "Location " + location_name + " @ " + location_pos.toString());
        return new LocationInfo(location_name, location_pos);
    }

    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    private LatLng readCoordinates(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "coordinates");
        double latitude = 0.0;
        double longitude = 0.0;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            Log.v("LIP::readCoordinates", "parsing tag " + name);
            if (name.equals("latitude")) {
                latitude = readDouble(parser, latitude_tag);
            } else if (name.equals("longitude")) {
                longitude = readDouble(parser, longitude_tag);
            } else {
                skip(parser);
            }
        }

        return new LatLng(latitude, longitude);
    }

    private double readDouble(XmlPullParser parser, String tag_name) throws  IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag_name);
        String content = readText(parser);
        Log.v("LIP::readDouble", "Got " + content);
        parser.require(XmlPullParser.END_TAG, ns, tag_name);
        return Double.parseDouble(content);
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                depth--;
                break;
            case XmlPullParser.START_TAG:
                depth++;
                break;
            }
        }
    }
}

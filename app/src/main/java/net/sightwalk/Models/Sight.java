package net.sightwalk.Models;

import android.database.Cursor;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Frank on 12/26/2015.
 */
public class Sight {

    public int id;
    public String type;
    public double latitude;
    public double longitude;
    public String name;
    public String title;
    public String text;
    public String image;
    public String shortdesc;

    public Sight(int id, String type, double latitude, double longitude, String name, String title, String text, String image, String shortdesc) {
        this.id = id;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.title = title;
        this.text = text;
        this.image = image;
        this.shortdesc = shortdesc;
    }

    public LatLng getCoordinates() {
        return new LatLng(latitude, longitude);
    }

}

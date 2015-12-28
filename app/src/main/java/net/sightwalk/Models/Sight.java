package net.sightwalk.Models;

import android.database.Cursor;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import net.sightwalk.Helpers.GeoDistanceCalculator;

import org.json.JSONException;
import org.json.JSONObject;

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

    public Sight(JSONObject jsonData) throws JSONException {
        this.id = jsonData.getInt("id");
        this.type = jsonData.getString("type");
        this.latitude = jsonData.getDouble("latitude");
        this.longitude = jsonData.getDouble("longitude");
        this.name = jsonData.getString("name");
        this.title = jsonData.getString("title");
        this.text = jsonData.getString("description");
        this.image = jsonData.getString("image_url");
        this.shortdesc = jsonData.getString("short_description");
    }

    public LatLng getCoordinates() {
        return new LatLng(latitude, longitude);
    }

    public boolean isInRange(LatLng to, double range) {
        return GeoDistanceCalculator.distance(getCoordinates(), to) <= range;
    }

    public boolean equals(Sight sight) {
        return type.equals(sight.type)
                && latitude == sight.latitude
                && longitude == sight.longitude
                && name.equals(sight.name)
                && title.equals(sight.title)
                && text.equals(sight.text)
                && image.equals(sight.image)
                && shortdesc.equals(sight.shortdesc);
    }

    public void commit(Sight sight) {
        type = sight.type;
        latitude = sight.latitude;
        longitude = sight.longitude;
        name = sight.name;
        title = sight.title;
        text = sight.text;
        image = sight.image;
        shortdesc = sight.shortdesc;
    }

}

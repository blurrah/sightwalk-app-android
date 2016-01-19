package net.sightwalk.Models;

import com.google.android.gms.maps.model.LatLng;

public class UserLocation {

    private static final UserLocation instance = new UserLocation();

    public static LatLng userlocation = new LatLng(0,0);

    public static UserLocation getInstance() {
        return instance;
    }
}
package net.sightwalk.Models;

import com.google.android.gms.maps.model.LatLng;

public class Legs {

    private static final Legs instance = new Legs();

    public static Integer duration = 0;
    public static Integer distance = 0;
    public static LatLng startroute = new LatLng(0,0);
    public static LatLng endroute = new LatLng(0,0);

    public static Legs getInstance() {
        return instance;
    }
}
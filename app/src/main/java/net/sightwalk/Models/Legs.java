package net.sightwalk.Models;

import com.google.android.gms.maps.model.LatLng;

public class Legs {

    private static final Legs instance = new Legs();

    public static String duration = new String();
    public static String distance = new String();
    public static LatLng startroute = new LatLng(0,0);
    public static LatLng endroute = new LatLng(0,0);

    public static Legs getInstance() {
        return instance;
    }
}
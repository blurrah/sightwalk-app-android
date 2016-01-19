package net.sightwalk.Models;

public class Polyline {

    private static final Polyline instance = new Polyline();

    public static String polyline = new String();

    public static Polyline getInstance() {
        return instance;
    }
}
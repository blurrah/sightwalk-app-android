package net.sightwalk.Models;

import android.database.Cursor;

public class Sights {

    private static final Sights instance = new Sights();

    public static Cursor activeSight = null;

    public static Sights getInstance(){
        return instance;
    }

    private Sights() { }
}
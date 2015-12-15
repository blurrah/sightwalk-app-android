package net.sightwalk.Models;

import android.database.Cursor;

import java.util.ArrayList;

public class Sights {

    private static final Sights instance = new Sights();

    public static final ArrayList<Cursor> mSightList = new ArrayList<Cursor>();

    public static Cursor activeSight = null;

    public static Sights getInstance(){
        return instance;
    }

    private Sights() {
    }
}
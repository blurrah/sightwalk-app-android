package net.sightwalk.Models;


import android.database.Cursor;

import java.util.ArrayList;

public class Cheeses {

    private static final Cheeses instance = new Cheeses();
    public static final ArrayList<String> mCheeseList = new ArrayList<String>();
    public static Cursor activeCheese = null;

    public static Cheeses getInstance(){
        return instance;
    }

    private Cheeses() {
    }
}

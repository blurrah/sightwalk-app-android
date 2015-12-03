package net.sightwalk.Stores;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class SightDBHandeler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "sights.sqlite";
    private static final int DATABASE_VERSION = 1;

    public SightDBHandeler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getSights() {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM sights";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        return c;
    }
}
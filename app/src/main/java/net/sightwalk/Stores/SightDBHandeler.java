package net.sightwalk.Stores;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class SightDBHandeler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "Sights Breda.sqlite";
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

    public Cursor getSelectedSight(Integer id){
        SQLiteDatabase db = getReadableDatabase();

        String format = String.format("SELECT * FROM sights WHERE id=\"%s\"", id);

        String query = format;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        return c;
    }
}
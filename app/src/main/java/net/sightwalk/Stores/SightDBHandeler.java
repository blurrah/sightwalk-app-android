package net.sightwalk.Stores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import net.sightwalk.Models.Sight;

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

    public Cursor getSelectedSight(Integer id) {
        SQLiteDatabase db = getReadableDatabase();

        String format = String.format("SELECT * FROM sights WHERE id=\"%s\"", id);

        String query = format;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        return c;
    }

    public void createSight(Sight sight) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues records = new ContentValues();
        records.put("id", sight.id);
        records.put("type", sight.type);
        records.put("longitude", sight.longitude);
        records.put("latitude", sight.latitude);
        records.put("name", sight.name);
        records.put("title", sight.title);
        records.put("text", sight.text);
        records.put("imgurl", sight.image);
        records.put("short_desc", sight.shortdesc);

        db.insert("sights", null, records);
        db.close();
    }

    public void deleteSight(Sight sight) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("sights", "id=?", new String[]{Integer.toString(sight.id)});
        db.close();
    }

    public void updateSight(Sight oldSight, Sight newSight) {
        deleteSight(oldSight);
        createSight(newSight);
    }
}
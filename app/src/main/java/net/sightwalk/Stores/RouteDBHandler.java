package net.sightwalk.Stores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import net.sightwalk.Models.Route;
import net.sightwalk.Models.Sight;

import java.text.SimpleDateFormat;

public class RouteDBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "Sights Breda.sqlite";
    private static final int DATABASE_VERSION = 1;

    public RouteDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getActivities() {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT id as _id, * FROM activities ORDER BY id DESC";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        return c;
    }

    public Cursor getActivitySights(Integer id){
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM sights JOIN activitySights ON sights.id = activitySights.sightId WHERE activitySights.activityId = "+ id;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        return c;
    }

    public void updateRouteEndtime(String tableName, ContentValues values, int id) {
        SQLiteDatabase db = getWritableDatabase();

        db.update(tableName, values, "id=" + id, null);
    }

    public void addActivity(Route route){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues records = new ContentValues();
        records.put("name", route.name);
        records.put("distance", route.distance);
        records.put("startTime", new SimpleDateFormat("dd/MM/yyy HH:mm:ss").format(route.startDate));
        records.put("endTime", new SimpleDateFormat("dd/MM/yyy HH:mm:ss").format(route.endDate));
        records.put("routeJson", route.routeJson);

        db.insert("activities", null, records);

        ContentValues records2 = new ContentValues();
        for(Sight sight : route.sights) {
            records2.put("activityId", getLastActivity());
            records2.put("sightId", sight.id);
            db.insert("activitySights", null, records2);
        }

        db.close();
    }

    public Integer getLastActivity(){
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT id FROM activities ORDER BY id DESC LIMIT 1";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int result = 1;

        result = c.getInt(c.getColumnIndex("id"));

        return result;
    }

    public Integer getActivityID(String Name) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT id FROM activities ORDER BY id DESC LIMIT 1 WHERE name =" + Name;

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int result = 1;

        result = c.getInt(c.getColumnIndex("id"));

        return result;
    }
}
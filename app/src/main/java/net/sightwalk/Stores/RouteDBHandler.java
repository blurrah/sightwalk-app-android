package net.sightwalk.Stores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import net.sightwalk.Models.Route;

import java.text.SimpleDateFormat;

/**
 * Created by Ruben on 07/01/2016.
 */
public class RouteDBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "Sights Breda.sqlite";
    private static final int DATABASE_VERSION = 1;

    public RouteDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getActivities() {
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT id as _id, * FROM activities";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        return c;
    }

    public void addActivity(Route route){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues records = new ContentValues();
        records.put("name", route.name);
        records.put("distance", route.distance);
        records.put("startTime", new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(route.startDate));
        records.put("endTime", new SimpleDateFormat("dd-MM-yyy HH:mm:ss").format(route.endDate));
        records.put("routeJson", route.routeJson);

        db.insert("activities", null, records);
        db.close();
    }
}

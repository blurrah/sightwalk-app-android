package net.sightwalk.Stores;

import android.content.Context;
import android.database.Cursor;

import net.sightwalk.Models.Route;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ruben on 07/01/2016.
 */
public class RouteStore {

    private static RouteStore sharedInstance;
    private static RouteDBHandler db;
    private ArrayList<Route> routes;

    protected RouteStore(Context context) {
        // set database handler
        if (!(db instanceof RouteDBHandler)) {
            db = new RouteDBHandler(context);
        }

        readRoutes();
    }

    public static RouteStore getSharedInstance(Context context) {
        if (!(sharedInstance instanceof RouteStore)) {
            sharedInstance = new RouteStore(context);
        }

        return sharedInstance;
    }

    private void readRoutes() {
        routes = new ArrayList<>();

        Cursor cursor = db.getActivities();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            routes.add(parseRoute(cursor));
            cursor.moveToNext();
        }
    }

    private Route parseRoute(Cursor cursor) {
        String name = cursor.getString(cursor.getColumnIndex("name"));
        int distance = cursor.getInt(cursor.getColumnIndex("distance"));
        Date startTime = formatDateString(cursor.getString(cursor.getColumnIndex("startTime")));
        Date endTime = formatDateString(cursor.getString(cursor.getColumnIndex("endTime")));
        String routeJson = cursor.getString(cursor.getColumnIndex("routeJson"));
        return new Route(name, distance, startTime, endTime, routeJson);
    }

    public void addRoute(Route route){
        db.addActivity(route);
    }

    private Date formatDateString(String timeString){

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm:ss", Locale.getDefault());

        Date dt = null;
        try {
            dt = sdf.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date result = dt;

        return result;
    }
}

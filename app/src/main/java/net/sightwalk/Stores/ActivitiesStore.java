package net.sightwalk.Stores;

import android.content.Context;
import android.database.Cursor;

import net.sightwalk.Models.Activities;

import java.util.ArrayList;

public class ActivitiesStore {

    private static SightDBHandeler db;
    private ArrayList<Activities> activities;

    public ActivitiesStore(Context context) {
        db = new SightDBHandeler(context);

        readSights();
    }

    public ArrayList<Activities> getAll() {
        return activities;
    }

    private void readSights() {
        activities = new ArrayList<>();

        Cursor cursor = db.getStatsData();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            activities.add(parseActivities(cursor));
            cursor.moveToNext();
        }
    }

    private Activities parseActivities(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        int distance = cursor.getInt(cursor.getColumnIndex("distance"));
        String starttijd = cursor.getString(cursor.getColumnIndex("startTime"));
        String eindtijd = cursor.getString(cursor.getColumnIndex("endTime"));
        String json = cursor.getString(cursor.getColumnIndex("routeJson"));

        return new Activities(id, name, distance, starttijd, eindtijd, json);
    }
}
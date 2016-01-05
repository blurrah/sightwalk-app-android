package net.sightwalk.Stores;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.util.Log;

import net.sightwalk.Helpers.GPSTracker;
import net.sightwalk.Helpers.SightSyncer;
import net.sightwalk.Models.Sight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Frank on 12/26/2015.
 */
public class SightStore implements SightSyncerInterface {
    private static SightStore sharedInstance;

    public static SightStore getSharedInstance(String slot, SightsInterface client) {
        if (!(sharedInstance instanceof SightStore)) {
            sharedInstance = new SightStore(slot, client);
        }
        return sharedInstance;
    }

    public static void unsubscribe(String slot) {
        clients.remove(slot);
    }

    private static SightDBHandeler db;
    private ArrayList<Sight> sights;
    private static HashMap<String, SightsInterface> clients = new HashMap<>();
    private ArrayList<Integer> favourites;

    protected SightStore(String slot, SightsInterface client) {
        // add subscriber
        clients.put(slot, client);

        // set database handler
        if (!(db instanceof SightDBHandeler)) {
            db = new SightDBHandeler(client.getApplicationContext());
        }

        readSights();
        readFavourites();
    }

    public ArrayList<Sight> getAll() {
        return sights;
    }
    public ArrayList<Integer> getFavourites() {return favourites;}

    public void sync(Location location) {
        SightSyncer.SyncForLocation(location, this);
    }

    public void sync(GPSTracker gpsTracker) {
        sync(gpsTracker.getLocation());
    }

    private void readSights() {
        sights = new ArrayList<>();

        Cursor cursor = db.getSights();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            sights.add(parseSight(cursor));
            cursor.moveToNext();
        }
    }

    private void readFavourites() {
        favourites = new ArrayList<>();

        Cursor cursor = db.getFavourites();
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            favourites.add(cursor.getInt(cursor.getColumnIndex("sightId")));
            cursor.moveToNext();
        }
    }

    private Sight parseSight(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex("id"));
        String type = cursor.getString(cursor.getColumnIndex("type"));
        double lat = cursor.getDouble(cursor.getColumnIndex("latitude"));
        double lon = cursor.getDouble(cursor.getColumnIndex("longitude"));
        String name = cursor.getString(cursor.getColumnIndex("name"));
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String text = cursor.getString(cursor.getColumnIndex("text"));
        String image = cursor.getString(cursor.getColumnIndex("imgurl"));
        String shortDesc = cursor.getString(cursor.getColumnIndex("short_desc"));
        return new Sight(id, type, lat, lon, name, title, text, image, shortDesc);
    }

    @Override
    public void triggerAddSight(Sight sight) {
        // commit changes
        sights.add(sight);
        db.createSight(sight);

        Log.d("SightStore", "trigger add");

        // communicate clients
        Iterator it = clients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SightsInterface client = (SightsInterface) pair.getValue();
            client.addedSight(sight);
        }
    }

    @Override
    public void triggerRemoveSight(Sight sight) {
        Log.d("SightStore", "trigger remove");

        // communicate clients
        Iterator it = clients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SightsInterface client = (SightsInterface) pair.getValue();
            client.removedSight(sight);
        }

        // commit changes
        sights.remove(sight);
        db.deleteSight(sight);
    }

    @Override
    public void triggerUpdateSight(Sight oldSight, Sight newSight) {
        db.updateSight(oldSight, newSight);

        Iterator it = clients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            SightsInterface client = (SightsInterface) pair.getValue();
            client.updatedSight(oldSight, newSight);
        }

        Log.d("SightStore", "trigger update");

        oldSight.commit(newSight);
    }

    public void AddFavourite(Sight sight) {
        // commit changes
        favourites.add(sight.id);
        db.addFavourite(sight);


    }

    public void RemoveFavourite(Sight sight) {

        for(int i = 0; i < favourites.size();i++){
            if(favourites.get(i) == sight.id){
                favourites.remove(i);
            }
        }

        db.deleteFavourite(sight);
    }
}

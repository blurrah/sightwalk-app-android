package net.sightwalk.Helpers;

import android.location.Location;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import net.sightwalk.Models.Sight;
import net.sightwalk.Stores.SightStore;
import net.sightwalk.Tasks.SightGeoLoadTask;
import net.sightwalk.Tasks.TaskInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Frank on 12/26/2015.
 */
public class SightSyncer implements TaskInterface {

    private static SightSyncer sharedInstance;
    private static int syncDistance = 10;

    public static SightSyncer getSharedInstance(SightStore store) {
        if (!(sharedInstance instanceof SightSyncer)) {
            sharedInstance = new SightSyncer(store);
        }
        return sharedInstance;
    }

    public static void SyncForLocation(Location location, SightStore store) {
        getSharedInstance(store).sync(location);
    }

    private Location lastLocation;
    private LatLng lastSyncLocation;
    private long lastTime;
    private SightStore store;


    protected SightSyncer(SightStore store) {
        this.store = store;
    }

    public void sync(Location location) {
        long currentTime = new Date().getTime();

        // only update if moved more than 100 meters or 60 seconds elapsed
        if (currentTime - 60 * 1000 > lastTime || lastLocation.distanceTo(location) > 100) {
            lastTime = currentTime;
            lastLocation = location;

            performSync();
        }
    }

    private void performSync() {
        double latitude = 51.5891072;
        double longitude = 4.7753679;

        LatLng breda = new LatLng(latitude, longitude);
        SightGeoLoadTask sglt = new SightGeoLoadTask(breda, syncDistance, this);

        lastSyncLocation = breda;

//        SightGeoLoadTask sglt = new SightGeoLoadTask(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), syncDistance, this);
        sglt.execute();
    }

    @Override
    public void onSuccess(JSONObject data) {
        HashMap<Integer, Sight> sights = new HashMap<>();
        try {
            JSONArray sightArray = data.getJSONArray("sights");
            for (int i = 0; i < sightArray.length(); i++) {
                Sight newSight = new Sight(sightArray.getJSONObject(i));
                sights.put(newSight.id, newSight);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        processSights(sights);
    }

    @Override
    public void onFailure(int errorCode) {
        Log.d(" ss", " nah");
    }

    private void processSights(HashMap<Integer, Sight> sights) {

        ArrayList<Sight> toRemove = new ArrayList<>();
        HashMap<Sight, Sight> toUpdate = new HashMap<>();

        for (Sight sight : store.getAll()) {
            if (sight.isInRange(lastSyncLocation, syncDistance)) {

                if (!sights.containsKey(sight.id)) {
                    // removed sight
                    toRemove.add(sight);
                    continue;
                }

                Sight cSight = sights.get(sight.id);
                sights.remove(sight.id);

                if (sight.id == cSight.id && !sight.equals(cSight)) {
                    toUpdate.put(sight, cSight);
                }

            }
        }

        for (Sight remove : toRemove) {
            store.triggerRemoveSight(remove);
        }

        Iterator it = sights.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            store.triggerAddSight((Sight) pair.getValue());
        }

        it = toUpdate.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            store.triggerUpdateSight((Sight) pair.getKey(), (Sight) pair.getValue());
        }
    }
}

package net.sightwalk.Helpers;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import net.sightwalk.Controllers.Introduction.MainActivity;
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
    private static int syncDistance = 500;
    private static Context baseContext;

    public static SightSyncer getSharedInstance(SightStore store) {
        if (!(sharedInstance instanceof SightSyncer)) {
            sharedInstance = new SightSyncer(store);
        }
        return sharedInstance;
    }

    public static void SyncForLocation(Location location, SightStore store, Context context) {
        getSharedInstance(store).sync(location);
        baseContext = context;
    }

    public static void SyncLastLocation(SightStore store, Context context) {
        getSharedInstance(store).sync();
        baseContext = context;
    }

    private Location lastLocation;
    private LatLng lastSyncLocation;
    private long lastTime;
    private SightStore store;


    protected SightSyncer(SightStore store) {
        this.store = store;
    }

    public void sync() {
        if (lastLocation == null) {
            Log.d("SightSyncer", "No position determined, sync not possible");
            return;
        }

        lastTime = new Date().getTime();
        performSync();
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
        lastSyncLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        SightGeoLoadTask sglt = new SightGeoLoadTask(lastSyncLocation, syncDistance, this);
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

        for (Sight currentSight : store.getAll()) {
            if (currentSight.isInRange(lastSyncLocation, syncDistance)) {

                if (!sights.containsKey(currentSight.id)) {
                    // removed sight
                    toRemove.add(currentSight);
                    continue;
                }

                Sight newSight = sights.get(currentSight.id);
                sights.remove(currentSight.id);

                if (!currentSight.equals(newSight)) {
                    toUpdate.put(currentSight, newSight);
                }
            }
        }

        for (Sight remove : toRemove) {
            store.triggerRemoveSight(remove);
        }

        if(sights.size() > 0) {
            // new sights found
            Toast.makeText(baseContext, "Nieuwe Sights beschikbaar", Toast.LENGTH_LONG).show();
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

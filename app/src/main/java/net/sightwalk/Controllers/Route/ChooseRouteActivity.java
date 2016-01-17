package net.sightwalk.Controllers.Route;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import net.sightwalk.Helpers.PermissionActivity;
import net.sightwalk.Helpers.PermissionInterface;
import net.sightwalk.Models.Sight;
import net.sightwalk.R;
import net.sightwalk.Stores.SightSelectionStore;
import net.sightwalk.Stores.SightsInterface;
import java.util.HashMap;

public class ChooseRouteActivity extends PermissionActivity implements SightsInterface, View.OnClickListener, GoogleMap.OnMarkerClickListener {

    /**
     * View elements
     */
    private SightDialogFragment sdfInfo;
    private FloatingActionButton fabAddSight;
    private FloatingActionButton fabRemoveSight;
    private FloatingActionButton fabAddFavourite;
    private FloatingActionButton fabRemoveFavourite;
    private GoogleMap googleMap;

    private Marker selectedMarker;
    private SightSelectionStore store;
    private HashMap<Marker, Sight> sights = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_route);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        sdfInfo = (SightDialogFragment) fm.findFragmentById(R.id.fragment_sight);
        fabAddSight = (FloatingActionButton) findViewById(R.id.fabAddSight);
        fabRemoveSight = (FloatingActionButton) findViewById(R.id.fabRemoveSight);
        fabAddFavourite = (FloatingActionButton) findViewById(R.id.fabAddFavourite);
        fabRemoveFavourite = (FloatingActionButton) findViewById(R.id.fabRemoveFavourite);

        fabAddSight.setOnClickListener(this);
        fabRemoveSight.setOnClickListener(this);
        fabAddFavourite.setOnClickListener(this);
        fabRemoveFavourite.setOnClickListener(this);

        initGoogleMaps();

        // bind store
        store = SightSelectionStore.getSharedInstance("chooseRouteActivity", this);
        for (Sight sight : store.getAll()) {
            addedSight(sight);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                overridePendingTransition(R.anim.activity_return_in, R.anim.activity_return_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void addedSight(Sight sight) {
        Marker m = googleMap.addMarker(
                new MarkerOptions().position(sight.getCoordinates()).title(sight.name)
        );

        sights.put(m, sight);

        if(store.isVisited(sight)){
            m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }

        if(store.isFavourited(sight)){
            m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }

        if (store.isSelected(sight)) {
            m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
    }

    @Override
    public void removedSight(Sight sight) {

    }

    @Override
    public void updatedSight(Sight oldSight, Sight newSight) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddSight:
                setSightSelection(selectedMarker, true);
                break;
            case R.id.fabRemoveSight:
                setSightSelection(selectedMarker, false);
                break;
            case R.id.fabAddFavourite:
                setSightFavourite(selectedMarker);
                break;
            case R.id.fabRemoveFavourite:
                setSightFavourite(selectedMarker);
                break;
        }
    }

    private void setSightSelection(Marker marker, boolean state) {
        // update in store
        Sight sight = sights.get(marker);
        store.setSelected(sight, state);

        if(state) {
            selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else {
            selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker());

            //if(store.isVisited(sight)) {
                selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                if (store.isFavourited(sight)) {
                    selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                }
            //}
        }

        // update view
        updateFloatingButtonStyle(state);
    }

    private void setSightFavourite(Marker marker) {
        // update in store
        Sight sight = sights.get(marker);
        boolean state = false;

        for (Integer sightId : store.getFavourites()) {
            if(sightId == sight.id){
                state = true;
                break;
            }
        }

        if(state){
            store.RemoveFavourite(sight);
            if(!store.isSelected(sight)) {
                selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
            }
            state = false;
        } else {
            store.AddFavourite(sight);
            if(!store.isSelected(sight)) {
                selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            }
            state = true;
        }

        updateFavouriteFloatingButtonStyle(state, marker);
    }

    private void updateFloatingButtonStyle(boolean state) {
        if (state) {
            fabAddSight.setVisibility(View.INVISIBLE);
            fabRemoveSight.setVisibility(View.VISIBLE);
        } else {
            fabAddSight.setVisibility(View.VISIBLE);
            fabRemoveSight.setVisibility(View.INVISIBLE);
        }
    }

    private void updateFavouriteFloatingButtonStyle(boolean state , Marker m) {
        if (state) {
            fabAddFavourite.setVisibility(View.INVISIBLE);
            fabRemoveFavourite.setVisibility(View.VISIBLE);
        } else {
            fabAddFavourite.setVisibility(View.VISIBLE);
            fabRemoveFavourite.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        selectedMarker = marker;

        updateFloatingButtonStyle(store.isSelected(sights.get(marker)));
        sdfInfo.show();
        sdfInfo.setScope(sights.get(marker));

        boolean state = false;
        for (Integer sightId : store.getFavourites()) {
            if(sightId == sights.get(marker).id){
                state = true;
                break;
            }
        }
        updateFavouriteFloatingButtonStyle(state, marker);

        return false;
    }

    private void initGoogleMaps() {
        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.chooseMapView)).getMap();
        googleMap.setOnMarkerClickListener(this);
        enableGPSTracking();
        zoomBreda();
    }

    private void zoomBreda() {
        double latitude = 51.5891072;
        double longitude = 4.7753679;

        LatLng breda = new LatLng(latitude, longitude);

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, latitude));
        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(breda, 15);

        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);
    }

    private void enableGPSTracking() {
        validateGranted(Manifest.permission.ACCESS_FINE_LOCATION, new PermissionInterface() {
            @Override
            public void granted(String... permission) {
                updateGPSTracking();
            }

            @Override
            public void denied(String... permission) {
                Log.d("gpstracker", "GPS permission not granted");
            }
        });
    }

    private void updateGPSTracking() {
        if (googleMap != null) {
            try {
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                Log.d("ChooseRouteActivity", "failed to enable map location tracker");
            }
        }
    }
}
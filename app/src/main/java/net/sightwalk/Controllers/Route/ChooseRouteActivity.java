package net.sightwalk.Controllers.Route;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.sightwalk.Controllers.Route.SightDialogFragment;
import net.sightwalk.Models.Cheeses;
import net.sightwalk.R;
import net.sightwalk.Stores.SightDBHandeler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ChooseRouteActivity extends AppCompatActivity {

    private GoogleMap googleMap;
    SightDBHandeler database;
    private Cursor cursor;
    private Cursor selectedCursor;

    HashMap<Marker, String> markerHaspMap = new HashMap <Marker, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_route);

        database = new SightDBHandeler(getApplicationContext());
        cursor = database.getSights();

        displayLocation();

        googleMap.setOnMarkerClickListener(new markerListener());
    }

    private class markerListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            //Toast.makeText(getApplicationContext(), marker.getPosition().toString(), Toast.LENGTH_LONG).show();
            //Log.i("CLICKED_MARKER_INFO", marker.getId());

            selectedCursor = database.getSelectedSight(markerHaspMap.get(marker));

            if (!contains(Cheeses.getInstance().mCheeseList, selectedCursor.getString(selectedCursor.getColumnIndex("name")))) {

                Cheeses.getInstance().mCheeseList.add(selectedCursor.getString(selectedCursor.getColumnIndex("name")));
            }

            Cheeses.activeCheese = selectedCursor;

            FragmentManager fm = getSupportFragmentManager();

            SightDialogFragment fragment = (SightDialogFragment) fm.findFragmentById(R.id.fragment_sight);
            fragment.refreshFragment();

            return false;
        }
    }

    boolean contains(ArrayList<String> list, String name) {
        for (String item : list) {
            if (item.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void displayLocation() {

        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.chooseMapView)).getMap();
            if (googleMap != null) {

                googleMap.setMyLocationEnabled(true);

                if (googleMap.getMyLocation() != null) {
                    LatLng myLocation = new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude());

                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude()));
                    CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(myLocation, 15);

                    googleMap.moveCamera(center);
                    googleMap.animateCamera(zoom);
                }
                else {
                    double latitude = 51.5891072;
                    double longitude = 4.7753679;

                    LatLng breda = new LatLng(latitude, longitude);

                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, latitude));
                    CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(breda, 15);

                    googleMap.moveCamera(center);
                    googleMap.animateCamera(zoom);
                }

                cursor.moveToFirst();

                while(!cursor.isAfterLast()) {
                    double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                    double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));

                    Marker m = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name));
                    markerHaspMap.put(m, name);
                    cursor.moveToNext();
                }
            }
        }
    }
}

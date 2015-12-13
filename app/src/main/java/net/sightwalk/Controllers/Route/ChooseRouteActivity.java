package net.sightwalk.Controllers.Route;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private SightDBHandeler database;
    private Cursor cursor;
    private Cursor selectedCursor;
    private FloatingActionButton fabAddSight;
    private FloatingActionButton fabRemoveSight;
    private Marker selectedMarker;

    HashMap<Marker, Integer> markerHaspMap = new HashMap <Marker, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_route);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = new SightDBHandeler(getApplicationContext());
        cursor = database.getSights();

        displayLocation();

        googleMap.setOnMarkerClickListener(new markerListener());

        fabAddSight = (FloatingActionButton) findViewById(R.id.fabAddSight);
        fabAddSight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Cheeses.getInstance().mCheeseList.add(selectedCursor);
                    Toast.makeText(getApplicationContext(), "Sight is toegevoegd", Toast.LENGTH_SHORT).show();

                    selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    fabAddSight.setVisibility(View.INVISIBLE);
                    fabRemoveSight.setVisibility(View.VISIBLE);
                }

        });

        fabRemoveSight = (FloatingActionButton) findViewById(R.id.fabRemoveSight);
        fabRemoveSight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    for(Cursor k : Cheeses.getInstance().mCheeseList){
                        if(k.getInt(k.getColumnIndex("id")) == selectedCursor.getInt(selectedCursor.getColumnIndex("id")))
                        {
                            Cheeses.getInstance().mCheeseList.remove(k);
                            selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
                            break;
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Sight is verwijderd", Toast.LENGTH_SHORT).show();
                    fabAddSight.setVisibility(View.VISIBLE);
                    fabRemoveSight.setVisibility(View.INVISIBLE);
                }

        });
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

    private class markerListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            selectedMarker = marker;
            selectedCursor = database.getSelectedSight(markerHaspMap.get(marker));

            Cheeses.activeCheese = selectedCursor;

            FragmentManager fm = getSupportFragmentManager();

            if (!contains(Cheeses.getInstance().mCheeseList, selectedCursor.getInt(selectedCursor.getColumnIndex("id")))) {
                fabAddSight.setVisibility(View.VISIBLE);
                fabRemoveSight.setVisibility(View.INVISIBLE);
            }else{
                fabAddSight.setVisibility(View.INVISIBLE);
                fabRemoveSight.setVisibility(View.VISIBLE);
            }

                SightDialogFragment fragment = (SightDialogFragment) fm.findFragmentById(R.id.fragment_sight);
            fragment.view.setVisibility(View.VISIBLE);
            fragment.refreshFragment();

            return false;
        }
    }

    boolean contains(ArrayList<Cursor> list, Integer id) {
        for (Cursor item : list) {
            if (item.getInt(item.getColumnIndex("id")) == id) {
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
                    Integer id = cursor.getInt(cursor.getColumnIndex("id"));

                    Marker m = googleMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitude, longitude))
                                    .title(name)
                    );

                    if (contains(Cheeses.getInstance().mCheeseList, cursor.getInt(cursor.getColumnIndex("id")))) {

                        m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }
                    markerHaspMap.put(m, id);
                    cursor.moveToNext();
                }
            }
        }
    }
}

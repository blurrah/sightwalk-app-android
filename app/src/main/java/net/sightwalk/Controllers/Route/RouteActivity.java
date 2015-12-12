package net.sightwalk.Controllers.Route;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import net.sightwalk.Controllers.SettingsActivity;
import net.sightwalk.Helpers.RouteAdapter;
import net.sightwalk.Models.Steps;
import net.sightwalk.R;
import net.sightwalk.Tasks.RouteTask;

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity implements RouteTask.RetrievePolyline {

    private GoogleMap googleMap;

    public locationListener listener;
    LocationManager mLocationManager;
    public LatLng mLocation;

    static AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new locationListener();

        locationManager();

        // Hardcoded values for testing
        final RouteTask routeTask = new RouteTask(this, "Etten-Leur,Vijfkamp", "Breda-Centrum", "", "walking", "nl");
        routeTask.execute();
    }

    public void locationManager() {
        boolean gpsIsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkIsEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(gpsIsEnabled) {
            try {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, listener);
            } catch (SecurityException e) {
                Log.e("ERROR_", e.getLocalizedMessage());
            }
        }
        else if (networkIsEnabled) {
            try {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, listener);
            } catch (SecurityException e) {
                Log.e("ERROR_", e.getLocalizedMessage());
            }
        }
        else {
            errorDialog("Locatie niet gevonden");
        }
    }

    public void errorDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Om deze functie te gebruiken hebben we je locatie nodig. Zet alsjeblieft je internet of GPS modus aan in de locatie opties.")
                .setTitle(error)
                .setCancelable(false)
                .setPositiveButton("Instellingen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        alert.dismiss();
                    }
                })
                .setNegativeButton("Annuleren", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getApplicationContext(), NewRouteActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private class locationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if(location == null) {
                errorDialog("Locatie niet gevonden");
            }
            else {
                mLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }

            // Hardcoded values for testing
            boolean range = inRange(51.5676979, 4.6547354, 51.567638, 4.654226299999999, location.getLatitude(), location.getLongitude());

            if(!range) {
                Toast.makeText(getApplication(), "false", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplication(), "true", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void RetrievePolyline(String poly, ArrayList<Steps> steps) {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.routeMapView)).getMap();
            if (googleMap != null) {

                googleMap.setMyLocationEnabled(true);

                if(mLocation == null) {
                    errorDialog("Locatie niet gevonden");
                }
                else {
                    CameraUpdate center = CameraUpdateFactory.newLatLng(mLocation);
                    CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(mLocation, 15);

                    googleMap.moveCamera(center);
                    googleMap.animateCamera(zoom);
                }

                googleMap.addPolyline(new PolylineOptions()
                        .addAll(PolyUtil.decode(poly))
                        .width(5)
                        .color(Color.RED));
            }
        }

        ListAdapter adapter = new RouteAdapter(this, steps);
        ListView listView = (ListView) findViewById(R.id.routeListView);
        listView.setAdapter(adapter);

        //Steps item = (Steps) listView.getItemAtPosition(1);
    }

    private static boolean inRange(double start_x, double start_y, double end_x, double end_y, double point_x, double point_y) {
        double dx = end_x - start_x;
        double dy = end_y - start_y;
        double innerProduct = (point_x - start_x)*dx + (point_y - start_y)*dy;
        return 0 <= innerProduct && innerProduct <= dx*dx + dy*dy;
    }
}
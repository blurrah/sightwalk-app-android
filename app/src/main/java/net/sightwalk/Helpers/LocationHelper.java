package net.sightwalk.Helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import net.sightwalk.Controllers.Route.NewRouteActivity;

public class LocationHelper {

    final Context context;

    private locationListener listener;
    private LocationManager mLocationManager;
    private LatLng mLocation;

    static AlertDialog alert;

    public LocationHelper(Context context) {
        this.context = context;
        mLocationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        listener = new locationListener();
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
                Toast.makeText(context, "false", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "true", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    }

    public LatLng getLocation() {
        return mLocation;
    }

    public void errorDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("Om deze functie te gebruiken hebben we je locatie nodig. Zet alsjeblieft je internet of GPS modus aan in de locatie opties.")
                .setTitle(error)
                .setCancelable(false)
                .setPositiveButton("Instellingen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);
                        alert.dismiss();
                    }
                })
                .setNegativeButton("Annuleren", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(context, NewRouteActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private static boolean inRange(double start_x, double start_y, double end_x, double end_y, double point_x, double point_y) {
        double dx = end_x - start_x;
        double dy = end_y - start_y;
        double innerProduct = (point_x - start_x)*dx + (point_y - start_y)*dy;
        return 0 <= innerProduct && innerProduct <= dx*dx + dy*dy;
    }
}
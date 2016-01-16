package net.sightwalk.Helpers;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by Frank on 12/26/2015.
 */
public class GPSTracker implements LocationListener {

    private static int PERMISSION_GPS = 1;

    private final PermissionActivity client;
    private final GPSTrackerInterface listener;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(PermissionActivity client, GPSTrackerInterface listener) {
        this.client = client;
        this.listener = listener;

        locationManager = (LocationManager) client.getApplicationContext().getSystemService(client.LOCATION_SERVICE);

        getPermissions();
        getLocation();
    }

    /**
     * @return
     * @SuppressLint
     */
    public Location getLocation() {
        try {
            if (!canGetLocation) {
                // no network provider is enabled
            } else {
                if (isNetworkEnabled) {
                    startNetworkLocator();
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled && location == null) {
                    startGPSLocator();
                }
            }
        } catch (SecurityException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    private void startNetworkLocator() throws SecurityException {
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }

    private void startGPSLocator() throws SecurityException {
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        Log.d("GPS Enabled", "GPS Enabled");
        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
    }

    private void getPermissions() {
        // ask for permission to retrieve gps location
        client.validateGranted(Manifest.permission.ACCESS_FINE_LOCATION, new PermissionInterface() {
            @Override
            public void granted(String... permission) {
                updateDeviceCapabilities();
            }

            @Override
            public void denied(String... permission) {
                Log.d("gpstracker", "GPS permission not granted");
            }
        });
    }

    private void updateDeviceCapabilities() {
        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        canGetLocation = isGPSEnabled || isNetworkEnabled;

        if (canGetLocation) {
            getLocation();
        }
    }


    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            try {
                locationManager.removeUpdates(this);
            } catch (SecurityException e) {

            }
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(client);
        // Setting Dialog Title
        alertDialog.setTitle("GPS instellingen");
        // Setting Dialog Message
        alertDialog.setMessage("Om deze functie te gebruiken hebben we je locatie nodig. Zet alsjeblieft je internet of GPS modus aan in de locatie opties.");
        // On pressing Settings button
        alertDialog.setPositiveButton("Instellingen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                client.startActivity(intent);
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Annuleren", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location currentLocation) {
// TODO Auto-generated method stub
        this.location = currentLocation;

        // update
        getLongitude();
        getLatitude();

        listener.updatedLocation(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
        this.updateDeviceCapabilities();

        if (!canGetLocation) {
            showSettingsAlert();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        this.updateDeviceCapabilities();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
// TODO Auto-generated method stub
    }
}
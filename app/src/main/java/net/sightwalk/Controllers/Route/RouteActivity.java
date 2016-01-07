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
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.geometry.*;

import net.sightwalk.Controllers.Dashboard.DashboardActivity;
import net.sightwalk.Controllers.SettingsActivity;
import net.sightwalk.Helpers.GPSTracker;
import net.sightwalk.Helpers.GPSTrackerInterface;
import net.sightwalk.Helpers.PermissionActivity;
import net.sightwalk.Models.*;
import net.sightwalk.Models.Polyline;
import net.sightwalk.R;
import net.sightwalk.Stores.SightSelectionStore;
import net.sightwalk.Stores.SightStore;
import net.sightwalk.Stores.SightsInterface;

import java.util.*;

public class RouteActivity extends PermissionActivity implements SightsInterface, GPSTrackerInterface {

    private GoogleMap googleMap;
    private locationListener listener;
    private LocationManager mLocationManager;
    private UserLocation mLocation;
    private TextView steps;
    private ImageView imageView;
    private GPSTracker gpsTracker;

    private static AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.routeMapView)).getMap();

        steps = (TextView) findViewById(R.id.routeTextView);
        imageView = (ImageView) findViewById(R.id.maneuverImageView);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new locationListener();

        locationManager();

        RetrievePolyline(Polyline.getInstance().polyline, Steps.getInstance().stepsArrayList);

        gpsTracker = new GPSTracker(this, this);

        setRoute();
    }

    public void setRoute() {
        Steps step = Steps.stepsArrayList.get(0);

        steps.setText(Html.fromHtml(step.getHtml_instructions()));

        imageView.setImageResource(R.drawable.ic_walking);

        googleMap.addPolyline(new PolylineOptions()
                .addAll(PolyUtil.decode(step.getPolyline()))
                .width(10)
                .color(Color.parseColor("#0088FF")));
    }

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

    public void RetrievePolyline(String poly, ArrayList<Steps> steps) {
        if (googleMap != null) {

            try {
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                Log.e("ERROR_", e.getLocalizedMessage());
            }

            if(UserLocation.getInstance().userlocation == null) {
                errorDialog("Locatie niet gevonden");
            } else {
                CameraUpdate center = CameraUpdateFactory.newLatLng(UserLocation.getInstance().userlocation);
                CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(UserLocation.getInstance().userlocation, 17);

                googleMap.moveCamera(center);
                googleMap.animateCamera(zoom);
            }

            ArrayList<Sight> sights = SightSelectionStore.getSharedInstance("RouteActivity", this).getSelectedSights();

            for(int i = 0; i < sights.size(); i++) {
                Double latitude = sights.get(i).latitude;
                Double longitude = sights.get(i).longitude;

                Marker m = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
                m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }

            googleMap.addPolyline(new PolylineOptions()
                    .addAll(PolyUtil.decode(poly))
                    .width(10)
                    .color(Color.parseColor("#AAAAAA")));
        }

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
        else {
            errorDialog("GPS locatie niet gevonden");
        }
        if (networkIsEnabled) {
            try {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, listener);
            } catch (SecurityException e) {
                Log.e("ERROR_", e.getLocalizedMessage());
            }
        }
        else {
            errorDialog("Netwerklocatie niet gevonden");
        }
    }

    @Override
    public void addedSight(Sight sight) {

    }

    @Override
    public void removedSight(Sight sight) {

    }

    @Override
    public void updatedSight(Sight oldSight, Sight newSight) {

    }

    @Override
    public void updatedLocation(Location location) {
        Marker m = googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));
        m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        gpsTracker.stopUsingGPS();
    }

    private class locationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if(location == null) {
                errorDialog("Locatie niet gevonden");
            }
            else {
                mLocation = UserLocation.getInstance();
                mLocation.userlocation = new LatLng(location.getLatitude(), location.getLongitude());

                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17);

                googleMap.moveCamera(center);
                googleMap.animateCamera(zoom);
            }

            if(!Steps.stepsArrayList.isEmpty()) {
                Steps step = Steps.stepsArrayList.get(0);

                List<LatLng> latLngs;
                latLngs = new ArrayList<>();
                latLngs.add(new LatLng(step.getStart_location().latitude, step.getStart_location().longitude));
                latLngs.add(new LatLng(step.getEnd_location().latitude, step.getEnd_location().longitude));

                boolean range = PolyUtil.isLocationOnEdge(new LatLng(location.getLatitude(), location.getLongitude()), latLngs, true, 30);

                if (range) {
                    Steps.stepsArrayList.remove(0);

                    Steps stepupdate = Steps.stepsArrayList.get(0);

                    steps.setText(Html.fromHtml(stepupdate.getHtml_instructions()));

                    if(stepupdate.getHtml_instructions().contains("linksaf")) {
                        imageView.setImageResource(R.drawable.ic_left);
                    }
                    else if(stepupdate.getHtml_instructions().contains("rechtsaf")) {
                        imageView.setImageResource(R.drawable.ic_right);
                    }
                    else if(stepupdate.getHtml_instructions().contains("rechtdoor") || stepupdate.getHtml_instructions().contains("vervolgen")) {
                        imageView.setImageResource(R.drawable.ic_arrow);
                    }
                    else {
                        imageView.setImageResource(R.drawable.ic_walking);
                    }

                    googleMap.addPolyline(new PolylineOptions()
                            .addAll(PolyUtil.decode(stepupdate.getPolyline()))
                            .width(10)
                            .color(Color.parseColor("#0088FF")));
                }
            }else{
                //if no legs left
                //check if near start location
                float[] results = new float[1];
                Location.distanceBetween( location.getLatitude(), location.getLongitude(), Legs.getInstance().endroute.latitude, Legs.getInstance().endroute.longitude, results);



                Log.d("RESULT", results.toString());
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
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
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
        alert = builder.create();
        alert.show();
    }
}
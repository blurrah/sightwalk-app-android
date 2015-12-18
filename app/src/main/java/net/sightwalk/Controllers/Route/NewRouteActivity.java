package net.sightwalk.Controllers.Route;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import net.sightwalk.Controllers.Dashboard.DashboardActivity;
import net.sightwalk.Controllers.SettingsActivity;
import net.sightwalk.Models.*;
import net.sightwalk.R;
import net.sightwalk.Tasks.RouteTask;

public class NewRouteActivity extends AppCompatActivity {

    private String waypoint;
    private StringBuilder builder;
    private CheckBox checkBox;
    private LatLng location;
    private locationListener listener;
    private LocationManager mLocationManager;
    private UserLocation mLocation;

    private static AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new locationListener();

        locationManager();

        checkBox = (CheckBox) findViewById(R.id.cbRouteDestination);
        checkBox.setOnCheckedChangeListener(new checkedListener());

        Button rButton = (Button) findViewById(R.id.routeButton);
        rButton.setOnClickListener(new routeListener());

        Button cButton = (Button) findViewById(R.id.chooseRouteButton);
        cButton.setOnClickListener(new chooseListener());
    }

    @Override
    public void onResume(){
        super.onResume();

        Button routeButton = (Button) findViewById(R.id.routeButton);
        TextView amountSights = (TextView) findViewById(R.id.tvAmountSights);
        amountSights.setText("Totaal " + Sights.mSightList.size() + " sights");

        if(Sights.getInstance().mSightList.size() == 0) {
            routeButton.setEnabled(false);
            routeButton.setBackgroundColor(getResources().getColor(R.color.colorDisabled));
        } else {
            routeButton.setEnabled(true);
            routeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            createRoute(checkBox.isChecked());
        }
    }

    public void createRoute(boolean cbChecked){
        builder = new StringBuilder();

        for (int i = 0; i < Sights.getInstance().mSightList.size(); i++) {
            String latitude = Sights.getInstance().mSightList.get(i).getString(Sights.getInstance().mSightList.get(i).getColumnIndex("latitude"));
            String longitude = Sights.getInstance().mSightList.get(i).getString(Sights.getInstance().mSightList.get(i).getColumnIndex("longitude"));

            builder.append(latitude + "," + longitude + "|");
        }

        waypoint = builder.toString();

        if (cbChecked) {
            location = UserLocation.getInstance().userlocation;
        } else {
            int last = Sights.getInstance().mSightList.size() - 1;
            Double latitude = Sights.getInstance().mSightList.get(last).getDouble(Sights.getInstance().mSightList.get(last).getColumnIndex("latitude"));
            Double longitude = Sights.getInstance().mSightList.get(last).getDouble(Sights.getInstance().mSightList.get(last).getColumnIndex("longitude"));
            location = new LatLng(latitude, longitude);
        }

        final RouteTask routeTask = new RouteTask(UserLocation.getInstance().userlocation, location.latitude + "," + location.longitude, waypoint.toString(), "walking", "nl", this, this);
        routeTask.execute();
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

    private class routeListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), RouteActivity.class);
            startActivity(i);
        }
    }

    private class chooseListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), ChooseRouteActivity.class);
            startActivity(i);

            overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);
        }
    }

    private class checkedListener implements CheckBox.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(Sights.getInstance().mSightList.size() > 0){
                createRoute(b);
            }
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

    private class locationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if(location == null) {
                errorDialog("Locatie niet gevonden");
            }
            else {
                mLocation = UserLocation.getInstance();
                mLocation.userlocation = new LatLng(location.getLatitude(), location.getLongitude());
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
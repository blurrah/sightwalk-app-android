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
import net.sightwalk.Helpers.GPSTracker;
import net.sightwalk.Helpers.GPSTrackerInterface;
import net.sightwalk.Helpers.PermissionActivity;
import net.sightwalk.Models.*;
import net.sightwalk.R;
import net.sightwalk.Tasks.RouteTask;

public class NewRouteActivity extends PermissionActivity implements GPSTrackerInterface {

    private String waypoint;
    private StringBuilder builder;
    private CheckBox checkBox;
    private LatLng location;

    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        checkBox = (CheckBox) findViewById(R.id.cbRouteDestination);
        checkBox.setOnCheckedChangeListener(new checkedListener());

        Button rButton = (Button) findViewById(R.id.routeButton);
        rButton.setOnClickListener(new routeListener());

        Button cButton = (Button) findViewById(R.id.chooseRouteButton);
        cButton.setOnClickListener(new chooseListener());

        gpsTracker = new GPSTracker(this, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateDistance();
    }

    private void updateDistance() {
        Button routeButton = (Button) findViewById(R.id.routeButton);
        TextView amountSights = (TextView) findViewById(R.id.tvAmountSights);
        amountSights.setText("Totaal " + Sights.mSightList.size() + " sights");

        if (Sights.getInstance().mSightList.size() == 0) {
            routeButton.setEnabled(false);
            routeButton.setBackgroundColor(getResources().getColor(R.color.colorDisabled));
        } else {
            routeButton.setEnabled(true);
            routeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            createRoute(checkBox.isChecked());
        }
    }

    public void createRoute(boolean cbChecked) {
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

    @Override
    public void updatedLocation(Location location) {
        this.location = new LatLng(location.getLatitude(), location.getLongitude());
        updateDistance();
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

    private class checkedListener implements CheckBox.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (Sights.getInstance().mSightList.size() > 0) {
                createRoute(b);
            }
        }
    }

}
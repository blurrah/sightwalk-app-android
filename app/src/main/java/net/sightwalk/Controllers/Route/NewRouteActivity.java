package net.sightwalk.Controllers.Route;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.*;

import net.sightwalk.Controllers.SettingsActivity;
import net.sightwalk.Helpers.GPSTracker;
import net.sightwalk.Helpers.GPSTrackerInterface;
import net.sightwalk.Helpers.PermissionActivity;
import net.sightwalk.Models.*;
import net.sightwalk.R;
import net.sightwalk.Stores.SightSelectionStore;
import net.sightwalk.Stores.SightsInterface;
import net.sightwalk.Tasks.RouteTask;

import java.util.ArrayList;

public class NewRouteActivity extends PermissionActivity implements GPSTrackerInterface, SightsInterface, CompoundButton.OnCheckedChangeListener {

    private String waypoint;
    private StringBuilder builder;
    private CheckBox checkBox;
    private LatLng location;
    private LatLng deviceLocation;
    private Button routeButton;
    private TextView amountSights;
    private Button cButton;
    private ListViewDraggingAnimation lvdaSights;

    private GPSTracker gpsTracker;
    private ArrayList<Sight> selectedSights;
    private SightSelectionStore sightStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        checkBox = (CheckBox) findViewById(R.id.cbRouteDestination);
        routeButton = (Button) findViewById(R.id.routeButton);
        amountSights = (TextView) findViewById(R.id.tvAmountSights);
        cButton = (Button) findViewById(R.id.chooseRouteButton);
        FragmentManager fm = getSupportFragmentManager();
        lvdaSights = (ListViewDraggingAnimation) fm.findFragmentById(R.id.article_fragment);

        checkBox.setOnCheckedChangeListener(this);
        routeButton.setOnClickListener(new routeListener());
        cButton.setOnClickListener(new chooseListener());

        gpsTracker = new GPSTracker(this, this);
        sightStore = SightSelectionStore.getSharedInstance("newRouteActivity", this);
        selectedSights = sightStore.getSelectedSights();
        lvdaSights.setSights(selectedSights);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateDistance();
    }

    public void updateDistance() {
        amountSights.setText("Totaal " + selectedSights.size() + " sights");

        if (selectedSights.size() == 0) {
            routeButton.setEnabled(false);
            routeButton.setBackgroundColor(getResources().getColor(R.color.colorDisabled));
        } else {
            routeButton.setEnabled(true);
            routeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            createRoute();
        }
    }

    public void createRoute() {
        LatLng startPosition = getStartPosition();
        LatLng endPosition = getEndPosition();

        // start- and endposition is required
        if (!(startPosition instanceof LatLng) || !(endPosition instanceof LatLng)) {
            return;
        }

        builder = new StringBuilder();

        for (int i = 0; i < selectedSights.size(); i++) {
            Sight sight = selectedSights.get(i);
            builder.append(Double.toString(sight.latitude) + "," + Double.toString(sight.longitude) + "|");
        }

        waypoint = builder.toString();

        final RouteTask routeTask = new RouteTask(startPosition, endPosition.latitude + "," + endPosition.longitude, waypoint.toString(), "walking", "nl", this, this);
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
        this.deviceLocation = new LatLng(location.getLatitude(), location.getLongitude());
        updateDistance();
    }

    @Override
    public void addedSight(Sight sight) {
        // nobody cares
    }

    @Override
    public void removedSight(Sight sight) {
        if (sightStore.isSelected(sight)) {
            sightStore.setSelected(sight, false);
            updateDistance();
        }
    }

    @Override
    public void updatedSight(Sight oldSight, Sight newSight) {
        // nobody cares
    }

    private LatLng getEndPosition() {
        if (checkBox.isChecked()) {
            // end on device location
            return deviceLocation;
        }

        // end on last sight location
        if (selectedSights.size() > 0) {
            return selectedSights.get(selectedSights.size() - 1).getCoordinates();
        }

        // no start position clear
        return null;
    }

    private LatLng getStartPosition() {
        return deviceLocation;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        createRoute();
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
}
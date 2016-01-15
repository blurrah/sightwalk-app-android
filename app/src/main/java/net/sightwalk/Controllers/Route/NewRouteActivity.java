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
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.model.*;

import net.sightwalk.Controllers.SettingsActivity;
import net.sightwalk.Helpers.GPSTracker;
import net.sightwalk.Helpers.GPSTrackerInterface;
import net.sightwalk.Helpers.PermissionActivity;
import net.sightwalk.Models.*;
import net.sightwalk.R;
import net.sightwalk.Stores.RouteStore;
import net.sightwalk.Stores.SightSelectionStore;
import net.sightwalk.Stores.SightsInterface;
import net.sightwalk.Tasks.RouteTask;

import java.util.ArrayList;
import java.util.Date;

public class NewRouteActivity extends PermissionActivity implements GPSTrackerInterface, SightsInterface, CompoundButton.OnCheckedChangeListener {

    private String waypoint;
    private StringBuilder builder;
    private CheckBox checkBox;
    private Location deviceLocation;
    private long lastGPSTime = 0;
    private Button routeButton;
    private TextView amountSights;
    private Button cButton;
    private ListViewDraggingAnimation lvdaSights;
    private GPSTracker gpsTracker;
    private ArrayList<Sight> selectedSights;
    private SightSelectionStore sightStore;
    private Switch startSight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        checkBox = (CheckBox) findViewById(R.id.cbRouteDestination);
        routeButton = (Button) findViewById(R.id.routeButton);
        amountSights = (TextView) findViewById(R.id.tvAmountSights);
        cButton = (Button) findViewById(R.id.chooseRouteButton);
        startSight = (Switch) findViewById(R.id.startSight);

        FragmentManager fm = getSupportFragmentManager();
        lvdaSights = (ListViewDraggingAnimation) fm.findFragmentById(R.id.article_fragment);

        checkBox.setOnCheckedChangeListener(this);
        routeButton.setOnClickListener(new routeListener());
        cButton.setOnClickListener(new chooseListener());

        gpsTracker = new GPSTracker(this, this);
        sightStore = SightSelectionStore.getSharedInstance("newRouteActivity", this);
        selectedSights = sightStore.getSelectedSights();
        lvdaSights.setSights(selectedSights);

        startSight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateDistance();
            }
        });
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
        } else if(startSight.isChecked() && selectedSights.size() < 2){
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
    public void onBackPressed(){
        Steps.getInstance().stepsArrayList = new ArrayList<Steps>();

        ArrayList<Sight> sights = SightSelectionStore.getSharedInstance("newRouteActivity", this).getSelectedSights();
        for(Sight sight : sights){
            SightSelectionStore.getSharedInstance("newRouteActivity", this).triggerRemoveSight(sight);
        }

        this.finish();
    }

    @Override
    public void updatedLocation(Location location) {
        long currentTime = new Date().getTime();

        // only update if moved more than 100 meters or 60 seconds elapsed
        if (currentTime - 60 * 1000 > lastGPSTime || deviceLocation.distanceTo(location) > 100) {
            lastGPSTime = currentTime;
            deviceLocation = location;

            // update store and views
            updateDistance();
            sightStore.sync(location, getBaseContext());
        }
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
            return getStartPosition();
        }

        // end on last sight location
        if (selectedSights.size() > 0) {
            return selectedSights.get(selectedSights.size() - 1).getCoordinates();
        }

        // no start position clear
        return null;
    }

    private LatLng getStartPosition() {

        if(startSight.isChecked()){
            if(selectedSights.size() > 1){


                return new LatLng(selectedSights.get(0).latitude, selectedSights.get(0).longitude);
            }
        }else{
            if (deviceLocation instanceof Location) {
                UserLocation.getInstance().userlocation = new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
                return new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
            }
        }

        // no start position clear
        return null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        updateDistance();
    }

    private class routeListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            showFinishDialog();
        }
    }

    private class chooseListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(getApplicationContext(), ChooseRouteActivity.class);
            startActivity(i);
        }
    }

    public void showFinishDialog(){
        android.app.FragmentManager fm = getFragmentManager();
        NameRouteDialogFragment dialogFragment = new NameRouteDialogFragment();
        dialogFragment.show(fm, "Sample Fragment");
    }

    public void saveRoute(String routeName){

        Intent i = new Intent(getApplicationContext(), RouteActivity.class);

        i.putExtra("FINISH_SIGHT",checkBox.isChecked());
        i.putExtra("START_SIGHT", startSight.isChecked());

        startActivity(i);

        Integer distance =  Legs.getInstance().distance;
        Date timeStart = new Date();
        Date timeEnd = new Date();
        String routejson = Legs.getInstance().routeJson;

        Route route = new Route(routeName, distance, timeStart, timeEnd, routejson, selectedSights);

        RouteStore routeStore = RouteStore.getSharedInstance(getApplicationContext());
        routeStore.addRoute(route);
        this.finish();
    }
}
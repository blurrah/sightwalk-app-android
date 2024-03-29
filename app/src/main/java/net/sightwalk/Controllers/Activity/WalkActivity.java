package net.sightwalk.Controllers.Activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;

import net.sightwalk.Controllers.Dashboard.DashboardActivity;
import net.sightwalk.Controllers.Route.FinishedDialogFragment;
import net.sightwalk.Controllers.Route.SightActivity;
import net.sightwalk.Controllers.Route.StopDialogFragment;
import net.sightwalk.Helpers.GPSTracker;
import net.sightwalk.Helpers.GPSTrackerInterface;
import net.sightwalk.Helpers.PermissionActivity;
import net.sightwalk.Helpers.RouteStepsAdapter;
import net.sightwalk.Models.Polyline;
import net.sightwalk.Models.Sight;
import net.sightwalk.Models.Steps;
import net.sightwalk.R;
import net.sightwalk.Stores.SightSelectionStore;
import net.sightwalk.Stores.SightsInterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WalkActivity extends PermissionActivity implements SightsInterface, GPSTrackerInterface, Button.OnClickListener{

    private GoogleMap googleMap;
    private GPSTracker gpsTracker;
    private Location userLocation;

    public Date startTime;
    public Date endTime;

    private ArrayList<Sight> selectedSights;
    private ArrayList<Sight> storeSight;

    private ListView routeStepListView;
    private LinearLayout nextSightLayout;
    private Button directionsButton;
    private Button nextSightButton;

    private SightSelectionStore selectionStore;

    private static AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.routeMapView)).getMap();

        gpsTracker = new GPSTracker(this, this);
        startTime = new Date();

        selectionStore = SightSelectionStore.getSharedInstance("WalkActivity", this);
        storeSight = selectionStore.getSelectedSights();
        selectedSights = new ArrayList<>();

        for(Sight sight : storeSight){
            selectedSights.add(sight);
        }

        ArrayList<Steps> stepsArrayList = Steps.getInstance().stepsArrayList;

        RouteStepsAdapter routeStepsAdapter = new RouteStepsAdapter(this, stepsArrayList);
        routeStepListView = (ListView) findViewById(R.id.routeStepListView);
        routeStepListView.setAdapter(routeStepsAdapter);

        directionsButton = (Button) findViewById(R.id.directionsButton);
        nextSightButton = (Button) findViewById(R.id.nextSightButton);
        nextSightLayout = (LinearLayout) findViewById(R.id.nextSightLayout);

        directionsButton.setOnClickListener(this);
        nextSightButton.setOnClickListener(this);

        RetrievePolyline(Polyline.getInstance().polyline, Steps.getInstance().stepsArrayList);

        // Create polyline route
        setRoute();
    }

    @Override
    public void onResume() {
        super.onResume();
        gpsTracker.getLocation();
    }

    public void setRoute() {
        Steps step = Steps.stepsArrayList.get(0);

        setNextSightInfo(selectedSights.get(0));

        googleMap.addPolyline(new PolylineOptions()
                .addAll(PolyUtil.decode(step.getPolyline()))
                .width(10)
                .color(Color.parseColor("#0088FF")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_route, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_stop:
                showStopDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        clearDataRouteActivity();
    }

    public void clearDataRouteActivity(){
        Steps.getInstance().stepsArrayList = new ArrayList<Steps>();

        SightSelectionStore.getSharedInstance("WalkActivity", this).clearSelection();

        this.finish();
    }

    public void RetrievePolyline(String poly, ArrayList<Steps> steps) {
        if (googleMap != null) {

            try {
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                Log.e("ERROR_", e.getLocalizedMessage());
            }

            ArrayList<Sight> sights = SightSelectionStore.getSharedInstance("WalkActivity", this).getSelectedSights();

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

    public void updatedLocation(Location location) {
        userLocation = location;

        if(location == null) {
            errorDialog("Locatie niet gevonden");
        } else {
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
                if(!Steps.stepsArrayList.isEmpty()) {
                    Steps stepupdate = Steps.stepsArrayList.get(0);

                    googleMap.addPolyline(new PolylineOptions()
                            .addAll(PolyUtil.decode(stepupdate.getPolyline()))
                            .width(10)
                            .color(Color.parseColor("#0088FF")));
                }
            }
        }

        if(selectedSights.size() > 0) {
            float[] results = new float[1];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(), selectedSights.get(0).latitude, selectedSights.get(0).longitude, results);

            if (results[0] < 30) {
                selectionStore.AddVisited(selectedSights.get(0));

                if(storeSight.size() > 0) {
                    gpsTracker.stopUsingGPS();
                    selectionStore.setActiveSight(storeSight.get(0));
                    storeSight.remove(0);
                }

                selectedSights.remove(0);
                if(selectedSights.size() == 0){
                    endTime = new Date();
                    showFinishDialog();
                    Toast.makeText(getBaseContext(), "Route afgerond!", Toast.LENGTH_SHORT).show();
                } else {
                    setNextSightInfo(selectedSights.get(0));

                    Intent i = new Intent(getApplicationContext(), SightActivity.class);

                    startActivity(i);
                }
            }
        }
    }

    public void setNextSightInfo(Sight sight){
        TextView nextSightTitle = (TextView) findViewById(R.id.nextSightTitle);
        TextView nextSightText = (TextView )findViewById(R.id.nextSightText);
        ImageView nextSightImage = (ImageView) findViewById(R.id.nextSightImage);

        nextSightTitle.setText(sight.title);
        nextSightText.setText(sight.shortdesc);
        Picasso.with(getApplicationContext()).load(sight.image).into(nextSightImage);
    }

    public void showFinishDialog(){
        FragmentManager fm = getFragmentManager();
        FinishedDialogFragment dialogFragment = new FinishedDialogFragment();
        dialogFragment.show(fm, "Sample Fragment");
    }

    public void showStopDialog(){
        FragmentManager fm = getFragmentManager();
        StopDialogFragment dialogFragment = new StopDialogFragment();
        dialogFragment.show(fm, "Sample Fragment");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.nextSightButton:
                nextSightLayout.setVisibility(View.VISIBLE);
                nextSightButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                directionsButton.setTextColor(ContextCompat.getColor(this, R.color.colorDisabled));
                routeStepListView.setVisibility(View.GONE);
                break;
            case R.id.directionsButton:
                routeStepListView.setVisibility(View.VISIBLE);
                directionsButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                nextSightButton.setTextColor(ContextCompat.getColor(this, R.color.colorDisabled));
                nextSightLayout.setVisibility(View.GONE);
                break;
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
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
        alert = builder.create();
        alert.show();
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
}
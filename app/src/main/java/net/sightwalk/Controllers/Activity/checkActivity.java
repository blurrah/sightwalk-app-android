package net.sightwalk.Controllers.Activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
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
import net.sightwalk.Models.Legs;
import net.sightwalk.Models.Polyline;
import net.sightwalk.Models.Sight;
import net.sightwalk.Models.Steps;
import net.sightwalk.Models.UserLocation;
import net.sightwalk.R;
import net.sightwalk.Stores.SightSelectionStore;
import net.sightwalk.Stores.SightsInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CheckActivity extends PermissionActivity implements SightsInterface{

    private GoogleMap googleMap;

    private Steps step;
    private Polyline line;
    private Legs leg;

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


        Bundle intentValues = getIntent().getExtras();
        String routeJson = intentValues.getString("ROUTE_JSON");

        getRouteData(routeJson);
        RetrievePolyline(Polyline.getInstance().polyline, Steps.getInstance().stepsArrayList);

        // Create polyline route
        setRoute();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void getRouteData(String response){
        try {
            Legs.getInstance().routeJson = response;

            JSONObject directionsObject = new JSONObject(response);

            JSONArray routesObject = directionsObject.getJSONArray("routes");

            for (int i = 0; i < routesObject.length(); i++) {
                JSONObject overview = routesObject.getJSONObject(i);

                JSONObject polyline = overview.getJSONObject("overview_polyline");

                line = Polyline.getInstance();

                line.polyline = polyline.getString("points");

                JSONArray legsObject = overview.getJSONArray("legs");

                int routedis = 0;
                int routedur = 0;

                for(int l = 0; l < legsObject.length(); l++) {
                    JSONObject stepsobject = legsObject.getJSONObject(l);

                    JSONObject routeDistance = stepsobject.getJSONObject("distance");
                    JSONObject routeDuration = stepsobject.getJSONObject("duration");
                    JSONObject startroute = stepsobject.getJSONObject("start_location");
                    JSONObject endroute = stepsobject.getJSONObject("end_location");

                    routedis += routeDistance.getInt("value");
                    routedur += routeDuration.getInt("value");

                    LatLng startroutelatlng = new LatLng(startroute.getDouble("lat"), startroute.getDouble("lng"));
                    LatLng endroutelatlng = new LatLng(endroute.getDouble("lat"), endroute.getDouble("lng"));

                    leg = Legs.getInstance();

                    leg.duration = routedur;
                    leg.distance = routedis;
                    leg.startroute = startroutelatlng;
                    leg.endroute = endroutelatlng;

                    JSONArray steps = stepsobject.getJSONArray("steps");

                    for (int s = 0; s < steps.length(); s++) {
                        JSONObject route = steps.getJSONObject(s);

                        String in = route.getString("html_instructions");

                        String man = null;

                        if(route.isNull("maneuver")) { }
                        else {
                            man = route.getString("maneuver");
                        }

                        String mode = route.getString("travel_mode");

                        JSONObject distance = route.getJSONObject("distance");
                        JSONObject duration = route.getJSONObject("duration");
                        JSONObject line = route.getJSONObject("polyline");

                        String dis = distance.getString("text");
                        String dur = duration.getString("text");
                        String linepoly = line.getString("points");

                        JSONObject start = route.getJSONObject("start_location");
                        JSONObject end = route.getJSONObject("end_location");

                        LatLng startlatlng = new LatLng(start.getDouble("lat"), start.getDouble("lng"));
                        LatLng endlatlng = new LatLng(end.getDouble("lat"), end.getDouble("lng"));

                        step = new Steps();
                        step.setDistance(dis);
                        step.setDuration(dur);
                        step.setHtml_instructions(in);
                        step.setManeuver(man);
                        step.setStart_location(startlatlng);
                        step.setEnd_location(endlatlng);
                        step.setTravel_mode(mode);
                        step.setPolyline(linepoly);

                        step.stepsArrayList.add(step);
                    }
                }
            }
        } catch (JSONException ex) {
            Log.e("ERROR_", ex.getLocalizedMessage());
        }
    }

    public void setRoute() {
        Steps step = Steps.stepsArrayList.get(0);

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

        ArrayList<Sight> sights = SightSelectionStore.getSharedInstance("RouteActivity", this).getSelectedSights();
        sights.clear();

        this.finish();
    }

    public void RetrievePolyline(String poly, ArrayList<Steps> steps) {
        if (googleMap != null) {

            try {
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                Log.e("ERROR_", e.getLocalizedMessage());
            }

            ArrayList<Sight> sights = SightSelectionStore.getSharedInstance("RouteActivity", this).getSelectedSights();

            /* TODO PLACE MARKERS
            for(int i = 0; i < sights.size(); i++) {
                Double latitude = sights.get(i).latitude;
                Double longitude = sights.get(i).longitude;

                Marker m = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
                m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }
            */

            googleMap.addPolyline(new PolylineOptions()
                    .addAll(PolyUtil.decode(poly))
                    .width(10)
                    .color(Color.parseColor("#AAAAAA")));
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
}
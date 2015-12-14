package net.sightwalk.Controllers.Route;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import net.sightwalk.Controllers.SettingsActivity;
import net.sightwalk.Helpers.LocationHelper;
import net.sightwalk.Helpers.RouteAdapter;
import net.sightwalk.Models.Steps;
import net.sightwalk.R;
import net.sightwalk.Tasks.RouteTask;

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity implements RouteTask.RetrievePolyline {

    private GoogleMap googleMap;
    private LocationHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        helper = new LocationHelper(this);
        helper.locationManager();

        // Hardcoded values for testing
        final RouteTask routeTask = new RouteTask(this, "Etten-Leur,Vijfkamp", "Breda-Centrum", "", "walking", "nl");
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
    public void RetrievePolyline(String poly, ArrayList<Steps> steps) {
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.routeMapView)).getMap();
            if (googleMap != null) {

                googleMap.setMyLocationEnabled(true);

                if(helper.getLocation() == null) {
                    helper.errorDialog("Locatie niet gevonden");
                } else {
                    CameraUpdate center = CameraUpdateFactory.newLatLng(helper.getLocation());
                    CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(helper.getLocation(), 15);

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
}
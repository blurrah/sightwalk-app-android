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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import net.sightwalk.Controllers.SettingsActivity;
import net.sightwalk.Helpers.LocationHelper;
import net.sightwalk.Helpers.RouteAdapter;
import net.sightwalk.Models.Polyline;
import net.sightwalk.Models.Sights;
import net.sightwalk.Models.Steps;
import net.sightwalk.Models.UserLocation;
import net.sightwalk.R;

import java.util.ArrayList;

public class RouteActivity extends AppCompatActivity {

    private GoogleMap googleMap;
    private LocationHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        RetrievePolyline(Polyline.getInstance().polyline, Steps.getInstance().stepsArrayList);
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
        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.routeMapView)).getMap();
            if (googleMap != null) {

                googleMap.setMyLocationEnabled(true);

                if(UserLocation.getInstance().userlocation == null) {
                    helper.errorDialog("Locatie niet gevonden");
                } else {
                    CameraUpdate center = CameraUpdateFactory.newLatLng(UserLocation.getInstance().userlocation);
                    CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(UserLocation.getInstance().userlocation, 15);

                    googleMap.moveCamera(center);
                    googleMap.animateCamera(zoom);
                }

                for(int i = 0; i < Sights.getInstance().mSightList.size(); i++) {
                    Double latitude = Sights.getInstance().mSightList.get(i).getDouble(Sights.getInstance().mSightList.get(i).getColumnIndex("latitude"));
                    Double longitude = Sights.getInstance().mSightList.get(i).getDouble(Sights.getInstance().mSightList.get(i).getColumnIndex("longitude"));

                    Marker m = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
                    m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }

                googleMap.addPolyline(new PolylineOptions()
                        .addAll(PolyUtil.decode(poly))
                        .width(10)
                        .color(Color.parseColor("#0088FF")));
            }
        }

        ListAdapter adapter = new RouteAdapter(this, steps);
        ListView listView = (ListView) findViewById(R.id.routeListView);
        listView.setAdapter(adapter);
    }
}
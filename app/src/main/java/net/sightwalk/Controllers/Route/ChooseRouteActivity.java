package net.sightwalk.Controllers.Route;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.sightwalk.R;
import net.sightwalk.Stores.SightDBHandeler;

public class ChooseRouteActivity extends AppCompatActivity {

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_route);

        displayLocation();

        googleMap.setOnMarkerClickListener(new markerListener());
    }

    private class markerListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Toast.makeText(getApplicationContext(), marker.getPosition().toString(), Toast.LENGTH_LONG).show();

            Intent i = new Intent(getApplicationContext(), NewRouteActivity.class);
            startActivity(i);

            return false;
        }
    }

    private void displayLocation() {

        SightDBHandeler database = new SightDBHandeler(getApplicationContext());
        Cursor cursor = database.getSights();

        if (googleMap == null) {
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.chooseMapView)).getMap();
            if (googleMap != null) {

                googleMap.setMyLocationEnabled(true);

                if (googleMap.getMyLocation() != null) {
                    LatLng myLocation = new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude());

                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(googleMap.getMyLocation().getLatitude(), googleMap.getMyLocation().getLongitude()));
                    CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(myLocation, 15);

                    googleMap.moveCamera(center);
                    googleMap.animateCamera(zoom);
                }
                else {
                    double latitude = 51.5891072;
                    double longitude = 4.7753679;

                    LatLng breda = new LatLng(latitude, longitude);

                    CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, latitude));
                    CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(breda, 15);

                    googleMap.moveCamera(center);
                    googleMap.animateCamera(zoom);
                }

                cursor.moveToFirst();

                while(!cursor.isAfterLast()) {
                    double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
                    double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));

                    googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(name));
                    cursor.moveToNext();
                }
            }
        }
    }
}

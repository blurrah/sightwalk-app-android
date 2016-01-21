package net.sightwalk.Controllers.Sight;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.sightwalk.Helpers.GPSTracker;
import net.sightwalk.Helpers.GPSTrackerInterface;
import net.sightwalk.Helpers.PermissionInterface;
import net.sightwalk.Models.Sight;
import net.sightwalk.R;
import net.sightwalk.Helpers.PermissionActivity;
import net.sightwalk.Stores.SightImageStore;
import net.sightwalk.Stores.SightStore;
import net.sightwalk.Tasks.CreateSightTask;
import net.sightwalk.Tasks.SightImageTask;
import net.sightwalk.Tasks.TaskInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class CreateSightActivity extends PermissionActivity implements GoogleMap.OnMapClickListener, GPSTrackerInterface, OnMapReadyCallback, View.OnClickListener, TaskInterface {

    static final int REQUEST_TAKE_PHOTO = 1;

    private EditText etTitle;
    private EditText etDescription;
    private GoogleMap googleMap;
    private Button btnAddSight;

    private Marker selectedMarker;
    private LatLng selectedLocation;

    private Boolean withPhoto;

    private Sight newSight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sight);

        bindUIControls();
        enableCamera();

        withPhoto = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // we've captured an image!
            withPhoto = true;
        }
        enableGPSTracking();
    }

    private void bindUIControls() {
        etTitle = (EditText) findViewById(R.id.etTitle);
        etDescription = (EditText) findViewById(R.id.etDescription);
        btnAddSight = (Button) findViewById(R.id.btnAddSight);

        btnAddSight.setOnClickListener(this);

        bindGoogleMaps();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = SightImageStore.getSingleInstance(this).getOutputMediaFile("_");

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void bindGoogleMaps() {
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.smfPickSight)).getMapAsync(this);
    }

    private void enableGPSTracking() {
        validateGranted(Manifest.permission.ACCESS_FINE_LOCATION, new PermissionInterface() {
            @Override
            public void granted(String... permission) {
                updateGPSTracking();
            }

            @Override
            public void denied(String... permission) {
                Log.d("CreateSightActivity", "GPS permission not granted");
            }
        });
    }

    private void enableCamera() {
        validateGranted(Manifest.permission.CAMERA, new PermissionInterface() {
            @Override
            public void granted(String... permission) {
                openCamera();
            }

            @Override
            public void denied(String... permission) {
                Log.d("CreateSightActivity", "Camera permission not granted");
            }
        });
    }

    private void updateGPSTracking() {
        if (googleMap != null) {
            try {
                googleMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
                Log.d("CreateSightActivity", "failed to enable map location tracker");
            }
        }
    }

    @Override
    public void onMapClick(LatLng location) {
        if (selectedMarker != null) {
            selectedMarker.remove();
            selectedMarker = null;
        }

        selectedMarker = googleMap.addMarker(
                new MarkerOptions().position(location).title("selected location")
        );

        CameraUpdate center = CameraUpdateFactory.newLatLng(location);
        CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(location, 15);

        googleMap.moveCamera(center);
        googleMap.animateCamera(zoom);

        selectedLocation = location;

        return;
    }

    @Override
    public void updatedLocation(Location location) {
        if (selectedLocation == null) {
            onMapClick(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    @Override
    public void onMapReady(GoogleMap gm) {
        googleMap = gm;
        googleMap.setOnMapClickListener(this);
        enableGPSTracking();
        new GPSTracker(this, this);
    }

    @Override
    public void onClick(View v) {
        if (selectedLocation == null) {
            Toast.makeText(this, "Selecteer een locatie a.u.b.", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String shortDescription = description.substring(0, description.length() > 100 ? 100 : description.length());

        if (title.length() == 0 || description.length() == 0) {
            Toast.makeText(this.getApplicationContext(), "Voer een title en beschrijving in", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // create the new sight
            newSight = new Sight(0, "monument", selectedLocation.latitude, selectedLocation.longitude, title, title, description, "-", shortDescription);

            CreateSightTask task = new CreateSightTask(newSight, this);
            task.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(JSONObject data) {
        Toast.makeText(this.getApplicationContext(), "Sight is toegevoegd", Toast.LENGTH_SHORT).show();

        try {
            Integer id = data.getInt("sight_id");
            newSight.id = id;
            SightStore.getSharedInstance(this).triggerAddSight(newSight);

            if (!withPhoto) {
                // no photo selected, just return
                SightStore.getSharedInstance(this).sync(this);
                this.finish();
                return;
            }

            final Context self = this;

            File file = SightImageStore.getSingleInstance(this).scaleAndCompressImage("_", Integer.toString(id) + "compressed");
            SightImageTask sit = new SightImageTask(this, Integer.toString(id), new TaskInterface() {
                @Override
                public void onSuccess(JSONObject data) {
                    newSight.image = "https://sightwalk.net/sight/" + newSight.id + "/photo";
                    SightStore.getSharedInstance(self).triggerUpdateSight(newSight, newSight);
                }

                @Override
                public void onFailure(int errorCode) {
                    Log.d("CreateSightActivity", "photo adding failed" + String.valueOf(errorCode));
                }
            });
            sit.execute(file);
        } catch (JSONException e) {
            Log.d("CreateSightActivity", "malformed onsuccess");
        }

        this.finish();
    }

    @Override
    public void onFailure(int errorCode) {
        Toast.makeText(this.getApplicationContext(), "Er trad een fout op: " + Integer.toString(errorCode), Toast.LENGTH_SHORT).show();
    }
}
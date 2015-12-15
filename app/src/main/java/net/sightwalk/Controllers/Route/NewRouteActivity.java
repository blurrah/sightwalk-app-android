package net.sightwalk.Controllers.Route;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import net.sightwalk.Controllers.SettingsActivity;
import net.sightwalk.Helpers.LocationHelper;
import net.sightwalk.Models.Sights;
import net.sightwalk.Models.UserLocation;
import net.sightwalk.R;
import net.sightwalk.Tasks.RouteTask;

public class NewRouteActivity extends AppCompatActivity {

    private LocationHelper helper;
    private String waypoint;
    private StringBuilder builder;

    CheckBox checkBox;

    LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        helper = new LocationHelper(this);
        helper.locationManager();

        checkBox = (CheckBox) findViewById(R.id.cbRouteDestination);

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
        amountSights.setText("Totaal "+ Sights.mSightList.size() +" sights");

        if(Sights.mSightList.size() == 0){
            routeButton.setEnabled(false);
            routeButton.setBackgroundColor(getResources().getColor(R.color.colorDisabled));
        } else {
            routeButton.setEnabled(true);
            routeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            builder = new StringBuilder();

            for(int i = 0; i < Sights.mSightList.size(); i++) {
                String latitude = Sights.mSightList.get(i).getString(Sights.mSightList.get(i).getColumnIndex("latitude"));
                String longitude = Sights.mSightList.get(i).getString(Sights.mSightList.get(i).getColumnIndex("longitude"));

                builder.append(latitude + "," + longitude + "|");
            }

            waypoint = builder.toString();

            if(checkBox.isChecked()) {
                location = UserLocation.getInstance().userlocation;
            }
            else {
                int last = Sights.mSightList.size() -1;
                Double latitude = Sights.mSightList.get(last).getDouble(Sights.mSightList.get(last).getColumnIndex("latitude"));
                Double longitude = Sights.mSightList.get(last).getDouble(Sights.mSightList.get(last).getColumnIndex("longitude"));
                location = new LatLng(latitude,longitude);
            }

            final RouteTask routeTask = new RouteTask(UserLocation.getInstance().userlocation, location.latitude + "," + location.longitude, waypoint.toString(), "walking", "nl", this, this);
            routeTask.execute();
        }
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


    public interface OnDataChangeListener{
        public void onDataChanged(int size);
    }

    private OnDataChangeListener mOnDataChangeListener;

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }
}
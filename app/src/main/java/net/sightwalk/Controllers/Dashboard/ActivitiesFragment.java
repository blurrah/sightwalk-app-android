package net.sightwalk.Controllers.Dashboard;

import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import net.sightwalk.Controllers.Activity.CheckActivity;
import net.sightwalk.Controllers.Activity.StartDialogFragment;
import net.sightwalk.Controllers.Introduction.MainActivity;
import net.sightwalk.Controllers.Route.NewRouteActivity;
import net.sightwalk.Helpers.ActivitiesAdapter;
import net.sightwalk.Models.Route;
import net.sightwalk.Models.Sight;
import net.sightwalk.R;
import net.sightwalk.Stores.RouteDBHandler;
import net.sightwalk.Stores.RouteStore;
import net.sightwalk.Stores.SightSelectionStore;
import net.sightwalk.Tasks.RouteTask;

import java.util.ArrayList;

public class ActivitiesFragment extends Fragment implements AdapterView.OnItemClickListener{

    private View view;
    private ActivitiesAdapter activitiesAdapter;
    private RouteDBHandler db;
    private ListView activityList;
    private RouteStore routeStore;

    private LocationManager locationManager;
    private ConnectivityManager connectivityManager;
    private NetworkInfo[] activeNetworkInfo;
    private Settings setting = new Settings();

    boolean GPS;
    boolean internetGPS;
    boolean internet = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activities, container, false);

        Button rButton = (Button) view.findViewById(R.id.routeButton);
        rButton.setOnClickListener(new routeListener());

        populateActivityList(view);


        locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
        connectivityManager = (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getAllNetworkInfo();


        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        populateActivityList(view);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor cursor = (Cursor) activityList.getItemAtPosition(i);
        String json = cursor.getString(cursor.getColumnIndex("routeJson"));

        routeStore = RouteStore.getSharedInstance(getContext());
        Route route = routeStore.parseRoute(cursor);
        LatLng startPosition = new LatLng(route.sights.get(0).latitude, route.sights.get(0).longitude);
        LatLng endPosition = new LatLng(route.sights.get(route.sights.size() -1).latitude, route.sights.get(route.sights.size() -1).longitude);

        SightSelectionStore sightSelectionStore = SightSelectionStore.getSharedInstance(getContext());

        for(Sight sight : route.sights) {
            sightSelectionStore.setSelected(sight);
        }

        String waypoint = selectedWaypoints(route.sights);

        final RouteTask routeTask = new RouteTask(startPosition, endPosition.latitude + "," + endPosition.longitude, waypoint, "walking", "nl", getContext(), getActivity());
        routeTask.execute();

        showStartDialog();
    }

    public void showStartDialog(){
        //FragmentManager fm = getActivity().getFragmentManager();
        StartDialogFragment dialogFragment = StartDialogFragment.newInstance();
        dialogFragment.show(getActivity().getFragmentManager(), "Sample Fragment");
    }

    private String selectedWaypoints(ArrayList<Sight> sights){
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < sights.size(); i++) {
            Sight sight = sights.get(i);
            builder.append(Double.toString(sight.latitude) + "," + Double.toString(sight.longitude) + "|");
        }

        return builder.toString();
    }

    private class routeListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            GPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            internetGPS = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            for(NetworkInfo networkInfo : activeNetworkInfo) {
                if(networkInfo.getState()== NetworkInfo.State.CONNECTED) {
                    internet = true;
                    break;
                }
            }

            if ((internet == true) & (GPS == true | internetGPS == true)) {
                Intent i = new Intent(view.getContext(), NewRouteActivity.class);
                startActivity(i);
                getActivity().finish();
            }
            else if (GPS == false || internetGPS == false) {
                showGPSSettingsAlert();
            }
            else if (internet == false) {
                showWIFISettingsAlert();
            }
            else {
                Toast.makeText(getContext(), "We hebben geen toegang tot je locatie en/of internet!", Toast.LENGTH_LONG).show();
            }
        }
    }


    public Cursor getActivityCursor(){

        //Set database in this activity
        db = new RouteDBHandler(getActivity().getApplicationContext());
        Cursor cursor = db.getActivities();

        return cursor;
    }

    public void populateActivityList(View rootView) {
        activityList = (ListView) rootView.findViewById(R.id.activitiesListView);

        activitiesAdapter = new ActivitiesAdapter(getActivity(), getActivityCursor(), false);

        activityList.setAdapter(activitiesAdapter);
        activityList.setOnItemClickListener(this);
    }

    public void showGPSSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        alertDialog.setTitle("GPS instellingen");
        alertDialog.setMessage("Om deze functie te gebruiken hebben we je locatie nodig. Zet alsjeblieft je internet of GPS modus aan in de locatie opties.");
        alertDialog.setPositiveButton("Instellingen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Annuleren", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void showWIFISettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        alertDialog.setTitle("Internet instellingen");
        alertDialog.setMessage("Om deze functie te gebruiken is er een internetverbinding nodig!");
        alertDialog.setPositiveButton("Instellingen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Annuleren", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();

    }
}
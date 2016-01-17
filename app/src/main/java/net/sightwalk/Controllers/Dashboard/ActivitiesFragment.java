package net.sightwalk.Controllers.Dashboard;

import android.content.Intent;
import android.database.Cursor;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import net.sightwalk.Controllers.Activity.StartDialogFragment;
import net.sightwalk.Controllers.Route.NewRouteActivity;
import net.sightwalk.Helpers.ActivitiesAdapter;
import net.sightwalk.Models.Legs;
import net.sightwalk.Models.Polyline;
import net.sightwalk.Models.Route;
import net.sightwalk.Models.Sight;
import net.sightwalk.Models.Steps;
import net.sightwalk.R;
import net.sightwalk.Stores.RouteDBHandler;
import net.sightwalk.Stores.RouteStore;
import net.sightwalk.Stores.SightSelectionStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private String activeActivityJson;

    private Steps step;
    private Polyline line;
    private Legs leg;


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
        activeActivityJson = cursor.getString(cursor.getColumnIndex("routeJson"));

        routeStore = RouteStore.getSharedInstance(getContext());
        Route route = routeStore.parseRoute(cursor);

        SightSelectionStore sightSelectionStore = SightSelectionStore.getSharedInstance(getContext());

        for(Sight sight : route.sights) {
            sightSelectionStore.setSelected(sight);
        }

        getRouteData(activeActivityJson);

        showStartDialog();
    }

    public void showStartDialog(){
        //FragmentManager fm = getActivity().getFragmentManager();
        StartDialogFragment dialogFragment = StartDialogFragment.newInstance();
        dialogFragment.show(getActivity().getFragmentManager(), "Sample Fragment");
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
}
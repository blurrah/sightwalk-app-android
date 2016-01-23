package net.sightwalk.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import net.sightwalk.Controllers.Route.NewRouteActivity;
import net.sightwalk.Models.Legs;
import net.sightwalk.Models.Polyline;
import net.sightwalk.Models.Steps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RouteTask extends AsyncTask<String, Void, String> {

    private Steps step;
    private Polyline line;
    private Legs leg;

    private Context context;
    private NewRouteActivity activity;

    private URL url;
    private HttpURLConnection urlConn;
    private String result = null;

    private LatLng origin;
    private String destination;
    private String waypoints;
    private String mode;
    private String language;

    public RouteTask(LatLng origin, String destination, String waypoints, String mode, String language, Context context, NewRouteActivity activity) {
        this.origin = origin;
        this.destination = destination;
        this.waypoints = waypoints;
        this.mode = mode;
        this.language = language;
        this.context = context;
        this.activity = activity;

        step.stepsArrayList = new ArrayList<>();
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject jsonParam = new JSONObject();

        try {
            url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude +","+ origin.longitude + "&destination=" + destination + "&waypoints=" + waypoints + "&mode=" + mode + "&language=" + language + "");

            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Accept", "application/json");

            OutputStream os = new BufferedOutputStream(urlConn.getOutputStream());
            os.write(jsonParam.toString().getBytes("UTF-8"));
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            result = response.toString();

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected void onPostExecute(String response) {

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
                activity.onRouteCalculated(routedis, routedur);
            }
        } catch (JSONException ex) {
            Log.e("ERROR_", ex.getLocalizedMessage());
        }
    }
}
package net.sightwalk.Tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

    private ArrayList<Steps> stepsArrayList = new ArrayList<Steps>();
    private Steps step;

    URL url;
    HttpURLConnection urlConn;
    private String result = null;

    String poly;

    private RetrievePolyline listener = null;

    public RouteTask(RetrievePolyline listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject jsonParam = new JSONObject();

        try {
            url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin=Etten-Leur,vijfkamp&destination=Breda-centrum&mode=walking&language=NL");

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
            JSONObject directionsObject = new JSONObject(response);

            JSONArray routesObject = directionsObject.getJSONArray("routes");

            for (int i = 0; i < routesObject.length(); i++) {
                JSONObject overview = routesObject.getJSONObject(i);

                JSONObject polyline = overview.getJSONObject("overview_polyline");

                poly = polyline.getString("points");

                JSONArray legsObject = overview.getJSONArray("legs");

                for(int l = 0; l < legsObject.length(); l++) {
                    JSONObject stepsobject = legsObject.getJSONObject(l);

                    JSONArray steps = stepsobject.getJSONArray("steps");

                    for(int s = 0; s < steps.length(); s++) {
                        JSONObject route = steps.getJSONObject(s);

                        JSONObject distance = route.getJSONObject("distance");
                        JSONObject duration = route.getJSONObject("duration");
                        //JSONObject instruction = route.getJSONObject("html_instructions");

                        String dis = distance.getString("text");
                        String dur = duration.getString("text");
                        String in = route.getString("html_instructions");

                        step = new Steps();
                        step.setDistance(dis);
                        step.setDuration(dur);
                        step.setHtml_instructions(in);

                        stepsArrayList.add(step);
                    }
                }
            }
            listener.RetrievePolyline(poly, stepsArrayList);

        } catch (JSONException ex) {
            Log.e("ERROR_", ex.getLocalizedMessage());
        }
    }

    public interface RetrievePolyline {
        void RetrievePolyline(String poly, ArrayList<Steps> stepsArrayList);
    }
}

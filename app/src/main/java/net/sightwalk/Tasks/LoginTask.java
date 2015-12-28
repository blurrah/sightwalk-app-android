package net.sightwalk.Tasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import net.sightwalk.Controllers.Dashboard.DashboardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginTask extends AsyncTask<String, Void, String> {

    URL url;
    HttpURLConnection urlConn;
    private String result = null;

    private Context appContext;
    private String username;
    private String password;
    private String instance_id;

    public LoginTask(Context appContext, String username, String password, String instance_id){
        this.appContext = appContext;
        this.username = username;
        this.password = password;
        this.instance_id = instance_id;
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject jsonParam = new JSONObject();

        try {
            url = new URL("https://sightwalk.net/auth/login");

            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Accept", "application/json");

            jsonParam.put("username", username);
            jsonParam.put("password", password);
            jsonParam.put("instance_id", instance_id);

            OutputStream os = new BufferedOutputStream(urlConn.getOutputStream());
            os.write(jsonParam.toString().getBytes("UTF-8"));
            os.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(urlConn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            result = response.toString();

            in.close();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected void onPostExecute(String response) {

        JSONObject jsonObject;

        if (response.isEmpty()) {
            Toast.makeText(appContext, "Oeps! Er is iets fouts gegaan aan onze kant, probeer het overal een aantal minuten opnieuw.", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                jsonObject = new JSONObject(response);
                Boolean requestStatus = jsonObject.getBoolean("success");

                if (requestStatus) {
                    String requestToken = jsonObject.getString("token");

                    SharedPreferences pref = appContext.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("USERNAME", username);
                    editor.putString("TOKEN", requestToken);
                    AuthenticatedAsyncNetworkTask.setAuthenticationToken(requestToken);
                    editor.commit();

                    Intent i = new Intent(appContext, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    appContext.startActivity(i);
                } else {
                    int errorCode = jsonObject.getInt("error");

                    switch (errorCode) {
                        case -1:
                            Toast.makeText(appContext, "Onbekende fout opgetreden.", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(appContext, "Inloggegevens kloppen niet.", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(appContext, "Er missen gegevens.", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(appContext, "Onbekende fout opgetreden.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException ex) {
                Log.e("ERROR_", ex.getLocalizedMessage());
            }
        }
    }
}
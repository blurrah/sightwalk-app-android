package net.sightwalk.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterTask extends AsyncTask<String, Void, String> {

    URL url;
    HttpURLConnection urlConn;
    private String result = null;

    private Context appContext;

    private String username;
    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String weight;
    private String length;
    private String birthdate;
    private String instance_id;

    public RegisterTask(Context appContext, String username, String email, String password, String firstname, String lastname, String weight, String length, String birthdate, String instance_id) {
        this.appContext = appContext;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.weight = weight;
        this.length = length;
        this.birthdate = birthdate;
        this.instance_id = instance_id;
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject jsonParam = new JSONObject();

        try {
            url = new URL("http://sightwalk.net/user");

            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Accept", "application/json");

            jsonParam.put("username", username);
            jsonParam.put("email", email);
            jsonParam.put("password", password);
            jsonParam.put("firstname", firstname);
            jsonParam.put("lastname", lastname);
            jsonParam.put("weight", weight);
            jsonParam.put("length", length);
            jsonParam.put("birthdate", birthdate);

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

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected void onPostExecute(String response) {

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(response);
            Boolean requestStatus = jsonObject.getBoolean("success");

            if(requestStatus){
                LoginTask loginTask = new LoginTask(appContext, username, password, instance_id);
                loginTask.execute();
            } else {
                int errorCode = jsonObject.getInt("error");

                switch(errorCode) {
                    case -1:
                        Toast.makeText(appContext, "Onbekende fout opgetreden.", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(appContext, "Er missen gegevens.", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(appContext, "E-mailadres klopt niet (is deze al in gebruik?).", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(appContext, "Wachtwoord klopt niet.", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        Toast.makeText(appContext, "Gebruikersnaam is al in gebruik.", Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        Toast.makeText(appContext, "Gewicht klopt niet.", Toast.LENGTH_SHORT).show();
                        break;
                    case 7:
                        Toast.makeText(appContext, "Lengte klopt niet.", Toast.LENGTH_SHORT).show();
                        break;
                    case 8:
                        Toast.makeText(appContext, "Geboortedatum klopt niet.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(appContext, "Onbekende fout opgetreden.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch( JSONException ex) {
            Log.e("ERROR_", ex.getLocalizedMessage());
        }
    }
}
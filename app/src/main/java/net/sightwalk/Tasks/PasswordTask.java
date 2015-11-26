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

public class PasswordTask extends AsyncTask<String, Void, String> {

    URL url;
    HttpURLConnection urlConn;
    private String result = null;

    private Context appContext;
    private String email;

    public PasswordTask(Context appContext, String email){
        this.appContext = appContext;
        this.email = email;
    }

    @Override
    protected String doInBackground(String... params) {
        JSONObject jsonParam = new JSONObject();

        try {
            url = new URL("http://sightwalk.net/auth/password-reset");

            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Accept", "application/json");

            jsonParam.put("email", email);

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

            } else {
                int errorCode = jsonObject.getInt("error");

                switch(errorCode) {
                    case 3:
                        Toast.makeText(appContext, "E-mailadres onbekend.", Toast.LENGTH_SHORT).show();
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
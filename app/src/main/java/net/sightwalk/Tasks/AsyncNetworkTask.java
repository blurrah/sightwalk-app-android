package net.sightwalk.Tasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

abstract public class AsyncNetworkTask extends AsyncTask<JSONObject, Void, JSONObject> {

    public final static String SERVER = "https://sightwalk.net/";

    protected static String METHOD_GET = "GET";
    protected static String METHOD_POST = "POST";

    abstract protected String getPath();

    abstract protected String getMethod();

    protected JSONObject getParams() {
        return new JSONObject();
    }

    protected void appendOutputStream(OutputStream stream) {

    }

    private TaskInterface callback;

    HttpURLConnection urlConn;
    private String result = null;

    public AsyncNetworkTask(TaskInterface callback) {
        super();

        this.callback = callback;
    }

    @Override
    final protected JSONObject doInBackground(JSONObject... params) {
        String path = SERVER + this.getPath();
        JSONObject data = new JSONObject();

        try {
            data.put("success", false);

            URL url = new URL(path);
            String method = getMethod();
            HttpURLConnection connection = openConnection(url, method);

            if (!method.equals(METHOD_GET)) {
                openStream(connection);
            }

            data = readInputStreamJSON(connection);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

    final protected void onPostExecute(JSONObject data) {
        int errorCode = -1;
        try {
            if (data.getBoolean("success") == true) {
                callback.onSuccess(data);
                return;
            }

            errorCode = data.getInt("error");
        } catch (JSONException e) {
            // invalid json
        }
        callback.onFailure(errorCode);
    }

    protected HttpURLConnection openConnection(URL url, String method) throws IOException {
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setDoInput(true);

        if (method.equals(METHOD_POST)) {
            urlConn.setDoOutput(true);
        }

        urlConn.setUseCaches(false);
        urlConn.setRequestMethod(method);
        urlConn.setRequestProperty("Content-Type", "application/json");
        urlConn.setRequestProperty("Accept", "application/json");

        return urlConn;
    }

    protected final void openStream(HttpURLConnection connection) throws IOException {
        OutputStream os = new BufferedOutputStream(connection.getOutputStream());
        writeParameters(os, connection.getRequestMethod());

        // allow modification
        appendOutputStream(os);

        // close stream
        os.close();
    }

    private void writeParameters(OutputStream stream, String method) throws IOException {
        JSONObject parameters = getParams();

        if (!method.equals(METHOD_GET) && parameters.length() > 0) {
            // dont write stream on get connections
            stream.write(parameters.toString().getBytes("UTF-8"));
        }
    }

    private JSONObject readInputStreamJSON(HttpURLConnection connection) throws IOException, JSONException {
        return new JSONObject(readInputStream(connection));
    }

    private String readInputStream(HttpURLConnection connection) {
        BufferedReader in;
        String result = "{\"success\":false}";
        try {
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                InputStream x = connection.getInputStream();
                in = new BufferedReader(new InputStreamReader(x));

                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                result = response.toString();

                in.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
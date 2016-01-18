package net.sightwalk.Tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

abstract public class FileUploadTask extends AsyncTask<File, Void, JSONObject> {

    private URL connectURL;
    private String responseString;
    private String Title;
    private String Description;
    private byte[] dataToServer;
    private FileInputStream fileInputStream = null;
    private TaskInterface callback;

    abstract protected String getPath();

    abstract protected String getTitle();

    abstract protected String getDescription();

    protected FileUploadTask(TaskInterface cb) {
        callback = cb;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        String path = AsyncNetworkTask.SERVER + this.getPath();
        Title = getTitle();
        Description = getDescription();

        try {
            connectURL = new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    final protected JSONObject doInBackground(File... params) {
        if (params.length > 0) {
            File file1 = params[0];
            FileInputStream fis = readImage(file1);

            if (fis != null) {
                return sendFile(fis);
            }
        }

        return new JSONObject();
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

    private JSONObject sendFile(FileInputStream fStream) {
        fileInputStream = fStream;
        JSONObject data = performSending();
        return data;
    }

    private JSONObject performSending() {
        String iFileName = "image.jpg";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "fSnd";

        try {
            Log.e(Tag, "Starting Http File Sending to URL");
            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Authtoken", AuthenticatedAsyncNetworkTask.getAuthenticationToken());
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            // send stream
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"title\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Title);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"description\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Description);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + Title + "\";filename=\"" + iFileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            Log.e(Tag, "Headers are written");

            // create a buffer of maximum size
            int bytesAvailable = fileInputStream.available();

            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            fileInputStream.close();

            dos.flush();

            Log.e(Tag, "File Sent, Response: " + String.valueOf(conn.getResponseCode()));

            return readInputStreamJSON(conn);
        } catch (MalformedURLException ex) {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
        } catch (JSONException e) {
            Log.e(Tag, "JSON error: " + e.getMessage(), e);
        }

        return new JSONObject();
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

    public FileInputStream readImage(File file) {
        if (file != null) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                Log.d("SightImageStore", "could not read file");
            }
        }
        return null;
    }
}
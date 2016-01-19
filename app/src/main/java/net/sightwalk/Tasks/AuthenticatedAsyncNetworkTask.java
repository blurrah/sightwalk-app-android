package net.sightwalk.Tasks;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

abstract public class AuthenticatedAsyncNetworkTask extends AsyncNetworkTask {

    private static String AuthenticationToken;

    public AuthenticatedAsyncNetworkTask(TaskInterface callback) {
        super(callback);
    }

    public static void setAuthenticationToken(String token) {
        AuthenticationToken = token;
    }

    public static void setAuthenticationToken(Context context) {
        SharedPreferences pref = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        AuthenticationToken = pref.getString("TOKEN", "");
    }

    public static String getAuthenticationToken() {
        return AuthenticationToken;
    }

    protected HttpURLConnection openConnection(URL url, String method) throws IOException {
        HttpURLConnection connection = super.openConnection(url, method);

        connection.setRequestProperty("Authtoken", AuthenticationToken);

        return connection;
    }
}
package net.sightwalk.Tasks;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.auth.api.Auth;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Frank on 12/27/2015.
 */
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

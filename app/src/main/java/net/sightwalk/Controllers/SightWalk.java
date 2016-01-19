package net.sightwalk.Controllers;

import android.app.Application;

import net.sightwalk.Tasks.AuthenticatedAsyncNetworkTask;

public class SightWalk extends Application {

    public SightWalk() { }

    @Override
    public void onCreate() {
        super.onCreate();

        // update authentication token
        AuthenticatedAsyncNetworkTask.setAuthenticationToken(getApplicationContext());
    }
}
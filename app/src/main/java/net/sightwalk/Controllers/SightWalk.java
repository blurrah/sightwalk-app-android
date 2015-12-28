package net.sightwalk.Controllers;

import android.app.Application;

import net.sightwalk.Tasks.AuthenticatedAsyncNetworkTask;

/**
 * Created by Frank on 12/28/2015.
 */
public class SightWalk extends Application {

    public SightWalk() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        // update authentication token
        AuthenticatedAsyncNetworkTask.setAuthenticationToken(getApplicationContext());
    }

}

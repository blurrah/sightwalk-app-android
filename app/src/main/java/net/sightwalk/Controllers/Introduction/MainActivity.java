package net.sightwalk.Controllers.Introduction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.sightwalk.Controllers.Dashboard.DashboardActivity;
import net.sightwalk.Controllers.Tutorial.TutorialActivity;
import net.sightwalk.R;

public class MainActivity extends AppCompatActivity {

    private ConnectivityManager connectivityManager;
    private NetworkInfo[] activeNetworkInfo;
    private boolean internet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        activeNetworkInfo = connectivityManager.getAllNetworkInfo();

        for(NetworkInfo networkInfo : activeNetworkInfo) {
            if(networkInfo.getState()== NetworkInfo.State.CONNECTED) {
                internet = true;
                break;
            }
        }

        firstRun();
        skipLogin();

        Button rButton = (Button) findViewById(R.id.registerButton);
        rButton.setOnClickListener(new registerListener());

        Button iButton = (Button) findViewById(R.id.loginButton);
        iButton.setOnClickListener(new loginListener());
    }

    public void skipLogin(){
        SharedPreferences tokenPref = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        String loginToken = tokenPref.getString("TOKEN",null);

        if(loginToken != null){
            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void firstRun() {
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);

        if (isFirstRun) {
            Intent intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);
            finish();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();
        }
    }

    private class registerListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            if (internet) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);

                finish();

                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);
            }
            else {
                showWIFISettingsAlert();
            }
        }
    }

    private class loginListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            if (internet) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);

                finish();

                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out);
            }
            else {
                showWIFISettingsAlert();
            }
        }
    }

    public void showWIFISettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Internet instellingen");
        alertDialog.setMessage("Om deze functie te gebruiken is er een internetverbinding nodig!");
        alertDialog.setPositiveButton("Instellingen", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Annuleren", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}
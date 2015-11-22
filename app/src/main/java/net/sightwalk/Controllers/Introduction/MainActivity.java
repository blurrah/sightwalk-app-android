package net.sightwalk.Controllers.Introduction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import net.sightwalk.Controllers.Dashboard.DashboardActivity;
import net.sightwalk.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        }
    }

    private class registerListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(i);
        }
    }

    private class loginListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        }
    }
}
package it.sightwalk.Controllers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import it.sightwalk.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button rButton = (Button) findViewById(R.id.registerButton);
        rButton.setOnClickListener(new registerListener());
    }

    private class registerListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(i);
        }
    }
}
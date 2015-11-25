package net.sightwalk.Controllers.Introduction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.iid.InstanceID;

import net.sightwalk.R;
import net.sightwalk.Tasks.LoginTask;

public class LoginActivity extends AppCompatActivity {

    EditText usernameField;
    EditText passwordField;
    String instanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usernameField = (EditText) findViewById(R.id.usernameEditText);
        passwordField = (EditText) findViewById(R.id.passwordEditText);
        instanceId = InstanceID.getInstance(this).getId();

        Button iButton = (Button) findViewById(R.id.loginButton);
        iButton.setOnClickListener(new loginListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class loginListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            postLogin();
        }
    }

    public void postLogin(){
        //get message from message box
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        //check whether the msg empty or not
        if(username.length()>0 && password.length()>0) {

            LoginTask loginTask = new LoginTask(getApplicationContext(),username, password, instanceId);
            loginTask.execute();
        } else {
            //display message if text field is empty
            Toast.makeText(getBaseContext(), "Alle velden moeten worden ingevuld!", Toast.LENGTH_SHORT).show();
        }
    }
}
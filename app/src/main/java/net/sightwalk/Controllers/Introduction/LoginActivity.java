package net.sightwalk.Controllers.Introduction;

import android.app.FragmentManager;
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

    private EditText usernameField;
    private EditText passwordField;
    private String instanceId;

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

        Button pButton = (Button) findViewById(R.id.passwordForgottenButton);
        pButton.setOnClickListener(new passwordListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                overridePendingTransition(R.anim.activity_return_in, R.anim.activity_return_out);
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

    private class passwordListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            FragmentManager fm = getFragmentManager();
            PasswordDialogFragment dialogFragment = new PasswordDialogFragment();
            dialogFragment.show(fm, "Sample Fragment");
        }
    }

    public void postLogin(){
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        if(username.length()>0 && password.length()>0) {
            LoginTask loginTask = new LoginTask(getApplicationContext(),username, password, instanceId);
            loginTask.execute();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Alle velden moeten worden ingevuld!", Toast.LENGTH_SHORT).show();
        }
    }
}
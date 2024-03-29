package net.sightwalk.Controllers.Introduction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.iid.InstanceID;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.*;

import net.sightwalk.R;
import net.sightwalk.Tasks.RegisterTask;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity implements Validator.ValidationListener {

    private Validator validator;

    private String instanceId;

    @NotEmpty(message = "Verplicht veld")
    @Email(message = "Ongeldige email")
    @Bind(R.id.emailEditText) EditText email;

    @NotEmpty(message = "Verplicht veld")
    @Length(min = 3, max = 10, message = "Gebruikersnaam tussen 3 en 10 karakters")
    @Bind(R.id.usernameEditText) EditText username;

    @NotEmpty(message = "Verplicht veld")
    @Length(min = 5, max = 20, message = "Wachtwoord tussen 5 en 20 karakters")
    @Password(scheme = Password.Scheme.ANY, message = "Ongeldige wachtwoord")
    @Bind(R.id.passwordEditText) EditText password;

    @Bind(R.id.ageEditText) EditText age;
    @Bind(R.id.weightEditText) EditText weight;
    @Bind(R.id.lengthEditText) EditText length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        instanceId = InstanceID.getInstance(this).getId();

        validator = new Validator(this);
        validator.setValidationListener(this);

        Button rButton = (Button) findViewById(R.id.registerButton);
        rButton.setOnClickListener(new registerListener());
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

    private class registerListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            validator.validate();
        }
    }

    @Override
    public void onValidationSucceeded() {
        postRegister();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void postRegister(){
        RegisterTask registerTask = new RegisterTask(
                getApplicationContext(),
                username.getText().toString(),
                email.getText().toString(),
                password.getText().toString(),
                "",
                "",
                weight.getText().toString(),
                length.getText().toString(),
                age.getText().toString(),
                instanceId);

        registerTask.execute();
    }
}
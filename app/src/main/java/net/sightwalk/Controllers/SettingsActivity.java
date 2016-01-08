package net.sightwalk.Controllers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import net.sightwalk.R;

public class SettingsActivity extends AppCompatActivity {

    private Boolean notificationEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        notificationEnabled = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("notificationEnabled", true);

        Switch reminder = (Switch) findViewById(R.id.reminderSwitch);
        reminder.setChecked(notificationEnabled);
        reminder.setOnCheckedChangeListener(new reminderListener());
    }

    private class reminderListener implements Switch.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("notificationEnabled", true).commit();
            }
            else {
                getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("notificationEnabled", false).commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                overridePendingTransition(R.anim.activity_return_in, R.anim.activity_return_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
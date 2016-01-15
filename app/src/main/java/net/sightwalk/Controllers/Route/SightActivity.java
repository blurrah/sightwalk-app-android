package net.sightwalk.Controllers.Route;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.sightwalk.Controllers.SettingsActivity;
import net.sightwalk.Models.Sight;
import net.sightwalk.R;
import net.sightwalk.Stores.SightSelectionStore;
import net.sightwalk.Stores.SightsInterface;

import org.w3c.dom.Text;

public class SightActivity extends AppCompatActivity implements SightsInterface{

    private TextView sightTitleTextView;
    private TextView sightDescriptionTextView;
    private ImageView sightImageView;
    private Sight sight;

    private SightSelectionStore selectionStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_sight);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectionStore = SightSelectionStore.getSharedInstance("RouteActivity", this);

        Bundle intentValues = getIntent().getExtras();
        sight = selectionStore.getActiveSight();

        sightTitleTextView = (TextView) findViewById(R.id.sightTitleTextView);
        sightDescriptionTextView = (TextView) findViewById(R.id.sightDescriptionTextView);
        sightImageView = (ImageView) findViewById(R.id.sightImageView);

        sightTitleTextView.setText(sight.title);
        sightDescriptionTextView.setText(sight.text);
        Picasso.with(getApplicationContext()).load(sight.image).into(sightImageView);
    }

    @Override
    public void onBackPressed(){
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void addedSight(Sight sight) {

    }

    @Override
    public void removedSight(Sight sight) {

    }

    @Override
    public void updatedSight(Sight oldSight, Sight newSight) {

    }
}
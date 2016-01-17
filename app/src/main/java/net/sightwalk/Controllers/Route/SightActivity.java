package net.sightwalk.Controllers.Route;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.sightwalk.Controllers.SettingsActivity;
import net.sightwalk.Models.Sight;
import net.sightwalk.R;
import net.sightwalk.Stores.SightSelectionStore;
import net.sightwalk.Stores.SightsInterface;

import org.w3c.dom.Text;

public class SightActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView sightTitleTextView;
    private TextView sightDescriptionTextView;
    private ImageView sightImageView;
    private Sight sight;
    private Button addFavourite;
    private Button removeFavourite;

    private SightSelectionStore selectionStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_sight);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectionStore = SightSelectionStore.getSharedInstance(this);

        sight = selectionStore.getActiveSight();

        sightTitleTextView = (TextView) findViewById(R.id.sightTitleTextView);
        sightDescriptionTextView = (TextView) findViewById(R.id.sightDescriptionTextView);
        sightImageView = (ImageView) findViewById(R.id.sightImageView);

        sightTitleTextView.setText(sight.title);
        sightDescriptionTextView.setText(sight.text);
        Picasso.with(getApplicationContext()).load(sight.image).into(sightImageView);

        addFavourite = (Button) findViewById(R.id.addFavouriteBtn);
        removeFavourite = (Button) findViewById(R.id.removeFavouriteBtn);
        addFavourite.setOnClickListener(this);
        removeFavourite.setOnClickListener(this);

        if(selectionStore.isFavourited(sight)){
            addFavourite.setVisibility(View.INVISIBLE);
            removeFavourite.setVisibility(View.VISIBLE);
        }else{
            addFavourite.setVisibility(View.VISIBLE);
            removeFavourite.setVisibility(View.INVISIBLE);
        }
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
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.addFavouriteBtn:
                addFavourite.setVisibility(View.INVISIBLE);
                removeFavourite.setVisibility(View.VISIBLE);
                selectionStore.AddFavourite(sight);
                break;
            case R.id.removeFavouriteBtn:
                addFavourite.setVisibility(View.VISIBLE);
                removeFavourite.setVisibility(View.INVISIBLE);
                selectionStore.RemoveFavourite(sight);
                break;
        }
    }
}
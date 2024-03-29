package net.sightwalk.Controllers.Tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import net.sightwalk.Controllers.Introduction.MainActivity;
import net.sightwalk.Helpers.TutorialAdapter;
import net.sightwalk.R;

import me.relex.circleindicator.CircleIndicator;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button skipBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        skipBtn = (Button) findViewById(R.id.btn_skip);
        viewPager = (ViewPager) findViewById(R.id.pager);

        TutorialAdapter adapter = new TutorialAdapter(getSupportFragmentManager(), 3);
        viewPager.setAdapter(adapter);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TutorialActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
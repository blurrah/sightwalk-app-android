package net.sightwalk.Controllers.Tutorial;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import net.sightwalk.Helpers.TutorialAdapter;
import net.sightwalk.R;

public class TutorialActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        viewPager = (ViewPager) findViewById(R.id.pager);
        TutorialAdapter adapter = new TutorialAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(adapter);
    }
}
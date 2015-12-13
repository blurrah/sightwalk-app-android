package net.sightwalk.Controllers.Route;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.sightwalk.Controllers.SettingsActivity;
import net.sightwalk.Models.Cheeses;
import net.sightwalk.R;

public class NewRouteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_route);

        Button rButton = (Button) findViewById(R.id.routeButton);
        rButton.setOnClickListener(new routeListener());

        Button cButton = (Button) findViewById(R.id.chooseRouteButton);
        cButton.setOnClickListener(new chooseListener());
    }

    @Override
    public void onResume(){
        super.onResume();

        Button routeButton = (Button) findViewById(R.id.routeButton);
        TextView amountSights = (TextView) findViewById(R.id.tvAmountSights);
        amountSights.setText("Totaal "+ Cheeses.mCheeseList.size() +" sights");

        if(Cheeses.mCheeseList.size() == 0){

            routeButton.setEnabled(false);
            routeButton.setBackgroundColor(getResources().getColor(R.color.colorDisabled));
        }else{
            routeButton.setEnabled(true);
            routeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class routeListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), RouteActivity.class);
            startActivity(i);
        }
    }

    private class chooseListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), ChooseRouteActivity.class);
            startActivity(i);
        }
    }


    public interface OnDataChangeListener{
        public void onDataChanged(int size);
    }

    private OnDataChangeListener mOnDataChangeListener;

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }
}
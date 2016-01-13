package net.sightwalk.Controllers.Dashboard;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import net.sightwalk.Controllers.Activity.CheckActivity;
import net.sightwalk.Controllers.Route.NewRouteActivity;
import net.sightwalk.Helpers.ActivitiesAdapter;
import net.sightwalk.R;
import net.sightwalk.Stores.RouteDBHandler;

public class ActivitiesFragment extends Fragment implements AdapterView.OnItemClickListener{

    private View view;
    private ActivitiesAdapter activitiesAdapter;
    private RouteDBHandler db;
    private ListView activityList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activities, container, false);

        Button rButton = (Button) view.findViewById(R.id.routeButton);
        rButton.setOnClickListener(new routeListener());
        populateActivityList(view);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        populateActivityList(view);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor k = (Cursor) activityList.getItemAtPosition(i);
        String json = k.getString(k.getColumnIndex("routeJson"));

        Intent intent = new Intent(getActivity(), CheckActivity.class);
        intent.putExtra("ROUTE_JSON", json);
        startActivity(intent);
    }

    private class routeListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(view.getContext(), NewRouteActivity.class);
            startActivity(i);
        }
    }

    public Cursor getActivityCursor(){

        //Set database in this activity
        db = new RouteDBHandler(getActivity().getApplicationContext());
        Cursor cursor = db.getActivities();

        return cursor;
    }

    public void populateActivityList(View rootView){
        activityList = (ListView) rootView.findViewById(R.id.activitiesListView);

        activitiesAdapter = new ActivitiesAdapter(getActivity(), getActivityCursor(), false);

        activityList.setAdapter(activitiesAdapter);
        activityList.setOnItemClickListener(this);
    }
}
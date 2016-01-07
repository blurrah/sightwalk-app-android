package net.sightwalk.Controllers.Dashboard;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import net.sightwalk.Controllers.Route.NewRouteActivity;
import net.sightwalk.Helpers.ActivitiesAdapter;
import net.sightwalk.R;
import net.sightwalk.Stores.RouteDBHandler;

public class ActivitiesFragment extends Fragment {

    private View view;
    private ActivitiesAdapter activitiesAdapter;
    private RouteDBHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activities, container, false);

        Button rButton = (Button) view.findViewById(R.id.routeButton);
        rButton.setOnClickListener(new routeListener());
        populateActivityList(view);
        return view;
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
        ListView activityList = (ListView) rootView.findViewById(R.id.activitiesListView);

        activitiesAdapter = new ActivitiesAdapter(getActivity(), getActivityCursor(), false);

        activityList.setAdapter(activitiesAdapter);
    }
}
package net.sightwalk.Controllers.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.sightwalk.Controllers.Route.NewRouteActivity;
import net.sightwalk.R;

public class ActivitiesFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_activities, container, false);

        Button rButton = (Button) view.findViewById(R.id.routeButton);
        rButton.setOnClickListener(new routeListener());

        return view;
    }

    private class routeListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(view.getContext(), NewRouteActivity.class);
            startActivity(i);
        }
    }
}
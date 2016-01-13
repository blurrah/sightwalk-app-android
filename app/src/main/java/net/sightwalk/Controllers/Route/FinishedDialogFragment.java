package net.sightwalk.Controllers.Route;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.sightwalk.Controllers.Route.RouteActivity;
import net.sightwalk.Models.Legs;
import net.sightwalk.R;
import net.sightwalk.Tasks.PasswordTask;

import java.util.Date;

public class FinishedDialogFragment extends DialogFragment {

    RouteActivity activity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_finishedroute_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);

        activity = (RouteActivity) getActivity();
        Integer distance =  Legs.getInstance().distance;
        String a = distanceConverter(distance);
        String b = elapsedTime(activity.startTime, activity.endTime);

        TextView routeStats = (TextView) view.findViewById(R.id.routeStats);
        routeStats.setText(a + b);

        builder.setPositiveButton("Route sluiten", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                activity.clearDataRouteActivity();
            }
        });

        builder.setNegativeButton("Annuleren", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });

        return builder.create();
    }

    private String distanceConverter(Integer distance){

        String outputString;

        if(distance > 1000){
            outputString = distance / 1000 + " km";
        }else {
            outputString = distance + " m";
        }

        return "Afstand: " + outputString;
    }

    private String elapsedTime(Date startDate, Date endDate){

        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;


        return " | Tijd: " + elapsedHours + " uur " + elapsedMinutes + " min.";
    }
}
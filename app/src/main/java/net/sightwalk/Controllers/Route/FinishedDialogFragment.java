package net.sightwalk.Controllers.Route;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.sightwalk.Models.Legs;
import net.sightwalk.Models.Sight;
import net.sightwalk.Models.Steps;
import net.sightwalk.R;
import net.sightwalk.Stores.SightSelectionStore;
import net.sightwalk.Stores.SightsInterface;

import java.util.ArrayList;
import java.util.Date;

public class FinishedDialogFragment extends DialogFragment implements SightsInterface {


    FinishedDialogFragment dialogFragment;

    public FinishedDialogFragment(){
        dialogFragment = this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.fragment_finishedroute_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);

        Integer distance =  Legs.getInstance().distance;
        String a = distanceConverter(distance);

        TextView routeStats = (TextView) view.findViewById(R.id.routeStats);
        routeStats.setText(a);

        builder.setPositiveButton("Route sluiten", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                Steps.getInstance().stepsArrayList = new ArrayList<>();

                SightSelectionStore.getSharedInstance(getActivity()).clearSelection();

                getActivity().finish();
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

    @Override
    public void addedSight(Sight sight) {

    }

    @Override
    public void removedSight(Sight sight) {

    }

    @Override
    public void updatedSight(Sight oldSight, Sight newSight) {

    }

    @Override
    public Context getApplicationContext() {
        return null;
    }
}
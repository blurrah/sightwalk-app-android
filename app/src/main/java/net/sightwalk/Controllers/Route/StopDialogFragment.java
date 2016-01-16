package net.sightwalk.Controllers.Route;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import net.sightwalk.Models.Sight;
import net.sightwalk.Models.Steps;
import net.sightwalk.R;
import net.sightwalk.Stores.SightSelectionStore;
import net.sightwalk.Stores.SightsInterface;

import java.util.ArrayList;

public class StopDialogFragment extends DialogFragment implements SightsInterface, DialogInterface.OnClickListener {

    StopDialogFragment dialogFragment;

    public StopDialogFragment(){
        dialogFragment = this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_stoproute_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);

        builder.setPositiveButton("Route Stoppen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                Steps.getInstance().stepsArrayList = new ArrayList<>();

                ArrayList<Sight> sights = SightSelectionStore.getSharedInstance("CheckActivity", dialogFragment).getSelectedSights();
                sights.clear();

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

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

    }
}
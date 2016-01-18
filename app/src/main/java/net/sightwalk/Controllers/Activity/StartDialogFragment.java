package net.sightwalk.Controllers.Activity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import net.sightwalk.Models.Sight;
import net.sightwalk.R;
import net.sightwalk.Stores.SightsInterface;

public class StartDialogFragment extends DialogFragment implements SightsInterface, DialogInterface.OnClickListener {

    StartDialogFragment dialogFragment;

    public StartDialogFragment(){
        dialogFragment = this;
    }

    public static StartDialogFragment newInstance(){
        StartDialogFragment startDialogFragment = new StartDialogFragment();

        return startDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_startroute_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);

        builder.setPositiveButton("Route Starten", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getActivity(), WalkActivity.class);
                startActivity(intent);
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
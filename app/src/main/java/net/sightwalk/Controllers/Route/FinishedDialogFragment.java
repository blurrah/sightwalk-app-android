package net.sightwalk.Controllers.Route;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.sightwalk.Controllers.Route.RouteActivity;
import net.sightwalk.R;
import net.sightwalk.Tasks.PasswordTask;

public class FinishedDialogFragment extends DialogFragment {

    EditText emailField;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_finishedroute_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);

        builder.setPositiveButton("Route sluiten", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                RouteActivity activity = (RouteActivity) getActivity();
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
}
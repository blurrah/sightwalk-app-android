package net.sightwalk.Controllers.Route;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.sightwalk.R;

public class NameRouteDialogFragment extends DialogFragment {

    private EditText nameField;
    private View view;
    private AlertDialog alert;
    private Button buttonPositive;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.fragment_nameroute_dialog, null);
        builder.setView(view);

        this.setCancelable(false);
        nameField = (EditText) view.findViewById(R.id.nameDialogEditText);

        builder.setPositiveButton("Start route", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String name = nameField.getText().toString();

                if (name.length() > 0) {
                    NewRouteActivity activity = (NewRouteActivity) getActivity();
                    activity.saveRoute(name);

                } else {
                    Toast.makeText(getActivity(), "Alle velden moeten worden ingevuld!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Annuleren", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

        buttonPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String name = nameField.getText().toString();
                if (name.length() > 0) {
                    buttonPositive.setEnabled(true);
                } else {
                    buttonPositive.setEnabled(false);
                }
            }
        });

        buttonPositive.setEnabled(false);
        return alert;
    }
}
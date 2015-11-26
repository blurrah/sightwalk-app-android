package net.sightwalk.Controllers.Introduction;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.sightwalk.R;
import net.sightwalk.Tasks.PasswordTask;

public class PasswordDialogFragment extends DialogFragment {

    EditText emailField;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.fragment_password_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(view);

        builder.setPositiveButton("Verzenden", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                emailField = (EditText) view.findViewById(R.id.emailDialogEditText);

                String email = emailField.getText().toString();

                if(email.length()>0) {
                    PasswordTask passwordTask = new PasswordTask(getActivity(), email);
                    passwordTask.execute();
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

        return builder.create();
    }
}
package net.sightwalk.Controllers.Route;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.sightwalk.Models.Cheeses;
import net.sightwalk.R;

import org.w3c.dom.Text;

/**
 * Created by Ruben on 10/12/2015.
 */
public class SightDialogFragment extends Fragment {

    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sight_dialog, container, false);

        return view;
    }

    public void refreshFragment(){
        Cursor active = Cheeses.activeCheese;

        TextView sightDescription = (TextView) view.findViewById(R.id.sightText);

        if(active != null) {
            sightDescription.setText(active.getString(active.getColumnIndex("name")));
        }
    }
}

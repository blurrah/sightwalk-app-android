package net.sightwalk.Helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.sightwalk.Models.Sight;
import net.sightwalk.R;

import java.util.ArrayList;

public class StableArrayAdapter extends ArrayAdapter<Sight> {

    final int INVALID_ID = -1;

    public StableArrayAdapter(Context context, int textViewResourceId, ArrayList<Sight> sights) {
        super(context, textViewResourceId, sights);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_new_route_list_item, parent, false);
        }

        TextView tvList = (TextView) convertView.findViewById(R.id.routeListText);
        tvList.setText(getItem(position).name);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= getCount()) {
            return INVALID_ID;
        }

        Sight item = getItem(position);
        return item.id;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
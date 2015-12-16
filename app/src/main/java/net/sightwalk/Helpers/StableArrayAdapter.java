package net.sightwalk.Helpers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import net.sightwalk.Models.Sights;
import net.sightwalk.Models.UserLocation;
import net.sightwalk.R;
import net.sightwalk.Tasks.RouteTask;

import java.util.HashMap;
import java.util.List;

public class StableArrayAdapter extends ArrayAdapter<Cursor> {

    final int INVALID_ID = -1;

    HashMap<Cursor, Integer> mIdMap = new HashMap<Cursor, Integer>();

    public StableArrayAdapter(Context context, int textViewResourceId, List<Cursor> objects) {
        super(context, textViewResourceId, objects);
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_new_route_list_item, parent, false);
        }

        TextView tvList = (TextView) convertView.findViewById(R.id.routeListText);
        tvList.setText(getItem(position).getString(getItem(position).getColumnIndex("name")));

        return convertView;
    }

        @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        Cursor item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

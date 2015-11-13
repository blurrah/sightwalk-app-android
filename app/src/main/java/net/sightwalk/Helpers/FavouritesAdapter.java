package net.sightwalk.Helpers;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import net.sightwalk.R;

public class FavouritesAdapter extends CursorAdapter {

    public FavouritesAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_row_activities, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Add binding for custom cell
    }
}
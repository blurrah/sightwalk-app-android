package net.sightwalk.Helpers;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.sightwalk.R;

public class FavouritesAdapter extends CursorAdapter {

    private Bitmap bitmap;
    private TextView favName;
    private TextView favDescription;
    private ImageView favImage;

    public FavouritesAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_favourites_list_item, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        favName = (TextView) view.findViewById(R.id.favouriteText);
        favDescription = (TextView) view.findViewById(R.id.favouriteDesc);
        favImage = (ImageView) view.findViewById(R.id.favouriteImage);

        String outputName = cursor.getString(cursor.getColumnIndex("name"));
        String outputDescription = cursor.getString(cursor.getColumnIndex("short_desc"));

        favName.setText(outputName);
        favDescription.setText(outputDescription);
        Picasso.with(context).load(cursor.getString(cursor.getColumnIndex("imgurl"))).into(favImage);
    }
}
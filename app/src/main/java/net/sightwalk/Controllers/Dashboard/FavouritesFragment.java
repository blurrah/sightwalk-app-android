package net.sightwalk.Controllers.Dashboard;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.sightwalk.Helpers.FavouritesAdapter;
import net.sightwalk.R;
import net.sightwalk.Stores.SightDBHandeler;

public class FavouritesFragment extends Fragment {

    private FavouritesAdapter favouriteAdapter;
    private SightDBHandeler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        populateFavouriteList(rootView);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    public Cursor getFavouriteCursor(){

        //Set database in this activity
        db = new SightDBHandeler(getActivity().getApplicationContext());
        Cursor cursor = db.getFavourites();

        return cursor;
    }

    public void populateFavouriteList(View rootView){
        ListView favouriteList = (ListView) rootView.findViewById(R.id.favouriteListView);

        favouriteAdapter = new FavouritesAdapter(getActivity(), getFavouriteCursor(), false);

        favouriteList.setAdapter(favouriteAdapter);
    }
}
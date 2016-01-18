package net.sightwalk.Controllers.Dashboard;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.sightwalk.Controllers.Route.SightActivity;
import net.sightwalk.Helpers.FavouritesAdapter;
import net.sightwalk.Models.Sight;
import net.sightwalk.R;
import net.sightwalk.Stores.SightDBHandeler;
import net.sightwalk.Stores.SightSelectionStore;

public class FavouritesFragment extends Fragment implements AdapterView.OnItemClickListener{

    private FavouritesAdapter favouriteAdapter;
    private SightDBHandeler db;
    private ListView favouriteList;
    private SightSelectionStore selectionStore;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        populateFavouriteList(rootView);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectionStore = SightSelectionStore.getSharedInstance(getContext());
    }

    @Override
    public void onResume(){
        super.onResume();
        populateFavouriteList(rootView);
    }

    public Cursor getFavouriteCursor(){
        //Set database in this activity
        db = new SightDBHandeler(getActivity().getApplicationContext());
        Cursor cursor = db.getFavourites();

        return cursor;
    }

    public void populateFavouriteList(View rootView){
        favouriteList = (ListView) rootView.findViewById(R.id.favouriteListView);

        favouriteAdapter = new FavouritesAdapter(getActivity(), getFavouriteCursor(), false);

        favouriteList.setAdapter(favouriteAdapter);
        favouriteList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor cursor = (Cursor) favouriteList.getItemAtPosition(i);
        Sight sight = selectionStore.parseSight(cursor);
        selectionStore.setActiveSight(sight);

        Intent intent = new Intent(getContext(), SightActivity.class);
        startActivity(intent);
    }
}
package net.sightwalk.Controllers.Route;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.sightwalk.Helpers.StableArrayAdapter;
import net.sightwalk.Models.Sights;
import net.sightwalk.R;

import java.util.ArrayList;

public class ListViewDraggingAnimation extends Fragment {

    private View view;
    private StableArrayAdapter adapter;
    private DynamicListView listView;
    private ArrayList<Cursor> mCheeseList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_new_route_list, container,false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCheeseList = Sights.getInstance().mSightList;

        adapter = new StableArrayAdapter(getContext(), R.layout.activity_new_route_list_item, mCheeseList);
        listView = (DynamicListView) view.findViewById(R.id.listview);

        listView.setCheeseList(mCheeseList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
}
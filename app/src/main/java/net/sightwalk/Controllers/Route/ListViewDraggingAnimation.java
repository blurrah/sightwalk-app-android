package net.sightwalk.Controllers.Route;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import net.sightwalk.Helpers.StableArrayAdapter;
import net.sightwalk.Models.Sight;
import net.sightwalk.R;

import java.util.ArrayList;

public class ListViewDraggingAnimation extends Fragment {

    private View view;
    private StableArrayAdapter adapter;
    private DynamicListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_new_route_list, container,false);
        listView = (DynamicListView) view.findViewById(R.id.listview);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        return view;
    }

    public void setSights(ArrayList<Sight> sights) {
        adapter = new StableArrayAdapter(getContext(), R.layout.activity_new_route_list_item, sights);
        listView.setAdapter(adapter);
        listView.setCheeseList(sights);
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.notifyDataSetChanged();
    }
}
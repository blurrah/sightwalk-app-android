package net.sightwalk.Controllers.Route;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.sightwalk.Helpers.StableArrayAdapter;
import net.sightwalk.Models.Cheeses;
import net.sightwalk.R;

import java.util.ArrayList;

public class ListViewDraggingAnimation extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_new_route_list, container,false);

        ArrayList<String>mCheeseList = new ArrayList<String>();
        for (int i = 0; i < Cheeses.sCheeseStrings.length; ++i) {
            mCheeseList.add(Cheeses.sCheeseStrings[i]);
        }

        StableArrayAdapter adapter = new StableArrayAdapter(getContext(), R.layout.activity_new_route_list_item, mCheeseList);
        DynamicListView listView = (DynamicListView) view.findViewById(R.id.listview);

        listView.setCheeseList(mCheeseList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        return view;
    }
}


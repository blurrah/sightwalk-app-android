package net.sightwalk.Controllers.Dashboard;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.sightwalk.Helpers.StatsAdapter;
import net.sightwalk.R;

import me.relex.circleindicator.CircleIndicator;

public class StatsFragment extends Fragment {

    private ViewPager viewPager;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_stats, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.pagerStats);

        StatsAdapter adapter = new StatsAdapter(getFragmentManager(), 3);
        viewPager.setAdapter(adapter);

        CircleIndicator indicator = (CircleIndicator) view.findViewById(R.id.indicatorStats);
        indicator.setViewPager(viewPager);

        return view;
    }
}
package net.sightwalk.Helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.sightwalk.Controllers.Dashboard.ActivitiesFragment;
import net.sightwalk.Controllers.Dashboard.FavouritesFragment;
import net.sightwalk.Controllers.Dashboard.StatsFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ActivitiesFragment tab1 = new ActivitiesFragment();
                return tab1;
            case 1:
                StatsFragment tab2 = new StatsFragment();
                return tab2;
            case 2:
                FavouritesFragment tab3 = new FavouritesFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
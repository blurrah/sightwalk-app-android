package it.sightwalk.Helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import it.sightwalk.Controllers.ActivitiesActivity;
import it.sightwalk.Controllers.FavouritesActivity;
import it.sightwalk.Controllers.StatsActivity;

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
                ActivitiesActivity tab1 = new ActivitiesActivity();
                return tab1;
            case 1:
                StatsActivity tab2 = new StatsActivity();
                return tab2;
            case 2:
                FavouritesActivity tab3 = new FavouritesActivity();
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
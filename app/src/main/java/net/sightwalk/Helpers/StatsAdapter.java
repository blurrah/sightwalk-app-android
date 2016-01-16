package net.sightwalk.Helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.sightwalk.Controllers.Dashboard.*;;

public class StatsAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public StatsAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                DayFragment day = new DayFragment();
                return day;
            case 1:
                WeekFragment week = new WeekFragment();
                return week;
            case 2:
                MonthFragment month = new MonthFragment();
                return month;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
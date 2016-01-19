package net.sightwalk.Helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.sightwalk.Controllers.Tutorial.*;

public class TutorialAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;

    public TutorialAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TutorialFragmentOne one = new TutorialFragmentOne();
                return one;
            case 1:
                TutorialFragmentTwo two = new TutorialFragmentTwo();
                return two;
            case 2:
                TutorialFragmentThree three = new TutorialFragmentThree();
                return three;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
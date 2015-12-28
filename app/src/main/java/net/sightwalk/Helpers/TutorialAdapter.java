package net.sightwalk.Helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import net.sightwalk.Controllers.Tutorial.TutorialFragment;

public class TutorialAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public TutorialAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TutorialFragment frag1 = new TutorialFragment();

                return frag1;
            case 1:
                TutorialFragment frag2 = new TutorialFragment();

                return frag2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
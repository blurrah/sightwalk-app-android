package net.sightwalk.Helpers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.TextView;

import net.sightwalk.Controllers.Tutorial.TutorialFragment;
import net.sightwalk.R;

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
                frag1.setTutorialImg(R.drawable.tutorial_1);
                return frag1;
            case 1:
                TutorialFragment frag2 = new TutorialFragment();
                frag2.setTutorialImg(R.drawable.tutorial_2);
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
package com.aile.tex;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Created by JW on 16. 3. 28..
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0 :
                Dashboard dashboard = new Dashboard();
                return dashboard;
            case 1 :
                History history = new History();
                return history;
            default :
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

package com.hasanyaman.guncelekonomi.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hasanyaman.guncelekonomi.Constants;
import com.hasanyaman.guncelekonomi.TabFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private String code;
    private boolean inCurrenyMode;

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public ViewPagerAdapter(FragmentManager fragmentManager, String code, boolean inCurrenyMode) {
        super(fragmentManager);
        this.code = code;
        this.inCurrenyMode = inCurrenyMode;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TabFragment.newInstance(this.code, Constants.DAILY_GRAPH, this.inCurrenyMode);
            case 1:
                return TabFragment.newInstance(this.code, Constants.WEEKLY_GRAPH, this.inCurrenyMode);
            case 2:
                return TabFragment.newInstance(this.code, Constants.MONTHLY_GRAPH, this.inCurrenyMode);
            case 3:
                return TabFragment.newInstance(this.code, Constants.YEARLY_GRAPH, this.inCurrenyMode);
            default:
                return TabFragment.newInstance(this.code, Constants.DAILY_GRAPH, this.inCurrenyMode);
        }

    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Günlük";
            case 1:
                return "Haftalık";
            case 2:
                return "Aylık";
            case 3:
                return "Yıllık";
            default:
                return "";
        }
    }
}

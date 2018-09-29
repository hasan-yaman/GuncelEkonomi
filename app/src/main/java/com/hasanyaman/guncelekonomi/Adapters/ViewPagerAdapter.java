package com.hasanyaman.guncelekonomi.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hasanyaman.guncelekonomi.TabFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return new TabFragment();
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
       switch (position) {
           case 0: return "Günlük";
           case 1: return "Haftalık";
           case 2: return "Aylık";
           case 3: return "Yıllık";
           default:return "";
       }
    }
}

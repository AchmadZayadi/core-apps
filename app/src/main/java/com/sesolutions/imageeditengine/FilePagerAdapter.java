package com.sesolutions.imageeditengine;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.droidninja.imageeditengine.BaseFrag;

import java.util.ArrayList;
import java.util.List;

public class FilePagerAdapter extends FragmentStatePagerAdapter {

    private final List<BaseFrag> fragmentList;
    //FragmentManager fragmentManager;

    public FilePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        this.fragmentList = new ArrayList<>();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return FragmentPagerAdapter.POSITION_NONE;
    }


    public void deleteItem(int position) {
        fragmentList.remove(position);
        notifyDataSetChanged();
    }

    public void addFragment(BaseFrag frag) {
        fragmentList.add(frag);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return fragmentList.size();
    }

    // Returns the fragment to display for that page
    @Override
    public BaseFrag getItem(int position) {
        return fragmentList.get(position);
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

}


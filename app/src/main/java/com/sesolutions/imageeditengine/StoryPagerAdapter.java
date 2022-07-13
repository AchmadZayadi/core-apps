package com.sesolutions.imageeditengine;


import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sesolutions.ui.storyview.StoryFragment;

import java.util.ArrayList;
import java.util.List;

public class StoryPagerAdapter extends FragmentStatePagerAdapter {
    private final List<StoryFragment> mFragmentList = new ArrayList<>();
    //private final List<StoryModel> stories;
    //private final OnUserClickedListener<Integer, Object> listener;
    //FragmentManager fragmentManager;

    public StoryPagerAdapter(FragmentManager fragmentManager/*, List<StoryModel> stories, OnUserClickedListener<Integer, Object> listener*/) {
        super(fragmentManager);
        //  this.fragmentManager = fragmentManager;
       /* this.stories = stories;
        this.listener = listener;*/
    }

   /* @Override
    public int getItemPosition(@NonNull Object object) {
        return FragmentPagerAdapter.POSITION_NONE;
    }*/

    public void addFragment(StoryFragment fragment) {
        mFragmentList.add(fragment);
    }

    public void deleteItem(int position) {
        // imagePaths.remove(position);
        notifyDataSetChanged();
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    // Returns the fragment to display for that page
    @Override
    public StoryFragment getItem(int position) {
        return mFragmentList.get(position);
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

}


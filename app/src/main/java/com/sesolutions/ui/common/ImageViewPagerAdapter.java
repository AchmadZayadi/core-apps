package com.sesolutions.ui.common;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.album.Albums;

import java.util.List;

/**
 * Created by root on 9/11/17.
 */

public class ImageViewPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Albums> mList;
    private boolean showTab;
    private OnUserClickedListener<Integer, Object> listener;

    public ImageViewPagerAdapter(FragmentManager manager, List<Albums> mList, OnUserClickedListener<Integer, Object> listener) {
        super(manager);
        this.mList = mList;
        this.listener = listener;
    }


    @Override
    public BaseFragment getItem(int position) {
        return PhotoListFragment.newInstance(mList.get(position), listener);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

/*

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
*/

}


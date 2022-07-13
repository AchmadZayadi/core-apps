package com.sesolutions.ui.music_album;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sesolutions.responses.music.Albums;
import com.sesolutions.ui.common.BaseFragment;

import java.util.List;

/**
 * Created by root on 9/11/17.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Albums> mList;
    private boolean showTab;

    public ViewPagerAdapter(FragmentManager manager, List<Albums> mList) {
        super(manager);
        this.mList = mList;
    }


    @Override
    public BaseFragment getItem(int position) {
        return MusicListItemFragment.newInstance(mList.get(position));
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


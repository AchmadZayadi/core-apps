package com.sesolutions.ui.message;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 9/11/17.
 */

public class MessageDashboardViewPagerAdapter extends FragmentStatePagerAdapter {
    private final List<BaseFragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private boolean showTab;

    public MessageDashboardViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }


    public void showTab(boolean showTab) {
        this.showTab = showTab;
    }

    @Override
    public BaseFragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(BaseFragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(showTab /*|| AppConfiguration.enableTabbarTitle*/ ? title : Constant.EMPTY);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

}


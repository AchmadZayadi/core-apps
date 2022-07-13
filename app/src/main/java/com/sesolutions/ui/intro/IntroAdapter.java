package com.sesolutions.ui.intro;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.sesolutions.responses.SlideShowImage;
import com.sesolutions.ui.common.BaseFragment;

import java.util.List;

public class IntroAdapter extends FragmentPagerAdapter {

    private final List<SlideShowImage> list;

    public IntroAdapter(List<SlideShowImage> list, FragmentManager fm) {
        super(fm);
        this.list = list;
    }

    @Override
    public BaseFragment getItem(int position) {
        return IntroFragment.newInstance(list.get(position), position); // blue
    }

    @Override
    public int getCount() {
        return list.size();
    }

}

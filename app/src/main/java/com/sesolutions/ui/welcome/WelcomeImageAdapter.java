package com.sesolutions.ui.welcome;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 1/11/17.
 */

public class WelcomeImageAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "WelcomeImageAdapter";
    private final int mSize;
    private final List<WelcomeModel> textList;


   /* 1st Screen
    Title: Feel Alive
    Description: Enjoy awesome music tracks & Videos and share with your friends too.

2nd Screen:
    Description: Write to share your ideas, thoughts & stories and let the world know you.

3rd Screen:
    Title: Get Connected
    Description: Find friends, make groups and share your memorable moments.

            4th Screen:
    Description: Explore things, join events and feel enthusiastic all the time.*/

    public WelcomeImageAdapter(FragmentManager fm, int size) {
        super(fm);
        mSize = size;
        textList = new ArrayList<WelcomeModel>();
        textList.add(new WelcomeModel("Feel Alive", "Enjoy awesome music tracks & Videos and share with your friends too."));
        textList.add(new WelcomeModel("Write to share your ideas, thoughts & stories and let the world know you.", Constant.EMPTY));
        textList.add(new WelcomeModel("Get Connected", "Find friends, make groups and share your memorable moments."));
        textList.add(new WelcomeModel("Explore things, join events and feel enthusiastic all the time.", Constant.EMPTY));
    }

    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public Fragment getItem(int position) {
        CustomLog.d(TAG, "position=" + position);
        WelcomeModel model = textList.get(position);
        return WelcomeImageFragment.newInstance(position, model.getTitle(), model.getDescription());
    }
}
package com.sesolutions.ui.clickclick.me;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.Objects;

public class FollowFollowingParent extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private boolean[] isLoaded = {false, false, false, false, false, false};
    public int selectedPagePosition;
    public MessageDashboardViewPagerAdapter adapter;
    private ImageView ivSearch;
    private TextView tvTitle;
    private int id;
    private String title;
    private int[] total = {0, 0, 0, 0, 0, 0, 0};


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_follow_following_parent, container, false);
        new ThemeManager().applyTheme((ViewGroup) v, context);
        try {
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public static FollowFollowingParent newInstance(int id, String title) {
        FollowFollowingParent frag = new FollowFollowingParent();
        frag.id = id;
        frag.title = title;
        return frag;
    }

    public void updateTotal(int index, int count) {
        total[index] = count;
        updateTitle(index);
    }

    private void updateTitle(int index) {
        try {
            String title = (tabLayout.getTabAt(index).getText().toString()).replace("Browse ", "")
                    + (total[index] > 0 ? " (" + total[index] + ")" : "");
            tvTitle.setText(title);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void init() {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        tvTitle = v.findViewById(R.id.tvTitle);
        updateTitle(title);
        tabLayout = v.findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.colorPrimary));
        //tabLayout.setTabTextColors(Color.parseColor(Constant.text_color_1), Color.parseColor(Constant.colorPrimary));

        viewPager = v.findViewById(R.id.viewpager);
        setupViewPager();

        tabLayout.setupWithViewPager(viewPager, true);
        applyTabListener();
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ivSearch = v.findViewById(R.id.ivSearch);
        ivSearch.setVisibility(View.GONE);
        ivSearch.setOnClickListener(this);
        if (title.equalsIgnoreCase("Following")) {
            changePagePoistion(0);
        } else {
            changePagePoistion(1);
        }
    }

    private void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        adapter.addFragment(FollowFollowingUser.newInstance(id, "Following"), "Following");
        adapter.addFragment(FollowFollowingUser.newInstance(id, "Followers"), "Followers");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }

    public void changePagePoistion(int postion) {
        try {
            TabLayout.Tab tab = tabLayout.getTabAt(postion);
            if (!Objects.requireNonNull(tab).isSelected())
                tab.select();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void updateTitle(String title) {
        tvTitle.setText(title);
    }

    private void applyTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTitle(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                try {
                    if (tab.getPosition() == 0) {
                        (adapter.getItem(tab.getPosition())).onRefresh();
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }

            }
        });
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;


            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.SUCCESS:
                isLoaded[2] = false;
                tabLayout.getTabAt(2).select();
                break;
            case Constant.Events.UPDATE_TOTAL:
                //here postion is total count and object2 is selected screen
                updateTotal(Integer.parseInt(("" + object2)), postion);
                break;
            case Constant.Events.SET_LOADED:
                isLoaded[postion] = true;
                // updateLoadStatus("" + object2, true);
                break;
        }

        return false;
    }
}

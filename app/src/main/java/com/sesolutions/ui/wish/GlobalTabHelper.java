package com.sesolutions.ui.wish;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GlobalTabHelper extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object>, TabLayout.OnTabSelectedListener {
    public List<Options> tabItems;
    public int FORM_EDIT;
    public int FORM_CREATE;
    public String FORM_CREATE_URL;
    public boolean[] tabLoaded = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};

    public View v;
    public ViewPager viewPager;
    public TabLayout tabLayout;

    public MessageDashboardViewPagerAdapter adapter;
    public boolean isBrowseLoaded;
    public boolean isManageLoaded;
    public boolean isCategoryLoaded;
    public ImageView ivSearch;
    public ImageView ivFilter;
    public int selectedItem;
    public TextView tvTitle;
    private int[] total = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_music_home, container, false);
        getActivity().getWindow().setStatusBarColor(Color.parseColor(Constant.colorPrimary));
        try {
            applyTheme(v);
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void init() {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        tabLayout = v.findViewById(R.id.tabs);
        tvTitle = v.findViewById(R.id.tvTitle);
        tvTitle.setText(Constant.EMPTY);
        viewPager = v.findViewById(R.id.viewpager);
        setupViewPager();

        tabLayout.setupWithViewPager(viewPager, true);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.menuButtonActiveTitleColor));
        tabLayout.setTabTextColors(Color.parseColor(Constant.menuButtonTitleColor), Color.parseColor(Constant.menuButtonActiveTitleColor));
        tabLayout.addOnTabSelectedListener(this);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ivSearch = v.findViewById(R.id.ivSearch);
        ivFilter = v.findViewById(R.id.ivFilter);
        ivSearch.setOnClickListener(this);
        v.findViewById(R.id.fabAdd).setOnClickListener(this);
        new Handler().postDelayed(() -> loadFragmentIfNotLoaded(0), 200);
    }

    public int getTotal(int index) {
        return total[index];
    }

    public void updateTotal(int index, int count) {
        if (index == selectedItem) {
            total[index] = count;
            updateTitle(index);
        }
    }


    public void updateTitle(int index) {
        try {
            String title = (tabLayout.getTabAt(index).getText().toString()).replace("Browse ", "")
                    + (total[index] > 0 ? " (" + total[index] + ")" : "");
            tvTitle.setText(title);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public abstract void setupViewPager();


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
//        updateToolbarIcons(tab.getPosition());
        loadFragmentIfNotLoaded(tab.getPosition());
        updateTitle(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        try {
                    /*if (tab.getPosition() == 0) {
                        ((VideoHelper) adapter.getItem(tab.getPosition())).scrollToStart();
                    }*/
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }


    public abstract void updateToolbarIcons(int position);

    public abstract void refreshScreenByPosition(int position);

    public abstract void loadFragmentIfNotLoaded(int position);


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivSearch:
                    goToSearchFragment();
                    break;

                case R.id.fabAdd:
                    openCreateForm();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void openCreateForm() {
        Map<String, Object> map = new HashMap<>();
        activity.filteredMap = null;
        //fragmentManager.beginTransaction().replace(R.id.container, new SearchFormFragment()).addToBackStack(null).commit();
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(FORM_CREATE, map, FORM_CREATE_URL))
                .addToBackStack(null)
                .commit();
    }

    public void showFabIcon() {
        new Handler().postDelayed(() -> (v.findViewById(R.id.fabAdd)).setVisibility(View.VISIBLE), 1000);
    }


    public abstract void goToSearchFragment(); /*{
        fragmentManager.beginTransaction().replace(R.id.container, new SearchQuoteFragment()).addToBackStack(null).commit();
    }*/

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        return false;
    }
}

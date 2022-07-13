package com.sesolutions.ui.news;


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
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

public class NewsParentFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private boolean[] isLoaded = {false, false, false, false, false, false, false, false};
    public int selectedPagePosition;
    public MessageDashboardViewPagerAdapter adapter;
    /* public boolean isNewsLoaded;
     public boolean isCategoriesLoaded;
     public boolean isMyNewsLoaded;*/
    private ImageView ivSearch;
    private TextView tvTitle;
    private int[] total = {0, 0, 0, 0, 0, 0, 0};


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_music_home, container, false);
        new ThemeManager().applyTheme((ViewGroup) v, context);
        try {
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
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
        updateTitle(getStrings(R.string.TITLE_NEWS));
        tabLayout = v.findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.colorPrimary));
        tabLayout.setTabTextColors(Color.parseColor(Constant.menuButtonTitleColor), Color.parseColor(Constant.menuButtonActiveTitleColor));

        viewPager = v.findViewById(R.id.viewpager);
        setupViewPager();

        tabLayout.setupWithViewPager(viewPager, true);
        applyTabListener();
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ivSearch = v.findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(this);
        new Handler().postDelayed(() -> loadFragmentIfNotLoaded(0), 200);
    }

    private void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        adapter.addFragment(BrowseNewsListFragment.newInstance(this, 0), getStrings(R.string.TAB_TITLE_NEWS_1));
        adapter.addFragment(BrowseRSSFragment.Companion.newInstance(this, 0), getString(R.string.TAB_TITLE_NEWS_2));
        adapter.addFragment(NewsCategoriesFragment.newInstance(this), getStrings(R.string.TAB_TITLE_NEWS_3));
        if (SPref.getInstance().isLoggedIn(context)) {
            adapter.addFragment(BrowseNewsListFragment.newInstance(this, SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID)), getStrings(R.string.TAB_TITLE_NEWS_4));
            adapter.addFragment(
                    CreateNewsFragment.newinstance(Constant.FormType.CREATE_NEWS, Constant.URL_CREATE_NEWS, this), getStrings(R.string.TAB_TITLE_NEWS_5));
            adapter.addFragment(BrowseRSSFragment.Companion.newInstance(this,SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID)), getString(R.string.TAB_TITLE_NEWS_6));
            adapter.addFragment(
                    CreateRSSFragment.newinstance(Constant.FormType.CREATE_NEWS,Constant.URL_CREATE_RSS, this), getString(R.string.TAB_TITLE_NEWS_7));
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
//         viewPager.setCurrentItem(0);
    }

    public void updateTitle(String title) {
        tvTitle.setText(title);
    }

    private void applyTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateToolbarIcons(tab.getPosition());
                loadFragmentIfNotLoaded(tab.getPosition());
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

    private void updateToolbarIcons(int position) {
        ivSearch.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
    }


    private void loadFragmentIfNotLoaded(int position) {
        if (!isLoaded[position]) ((adapter.getItem(position))).initScreenData();
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivSearch:
//                    if (activity.currentFragment instanceof BrowseNewsListFragment) {
                        goToSearchNewsFragment();
//                    }
//                    else if(activity.currentFragment instanceof BrowseRSSFragment){
//                        goToSearchRSSFragment();
//                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

   /* private void goToFormFragment() {
        Map<String, Object> map = new HashMap<>();
       // map.put("moduleName", "sesvideo");
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.ADD_CHANNEL, map, Constant.URL_NEWS_CREATE))
                .addToBackStack(null)
                .commit();
    }*/

    private void goToSearchNewsFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchNewsFragment()).addToBackStack(null).commit();
    }
    private void goToSearchRSSFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchRSSFragment()).addToBackStack(null).commit();
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

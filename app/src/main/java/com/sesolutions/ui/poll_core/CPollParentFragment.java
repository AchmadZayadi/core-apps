package com.sesolutions.ui.poll_core;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.video.CreateVideoForm;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

import java.util.HashMap;
import java.util.Map;

public class CPollParentFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    public int loggedinId;
    public int selectedPagePosition;
    public MessageDashboardViewPagerAdapter adapter;
    public boolean isPollloaded;
    public boolean isMypollloaded;
    private ImageView ivSearch;
    private int selectedItem;
    private TextView tvTitle;
    private int[] total = {0, 0, 0, 0, 0, 0, 0};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_music_home_poll, container, false);
        try {
            applyTheme(v);
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void updateTitle(String title) {
        tvTitle.setText(title);
    }

    private void init() {
        toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        tabLayout = v.findViewById(R.id.tabs);
        tvTitle = v.findViewById(R.id.tvTitle);
        updateTitle(Constant.TITLE_POLLS);

        viewPager = v.findViewById(R.id.viewpager);
        setupViewPager();
        v.findViewById(R.id.fabAdd).setOnClickListener(this);
        if (SPref.getInstance().isLoggedIn(context)) {
            tabLayout.setupWithViewPager(viewPager, true);
            tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.colorPrimary));
            tabLayout.setTabTextColors(Color.parseColor(Constant.menuButtonTitleColor), Color.parseColor(Constant.menuButtonActiveTitleColor));
            tabLayout.setTabMode(TabLayout.MODE_FIXED);

            applyTabListener();
        } else {
            tabLayout.setupWithViewPager(viewPager, true);
            tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.colorPrimary));
            tabLayout.setTabTextColors(Color.parseColor(Constant.text_color_1), Color.parseColor(Constant.colorPrimary));
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

            applyTabListener();
        }

        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ivSearch = v.findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadFragmentIfNotLoaded(0);
            }
        }, 200);

        // tabLayout.getTabAt(0).select();

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

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.EDIT_CORE_POLL) {
            activity.taskPerformed = 0;
            isMypollloaded = false;
            isPollloaded = false;
            loadFragmentIfNotLoaded(1);
        } else if (activity.taskPerformed == Constant.TASK_ALBUM_DELETED) {
            activity.taskPerformed = 0;
            isMypollloaded = false;
            isPollloaded = false;
            refreshScreenByPosition(0);
            refreshScreenByPosition(1);
            // loadFragmentIfNotLoaded(0);
        } else if (activity.taskPerformed == Constant.FormType.CREATE_POLL) {
            activity.taskPerformed = 0;
            isMypollloaded = false;
            isPollloaded = false;
            loadFragmentIfNotLoaded(1);
            openCCViewPollFragment(activity.taskId);
        }
    }

    public void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        loggedinId = SPref.getInstance().getUserMasterDetail(context).getLoggedinUserId();

        //1st tab
        adapter.addFragment(CBrowsePollsFragment.newInstance(this, 0), "Browse Polls");
        //2nd tab
        if (SPref.getInstance().isLoggedIn(context)) {
            if (loggedinId > 0) {
                adapter.addFragment(CMyPollsFragment.newInstance(this, SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID)), Constant.TAB_TITLE_POLL_4);
                //3rd tab
//            adapter.addFragment(
//                    CCreatePollsFragment.newInstance(Constant.FormType.CREATE_POLL, Constant.URL_POLL_CREATE, this), Constant.TAB_TITLE_POll_3);
                showFabIcon();
            }
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        // viewPager.setCurrentItem(0);
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
                    /*if (tab.getPosition() == 0) {
                        ((VideoHelper) adapter.getItem(tab.getPosition())).scrollToStart();
                    }*/
                } catch (Exception e) {
                    CustomLog.e(e);
                }

            }
        });
    }

    private void updateToolbarIcons(int position) {
        selectedItem = position;
        ivSearch.setVisibility(position <= 1 ? View.VISIBLE : View.GONE);
    }


    private void refreshScreenByPosition(int position) {

        try {
            switch (position) {
                case 0:
                    if (!isPollloaded)
                        ((CBrowsePollsFragment) (adapter.getItem(position))).onRefresh();
                    break;

                case 1:
                    if (!isMypollloaded)
                        ((CMyPollsFragment) adapter.getItem(position)).onRefresh();
                    break;

             /*   case 2:
                    if (!isCategoryLoaded)
                        //  ((QuotesCategoriesFragment) adapter.getItem(position)).
                        break;
                case 3:
                    if (!isMyAlbumLoaded)
                        ((MyAlbumFragment) adapter.getItem(position)).onRefresh();
                    break;
*/
                case 2:
                    // if (!isPostVideoLoaded)
                    ((CreateVideoForm) adapter.getItem(position)).initScreenData();
                    //  break;


            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void loadFragmentIfNotLoaded(int position) {

        try {
            switch (position) {
                case 0:
                    if (!isPollloaded)
                        ((CBrowsePollsFragment) (adapter.getItem(position))).initScreenData();
                    break;

                case 1:
                    if (!isMypollloaded)
                        ((CMyPollsFragment) adapter.getItem(position)).initScreenData();
                    break;

            /*    case 2:
                    if (!isCategoryLoaded)
                        ((QuotesCategoriesFragment) adapter.getItem(position)).initScreenData();
                    break;*/
              /*  case 3:
                    if (!isMyAlbumLoaded)
                        ((MyAlbumFragment) adapter.getItem(position)).initScreenData();
                    break;*/

                case 2:
                    // if (!isPostVideoLoaded)
                    ((CCreatePollsFragment) adapter.getItem(position)).initScreenData();
                    //  break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
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

   /* private void goToFormFragment() {
        Map<String, Object> map = new HashMap<>();
       // map.put("moduleName", "sesvideo");
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.ADD_CHANNEL, map, Constant.URL_BLOG_CREATE))
                .addToBackStack(null)
                .commit();
    }*/

    private void goToSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new CSearchPollFragment()).addToBackStack(null).commit();
    }

    public void openCreateForm() {
        Map<String, Object> map = new HashMap<>();
        activity.filteredMap = null;
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CCreatePollsFragment.newInstance(Constant.FormType.CREATE_POLL, map, Constant.URL_POLL_CREATE))
                .addToBackStack(null)
                .commit();
    }

    public void showFabIcon() {
        new Handler().postDelayed(() -> (v.findViewById(R.id.fabAdd)).setVisibility(View.VISIBLE), 1000);
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        try {
            if (object1 == Constant.Events.SUCCESS) {
                isPollloaded = false;
                isMypollloaded = false;
                tabLayout.getTabAt(0).select();
                goToViewPrayerFragment(postion);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }
}

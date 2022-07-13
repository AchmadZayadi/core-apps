package com.sesolutions.ui.group_core;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.CreateEditCoreForm;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

public class CGroupParentFragment extends BaseFragment implements View.OnClickListener {

    private View v;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;

    public int selectedPagePosition;
    public MessageDashboardViewPagerAdapter adapter;
    public boolean isGroupLoaded;
    public boolean isPhotoLoaded;
    public boolean isCategoryLoaded;
    public boolean isMyAlbumLoaded;
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
        v = inflater.inflate(R.layout.fragment_music_home, container, false);
        try {
            applyTheme(v);
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        tabLayout = v.findViewById(R.id.tabs);
        tvTitle = v.findViewById(R.id.tvTitle);
        tvTitle.setText(Constant.EMPTY);

        viewPager = v.findViewById(R.id.viewpager);
        setupViewPager();
        // tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //  tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager, true);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.menuButtonActiveTitleColor));
        tabLayout.setTabTextColors(Color.parseColor(Constant.menuButtonTitleColor), Color.parseColor(Constant.menuButtonActiveTitleColor));

        applyTabListener();
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ivSearch = v.findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(this);
        v.findViewById(R.id.fabAdd).setOnClickListener(this);
        new Handler().postDelayed(() -> loadFragmentIfNotLoaded(0), 200);
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
        try {
            if (activity.taskPerformed == Constant.FormType.CREATE_GROUP) {
                activity.taskPerformed = 0;
                isMyAlbumLoaded = false;
                isGroupLoaded = false;
                tabLayout.getTabAt(2).select();
                goToViewCGroupFragment(activity.taskId);
            } else if (activity.taskPerformed == Constant.FormType.EDIT_GROUP) {
                activity.taskPerformed = 0;
                isMyAlbumLoaded = false;
                isGroupLoaded = false;
                refreshScreenByPosition(2);
            } else if (activity.taskPerformed == Constant.TASK_ALBUM_DELETED) {
                activity.taskPerformed = 0;
                isMyAlbumLoaded = false;
                isGroupLoaded = false;
                refreshScreenByPosition(0);
                refreshScreenByPosition(2);
                // loadFragmentIfNotLoaded(0);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        adapter.addFragment(BrowseCGroupFragment.newInstance(this, 0), context.getResources().getString(R.string.tab_title_group_browse));
//        adapter.addFragment(CGroupCategoryFragment.newInstance(this), context.getResources().getString(R.string.TAB_TITLE_ALBUM_3));

        if (SPref.getInstance().isLoggedIn(context)) {
            showFabIcon();
            // adapter.addFragment(BrowseAlbumFragment.newInstance(this, SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID)), Constant.TAB_TITLE_ALBUM_4);
            adapter.addFragment(MyCGroupFragment.newInstance(this), context.getResources().getString(R.string.tab_title_group_my));
            // adapter.addFragment(CreateVideoForm.newinstance(Constant.FormType.CREATE_ALBUM, Constant.URL_CREATE_ALBUM, this), Constant.TAB_TITLE_ALBUM_5);
        }
        // viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        // viewPager.setCurrentItem(0);
    }

    private void showFabIcon() {
        new Handler().postDelayed(() -> (v.findViewById(R.id.fabAdd)).setVisibility(View.VISIBLE), 1000);
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
                    if (!isGroupLoaded)
                        ((BrowseCGroupFragment) (adapter.getItem(position))).onRefresh();
                    break;

               /* case 1:
                    if (!isPhotoLoaded)
                        ((MyGroupFragment) adapter.getItem(position)).onRefresh();
                    break;*/

                case 1:
                    if (!isCategoryLoaded)
                        ((CGroupCategoryFragment) adapter.getItem(position)).onRefresh();
                    break;
                case 2:
                    if (!isMyAlbumLoaded)
                        ((MyCGroupFragment) adapter.getItem(position)).onRefresh();
                    break;

               /* case 4:
                    ((CreateVideoForm) adapter.getItem(position)).initScreenData();*/


            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void loadFragmentIfNotLoaded(int position) {

        try {
            switch (position) {
                case 0:
                    if (!isGroupLoaded)
                        ((adapter.getItem(position))).initScreenData();
                    break;

               /* case 1:
                    if (!isPhotoLoaded)
                        ((BrowsePhotoFragment) adapter.getItem(position)).initScreenData();
                    break;*/

                case 1:
                    if (!isCategoryLoaded)
                        (adapter.getItem(position)).initScreenData();
                    break;
                case 2:
                    if (!isMyAlbumLoaded)
                        (adapter.getItem(position)).initScreenData();
                    break;
               /* case 4:
                    // if (!isPostVideoLoaded)
                    ((CreateVideoForm) adapter.getItem(position)).initScreenData();
                    //  break;
*/

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

    private void openCreateForm() {
        // Map<String, Object> map = new HashMap<>();
        activity.filteredMap = null;
        fragmentManager.beginTransaction().replace(R.id.container, CreateEditCoreForm.newInstance(Constant.FormType.CREATE_GROUP, null, Constant.URL_CREATE_CGROUP)).addToBackStack(null).commit();

        //fragmentManager.beginTransaction().replace(R.id.container, new SearchFormFragment()).addToBackStack(null).commit();
      /*  fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.CREATE_GROUP, map, Constant.URL_CREATE_CGROUP))
                .addToBackStack(null)
                .commit();*/
    }

    private void goToSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchCGroupFragment()).addToBackStack(null).commit();
    }
}

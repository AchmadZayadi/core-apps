package com.sesolutions.ui.albums_core;


import android.os.Bundle;
import android.os.Handler;
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
import com.sesolutions.ui.albums.BrowseAlbumFragment;
import com.sesolutions.ui.albums.MyAlbumFragment;
import com.sesolutions.ui.albums.SearchAlbumFragment;
import com.sesolutions.ui.albums.SearchPhotoFragment;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.video.CreateVideoForm;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

import java.util.ArrayList;
import java.util.List;

public class C_AlbumParentFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;

    public int selectedPagePosition;
    public MessageDashboardViewPagerAdapter adapter;
    // public boolean isBlogLoaded;
    // public boolean isPhotoLoaded;
    // public boolean isCategoryLoaded;
    // public boolean isMyAlbumLoaded;
    private ImageView ivSearch;
    private int selectedItem;
    private TextView tvTitle;
    private int[] total;
    private List<String> tabItems;
    private boolean[] tabLoaded;

    private final String BROWSE = "browse", PHOTOS = "photos", CATEGORY = "category", MANAGE = "manage", CREATE = "create";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
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

        tabLayout.setupWithViewPager(viewPager, true);

        applyTabListener();
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ivSearch = v.findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(this);
        new Handler().postDelayed(() -> loadFragmentIfNotLoaded(0), 200);
    }


    public void updateTotal(int index, int count) {
        total[index] = count;
        updateTitle(index);
    }

    private void updateTitle(int index) {
        try {
            String title = (tabLayout.getTabAt(index).getText().toString()).replace("Browse ", "");
            tvTitle.setText(title);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.EDIT_ALBUM) {
            activity.taskPerformed = 0;
            tabLoaded[getTabIndex(MANAGE)] = false;
            tabLoaded[getTabIndex(BROWSE)] = false;
            loadFragmentIfNotLoaded(getTabIndex(MANAGE));
        } else if (activity.taskPerformed == Constant.TASK_ALBUM_DELETED) {
            activity.taskPerformed = 0;
            tabLoaded[getTabIndex(MANAGE)] = false;
            tabLoaded[getTabIndex(BROWSE)] = false;
            refreshScreenByPosition(getTabIndex(BROWSE));
            refreshScreenByPosition(getTabIndex(MANAGE));
        }
    }


    public int getTabIndex(String selectedScreen) {
        return tabItems.indexOf(selectedScreen);
       /* for (int i = 0; i < tabItems.size(); i++) {
            if (tabItems.get(i).equals(selectedScreen)) {
                return i;
            }
        }
        return -1;*/
    }

    public void updateTotal(String index, int count) {
        updateTotal(getTabIndex(index), count);
    }

    public void updateLoadStatus(String selectedScreen, boolean isLoaded) {
        try {
            tabLoaded[getTabIndex(selectedScreen)] = isLoaded;
        } catch (Exception e) {
            CustomLog.e("AIOOBE", "tabItem not found ->" + selectedScreen);
        }
    }

    private void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);

        tabItems = new ArrayList<>();
        tabItems.add(BROWSE);
//        if (!ModuleUtil.getInstance().isCoreAlbumEnabled(context)) {
            tabItems.add(PHOTOS);
//        }
//        tabItems.add(CATEGORY);
        if (SPref.getInstance().isLoggedIn(context)) {
            tabItems.add(MANAGE);
            tabItems.add(CREATE);
        }

        tabLoaded = new boolean[tabItems.size()];
        total = new int[tabItems.size()];
        for (String name : tabItems) {
            switch (name) {
                case BROWSE:
                    adapter.addFragment(C_BrowseAlbumFragment.newInstance(this, name), getString(R.string.TAB_TITLE_ALBUM_1));
                    break;
                case PHOTOS:
                    adapter.addFragment(C_BrowsePhotoFragment.newInstance(this, name), getString(R.string.TAB_TITLE_ALBUM_2));
                    break;
//                case CATEGORY:
//                    adapter.addFragment(AlbumCategoriesFragment.newInstance(this, name), getString(R.string.TAB_TITLE_ALBUM_3));
//                    break;
//                case MANAGE:
//                    adapter.addFragment(C_MyAlbumFragment.newInstance(this, name), getString(R.string.TAB_TITLE_ALBUM_4));
//                    break;
//                case CREATE:
//                    adapter.addFragment(
//                            CreateVideoForm.newinstance(Constant.FormType.CREATE_ALBUM, Constant.URL_CREATE_ALBUM, this), getString(R.string.TAB_TITLE_ALBUM_5));
//                    break;
            }
        }


        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(tabItems.size());
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
        ivSearch.setVisibility(position <= 2 ? View.VISIBLE : View.GONE);
    }


    private void refreshScreenByPosition(int position) {
        try {
            adapter.getItem(position).onRefresh();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void loadFragmentIfNotLoaded(int position) {

        try {
            if (!tabLoaded[position]) {
                adapter.getItem(position).initScreenData();
            }
           /* switch (position) {
                case 0:
                    if (!isBlogLoaded)
                        ((adapter.getItem(position))).initScreenData();
                    break;

                case 1:
                    if (!isPhotoLoaded)
                        (adapter.getItem(position)).initScreenData();
                    break;

                case 2:
                    if (!isCategoryLoaded)
                        (adapter.getItem(position)).initScreenData();
                    break;
                case 3:
                    if (!isMyAlbumLoaded)
                        (adapter.getItem(position)).initScreenData();
                    break;

                case 4:
                    // if (!isPostVideoLoaded)
                    (adapter.getItem(position)).initScreenData();
                    break;


            }
*/
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
        if (selectedItem == 0 || selectedItem == 2) {
            fragmentManager.beginTransaction().replace(R.id.container, new C_SearchAlbumFragment()).addToBackStack(null).commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.container, new C_SearchPhotoFragment()).addToBackStack(null).commit();
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.SUCCESS:
                tabLoaded[getTabIndex(BROWSE)] = false;
                tabLoaded[getTabIndex(MANAGE)] = false;
                tabLayout.getTabAt(getTabIndex(BROWSE)).select();
                goToViewAlbumBasicFragment(postion, false);

            case Constant.Events.SET_LOADED:
                updateLoadStatus("" + object2, true);
                break;
            case Constant.Events.UPDATE_TOTAL:
                updateTotal("" + object2, postion);
                break;
        }
        return false;
    }
}

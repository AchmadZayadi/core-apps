package com.sesolutions.ui.thought;


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
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.quotes.CreateQuoteFragment;
import com.sesolutions.ui.video.CreateVideoForm;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

public class ThoughtParentFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;

    public int selectedPagePosition;
    public MessageDashboardViewPagerAdapter adapter;
    public boolean isQuoteLoaded;
    public boolean isMyQuoteLoaded;
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

        tabLayout.setupWithViewPager(viewPager, true);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.colorPrimary));
        tabLayout.setTabTextColors(Color.parseColor(Constant.menuButtonTitleColor), Color.parseColor(Constant.menuButtonActiveTitleColor));

        applyTabListener();
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
        if (activity.taskPerformed == Constant.FormType.EDIT_QUOTE) {
            activity.taskPerformed = 0;
            isMyQuoteLoaded = false;
            isQuoteLoaded = false;
            loadFragmentIfNotLoaded(1);
        } else if (activity.taskPerformed == Constant.TASK_ALBUM_DELETED) {
            activity.taskPerformed = 0;
            isMyQuoteLoaded = false;
            isQuoteLoaded = false;
            refreshScreenByPosition(0);
            refreshScreenByPosition(1);
            // loadFragmentIfNotLoaded(0);
        }

    }

    private void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        adapter.addFragment(BrowseThoughtFragment.newInstance(this, 0), Constant.TAB_TITLE_THOUGHTS_1);

        // adapter.addFragment(QuotesCategoriesFragment.newInstance(this), Constant.TAB_TITLE_QUOTES_3);
        if (SPref.getInstance().isLoggedIn(context)) {
            // adapter.addFragment(BrowseAlbumFragment.newInstance(this, SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID)), Constant.TAB_TITLE_ALBUM_4);
            adapter.addFragment(ManageThoughtFragment.newInstance(this, 0), Constant.TAB_TITLE_THOUGHTS_2);
            //   adapter.addFragment(MyAlbumFragment.newInstance(this), Constant.TAB_TITLE_QUOTES_4);
            adapter.addFragment(
                    CreateQuoteFragment.newinstance(Constant.FormType.CREATE_THOUGHT, Constant.URL_CREATE_THOUGHT, this), Constant.TAB_TITLE_THOUGHTS_3);
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
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
                    if (!isQuoteLoaded)
                        ((BrowseThoughtFragment) (adapter.getItem(position))).onRefresh();
                    break;

                case 1:
                    if (!isMyQuoteLoaded)
                        ((ManageThoughtFragment) adapter.getItem(position)).onRefresh();
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
                    if (!isQuoteLoaded)
                        ((BrowseThoughtFragment) (adapter.getItem(position))).initScreenData();
                    break;

                case 1:
                    if (!isMyQuoteLoaded)
                        ((ManageThoughtFragment) adapter.getItem(position)).initScreenData();
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
                    ((CreateQuoteFragment) adapter.getItem(position)).initScreenData();
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
        //  if (selectedItem == 0) {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchThoughtFragment()).addToBackStack(null).commit();
        //  } else {
        //      fragmentManager.beginTransaction().replace(R.id.container, new SearchPhotoFragment()).addToBackStack(null).commit();
        //  }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        try {
            if (object1 == Constant.Events.SUCCESS) {
                isQuoteLoaded = false;
                isMyQuoteLoaded = false;
                tabLayout.getTabAt(0).select();
                goToViewThoughtFragment(postion);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }
}

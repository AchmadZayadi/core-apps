package com.sesolutions.ui.articles;


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
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.blogs.CreateBlogFragment;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

public class ArticleParentFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;

    public int selectedPagePosition;
    public MessageDashboardViewPagerAdapter adapter;
    public boolean isBlogLoaded;
    public boolean isCategoriesLoaded;
    public boolean isMyBlogLoaded;
    private ImageView ivSearch;
    private TextView tvTitle;
    private int[] total = {0, 0, 0, 0, 0, 0, 0};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_music_home, container, false);
        getActivity().getWindow().setStatusBarColor(Color.parseColor(Constant.colorPrimary));
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
        toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        tvTitle = v.findViewById(R.id.tvTitle);
        updateTitle(Constant.TITLE_ARTICLES);
        tabLayout = v.findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.menuButtonActiveTitleColor));
		tabLayout.setTabTextColors(Color.parseColor(Constant.menuButtonTitleColor), Color.parseColor(Constant.menuButtonActiveTitleColor));
        viewPager = v.findViewById(R.id.viewpager);
        setupViewPager();

        tabLayout.setupWithViewPager(viewPager, true);
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

    private void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        adapter.addFragment(BrowseArticlesFragment.newInstance(this, 0), Constant.TAB_TITLE_ARTICLE_1);
        adapter.addFragment(ArticleCategoriesFragment.newInstance(this), Constant.TAB_TITLE_ARTICLE_2);
//        if (SPref.getInstance().isLoggedIn(context)) {
//            adapter.addFragment(BrowseArticlesFragment.newInstance(this, SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID)), Constant.TAB_TITLE_ARTICLE_3);
//            adapter.addFragment(
//                    CreateBlogFragment.newinstance(Constant.FormType.CREATE_ARTICLE, Constant.URL_CREATE_ARTICLE, this), Constant.TAB_TITLE_ARTICLE_4);
//        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(6);
        // viewPager.setCurrentItem(0);
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
        ivSearch.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
    }


    private void loadFragmentIfNotLoaded(int position) {

        try {
            switch (position) {
                case 0:
                    if (!isBlogLoaded)
                        ((BrowseArticlesFragment) (adapter.getItem(position))).initScreenData();
                    break;

                case 1:
                    if (!isCategoriesLoaded)
                        ((ArticleCategoriesFragment) adapter.getItem(position)).initScreenData();
                    break;

                case 2:
                    if (!isMyBlogLoaded)
                        ((BrowseArticlesFragment) adapter.getItem(position)).initScreenData();
                    break;

                case 3:
                    // if (!isPostVideoLoaded)
                    ((CreateBlogFragment) adapter.getItem(position)).initScreenData();
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
                    goToSearchBlogFragment();
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

    private void goToSearchBlogFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchArticlesFragment()).addToBackStack(null).commit();
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {

        if (object1 == Constant.Events.SUCCESS) {
            isMyBlogLoaded = false;
            tabLayout.getTabAt(2).select();
            //  loadFragmentIfNotLoaded(2);
        }
        return false;
    }
}

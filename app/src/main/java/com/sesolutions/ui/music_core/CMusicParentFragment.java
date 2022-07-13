package com.sesolutions.ui.music_core;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.music_album.CreateMusicForm;
import com.sesolutions.ui.musicplayer.MusicService;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.HashMap;
import java.util.Map;

public class CMusicParentFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public int selectedPagePosition;
    public MessageDashboardViewPagerAdapter adapter;
    private int selectedItem;
    private TextView tvTitle;
    private int[] total = {0, 0, 0, 0, 0, 0, 0};
    private boolean[] isLoaded = {false, false, false, false, false, false, false};


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
            new Handler().postDelayed(() -> {
                tabLayout.getTabAt(selectedPagePosition).select();
                if (selectedPagePosition == 0)
                    loadFragmentIfNotLoaded(selectedPagePosition);
            }, 200);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        tvTitle = v.findViewById(R.id.tvTitle);
        tvTitle.setText(Constant.EMPTY);
        activity.setSupportActionBar(toolbar);
        tabLayout = v.findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.colorPrimary));
        tabLayout.setTabTextColors(Color.parseColor(Constant.menuButtonTitleColor), Color.parseColor(Constant.menuButtonActiveTitleColor));
        viewPager = v.findViewById(R.id.viewpager);
        setupViewPager();
        v.findViewById(R.id.fabAdd).setOnClickListener(this);
        tabLayout.setupWithViewPager(viewPager, true);
        applyTabListener();
        v.findViewById(R.id.ivSearch).setOnClickListener(this);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
    }

    private void showFabIcon() {
        new Handler().postDelayed(() -> (v.findViewById(R.id.fabAdd)).setVisibility(View.VISIBLE), 1000);
    }

    private void openCreateForm() {
        Map<String, Object> map = new HashMap<>();
        activity.filteredMap = null;
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateMusicForm.newInstance(Constant.FormType.CREATE_MUSIC, Constant.URL_CREATE_MUSIC, null))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (Util.isServiceRunning(MusicService.class.getName(), context)) {
                if (((CommonActivity) activity).isPlaying()) {
                    if (((CommonActivity) activity).cvMusicMain.getVisibility() != View.VISIBLE)
                        ((CommonActivity) activity).showMusicLayout();
                }
            }
            if (activity.taskPerformed == Constant.TASK_PLAYLIST_DELETED) {
                int selected = viewPager.getCurrentItem();
                isLoaded[0] = false;
                isLoaded[1] = false;
                activity.taskPerformed = 0;
                //   init();
                loadFragmentIfNotLoaded(selected);
                //     tabLayout.getTabAt(selected).select();

            } else if (activity.taskPerformed == Constant.FormType.CREATE_MUSIC
                    || activity.taskPerformed == Constant.FormType.EDIT_MUSIC_ALBUM
                    || activity.taskPerformed == Constant.FormType.EDIT_MUSIC_PLAYLIST
            ) {
                isLoaded[0] = false;
                isLoaded[1] = false;
                activity.taskPerformed = 0;
                tabLayout.getTabAt(1).select();
                //  loadFragmentIfNotLoaded(5);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void updateTotal(int index, int count) {
        total[index] = count;
        updateTitle(index);
    }

    private void updateTitle(int index) {
        try {
            String title = (tabLayout.getTabAt(index).getText().toString())//.replace("Music ", "")
                    + (total[index] > 0 ? " (" + total[index] + ")" : "");
            tvTitle.setText(title);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void setupViewPager() {
        try {
            adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
            adapter.showTab(true);
            // adapter.addFragment(MusicAlbumFragment.newInstance(this), getStrings(R.string.TAB_TITLE_MUSIC_ALBUMS_1));
            // adapter.addFragment(SongsFragment.newInstance(this), getStrings(R.string.TAB_TITLE_MUSIC_ALBUMS_2));
            adapter.addFragment(CMusicPlaylistFragment.newInstance("0", this), getStrings(R.string.TAB_TITLE_MUSIC_ALBUMS_7));
            // adapter.addFragment(ArtistsFragment.newInstance(this), getStrings(R.string.TAB_TITLE_MUSIC_ALBUMS_4));
            // adapter.addFragment(LyricsFragment.newInstance(this), getStrings(R.string.TAB_TITLE_MUSIC_ALBUMS_5));
            if (SPref.getInstance().isLoggedIn(context)) {
                adapter.addFragment(MyCMusicFragment.newInstance("1", this), getStrings(R.string.TAB_TITLE_MUSIC_ALBUMS_6));
                // adapter.addFragment(MyMusicFragment.newInstance(this), );
                showFabIcon();
            }
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(adapter.getCount());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void applyTabListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    updateToolbarIcon(tab.getPosition());
                    loadFragmentIfNotLoaded(tab.getPosition());
                    updateTitle(tab.getPosition());
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                try {
                    if (tab.getPosition() == 0) {
                        // ((HomeFragment) adapter.getItem(0)).scrollToStart();
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }

            }
        });
    }

    private void updateToolbarIcon(int position) {
        selectedItem = position;
    }


    private void loadFragmentIfNotLoaded(int position) {

        if (!isLoaded[position]) {
            adapter.getItem(position).initScreenData();
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivSearch:
                    CMusicUtil.openSearchFragment(fragmentManager);
                    break;

                case R.id.fabAdd:
                    openCreateForm();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.UPDATE_TOTAL:
                updateTotal(Integer.parseInt("" + object2), postion);
                break;
            case Constant.Events.SET_LOADED:
                isLoaded[Integer.parseInt("" + object2)] = true;
                break;
        }
        return false;
    }

    public static CMusicParentFragment newInstance(int index) {

        CMusicParentFragment frag = new CMusicParentFragment();
        frag.selectedPagePosition = index;
        return frag;
    }
}

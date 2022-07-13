package com.sesolutions.ui.music_album;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.musicplayer.MusicService;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.HashMap;
import java.util.Map;

public class MusicParentFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, String> {

    private View v;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ImageView ivSearch;

    public int selectedPagePosition;
    public MessageDashboardViewPagerAdapter adapter;
    public boolean isMusicLoaded;
    public boolean isSongLoaded;
    public boolean isPlaylistLoaded;
    public boolean isArtistLoaded;
    public boolean isLyricsLoaded;
    public boolean isMyAlbumLoaded;
    private int selectedItem;
    private TextView tvTitle;
    private int[] total = {0, 0, 0, 0, 0, 0, 0};


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_music_home, container, false);
        getActivity().getWindow().setStatusBarColor(Color.parseColor(Constant.colorPrimary));
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
        toolbar = v.findViewById(R.id.toolbar);
        tvTitle = v.findViewById(R.id.tvTitle);
        tvTitle.setText(Constant.EMPTY);
        activity.setSupportActionBar(toolbar);
        tabLayout = v.findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.colorPrimary));
        tabLayout.setTabTextColors(Color.parseColor(Constant.menuButtonTitleColor), Color.parseColor(Constant.menuButtonActiveTitleColor));
        viewPager = v.findViewById(R.id.viewpager);
        setupViewPager();
        v.findViewById(R.id.fabAdd).setOnClickListener(this);
        updateFabColor(v.findViewById(R.id.fabAdd));
        tabLayout.setupWithViewPager(viewPager, true);
        applyTabListener();
        ivSearch = v.findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(this);
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
            Log.e("TaskFormed",""+activity.taskPerformed);

            if (Util.isServiceRunning(MusicService.class.getName(), context)) {
                if (((CommonActivity) activity).isPlaying()) {
                    if (((CommonActivity) activity).cvMusicMain.getVisibility() != View.VISIBLE)
                        ((CommonActivity) activity).showMusicLayout();
                }
            }
            if (activity.taskPerformed == Constant.TASK_PLAYLIST_DELETED) {
                int selected = viewPager.getCurrentItem();
                isMusicLoaded = false;
                isSongLoaded = false;
                isPlaylistLoaded = false;
                isArtistLoaded = false;
                isLyricsLoaded = false;
                isMyAlbumLoaded = false;
                activity.taskPerformed = 0;
                //   init();
                loadFragmentIfNotLoaded(selected);
                //     tabLayout.getTabAt(selected).select();

            } else if (activity.taskPerformed == Constant.FormType.CREATE_MUSIC
                    || activity.taskPerformed == Constant.FormType.EDIT_MUSIC_ALBUM
                    || activity.taskPerformed == Constant.FormType.EDIT_MUSIC_PLAYLIST
                    ) {
                isMusicLoaded = false;
                isMyAlbumLoaded = false;
                activity.taskPerformed = 0;
                tabLayout.getTabAt(5).select();
                loadFragmentIfNotLoaded(5);
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
            adapter.addFragment(MusicAlbumFragment.newInstance(this), getStrings(R.string.TAB_TITLE_MUSIC_ALBUMS_1));
            adapter.addFragment(SongsFragment.newInstance(this), getStrings(R.string.TAB_TITLE_MUSIC_ALBUMS_2));
            adapter.addFragment(PlaylistFragment.newInstance(this), getStrings(R.string.TAB_TITLE_MUSIC_ALBUMS_3));
            adapter.addFragment(ArtistsFragment.newInstance(this), getStrings(R.string.TAB_TITLE_MUSIC_ALBUMS_4));
            adapter.addFragment(LyricsFragment.newInstance(this), getStrings(R.string.TAB_TITLE_MUSIC_ALBUMS_5));
            if (SPref.getInstance().isLoggedIn(context)) {
                adapter.addFragment(MyMusicFragment.newInstance(this), getStrings(R.string.TAB_TITLE_MUSIC_ALBUMS_6));
                showFabIcon();
            }
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(5);
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
        ivSearch.setVisibility((position == 3 || position == 5) ? View.GONE : View.VISIBLE);
    }


    private void loadFragmentIfNotLoaded(int position) {
        switch (position) {
            case 0:
                if (!isMusicLoaded)
                    ((MusicAlbumFragment) (adapter.getItem(position))).initScreenData();
                break;
            case 1:
                if (!isSongLoaded)
                    ((SongsFragment) adapter.getItem(position)).initScreenData();
                break;
            case 2:
                if (!isPlaylistLoaded)
                    ((PlaylistFragment) adapter.getItem(position)).initScreenData();
                break;
            case 3:
                if (!isArtistLoaded)
                    ((ArtistsFragment) adapter.getItem(position)).initScreenData();
                break;

            case 4:
                if (!isLyricsLoaded)
                    ((LyricsFragment) adapter.getItem(position)).initScreenData();
                break;

            case 5:
                if (!isMyAlbumLoaded)
                    ((MyMusicFragment) adapter.getItem(position)).initScreenData();
                break;
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

    private void goToSearchFragment() {

        if (selectedItem == 0) {
            fragmentManager.beginTransaction().replace(R.id.container, new SearchMusicAlbumFragment()).addToBackStack(null).commit();

        } else if (selectedItem == 1 || selectedItem == 4) {
            fragmentManager.beginTransaction().replace(R.id.container, new SearchSongFragment()).addToBackStack(null).commit();

        } else if (selectedItem == 2) {
            fragmentManager.beginTransaction().replace(R.id.container, new SearchPlaylistFragment()).addToBackStack(null).commit();
        }


    }

    @Override
    public boolean onItemClicked(Integer object1, String object2, int postion) {
        return false;
    }

    public static MusicParentFragment newInstance(int index) {
        MusicParentFragment frag = new MusicParentFragment();
        frag.selectedPagePosition = index;
        return frag;
    }
}

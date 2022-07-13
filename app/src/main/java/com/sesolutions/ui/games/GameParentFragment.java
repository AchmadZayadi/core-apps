package com.sesolutions.ui.games;

import android.content.Intent;
import android.graphics.Color;
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
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.ui.video.MyVideoFragment;
import com.sesolutions.ui.video.SearchChannelFragment;
import com.sesolutions.ui.video.SearchPlaylistFragment;
import com.sesolutions.ui.video.SearchVideoFragment;
import com.sesolutions.ui.video.ViewChannelFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.SPref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sesolutions.utils.Constant.EDIT_CHANNEL_ME;

public class GameParentFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private static final String TAG = "VideoParentFragment";

    private View v;
    private int[] total;
    private View ivSearch;
    private TextView tvTitle;
    private boolean isLoggedIn;
    private boolean[] tabLoaded;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageView ivOptionAdd;
    public int myVideoSubType = 0;
    private List<String> tabItems;
    public int selectedPagePosition;
    public MessageDashboardViewPagerAdapter adapter;

    public static GameParentFragment newInstance(int index) {
        GameParentFragment frag = new GameParentFragment();
        frag.selectedPagePosition = index;
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

    public void changeTabPosition(int position) {
        tabLayout.getTabAt(position).select();
    }


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
        activity.setSupportActionBar(toolbar);
        tabLayout = v.findViewById(R.id.tabs);
        tvTitle = v.findViewById(R.id.tvTitle);
        tvTitle.setText(Constant.EMPTY);
        isLoggedIn = SPref.getInstance().isLoggedIn(context);
        viewPager = v.findViewById(R.id.viewpager);
        setupViewPager();

        tabLayout.setupWithViewPager(viewPager, true);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.menuButtonActiveTitleColor));
        tabLayout.setTabTextColors(Color.parseColor(Constant.menuButtonTitleColor), Color.parseColor(Constant.menuButtonActiveTitleColor));

        applyTabListener();
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ivOptionAdd = v.findViewById(R.id.ivOptionAdd);
        ivSearch = v.findViewById(R.id.ivSearch);
        ivSearch.setOnClickListener(this);
        ivOptionAdd.setOnClickListener(this);
        // tabLayout.getTabAt(0).select();

    }

    private void setupViewPager() {

        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);

        boolean isCoreVideoPluginEnabled = ModuleUtil.getInstance().isCoreVideoEnabled(context);
        tabItems = new ArrayList<>();
        tabItems.add("browse");
       // tabItems.add("myVideos");
       // tabItems.add("createVideos");

        tabLoaded = new boolean[tabItems.size()];
        total = new int[tabItems.size()];

        for (String name : tabItems) {
            switch (name) {

                case "browse":
                    adapter.addFragment(GameBrowseFragment.newInstance(this, name), getStrings(R.string.TAB_GAME1));
                    break;
           /*       case "myVideos":
                    adapter.addFragment(MyVideoFragment.newInstance(this, name), getStrings(R.string.TAB_GAME2));
                    break;
                case "createVideos":
                    adapter.addFragment(
                            CreateVideoForm.newinstance(Constant.FormType.CREATE_GAME_ACCOUNT, Constant.CREDIT_RESUME_OBJECTIVES, this), getString(R.string.carrier_object));

                    break;*/
            }
        }

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(tabItems.size());
    }

    private void goToSearchFragment() {
        try {
            BaseFragment frag = null;
            int pos = tabLayout.getSelectedTabPosition();
            if ("browse".equals(tabItems.get(pos))) {
                frag = new SearchVideoFragment();
            } else if ("channel".equals(tabItems.get(pos))) {
                frag = new SearchChannelFragment();
            } else if ("playlist".equals(tabItems.get(pos))) {
                frag = new SearchPlaylistFragment();
            }
            fragmentManager.beginTransaction().replace(R.id.container, frag).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
        }
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
        ivOptionAdd.setVisibility(isLoggedIn && "channel".equals(tabItems.get(position)) ? View.VISIBLE : View.GONE);
        ivSearch.setVisibility(canShowSearch(position) ? View.VISIBLE : View.GONE);
    }

    private boolean canShowSearch(int position) {
        switch (tabItems.get(position)) {
            case "browse":
            case "playlist":
            case "channel":
                return true;

        }
        return false;
    }


    private void loadFragmentIfNotLoaded(int position) {

        try {
            if (!tabLoaded[position]) {
                adapter.getItem(position).initScreenData();
            }
          /*  switch (position) {
                case 0:
                    if (!isVideoLoaded)
                        ((VideoAlbumFragment) (adapter.getItem(position))).initScreenData();
                    break;
                case 1:
                    if (!isChannelLoaded)
                        ((VideoChannelFragment) adapter.getItem(position)).initScreenData();
                    break;
                case 2:
                    if (!isPlaylistLoaded)
                        ((VideoPlaylistFragment) adapter.getItem(position)).initScreenData();
                    break;
                case 3:
                    if (!isArtistLoaded)
                        ((VideoArtistsFragment) adapter.getItem(position)).initScreenData();
                    break;

                case 4:
                    if (!isCategoriesLoaded)
                        ((VideoCategoriesFragment) adapter.getItem(position)).initScreenData();
                    break;

                case 5:
                    if (!isMyAlbumLoaded)
                        ((MyVideoFragment) adapter.getItem(position)).initScreenData(myVideoSubType);
                    break;

                case 6:
                    // if (!isPostVideoLoaded)
                    ((PostVideoForm) adapter.getItem(position)).initScreenData();
                    //  break;
            }*/
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            if (activity.taskPerformed == Constant.FormType.ADD_CHANNEL) {
                activity.taskPerformed = 0;
                tabLoaded[getTabIndex("channel")] = false;
                tabLoaded[getTabIndex("myVideo")] = false;
                this.myVideoSubType = 1;
                this.changeTabPosition(getTabIndex("myVideo"));
                goToViewChannelFragment(Constant.channelId);
                // this.changeTabPosition(5);
            } else if (activity.taskPerformed == Constant.FormType.TYPE_PLAYLIST_VIDEO) {
              /*  this means current selected fragment is "My Video-> Playlist"
                So update the playlist */
                activity.taskPerformed = 0;
                tabLoaded[getTabIndex("myVideo")] = false;
                ((MyVideoFragment) adapter.getItem(getTabIndex("myVideo"))).initScreenData(2);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void goToViewChannelFragment(int channelId) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewChannelFragment.newInstance(channelId))
                .addToBackStack(null)
                .commit();
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivOptionAdd:
                 //   goToFormFragment();
                    Intent intent = new Intent(activity, CommonActivity.class);
                    intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CREATE_CHANNEL);
                    startActivityForResult(intent, EDIT_CHANNEL_ME);
                    break;
                case R.id.ivSearch:
                    goToSearchFragment();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void goToFormFragment() {
        Map<String, Object> map = new HashMap<>();
        map.put("moduleName", "sesvideo");
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        FormFragment.newInstance(Constant.FormType.ADD_CHANNEL, map, Constant.URL_CHANNEL_CREATE))
                .addToBackStack(null)
                .commit();
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

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.SUCCESS:
                tabLoaded[getTabIndex("myVideo")] = false;
                changeTabPosition(getTabIndex("myVideo"));
                if (object2.equals("video_view")) {
                    goTo(Constant.GoTo.VIDEO, postion);
                }
                break;
            case Constant.Events.SET_LOADED:
                updateLoadStatus("" + object2, true);
                break;
            case Constant.Events.UPDATE_TOTAL:
                updateTotal("" + object2, postion);
                break;
            case Constant.Events.SUCCESSVIDEO:

               if (object2.equals("video_view")) {
                    goTo(Constant.GoTo.VIDEO, postion);
                }
                break;
        }
        return false;
    }
}

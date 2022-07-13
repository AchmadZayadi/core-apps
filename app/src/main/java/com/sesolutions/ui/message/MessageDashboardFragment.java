package com.sesolutions.ui.message;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.dashboard.MainActivity;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;

public class MessageDashboardFragment extends BaseFragment implements View.OnClickListener {

    private View v;

    private int menuButtonBackgroundColor;
    private int menuButtonTitleColor;
    private int menuButtonActiveTitleColor;
    private TextView tvInbox;
    private TextView tvOutbox;
    private boolean isInboxSelected;
    private TabLayout tabLayout;

    private OnUserClickedListener<Integer, Object> parent;

    public static MessageDashboardFragment newInstance(OnUserClickedListener<Integer, Object> parent) {
        MessageDashboardFragment frag = new MessageDashboardFragment();
        frag.parent = parent;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setHasOptionsMenu(true);
        if (v != null) {
            return v;
        }

        v = inflater.inflate(R.layout.fragment_message_dashboard, container, false);
        ((MainActivity) activity).changeCurrentFragment();


        return v;
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) activity).dashboardFragment.onBackPressed();
    }

    public void initScreenData() {
        applyTheme(v);
        init();
    }

    public void showSelectedScreen(boolean isShowingInbox) {
        BaseFragment fragment = isShowingInbox ? new MessageInboxFragment() : new MessageSentFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.container_message, fragment).commit();
    }

    private void init() {
        try {

            menuButtonBackgroundColor = Color.parseColor(Constant.menuButtonBackgroundColor);
            menuButtonTitleColor = Color.parseColor(Constant.menuButtonTitleColor);
            menuButtonActiveTitleColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
            tvInbox = v.findViewById(R.id.tvVideos);
            tvOutbox = v.findViewById(R.id.tvPlaylists);
            TabLayout tabLayout = v.findViewById(R.id.tabs);
            //tabLayout.setSelectedTabIndicatorColor(menuButtonActiveTitleColor);
            //tabLayout.setTabTextColors(menuButtonTitleColor, menuButtonActiveTitleColor);
            //tabLayout.setBackgroundColor(menuButtonBackgroundColor);
            tabLayout.addTab(tabLayout.newTab().setText(R.string.INBOX), true);
            tabLayout.addTab(tabLayout.newTab().setText(R.string.OUTBOX));

            isInboxSelected = true;
            if (parent != null) {
                parent.onItemClicked(Constant.Events.SET_LOADED, MenuTab.Dashboard.MESSAGE, 1);
            }
            toggleTab();
            tvInbox.setOnClickListener(this);
            tvOutbox.setOnClickListener(this);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    isInboxSelected = tab.getPosition() == 0;
                    toggleTab();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.fabCompose:
                    super.goToComposeMessageFragment();
                    break;

                case R.id.tvVideos:
                    if (!isInboxSelected) {
                        isInboxSelected = true;
                        toggleTab();
                    }
                    break;
                case R.id.tvPlaylists:
                    if (isInboxSelected) {
                        isInboxSelected = false;
                        toggleTab();
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void toggleTab() {
        try {
           /* if (isInboxSelected) {
                tvInbox.setTextColor(menuButtonActiveTitleColor);
                tvInbox.setBackgroundColor(menuButtonBackgroundColor);
                tvOutbox.setBackgroundColor(menuButtonBackgroundColor);
                tvOutbox.setTextColor(menuButtonTitleColor);

            } else {
                tvInbox.setBackgroundColor(menuButtonBackgroundColor);
                tvInbox.setTextColor(menuButtonTitleColor);
                tvOutbox.setBackgroundColor(menuButtonBackgroundColor);
                tvOutbox.setTextColor(menuButtonActiveTitleColor);


            }*/
            showSelectedScreen(isInboxSelected);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}

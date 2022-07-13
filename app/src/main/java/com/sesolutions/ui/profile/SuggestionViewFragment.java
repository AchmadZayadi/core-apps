package com.sesolutions.ui.profile;


import android.graphics.Color;
import com.google.android.material.tabs.TabLayout;
import android.view.View;

import com.sesolutions.R;
import com.sesolutions.ui.events.CreateEditEventFragment;
import com.sesolutions.ui.friend.FriendRequestFragment;
import com.sesolutions.ui.friend.SuggestionFragment;
import com.sesolutions.ui.member.MemberListFragment;
import com.sesolutions.ui.member.SearchMemberFragment;
import com.sesolutions.ui.member.SearchMemberSuggestionFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.wish.GlobalTabHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

public class SuggestionViewFragment extends GlobalTabHelper {
    public boolean isRequestLoaded;
    public boolean isSearchLoaded;
    public boolean isSuggestionLoaded;
    public boolean isFriendLoaded;

    @Override
    public void onStart() {
        super.onStart();
       /* if (activity.taskPerformed == Constant.FormType.CREATE_EVENT) {
            activity.taskPerformed = 0;
            isBrowseLoaded = false;
            isManageLoaded = false;
            loadFragmentIfNotLoaded(1);
            goToViewEventFragment(activity.taskId);
        } else if (activity.taskPerformed == Constant.FormType.EDIT_EVENT) {
            activity.taskPerformed = 0;
            isBrowseLoaded = false;
            isManageLoaded = false;
            loadFragmentIfNotLoaded(1);
        } else if (activity.taskPerformed == Constant.TASK_ALBUM_DELETED) {
            activity.taskPerformed = 0;
            isBrowseLoaded = false;
            isManageLoaded = false;
            refreshScreenByPosition(0);
            refreshScreenByPosition(1);
            // loadFragmentIfNotLoaded(0);
        }*/
    }

    @Override
    public void init() {
        FORM_CREATE = Constant.FormType.CREATE_EVENT;
        FORM_CREATE_URL = Constant.URL_CREATE_WISH;
        super.init();
        tvTitle.setText(getStrings(R.string.find_friends));
        ivSearch.setVisibility(View.GONE);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Constant.foregroundColor));
    }

    @Override
    public void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        adapter.addFragment(SuggestionFragment.newInstance(this), getStrings(R.string.title_suggestions));
        adapter.addFragment(new SearchMemberSuggestionFragment(), getStrings(R.string.search));
        adapter.addFragment(FriendRequestFragment.newInstance(this), getStrings(R.string.requests));
        adapter.addFragment(MemberListFragment.newInstance(SPref.getInstance().getLoggedInUserId(context), this), getStrings(R.string.friends));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }

    @Override
    public void updateToolbarIcons(int position) {
        selectedItem = position;
        // ivSearch.setVisibility(position <= 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateTitle(int index) {
    }

    @Override
    public void refreshScreenByPosition(int position) {

        try {
            switch (position) {
              /*  case 0:
                    if (!isBrowseLoaded)
                         (adapter.getItem(position)).onRefresh();
                    break;

                case 1:
                    if (!isPastLoaded)
                        ((UpcomingEventFragment) adapter.getItem(position)).onRefresh();
                    break;

                case 2:
                    if (!isCategoryLoaded)
                        // ((EventCategoriesFragment) adapter.getItem(position)).onRe
                        break;
                case 3:
                    if (!isManageLoaded)
                        ((UpcomingEventFragment) adapter.getItem(position)).onRefresh();
                    break;*/


            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void loadFragmentIfNotLoaded(int position) {
        try {
            switch (position) {
                case 0:
                    if (!isSuggestionLoaded)
                        (adapter.getItem(position)).initScreenData();
                    break;

                case 1:
                    if (!isSearchLoaded) {
                        adapter.getItem(position).initScreenData();
                        isSearchLoaded = true;
                    }
                    break;
                case 2:
                    if (!isRequestLoaded)
                        adapter.getItem(position).initScreenData();
                    break;

                case 3:
                    if (!isFriendLoaded)
                        (adapter.getItem(position)).initScreenData();
                    break;
             /*   case 3:
                    if (!isManageLoaded)
                        (adapter.getItem(position)).initScreenData();
                    break;*/

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void openCreateForm() {

        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditEventFragment.newinstance(Constant.FormType.CREATE_EVENT, Constant.URL_CREATE_EVENT, null))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchMemberFragment()).addToBackStack(null).commit();
    }
}

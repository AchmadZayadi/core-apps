package com.sesolutions.ui.poll;


import android.view.View;

import com.sesolutions.R;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.wish.GlobalTabHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;

public class PollParentFragment extends GlobalTabHelper {

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.CREATE_POLL) {
            activity.taskPerformed = 0;
            isBrowseLoaded = false;
            isManageLoaded = false;
            loadFragmentIfNotLoaded(1);
            openViewPollFragment(null,activity.taskId);
        } else if (activity.taskPerformed == Constant.FormType.EDIT_POLL) {
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
        }
    }

    @Override
    public void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        adapter.addFragment(PollFragment.newInstance(MenuTab.Poll.TYPE_BROWSE, this), getStrings(R.string.TAB_TITLE_POLL_1));

        if (SPref.getInstance().isLoggedIn(context)) {
            adapter.addFragment(PollFragment.newInstance(MenuTab.Poll.TYPE_MANAGE, this), getStrings(R.string.TAB_TITLE_POLL_2));
            showFabIcon();
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
    }

    @Override
    public void updateToolbarIcons(int position) {
        selectedItem = position;
        ivSearch.setVisibility(position <= 1 ? View.VISIBLE : View.GONE);
    }


    @Override
    public void refreshScreenByPosition(int position) {

        try {
            switch (position) {
                case 0:
                    if (!isBrowseLoaded)
                        (adapter.getItem(position)).onRefresh();
                    break;

                case 1:
                    if (!isManageLoaded)
                        adapter.getItem(position).onRefresh();
                    break;


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
                    if (!isBrowseLoaded)
                        (adapter.getItem(position)).initScreenData();
                    break;

                case 1:
                    if (!isManageLoaded)
                        (adapter.getItem(position)).initScreenData();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void openCreateForm() {
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditPollFragment.newInstance(Constant.FormType.CREATE_POLL, null, Constant.URL_CREATE_POLLS))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchPollFragment()).addToBackStack(null).commit();
    }
}

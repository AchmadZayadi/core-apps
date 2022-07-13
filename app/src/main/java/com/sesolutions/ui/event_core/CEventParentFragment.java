package com.sesolutions.ui.event_core;


import android.view.View;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.PageResponse;
import com.sesolutions.ui.common.CreateEditCoreForm;
import com.sesolutions.ui.events.UpcomingEventFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.wish.GlobalTabHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.ArrayList;

public class CEventParentFragment extends GlobalTabHelper {
    private static final int EVENT_DEFAULT = 600;
    public boolean isPastLoaded;

    //private final String BROWSE = "browse", PAST = "past", CATEGORY = "category", MANAGE = "manage", CREATE = "create";

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.CREATE_EVENT) {
            activity.taskPerformed = 0;
            //  isBrowseLoaded = false;
            //  isManageLoaded = false;
            loadFragmentIfNotLoaded(0);
            goToViewCEventFragment(activity.taskId);
        } else if (activity.taskPerformed == Constant.FormType.EDIT_EVENT ||
                activity.taskPerformed == Constant.FormType.EDIT_HOST ||
                activity.taskPerformed == Constant.FormType.EDIT_EVENT_LIST) {
            activity.taskPerformed = 0;
            //  isBrowseLoaded = false;
            //  isManageLoaded = false;


            adapter.getItem(tabLayout.getSelectedTabPosition()).onRefresh();
        } else if (activity.taskPerformed == Constant.TASK_ALBUM_DELETED) {
            activity.taskPerformed = 0;
            //  isBrowseLoaded = false;
            // isManageLoaded = false;
            refreshScreenByPosition(0);
            refreshScreenByPosition(1);
            // loadFragmentIfNotLoaded(0);
        }
    }

    @Override
    public void init() {
        super.init();
        ivSearch.setVisibility(View.VISIBLE);
        ivFilter.setOnClickListener(this);
    }


    @Override
    public void setupViewPager() {
        try {
            adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
            adapter.showTab(true);
            tabItems = new ArrayList<>();
            tabItems.add(new Options(CEventHelper.TYPE_BROWSE, getString(R.string.events_upcoming)));
            tabItems.add(new Options(CEventHelper.TYPE_LIST_PAST, getString(R.string.events_past)));
//            tabItems.add(new Options(CEventHelper.TYPE_CATEGORY, getString(R.string.category_browse)));

            if (SPref.getInstance().isLoggedIn(context)) {
                tabItems.add(new Options(CEventHelper.TYPE_MANAGE, getString(R.string.events_manage)));
                tabItems.add(new Options(CEventHelper.TYPE_CREATE, getString(R.string.EMPTY)));
            }

            tabLoaded = new boolean[tabItems.size()];
            for (Options opt : tabItems) {
                switch (opt.getName()) {
                   /* case UpcomingEventFragment.TYPE_HOME:
                    case "sesevent_main_mytickets":
                    case "sesevent_main_eventlocation":
                    case "seseventvideo_main_eventvideolocation":
                    case "seseventmusic_main_home":
                        //ignore these menu type
                        break;*/
                    case CEventHelper.TYPE_CATEGORY:
                        //tabItems.add(opt);
                        adapter.addFragment(CEventCategoriesFragment.newInstance(this), opt.getLabel());
                        break;
                   /* case CEventHelper.TYPE_VIDEO:
                        tabItems.add(opt);
                        adapter.addFragment(CEventVideoFragment.newInstance(this), opt.getLabel());
                        break;*/

                    case CEventHelper.TYPE_CREATE:
                        showFabIcon();
                        break;
                    default:
                        //tabItems.add(opt);
                        adapter.addFragment(CEventFragment.newInstance(opt.getName(), this), opt.getLabel());
                        break;
                }
            }
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(adapter.getCount());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void updateTotal(String index, int count) {
        updateTotal(getTabIndex(index), count);
    }

    public int getTabIndex(String selectedScreen) {
        for (int i = 0; i < tabItems.size(); i++) {
            if (tabItems.get(i).getName().equals(selectedScreen)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void updateToolbarIcons(int position) {
        selectedItem = position;
        ivSearch.setVisibility(canShowSearch(tabItems.get(position).getName()) ? View.VISIBLE : View.GONE);

        //show filter icon only in Mangae tab
        // ivFilter.setVisibility(CEventHelper.TYPE_MANAGE.equals(tabItems.get(position).getName()) ? View.VISIBLE : View.GONE);
    }

    private boolean canShowSearch(String name) {
        switch (name) {
            case CEventHelper.TYPE_MANAGE:
            case CEventHelper.TYPE_VIDEO:
            case CEventHelper.TYPE_CATEGORY:
                //  case CEventHelper.TYPE_BROWSE_LIST:
                //  case CEventHelper.TYPE_BROWSE_HOST:
                return false;
        }
        return true;
    }


    @Override
    public void refreshScreenByPosition(int position) {
    }

    @Override
    public void loadFragmentIfNotLoaded(int position) {
        try {
            if (!tabLoaded[position])
                (adapter.getItem(position)).initScreenData();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        try {
            switch (object1) {
                case Constant.Events.UPDATE_TOTAL:
                    //here postion is total count and object2 is selected screen
                    updateTotal("" + object2, postion);
                    break;
                case Constant.Events.SET_LOADED:
                    updateLoadStatus("" + object2, true);
                    break;
                case EVENT_DEFAULT:
                    hideBaseLoader();
                    FORM_CREATE = Constant.FormType.CREATE_EVENT;
                    FORM_CREATE_URL = Constant.URL_CREATE_EVENT;

                    if (object2 != null) {
                        PageResponse resp = new Gson().fromJson((String) object2, PageResponse.class);
                        if (resp.isSuccess()) {
                            if (resp.getResult() != null) {
                                tabItems = resp.getResult().getMenus();
                                super.init();
                                ivSearch.setVisibility(View.VISIBLE);
                                ivFilter.setOnClickListener(this);
                            } else {
                                somethingWrongMsg(v);
                            }
                        } else {
                            Util.showSnackbar(v, resp.getErrorMessage());
                            goIfPermissionDenied(resp.getError());
                        }
                    } else {
                        somethingWrongMsg(v);
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
            somethingWrongMsg(v);
        }

        return super.onItemClicked(object1, object2, postion);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ivFilter:
                ((CEventFragment) adapter.getItem(selectedItem)).onFilterClick(ivFilter);
                break;
        }
    }

    public void updateLoadStatus(String selectedScreen, boolean isLoaded) {
        try {
            tabLoaded[getTabIndex(selectedScreen)] = isLoaded;
        } catch (Exception e) {
            CustomLog.e("AIOOBE", "tabItem not found ->" + selectedScreen);
        }
    }

    @Override
    public void openCreateForm() {
        fragmentManager.beginTransaction().replace(R.id.container, CreateEditCoreForm.newInstance(Constant.FormType.CREATE_EVENT, null, Constant.URL_CREATE_CEVENT)).addToBackStack(null).commit();

        // openCreateEditFormFragment(Constant.FormType.CREATE_EVENT, null, Constant.URL_CREATE_CEVENT);
    }

    @Override
    public void goToSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, SearchCEventFragment.newInstance(tabItems.get(selectedItem).getName())).addToBackStack(null).commit();
    }
}

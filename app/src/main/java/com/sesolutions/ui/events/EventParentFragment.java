package com.sesolutions.ui.events;


import android.view.View;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.PageResponse;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.wish.GlobalTabHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventParentFragment extends GlobalTabHelper {
    private static final int EVENT_DEFAULT = 600;
    public boolean isPastLoaded;
    private List<Options> tempMenu;

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.CREATE_EVENT) {
            activity.taskPerformed = 0;
            //  isBrowseLoaded = false;
            //  isManageLoaded = false;
            loadFragmentIfNotLoaded(0);
            goToViewEventFragment(activity.taskId);
        } else if (activity.taskPerformed == Constant.FormType.EDIT_EVENT ||
                activity.taskPerformed == Constant.FormType.EDIT_HOST ||
                activity.taskPerformed == Constant.FormType.EDIT_EVENT_LIST) {
            activity.taskPerformed = 0;
            //  isBrowseLoaded = false;
            //  isManageLoaded = false;
            adapter.getItem(getTabIndex(UpcomingEventFragment.TYPE_MANAGE)).onRefresh();
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

        //first fetch tab items and then call init()
        Map<String, Object> map = new HashMap<>();
        if (isNetworkAvailable(context)) {
            showBaseLoader(false);
            new ApiController(Constant.URL_EVENT_DEFAULT, map, context, this, EVENT_DEFAULT).execute();
        } else {
            notInternetMsg(v);
        }
    }


    @Override
    public void setupViewPager() {
        try {
            adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
            adapter.showTab(true);
            tabItems = new ArrayList<>();
            for (Options opt : tempMenu) {
                switch (opt.getName()) {
                   /* case UpcomingEventFragment.TYPE_HOME:
                    case "sesevent_main_mytickets":
                    case "sesevent_main_eventlocation":
                    case "seseventvideo_main_eventvideolocation":
                    case "seseventmusic_main_home":
                        //ignore these menu type
                        break;*/
                    case UpcomingEventFragment.TYPE_CATEGORY:
                        tabItems.add(opt);
                        adapter.addFragment(EventCategoriesFragment.newInstance(this), opt.getLabel());
                        break;
                    case UpcomingEventFragment.TYPE_VIDEO:
                        tabItems.add(opt);
                        adapter.addFragment(EventVideoFragment.newInstance(this), opt.getLabel());
                        break;

                    case UpcomingEventFragment.TYPE_CALENDAR:
                        tabItems.add(opt);
                        adapter.addFragment(CalendarWebViewFragment.newInstance(opt.getValue(), null), opt.getLabel());
                        break;

                    case UpcomingEventFragment.TYPE_REVIEW:
                       /* Map map = new HashMap<>();
                        map.put(Constant.KEY_EVENT_ID, mEventId);
                        map.put(Constant.KEY_URI, Constant.URL_EVENT_REVIEWS);
                        map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.SES_EVENT);*/
                        tabItems.add(opt);
                        adapter.addFragment(ReviewFragment.newInstance(null, this), opt.getLabel());
                        break;
                    case UpcomingEventFragment.TYPE_CREATE:
                        showFabIcon();
                        break;
                    default:
                        tabItems.add(opt);
                        adapter.addFragment(UpcomingEventFragment.newInstance(opt.getName(), this), opt.getLabel());
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
        ivFilter.setVisibility(UpcomingEventFragment.TYPE_MANAGE.equals(tabItems.get(position).getName()) ? View.VISIBLE : View.GONE);
    }

    private boolean canShowSearch(String name) {
        switch (name) {
            case UpcomingEventFragment.TYPE_MANAGE:
            case UpcomingEventFragment.TYPE_CALENDAR:
            case UpcomingEventFragment.TYPE_REVIEW:
            case UpcomingEventFragment.TYPE_VIDEO:
            case UpcomingEventFragment.TYPE_CATEGORY:
                //  case UpcomingEventFragment.TYPE_BROWSE_LIST:
                //  case UpcomingEventFragment.TYPE_BROWSE_HOST:
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
                                tempMenu = resp.getResult().getMenus();
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
                ((UpcomingEventFragment) adapter.getItem(selectedItem)).onFilterClick(ivFilter);
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

        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        CreateEditEventFragment.newinstance(Constant.FormType.CREATE_EVENT, Constant.URL_CREATE_EVENT, null))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, SearchEventFragment.newInstance(tabItems.get(selectedItem).getName())).addToBackStack(null).commit();
    }
}

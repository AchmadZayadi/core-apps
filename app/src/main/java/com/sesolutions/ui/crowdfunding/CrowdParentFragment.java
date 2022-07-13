package com.sesolutions.ui.crowdfunding;


import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.fund.FundResponse;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.ui.wish.GlobalTabHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrowdParentFragment extends GlobalTabHelper {

    private static final int PAGE_DEFAULT = 600;
    private List<Options> tempMenu;
    private FloatingActionButton fab;

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.CREATE_FUND) {
            activity.taskPerformed = 0;
            int myPageIndex = getTabIndex(CrowdFragment.TYPE_MANAGE);
            int browseIndex = getTabIndex(CrowdFragment.TYPE_BROWSE);

            tabLoaded[myPageIndex] = false;
            tabLoaded[browseIndex] = false;
            if ("dashboard".equals(activity.stringValue)) {
                viewPager.setCurrentItem(browseIndex, true);
                if (selectedItem == browseIndex) {
                    adapter.getItem(browseIndex).onRefresh();
                }
            } else if ("manage".equals(activity.stringValue)) {
                viewPager.setCurrentItem(myPageIndex, true);
                if (selectedItem == myPageIndex) {
                    adapter.getItem(myPageIndex).onRefresh();
                }
            } else {
                if (activity.taskId > 0) {
                    CrowdUtil.openViewFragment(fragmentManager, activity.taskId);
                }
            }
        } else if (activity.taskPerformed == Constant.FormType.EDIT_FUND) {
            activity.taskPerformed = 0;
            int myPageIndex = getTabIndex(CrowdFragment.TYPE_MANAGE);
            tabLoaded[myPageIndex] = false;
            tabLoaded[getTabIndex(CrowdFragment.TYPE_BROWSE)] = false;
            viewPager.setCurrentItem(myPageIndex);
            adapter.getItem(myPageIndex).onRefresh();
            //loadFragmentIfNotLoaded(1);
        } else if (activity.taskPerformed == Constant.TASK_ALBUM_DELETED) {
            activity.taskPerformed = 0;
            int myPageIndex = getTabIndex(CrowdFragment.TYPE_MANAGE);
            int browseIndex = getTabIndex(CrowdFragment.TYPE_BROWSE);

            tabLoaded[myPageIndex] = false;
            tabLoaded[browseIndex] = false;
            adapter.getItem(myPageIndex).onRefresh();
            adapter.getItem(browseIndex).onRefresh();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_contest_parent, container, false);
        try {
            applyTheme(v);
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void init() {
        //first fetch tab items and then call init()
        Map<String, Object> map = new HashMap<>();
        if (isNetworkAvailable(context)) {
            showBaseLoader(false);
            new ApiController(Constant.URL_FUND_MENUS, map, context, this, PAGE_DEFAULT).execute();
        } else {
            notInternetMsg(v);
        }

        initButtons();
    }

    @Override
    public void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        tabItems = new ArrayList<>();
        for (Options opt : tempMenu) {
            switch (opt.getAction()) {

                case CrowdFragment.TYPE_CREATE:
                    showFabIcon();
                    break;
                case CrowdFragment.TYPE_FAQ_CROWD:
                case CrowdFragment.TYPE_FAQ:
                    break;

              /*  case CrowdFragment.TYPE_ALBUM_BROWSE:
                    tabItems.add(opt);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_URI, Constant.URL_PAGE_BROWSE_ALBUM);
                    map.put(Constant.SELECT, CrowdFragment.TYPE_ALBUM_BROWSE);
                    map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PAGE);
                    adapter.addFragment(PageAlbumFragment.newInstance(this, map), opt.getLabel());
                    break;*/
                case CrowdFragment.TYPE_RECIEVED_DONATION:
                case CrowdFragment.TYPE_MY_DONATION:
                case CrowdFragment.TYPE_CATEGORY:
                case CrowdFragment.TYPE_BROWSE:
                case CrowdFragment.TYPE_MANAGE:
                    tabItems.add(opt);
                    adapter.addFragment(CrowdFragment.newInstance(opt.getAction(), this), opt.getLabel());
                    break;
            }
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }

    @Override
    public void updateToolbarIcons(int position) {
        selectedItem = position;
        ivSearch.setVisibility(canShowSearch(tabItems.get(position).getAction()) ? View.VISIBLE : View.GONE);
        if (canShowCreate(tabItems.get(position).getAction())) {
            fab.show();
        } else {
            fab.hide();
        }
    }

    private boolean canShowSearch(String name) {
        switch (name) {
            case CrowdFragment.TYPE_MANAGE:
            case CrowdFragment.TYPE_CATEGORY:
                return false;
        }
        return true;
    }

    private boolean canShowCreate(String name) {
        switch (name) {

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
                case PAGE_DEFAULT:
                    hideBaseLoader();

                    if (object2 != null) {
                        FundResponse resp = new Gson().fromJson((String) object2, FundResponse.class);
                        if (resp.isSuccess()) {
                            if (resp.getResult() != null) {
                                tempMenu = resp.getResult().getMenus();
                                super.init();
                                ivSearch.setVisibility(View.VISIBLE);
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
                case Constant.Events.SET_LOADED:
                    updateLoadStatus("" + object2, true);
                    break;
                case Constant.Events.UPDATE_TOTAL:
                    updateTotal("" + object2, postion);
                    break;
                case Constant.Events.CLICKED_OPTION:
                    v.findViewById(R.id.ll1).setVisibility(View.GONE);
                    v.findViewById(R.id.ll4).setVisibility(View.GONE);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
            somethingWrongMsg(v);
        }
        return super.onItemClicked(object1, object2, postion);
    }

    private void initButtons() {
        fab = v.findViewById(R.id.fabAdd);
        super.updateFabColor(fab);
    }


    @Override
    public void showFabIcon() {
        ((FloatingActionButton) v.findViewById(R.id.fabAdd)).show();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ivFilter:
                //TODO  31/1/2019 add filter ;logic on @CrowdFragment and iuncomment this code
                // ((CrowdFragment) adapter.getItem(selectedItem)).onFilterClick(ivFilter);
                break;
        }
    }


    @Override
    public void openCreateForm() {
        //in case of public user ,send him to sign-in screen
        /*if (!SPref.getInstance().isLoggedIn(context)) {
            goToWelcome(1);
            return;
        }*/

        fetchFormData();

    }

    @Override
    public void goToSearchFragment() {
        /*if (CrowdFragment.TYPE_ALBUM_BROWSE.equals(tabItems.get(selectedItem).getAction())) {
            HashMap<String, Object> map = new HashMap<>();
            //  map.put(Constant.KEY_PAGE_ID, mPageId);
            map.put(Constant.KEY_URI, Constant.URL_PAGE_BROWSE_ALBUM);
            map.put(Constant.SELECT, CrowdFragment.TYPE_ALBUM_BROWSE);
            map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.PAGE);
            fragmentManager.beginTransaction().replace(R.id.container, SearchPageAlbumFragment.newInstance(null, map)).addToBackStack(null).commit();
        }  else*/
        {
            fragmentManager.beginTransaction().replace(R.id.container, new SearchCrowdFragment()).addToBackStack(null).commit();
        }
    }

    private void fetchFormData() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FUND_CREATE);
                    request.params.put(Constant.KEY_GET_FORM, 1);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (err.isSuccess()) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    if (resp != null && resp.getResult() != null && resp.getResult().getCategory() != null) {
                                        openSelectCategory(resp.getResult().getCategory(), null, Constant.ResourceType.FUND);
                                    } else {
                                        Dummy vo = new Gson().fromJson(response, Dummy.class);
                                        if (vo != null && vo.getResult() != null && vo.getResult().getFormfields() != null) {
                                            CrowdUtil.openCreateForm(fragmentManager, vo.getResult(), new HashMap<String, Object>());
                                        }
                                    }
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                }
                            } else {
                                Util.showSnackbar(v, getStrings(R.string.msg_something_wrong));
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);
                } catch (Exception ignore) {

                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public int getTabIndex(String selectedScreen) {
        for (int i = 0; i < tabItems.size(); i++) {
            if (tabItems.get(i).getAction().equals(selectedScreen)) {
                return i;
            }
        }
        return -1;
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
}

package com.sesolutions.ui.groups;


import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.groups.GroupResponse;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.packages.BrowseTransactionFragment;
import com.sesolutions.ui.packages.MyPackageFragment;
import com.sesolutions.ui.page.PageAlbumFragment;
import com.sesolutions.ui.page.PageVideoFragment;
import com.sesolutions.ui.page.SearchPageAlbumFragment;
import com.sesolutions.ui.page.SearchPollFragment;
import com.sesolutions.ui.poll.PollFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.ui.wish.GlobalTabHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupParentFragment extends GlobalTabHelper {

    private static final int GROUP_DEFAULT = 600;
    private List<Options> tempMenu;
    private FloatingActionButton fab;

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.CREATE_GROUP) {
            activity.taskPerformed = 0;
            int myPageIndex = getTabIndex(GroupFragment.TYPE_MANAGE);
            int browseIndex = getTabIndex(GroupFragment.TYPE_BROWSE);
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
                    openViewGroupFragment(activity.taskId);
                }
            }
        } else if (activity.taskPerformed == Constant.FormType.EDIT_GROUP) {
            activity.taskPerformed = 0;
            int myPageIndex = getTabIndex(GroupFragment.TYPE_MANAGE);
            tabLoaded[myPageIndex] = false;
            tabLoaded[getTabIndex(GroupFragment.TYPE_BROWSE)] = false;
            viewPager.setCurrentItem(myPageIndex);
            loadFragmentIfNotLoaded(myPageIndex);
        }
        else if (activity.taskPerformed == Constant.TASK_DELETE_POLL) {
            activity.taskPerformed = 0;
            int browseIndex = tabLayout.getSelectedTabPosition();
            adapter.getItem(browseIndex).onRefresh();
        }
        else if (activity.taskPerformed == Constant.TASK_ALBUM_DELETED) {
            activity.taskPerformed = 0;
            int myPageIndex = getTabIndex(GroupFragment.TYPE_MANAGE);
            int browseIndex = getTabIndex(GroupFragment.TYPE_BROWSE);
            int browsePollIndex = getTabIndex(MenuTab.Group.TYPE_BROWSE_POLL);

            try {
                tabLoaded[myPageIndex] = false;
                adapter.getItem(myPageIndex).onRefresh();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            try {
                tabLoaded[browseIndex] = false;
                adapter.getItem(browseIndex).onRefresh();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            try {
                tabLoaded[browsePollIndex] = false;
                adapter.getItem(browsePollIndex).onRefresh();
            }catch (Exception ex){
                ex.printStackTrace();
            }

        } else if (activity.taskPerformed == Constant.FormType.JOIN_GROUP) {
            activity.taskPerformed = 0;
            ((GroupFragment) adapter.getItem(selectedItem)).updateItemOption(activity.stringValue, activity.taskId);
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
         //   showBaseLoader(false);
            new ApiController(Constant.URL_GROUP_DEFAULT, map, context, this, GROUP_DEFAULT).execute();
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
                case GroupFragment.TYPE_HOME:
                    break;
                case GroupFragment.TYPE_CREATE:
                    showFabIcon();
                    break;
                case GroupFragment.TYPE_PACKAGE:
                    tabItems.add(opt);
                    adapter.addFragment(MyPackageFragment.newInstance(opt.getAction(), Constant.ResourceType.GROUP, this), opt.getLabel());
                    break;
                case MenuTab.Group.TYPE_BROWSE_POLL:
                    tabItems.add(opt);
                    adapter.addFragment(PollFragment.newInstance(opt.getAction(), this), opt.getLabel());
                    break;
                case GroupFragment.TYPE_ALBUM_BROWSE:
                    tabItems.add(opt);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_URI, Constant.URL_GROUP_BROWSE_ALBUM);
                    map.put(Constant.SELECT, GroupFragment.TYPE_ALBUM_BROWSE);
                    map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP);
                    adapter.addFragment(PageAlbumFragment.newInstance(map), opt.getLabel());
                    break;
                case GroupFragment.TYPE_VIDEO_BROWSE:
                    tabItems.add(opt);
                    adapter.addFragment(PageVideoFragment.newInstance(opt.getAction(), Constant.ResourceType.GROUP, this), opt.getLabel());
                    break;
                default:
                    tabItems.add(opt);
                    adapter.addFragment(GroupFragment.newInstance(opt.getAction(), this), opt.getLabel());
                    break;
            }
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }

    @Override
    public void updateToolbarIcons(int position) {
        selectedItem = position;
        ivSearch.setVisibility(canShowSearch(position) ? View.VISIBLE : View.GONE);
        //show filter icon only in Mangae tab
     //   ivFilter.setVisibility(GroupFragment.TYPE_MANAGE.equals(tabItems.get(position).getAction()) ? View.VISIBLE : View.GONE);

        if (canShowCreate(tabItems.get(position).getAction())) {
            fab.setImageResource(GroupFragment.TYPE_PACKAGE.equals(tabItems.get(selectedItem).getAction()) ? R.drawable.option_edit : R.drawable.add_24dp);
            updateFabColor(fab);
            fab.show();
        } else {
            fab.hide();
        }
    }

    private boolean canShowSearch(int position) {
        switch (tabItems.get(position).getAction()) {
            case GroupFragment.TYPE_CATEGORY:
            case GroupFragment.TYPE_MANAGE:
            case GroupFragment.TYPE_PACKAGE:
            case MenuTab.Group.TYPE_BROWSE_POLL:
                return false;
        }
        return true;
    }

    private boolean canShowCreate(String name) {
        switch (name) {
            case MenuTab.Group.TYPE_BROWSE_POLL:
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
                case GROUP_DEFAULT:
                    hideBaseLoader();

                    if (object2 != null) {
                        GroupResponse resp = new Gson().fromJson((String) object2, GroupResponse.class);
                        if (resp.isSuccess()) {
                            if (resp.getResult() != null) {
                                tempMenu = resp.getResult().getMenus();
                                super.init();
                                ivSearch.setVisibility(View.VISIBLE);
                                //filter icon : used to filter "My Groups"
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
        v.findViewById(R.id.llCreateMenu).setOnClickListener(this);
        //v.findViewById(R.id.fabCreate).setOnClickListener(this);

        v.findViewById(R.id.ll1).setOnClickListener(this);
        v.findViewById(R.id.ll2).setOnClickListener(this);
        v.findViewById(R.id.ll3).setOnClickListener(this);
        v.findViewById(R.id.ll4).setOnClickListener(this);
        v.findViewById(R.id.ll5).setOnClickListener(this);

        ((TextView) v.findViewById(R.id.tvCreate)).setText(R.string.title_create_page);

        // v.findViewById(R.id.ll1).setVisibility(resp.getPermission().getCanCreateAlbum() ? View.VISIBLE : View.GONE);
        //v.findViewById(R.id.ll2).setVisibility(null != myPackageList && myPackageList.size() > 0 ? View.VISIBLE : View.GONE);
        //v.findViewById(R.id.ll3).setVisibility(View.GONE);
        //v.findViewById(R.id.ll4).setVisibility(isMyPackageSelected ? View.VISIBLE : View.GONE);
        super.updateFabColor(v.findViewById(R.id.fabAdd));
    }

    private void showBottomOptions() {
        //  v.findViewById(R.id.ll4).setVisibility(isMyPackageSelected ? View.VISIBLE : View.GONE);
        //((TextView) v.findViewById(R.id.tvManage)).setText(isMyPackageSelected ? R.string.all_packages : R.string.my_package);
        this.v.findViewById(R.id.llCreateMenu).setVisibility(View.VISIBLE);
        this.v.findViewById(R.id.ll2).setVisibility(View.GONE);
        this.v.findViewById(R.id.ll4).setVisibility(getTotal(getTabIndex(GroupFragment.TYPE_PACKAGE)) > 0 ? View.VISIBLE : View.GONE);
        ((FloatingActionButton) v.findViewById(R.id.fabAdd)).hide();
    }


    @Override
    public void onBackPressed() {
        if (v.findViewById(R.id.llCreateMenu).getVisibility() == View.VISIBLE) {
            hideBottomOptions();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showFabIcon() {
        new Handler().postDelayed(this::hideBottomOptions, 1000);

    }

    private void hideBottomOptions() {
        this.v.findViewById(R.id.llCreateMenu).setVisibility(View.GONE);
        ((FloatingActionButton) v.findViewById(R.id.fabAdd)).show();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.ivFilter:
                ((GroupFragment) adapter.getItem(selectedItem)).onFilterClick(ivFilter);
                break;

            case R.id.ll1:
                hideBottomOptions();
                ((MyPackageFragment) adapter.getItem(selectedItem)).callCreateContestApi();
                break;
            case R.id.ll2:
                // hideBottomOptions();
                // isMyPackageSelected = !isMyPackageSelected;
                // ChangePackageViewingType(isMyPackageSelected);
                break;
            case R.id.ll3:
                hideBottomOptions();
                fragmentManager.beginTransaction().replace(R.id.container, BrowseTransactionFragment.newInstance(Constant.ResourceType.GROUP)).addToBackStack(null).commit();
                break;
            case R.id.ll4:
                hideBottomOptions();
                ((MyPackageFragment) adapter.getItem(selectedItem)).showCancelDialog();
                //callCancelPackageApi();
                break;
            case R.id.llCreateMenu:
            case R.id.ll5:
                hideBottomOptions();
                break;

        }
    }


    @Override
    public void openCreateForm() {
        //in case of public user ,send him to sign-in screen
        if (!SPref.getInstance().isLoggedIn(context)) {
            goToWelcome(1);
            return;
        }
        if (GroupFragment.TYPE_PACKAGE.equals(tabItems.get(selectedItem).getAction())) {
            showBottomOptions();
        } else if (MenuTab.Group.TYPE_BROWSE_POLL.equals(tabItems.get(selectedItem).getAction())) {
            //TODO open create page poll fragment
        } else {
            fetchFormData();
        }
    }

    @Override
    public void goToSearchFragment() {
        int pos = tabLayout.getSelectedTabPosition();
        if (GroupFragment.TYPE_ALBUM_BROWSE.equals(tabItems.get(pos).getAction())) {
            HashMap<String, Object> map = new HashMap<>();
            //  map.put(Constant.KEY_GROUP_ID, mPageId);
            map.put(Constant.KEY_URI, Constant.URL_GROUP_BROWSE_ALBUM);
            map.put(Constant.SELECT, GroupFragment.TYPE_ALBUM_BROWSE);
            map.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.GROUP);
            fragmentManager.beginTransaction().replace(R.id.container, SearchPageAlbumFragment.newInstance(null, map)).addToBackStack(null).commit();
        } else if (MenuTab.Group.TYPE_BROWSE_POLL.equals(tabItems.get(pos).getAction())) {
            fragmentManager.beginTransaction().replace(R.id.container, new SearchGroupPollFragment()).addToBackStack(null).commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.container, SearchGroupFragment.newInstance(tabItems.get(pos).getAction())).addToBackStack(null).commit();
        }
    }

    private void fetchFormData() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_GROUP_CREATE);
                    request.params.put(Constant.KEY_GET_FORM, 1);
                    request.params.put("filter_sort", "");

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
                                    if (resp != null && resp.getResult() != null && resp.getResult().arePackagesAvailabel()) {
                                        openSelectPackage(resp.getResult().getPackages(), resp.getResult().getExistingPackage(), null, Constant.ResourceType.GROUP);
                                    } else if (resp != null && resp.getResult() != null && resp.getResult().getCategory() != null) {
                                        openSelectCategory(resp.getResult().getCategory(), null, Constant.ResourceType.GROUP);
                                    } else {
                                        Dummy vo = new Gson().fromJson(response, Dummy.class);
                                        if (vo != null && vo.getResult() != null && vo.getResult().getFormfields() != null) {
                                            openGroupCreateForm(vo.getResult(), new HashMap<String, Object>());
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

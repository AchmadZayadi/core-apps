package com.sesolutions.ui.courses.course;


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
import com.sesolutions.responses.page.PageResponse;
import com.sesolutions.ui.courses.CourseUtil;
import com.sesolutions.ui.courses.myaccount.MyAccount;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.packages.BrowseTransactionFragment;
import com.sesolutions.ui.packages.MyPackageFragment;
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

public class CourseParentFragment extends GlobalTabHelper {

    private static final int PAGE_DEFAULT = 600;
    private List<Options> tempMenu;
    private PageResponse.Result createOptions;
    private FloatingActionButton fab;


    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.CREATE_PAGE) {
            activity.taskPerformed = 0;
            int myPageIndex = getTabIndex(CourseFragment.TYPE_MANAGE);
            int browseIndex = getTabIndex(CourseFragment.TYPE_BROWSE);

            tabLoaded[0] = false;
            tabLoaded[1] = false;
            if ("dashboard".equals(activity.stringValue)) {
                viewPager.setCurrentItem(1, true);
                if (selectedItem == 1) {
                    adapter.getItem(1).onRefresh();
                }
            } else if ("manage".equals(activity.stringValue)) {
                viewPager.setCurrentItem(0, true);
                if (selectedItem == 0) {
                    adapter.getItem(0).onRefresh();
                }
            } else {
                if (activity.taskId > 0) {
                    openViewPageFragment(activity.taskId);
                }
            }
        } else if (activity.taskPerformed == Constant.FormType.EDIT_PAGE) {
            activity.taskPerformed = 0;
            int myPageIndex = getTabIndex(CourseFragment.TYPE_MANAGE);
            tabLoaded[myPageIndex] = false;
            tabLoaded[getTabIndex(CourseFragment.TYPE_BROWSE)] = false;
            viewPager.setCurrentItem(myPageIndex);
            adapter.getItem(myPageIndex).onRefresh();
            //loadFragmentIfNotLoaded(1);
        } else if (activity.taskPerformed == Constant.TASK_ALBUM_DELETED) {
            activity.taskPerformed = 0;
            int myPageIndex = getTabIndex(CourseFragment.TYPE_MANAGE);
            int browseIndex = getTabIndex(CourseFragment.TYPE_BROWSE);
//            int browsePollIndex = getTabIndex(MenuTab.Page.TYPE_BROWSE_POLL);
            tabLoaded[0] = false;
            tabLoaded[1] = false;
//            tabLoaded[browsePollIndex] = false;
            adapter.getItem(0).onRefresh();
            adapter.getItem(1).onRefresh();
//            adapter.getItem(browsePollIndex).onRefresh();
        }
        else if (activity.taskPerformed == Constant.FormType.CREATE_COURSE) {
            activity.taskPerformed = 0;
            tabLoaded[0] = false;
            tabLoaded[1] = false;
//            loadFragmentIfNotLoaded(0);
            openViewCourseFragment(activity.taskId);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_classroom_parent, container, false);
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
            new ApiController(Constant.URL_INDEX_MENU, map, context, this, PAGE_DEFAULT).execute();
        } else {
            notInternetMsg(v);
        }
        if(SPref.getInstance().isLoggedIn(context)) {
            v.findViewById(R.id.ivCart).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivCart).setOnClickListener(this);
            v.findViewById(R.id.ivAccount).setVisibility(View.VISIBLE);
            v.findViewById(R.id.fabAdd).setVisibility(View.GONE);
            v.findViewById(R.id.ivAccount).setOnClickListener(this);
        }
        else {
            v.findViewById(R.id.ivCart).setVisibility(View.GONE);
            v.findViewById(R.id.ivCart).setVisibility(View.GONE);
            v.findViewById(R.id.fabAdd).setVisibility(View.GONE);
        }
        v.findViewById(R.id.ivCart).setVisibility(View.VISIBLE);
        v.findViewById(R.id.ivCart).setOnClickListener(this);
        initButtons();
    }

    @Override
    public void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        tabItems = new ArrayList<>();
        if(createOptions.CanCreateCourse() && SPref.getInstance().isLoggedIn(context)){
            v.findViewById(R.id.fabAdd).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.fabAdd).setVisibility(View.GONE);
        }
        for (Options opt : tempMenu) {
            switch (opt.getAction()) {
                case CourseFragment.TYPE_HOME:
                    break;
                case CourseFragment.TYPE_CREATE:
                    showFabIcon();
                    break;
                case "courses_main_browse":
                    adapter.addFragment(CourseFragment.newInstance(opt.getAction(), this), opt.getLabel());
                    adapter.addFragment(CourseCategoriesFragment.newInstance(this), "Browse Categories");
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
            fab.setImageResource(CourseFragment.TYPE_PACKAGE.equals(tabItems.get(selectedItem).getAction()) ? R.drawable.option_edit : R.drawable.add_24dp);
//            updateFabColor(fab);
            fab.show();
        } else {
            fab.hide();
        }
    }

    private boolean canShowSearch(String name) {
        switch (name) {
            case CourseFragment.TYPE_MANAGE:
            case CourseFragment.TYPE_REVIEW_BROWSE:
            case CourseFragment.TYPE_CATEGORY:
            case MenuTab.Page.TYPE_BROWSE_POLL:
                return false;
        }
        return true;
    }

    private boolean canShowCreate(String name) {
        switch (name) {
            case MenuTab.Page.TYPE_BROWSE_POLL:
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
                case PAGE_DEFAULT:
                    hideBaseLoader();

                    if (object2 != null) {
                        PageResponse resp = new Gson().fromJson((String) object2, PageResponse.class);
                        if (resp.isSuccess()) {
                            if (resp.getResult() != null) {
                                tempMenu = resp.getResult().getMenus();
                                createOptions = resp.getResult();
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
                    updateLoadStatus("" + object2, true, postion);
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
//        super.updateFabColor(v.findViewById(R.id.fabAdd));
    }

    private void showBottomOptions() {
        //  v.findViewById(R.id.ll4).setVisibility(isMyPackageSelected ? View.VISIBLE : View.GONE);
        //((TextView) v.findViewById(R.id.tvManage)).setText(isMyPackageSelected ? R.string.all_packages : R.string.my_package);
        this.v.findViewById(R.id.llCreateMenu).setVisibility(View.VISIBLE);
        this.v.findViewById(R.id.ll2).setVisibility(View.GONE);
        this.v.findViewById(R.id.ll4).setVisibility(getTotal(getTabIndex(CourseFragment.TYPE_PACKAGE)) > 0 ? View.VISIBLE : View.GONE);
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
                //TODO  20/9/2018 add filter ;logic on @PageFragment and iuncomment this code
                // ((PageFragment) adapter.getItem(selectedItem)).onFilterClick(ivFilter);
                break;
            case R.id.ivCart:
                CourseUtil.openCourseCartFragment(fragmentManager);
                break;
            case R.id.ivAccount:
                goToMyAccount();
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
                fragmentManager.beginTransaction().replace(R.id.container, BrowseTransactionFragment.newInstance(Constant.ResourceType.PAGE)).addToBackStack(null).commit();
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

    public void goToMyAccount(){
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        new MyAccount())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new SearchCourseFragment()).addToBackStack(null).commit();
    }

    @Override
    public void openCreateForm() {
//        Map<String, Object> map = new HashMap<>();
//        activity.filteredMap = null;
//        fragmentManager.beginTransaction()
//                .replace(R.id.container,
//                        CreateEditCourseFragment.newInstance(Constant.FormType.CREATE_COURSE, map, Constant.URL_CREATE_COURSE,null))
//                .addToBackStack(null)
//                .commit();
        fetchFormData();
    }

    private void fetchFormData() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CREATE_COURSE);
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
                                    if (resp != null && resp.getResult() != null && resp.getResult().arePackagesAvailabel()) {
                                        openSelectPackage(resp.getResult().getPackages(), resp.getResult().getExistingPackage(), null, Constant.ResourceType.PAGE);
                                    } else if (resp != null && resp.getResult() != null && resp.getResult().getCategory() != null) {
                                        openSelectCategory(resp.getResult().getCategory(), null, Constant.ResourceType.PAGE);
                                    } else {
                                        Dummy vo = new Gson().fromJson(response, Dummy.class);
                                        if (vo != null && vo.getResult() != null && vo.getResult().getFormfields() != null) {
                                            openCourseCreateForm(vo.getResult(), new HashMap<String, Object>());
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
        updateTotal(0, count);
    }

    public void updateLoadStatus(String selectedScreen, boolean isLoaded, int pos) {
        try {
            tabLoaded[pos] = isLoaded;
        } catch (Exception e) {
            CustomLog.e("AIOOBE", "tabItem not found ->" + selectedScreen);
        }
    }
}

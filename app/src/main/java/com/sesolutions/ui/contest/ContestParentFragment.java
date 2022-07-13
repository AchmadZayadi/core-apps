package com.sesolutions.ui.contest;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.sesolutions.responses.contest.ContestResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.packages.BrowseTransactionFragment;
import com.sesolutions.ui.packages.MyPackageFragment;
import com.sesolutions.ui.page.PageFragment;
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

import static com.sesolutions.utils.Constant.EDIT_CHANNEL_ME;

public class ContestParentFragment extends GlobalTabHelper {
    private static final int _DEFAULT = 600;
    private List<Options> tempMenu;
    private FloatingActionButton fab;

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.JOIN) {
            activity.taskPerformed = 0;
          //  adapter.getItem(getTabIndex(ContestHelper.TYPE_ENTRIES)).onRefresh();
         //   goToViewEntryFragment(activity.taskId);
            Intent intent2 = new Intent(activity, CommonActivity.class);
            intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.VIEW_ENTRY);
            intent2.putExtra(Constant.KEY_ID, activity.taskId);
            startActivityForResult(intent2, EDIT_CHANNEL_ME);
        } else if (activity.taskPerformed == Constant.FormType.CREATE_CONTEST) {
           activity.taskPerformed = 0;
            int index = getTabIndex(ContestHelper.TYPE_BROWSE);
            //int myIndex = getTabIndex(ContestHelper.TYPE_MANAGE);
            //adapter.getItem(myIndex).onRefresh();
            adapter.getItem(index).onRefresh();
            viewPager.setCurrentItem(index, true);
          /*  if ("dashboard".equals(activity.stringValue)) {
                viewPager.setCurrentItem(index, true);
            } else if (activity.taskId > 0) {
//                goToViewContestFragment(activity.taskId);
                Intent intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.VIEW_CONTEST);
                intent2.putExtra(Constant.KEY_ID, activity.taskId);
                startActivityForResult(intent2, EDIT_CHANNEL_ME);


            } else {
                viewPager.setCurrentItem(myIndex, true);
            }*/

        } else if (activity.taskPerformed == Constant.FormType.EDIT_CONTEST) {
            activity.taskPerformed = 0;
            adapter.getItem(getTabIndex(ContestHelper.TYPE_MANAGE)).onRefresh();
        } else if (activity.taskPerformed == Constant.TASK_ALBUM_DELETED) {
            activity.taskPerformed = 0;
            adapter.getItem(getTabIndex(ContestHelper.TYPE_MANAGE)).onRefresh();
            adapter.getItem(getTabIndex(ContestHelper.TYPE_BROWSE)).onRefresh();
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
            new ApiController(Constant.URL_CONTEST_DEFAULT, map, context, this, _DEFAULT).execute();
        } else {
            notInternetMsg(v);
        }

        initButtons();
    }

    @Override
    public void setupViewPager() {

        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);

        try {
            adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
            adapter.showTab(true);
            tabItems = new ArrayList<>();
            for (Options opt : tempMenu) {
                switch (opt.getAction()) {
                    case ContestHelper.TYPE_TEXT:
                    case ContestHelper.TYPE_PHOTO:
                    case ContestHelper.TYPE_VIDEO:
                    case ContestHelper.TYPE_AUDIO:
                        tabItems.add(opt);
                        adapter.addFragment(MediaContestFragment.newInstance(opt.getAction(), this), opt.getLabel());
                        break;
                    case ContestHelper.TYPE_MY_PACKAGE:
                        tabItems.add(opt);
                        adapter.addFragment(MyPackageFragment.newInstance(opt.getAction(), Constant.ResourceType.CONTEST, this), opt.getLabel());
                        break;
                    case ContestHelper.TYPE_MANAGE:
                        tabItems.add(opt);
                        adapter.addFragment(ContestManageFragment.newInstance(opt.getAction(), this), opt.getLabel());
                        break;
                    case ContestHelper.TYPE_CREATE:
                        showFabIcon();
                        break;
                    default:
                        tabItems.add(opt);
                        adapter.addFragment(ContestFragment.newInstance(opt.getAction(), this), opt.getLabel());
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
            if (tabItems.get(i).getAction().equals(selectedScreen)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void updateToolbarIcons(int position) {
        selectedItem = position;
        ivSearch.setVisibility(canShowSearch(tabItems.get(position).getAction()) ? View.VISIBLE : View.GONE);
        //show filter icon only in Manage tab
        ivFilter.setVisibility(ContestHelper.TYPE_MANAGE.equals(tabItems.get(position).getAction()) ? View.VISIBLE : View.GONE);
        fab.setImageResource(PageFragment.TYPE_PACKAGE.equals(tabItems.get(selectedItem).getAction()) ? R.drawable.option_edit : R.drawable.add_24dp);
        updateFabColor(fab);
    }

    private boolean canShowSearch(String name) {
        switch (name) {
            case ContestHelper.TYPE_MANAGE:
            case ContestHelper.TYPE_CATEGORY:
            case ContestHelper.TYPE_MY_PACKAGE:
            case ContestHelper.TYPE_MY_ORDERS:
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
                case _DEFAULT:
                    hideBaseLoader();

                    if (object2 != null) {
                        ContestResponse resp = new Gson().fromJson((String) object2, ContestResponse.class);
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
                case Constant.Events.UPDATE_TOTAL:
                    //here postion is total count and object2 is selected screen
                    updateTotal("" + object2, postion);
                    break;
                case Constant.Events.SET_LOADED:
                    updateLoadStatus("" + object2, true);
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
        v.findViewById(R.id.llCreateMenu).setOnClickListener(this);
        fab = v.findViewById(R.id.fabAdd);
        //v.findViewById(R.id.fabCreate).setOnClickListener(this);

        v.findViewById(R.id.ll1).setOnClickListener(this);
        v.findViewById(R.id.ll2).setOnClickListener(this);
        v.findViewById(R.id.ll3).setOnClickListener(this);
        v.findViewById(R.id.ll4).setOnClickListener(this);
        v.findViewById(R.id.ll5).setOnClickListener(this);

        ((TextView) v.findViewById(R.id.tvCreate)).setText(R.string.title_create_contest);

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
        this.v.findViewById(R.id.ll4).setVisibility(View.VISIBLE);
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
                ((ContestManageFragment) adapter.getItem(selectedItem)).onFilterClick(ivFilter);
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
                fragmentManager.beginTransaction().replace(R.id.container, BrowseTransactionFragment.newInstance(Constant.ResourceType.CONTEST)).addToBackStack(null).commit();
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

    public void updateLoadStatus(String selectedScreen, boolean isLoaded) {
        try {
            tabLoaded[getTabIndex(selectedScreen)] = isLoaded;
        } catch (Exception e) {
            CustomLog.e("AIOOBE", "tabItem not found ->" + selectedScreen);
        }
    }

    @Override
    public void openCreateForm() {
        if (ContestHelper.TYPE_MY_PACKAGE.equals(tabItems.get(selectedItem).getAction())) {
            showBottomOptions();
        } else {
            fetchFormData();
        }

    }

    private void fetchFormData() {

        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CONTEST_CREATE);
                    request.params.put(Constant.KEY_GET_FORM, 1);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (err.isSuccess()) {
                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        if (resp != null && resp.getResult() != null && resp.getResult().arePackagesAvailabel()) {
                                            openSelectPackage(resp.getResult().getPackages(), resp.getResult().getExistingPackage(), null, Constant.ResourceType.CONTEST);
                                        } else if (resp != null && resp.getResult() != null && resp.getResult().getCategory() != null) {
                                            openSelectCategory(resp.getResult().getCategory(), null, Constant.ResourceType.CONTEST);
                                        } else {
                                            Dummy vo = new Gson().fromJson(response, Dummy.class);
                                            if (vo != null && vo.getResult() != null && vo.getResult().getFormfields() != null) {
                                                openContestCreateForm(vo.getResult(), new HashMap<String, Object>());
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
                        }
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


    @Override
    public void goToSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.container, SearchContestFragment.newInstance(tabItems.get(selectedItem).getAction())).addToBackStack(null).commit();
    }
}

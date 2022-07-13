package com.sesolutions.ui.bookings;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SuccessResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.PageResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.classified.CreateClassifiedFragment;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.CreateEditCoreForm;
import com.sesolutions.ui.courses.CourseUtil;
import com.sesolutions.ui.events.CreateEditEventFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.packages.BrowseTransactionFragment;
import com.sesolutions.ui.packages.MyPackageFragment;
import com.sesolutions.ui.page.CreateEditPageFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.ui.wish.GlobalTabHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingParentFragment extends GlobalTabHelper {

    private static final int PAGE_DEFAULT = 600;
    private List<Options> tempMenu;
    private PageResponse.Result result;
    private final int REQ_DELETE = 400;
    private FloatingActionButton fab;

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.CREATE_PAGE) {
            activity.taskPerformed = 0;
            int myPageIndex = 0;
            int browseIndex = 1;

            tabLoaded[0] = false;
            tabLoaded[1] = false;
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
                    openViewClassroomFragment(activity.taskId);
                }
            }
        } else if (activity.taskPerformed == Constant.FormType.EDIT_CLASSROOM) {
            activity.taskPerformed = 0;
            int myPageIndex = getTabIndex(MenuTab.Store.CLASS_CAT);
            tabLoaded[1] = false;
            tabLoaded[0] = false;
            viewPager.setCurrentItem(0);
            adapter.getItem(0).onRefresh();
            //loadFragmentIfNotLoaded(1);
        } else if (activity.taskPerformed == Constant.TASK_ALBUM_DELETED) {
            activity.taskPerformed = 0;
            int myPageIndex = getTabIndex("eclassroom_main_browse");
            int browseIndex = getTabIndex(MenuTab.Store.CLASS_CAT);
//            int browsePollIndex = getTabIndex(MenuTab.Page.TYPE_BROWSE_POLL);
            tabLoaded[0] = false;
            tabLoaded[1] = false;
//            tabLoaded[browsePollIndex] = false;
            adapter.getItem(0).onRefresh();
            adapter.getItem(1).onRefresh();
//            adapter.getItem(browsePollIndex).onRefresh();
        } else if (activity.taskPerformed == Constant.TASK_DELETE_SERVICE) {
            activity.taskPerformed = 0;
            tabLoaded[0] = false;
            adapter.getItem(0).onRefresh();
        } else if (activity.taskPerformed == Constant.FormType.CREATE_CLASSROOM) {
            activity.taskPerformed = 0;
            tabLoaded[0] = false;
            tabLoaded[1] = false;
//            loadFragmentIfNotLoaded(0);
            openViewClassroomFragment(activity.taskId);
        } else if (activity.taskPerformed == Constant.FormType.BECOME_PROFESSIONAL) {
            activity.taskPerformed = 0;
            Util.showSnackbar(v, "You have successfully become a professional");
            result.setProfessional(true);
            updateButtons();
        }else if (activity.taskPerformed == Constant.FormType.BECOME_NORMAL) {
            activity.taskPerformed = 0;
            Util.showSnackbar(v, "You have successfully become a normal user");
            result.setProfessional(false);
            updateButtons();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_booking_parent, container, false);
        getActivity().getWindow().setStatusBarColor(Color.parseColor(Constant.colorPrimary));
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
            new ApiController(Constant.URL_BOOKING_INDEX, map, context, this, PAGE_DEFAULT).execute();
        } else {
            notInternetMsg(v);
        }
        if (SPref.getInstance().isLoggedIn(context)) {
            v.findViewById(R.id.ivContact).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivContact).setOnClickListener(this);
            v.findViewById(R.id.fabAdd).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.fabAdd).setVisibility(View.GONE);
            v.findViewById(R.id.ivContact).setVisibility(View.GONE);
        }
        initButtons();
    }

    public void updateButtons() {
        if (result.isProfessional()) {
            v.findViewById(R.id.ivSetting).setVisibility(View.VISIBLE);
            ((ImageView) v.findViewById(R.id.ivSetting)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_settings));
            v.findViewById(R.id.ivSetting).setOnClickListener(v -> {
                String settingUrl = URL.BASE_URL + "bookings/settings" + "?removeSiteHeaderFooter=true";
                openWebView(settingUrl, "Settings");
            });
        } else {
            v.findViewById(R.id.ivSetting).setVisibility(View.VISIBLE);
            ((ImageView) v.findViewById(R.id.ivSetting)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_plus));

            v.findViewById(R.id.ivSetting).setOnClickListener(v -> {
                fetchFormData();
            });
        }
    }

    @Override
    public void setupViewPager() {
        adapter = new MessageDashboardViewPagerAdapter(fragmentManager);
        adapter.showTab(true);
        tabItems = new ArrayList<>();
        if (result.isProfessional()) {
            v.findViewById(R.id.ivSetting).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivSetting).setOnClickListener(v -> {
                String settingUrl = URL.BASE_URL + "bookings/settings" + "?removeSiteHeaderFooter=true";
                openWebView(settingUrl, "Settings");
            });
        } else {
            v.findViewById(R.id.ivSetting).setVisibility(View.VISIBLE);
            ((ImageView) v.findViewById(R.id.ivSetting)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_plus));

            v.findViewById(R.id.ivSetting).setOnClickListener(v -> {
                fetchFormData();

            });
        }

        for (Options opt : tempMenu) {
            switch (opt.getAction()) {
                case ProfessionalFragment.TYPE_HOME:
                    break;

                case MenuTab.BOOKING.BROWSE_PROFESSIONAL:
                    adapter.addFragment(ProfessionalFragment.newInstance(opt.getAction(), this), opt.getLabel());
                    break;
                case MenuTab.BOOKING.BROWSE_SERVICE:
                    adapter.addFragment(ServiceFragment.newInstance(opt.getAction(), this), opt.getLabel());
                    break;
            }
        }
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }

    @Override
    public void updateToolbarIcons(int position) {
        selectedItem = position;
        ivSearch.setVisibility(canShowSearch(tabItems.get(position).getAction()) ? View.GONE : View.GONE);
        if (canShowCreate(tabItems.get(position).getAction())) {
            fab.setImageResource(ProfessionalFragment.TYPE_PACKAGE.equals(tabItems.get(selectedItem).getAction()) ? R.drawable.option_edit : R.drawable.add_24dp);
//            updateFabColor(fab);
            fab.show();
        } else {
            fab.hide();
        }
    }

    private boolean canShowSearch(String name) {
        switch (name) {
            case ProfessionalFragment.TYPE_MANAGE:
            case ProfessionalFragment.TYPE_REVIEW_BROWSE:
            case MenuTab.Store.CLASS_CAT:
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

    public void goToMyAccount() {
        Map<String, Object> map = new HashMap<>();
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                        BecomeProfessionalFragment.newInstance(Constant.FormType.BECOME_PROFESSIONAL, map, Constant.URL_BECOME_PROFESSIONAL, null))
                .addToBackStack(null)
                .commit();
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
                                result = resp.getResult();
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
                case Constant.Events.UPDATE_TYPE:
                    String title = "Professionals " + "(" + postion + ")";
                    ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
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
        this.v.findViewById(R.id.ll4).setVisibility(getTotal(getTabIndex(ProfessionalFragment.TYPE_PACKAGE)) > 0 ? View.VISIBLE : View.GONE);
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

            case R.id.ivContact:
                String appointmentUrl = URL.BASE_URL + "bookings/appointments?removeSiteHeaderFooter=true";
                openWebView(appointmentUrl, "Appointments");
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

    @Override
    public void openCreateForm() {
        fetchFormData();
    }

    public void goToSearchFragment() {
        try {
            BaseFragment frag = null;
            int pos = tabLayout.getSelectedTabPosition();
            if (pos == 0) {
                frag = new SearchService();
            } else if (pos == 1) {
                frag = new SearchProfessional();
            }
            fragmentManager.beginTransaction().replace(R.id.container, frag).addToBackStack(null).commit();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showDeleteDialog(int profId) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = (TextView) progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(getStrings(R.string.MSG_DELETE_REENABLE_PRO));

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText("Enable");
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText("Cancel");

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi(REQ_DELETE, Constant.URL_PROFESSIONAL_REENABLE, profId);
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi(final int REQ, String url, int profId) {

        try {
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_PROFESSIONAL_ID, profId);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        if (REQ == REQ_DELETE) {
                                            Map<String, Object> map = new HashMap<>();
                                            result.setProfessional(true);
                                            Util.showSnackbar(v, "You have successfully re-enabled your Professional profile.");
                                            updateButtons();

                                        } else if (REQ > REQ_DELETE) {
                                            SuccessResponse res = new Gson().fromJson(response, SuccessResponse.class);
                                            Util.showSnackbar(v, res.getResult().getMessage());
                                        }
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                }

                            } catch (Exception e) {
                                hideBaseLoader();
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();
                }
            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
        }

    }

    private void fetchFormData() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_BECOME_PROFESSIONAL);
                    request.params.put(Constant.KEY_GET_FORM, 1);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            String message = Constant.EMPTY;
                            JSONObject json = new JSONObject(response);
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (err.isSuccess()) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    if (resp != null && resp.getResult() != null && resp.getResult().arePackagesAvailabel()) {
                                        openSelectPackage(resp.getResult().getPackages(), resp.getResult().getExistingPackage(), null, Constant.ResourceType.CLASSROOM);
                                    } else if (resp != null && resp.getResult() != null && resp.getResult().getCategory() != null) {
                                        openSelectCategory(resp.getResult().getCategory(), null, Constant.ResourceType.PAGE);
                                    } else {
                                        Dummy vo = new Gson().fromJson(response, Dummy.class);
                                        if (vo != null && vo.getResult() != null && vo.getResult().getFormfields() != null) {
                                            goToMyAccount();
                                        } else if (json.getJSONObject(Constant.KEY_RESULT).getString("message") instanceof String) {
                                            message = json.getJSONObject(Constant.KEY_RESULT).getString("message");
                                            CustomLog.e("message:", message);
                                            int proId = json.getJSONObject(Constant.KEY_RESULT).getInt("professional_id");
                                            CustomLog.e("proId:", "" + proId);
                                            showDeleteDialog(proId);
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

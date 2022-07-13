package com.sesolutions.ui.packages;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.animate.DetailsTransition;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SuccessResponse;
import com.sesolutions.responses.contest.Packages;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageFragment extends BaseFragment implements OnUserClickedListener<Integer, Object>, View.OnClickListener {

    private final int REQ_DELETE = 104;
    private View v;
    private List<Packages> allPackageList;
    private List<Packages> myPackageList;
    private List<Packages> list;
    private View ivBack;
    private Map<String, Object> map;
    private String rcType;
    private int selectedPosition;
    // private Bundle bundle;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setEnterTransition(new Explode());
            setExitTransition(new Explode());
            setSharedElementEnterTransition(new DetailsTransition());
            setSharedElementReturnTransition(new DetailsTransition());
            setAllowEnterTransitionOverlap(false);
            setAllowReturnTransitionOverlap(false);
        }
    }

    public static PackageFragment newInstance(List<Packages> packages, List<Packages> myPackages, Map<String, Object> map, String rcType) {
        PackageFragment frag = new PackageFragment();
        frag.allPackageList = packages;
        frag.myPackageList = myPackages;
        frag.map = map;
        // frag.bundle = bundle;
        frag.rcType = rcType;
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_package_selection, container, false);
        setUpModuleData();
        initScreenData();
        return v;
    }

    private String DESCRIPTION_TEXT, CREATE_URL, DELETE_URL;
    private int CREATE_TITLE;

    private void setUpModuleData() {
        try {
            switch (rcType) {
                case Constant.ResourceType.PAGE:
                    DESCRIPTION_TEXT = getStrings(R.string.page);
                    DELETE_URL = Constant.URL_PAGE_DELETE_PACKAGE;
                    CREATE_URL = Constant.URL_PAGE_CREATE;
                    CREATE_TITLE = R.string.title_create_page;
                    break;
                case Constant.ResourceType.GROUP:
                    DESCRIPTION_TEXT = getStrings(R.string.group);
                    DELETE_URL = Constant.URL_GROUP_DELETE_PACKAGE;
                    CREATE_URL = Constant.URL_GROUP_CREATE;
                    CREATE_TITLE = R.string.title_create_group;
                    break;
                case Constant.ResourceType.BUSINESS:
                    DESCRIPTION_TEXT = getStrings(R.string.business);
                    DELETE_URL = Constant.URL_BUSINESS_DELETE_PACKAGE;
                    CREATE_URL = Constant.URL_BUSINESS_CREATE;
                    CREATE_TITLE = R.string.title_create_business;
                    break;
                default:
                    //default is contest
                    DESCRIPTION_TEXT = getStrings(R.string.contest);
                    DELETE_URL = Constant.URL_CONTEST_DELETE_PACKAGE;
                    CREATE_URL = Constant.URL_CONTEST_CREATE;
                    CREATE_TITLE = R.string.title_create_contest;
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void openCreateForm(Dummy.Result result, Map<String, Object> map) {
        switch (rcType) {

            case Constant.ResourceType.PAGE:
                openPageCreateForm(result, map);
                break;
            case Constant.ResourceType.GROUP:
                openGroupCreateForm(result, map);
                break;
            case Constant.ResourceType.BUSINESS:
                openBusinessCreateForm(result, map);
                break;
            default:
                openContestCreateForm(result, map);
                break;


        }
    }

    @Override
    public void initScreenData() {
        ((TextView) v.findViewById(R.id.tvSelectDesc)).setText(getString(R.string.select_a_package_desc, DESCRIPTION_TEXT));
        ((TextView) v.findViewById(R.id.tvCreate)).setText(CREATE_TITLE);
        v.findViewById(R.id.rlDetail).setBackgroundColor(Color.parseColor(Constant.colorPrimary));
        initCollapsingToolbar();
        // updateFabColor();
        initPackageRecyclerView();
        initButtons();
        updateFabIcon();
    }

    private void updateFabIcon() {
        ((FloatingActionButton) v.findViewById(R.id.fabCreate)).setImageResource(null != myPackageList && myPackageList.size() > 0 ? R.drawable.option_edit : R.drawable.add_24dp);
    }

    private boolean isMyPackageSelected;

    private void ChangePackageViewingType() {

        list.addAll(isMyPackageSelected ? myPackageList : allPackageList);
        adapterPhoto.notifyDataSetChanged();
        updateBottomLayout(0);
    }

    private void initCollapsingToolbar() {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setContentScrimColor(Color.parseColor(Constant.colorPrimary));
        AppBarLayout appBarLayout = v.findViewById(R.id.appbar);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                try {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        //ivBack.setVisibility(View.VISIBLE);
                        collapsingToolbar.setTitle(getStrings(R.string.select_a_package));
                        isShow = true;
                    } else if (isShow) {
                        // ivBack.setVisibility(View.GONE);
                        collapsingToolbar.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                        isShow = false;
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }
        });
    }

    private PackageAdapter adapterPhoto;
    // private PageIndicatorView pageIndicatorView;

    private void initPackageRecyclerView() {
        try {
            MultiSnapRecyclerView rvPhotos = (MultiSnapRecyclerView) v.findViewById(R.id.rvPackage);
            // pageIndicatorView = v.findViewById(R.id.pageIndicatorView);
            list = new ArrayList<>();
            list.addAll(allPackageList);
            rvPhotos.setHasFixedSize(true);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rvPhotos.setLayoutManager(layoutManager);
            adapterPhoto = new PackageAdapter(list, context, this);
            rvPhotos.setAdapter(adapterPhoto);
            //  pageIndicatorView.setCount(adapterPhoto.getItemCount());
            // pageIndicatorView.setSelection(position12);
            rvPhotos.setOnSnapListener(this::updateBottomLayout);
            //by Default show first item details
            updateBottomLayout(0);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void initButtons() {
        v.findViewById(R.id.llCreateMenu).setOnClickListener(this);
        v.findViewById(R.id.fabCreate).setOnClickListener(this);

        v.findViewById(R.id.ll1).setOnClickListener(this);
        v.findViewById(R.id.ll2).setOnClickListener(this);
        v.findViewById(R.id.ll3).setOnClickListener(this);
        v.findViewById(R.id.ll4).setOnClickListener(this);
        v.findViewById(R.id.ll5).setOnClickListener(this);

        // v.findViewById(R.id.ll1).setVisibility(resp.getPermission().getCanCreateAlbum() ? View.VISIBLE : View.GONE);
        //  v.findViewById(R.id.ll2).setVisibility(null != myPackageList && myPackageList.size() > 0 ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.ll3).setVisibility(View.GONE);
        //   v.findViewById(R.id.ll4).setVisibility(isMyPackageSelected ? View.VISIBLE : View.GONE);
        updateFabColor(v.findViewById(R.id.fabCreate));
    }

    private void showBottomOptions() {

        if (null != myPackageList && myPackageList.size() > 0) {
            v.findViewById(R.id.ll4).setVisibility(isMyPackageSelected ? View.VISIBLE : View.GONE);
            v.findViewById(R.id.ll2).setVisibility(View.VISIBLE);
        } else {
            v.findViewById(R.id.ll4).setVisibility(View.GONE);
            v.findViewById(R.id.ll2).setVisibility(View.GONE);
        }

        v.findViewById(R.id.ll1).setVisibility(list.size() > 0 ? View.VISIBLE : View.GONE);
        ((TextView) v.findViewById(R.id.tvManage)).setText(isMyPackageSelected ? R.string.all_packages : R.string.my_package);
        this.v.findViewById(R.id.llCreateMenu).setVisibility(View.VISIBLE);
        hideFab((FloatingActionButton) v.findViewById(R.id.fabCreate));
    }

    private void hideBottomOptions() {
        this.v.findViewById(R.id.llCreateMenu).setVisibility(View.GONE);
        showFab(v.findViewById(R.id.fabCreate));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabCreate:
                if (null != myPackageList && myPackageList.size() > 0) {
                    showBottomOptions();
                } else {
                    callCreateApi();
                }
                break;

            case R.id.ll1:
                hideBottomOptions();
                callCreateApi();
                break;
            case R.id.ll2:
                hideBottomOptions();
                isMyPackageSelected = !isMyPackageSelected;
                list.clear();
                adapterPhoto.notifyDataSetChanged();
                new Handler().postDelayed(this::ChangePackageViewingType, 200);
                break;
            case R.id.ll3:
                hideBottomOptions();
                //onItemClicked(REQ_TRANSACTIONS, null, REQ_TRANSACTIONS);
                break;
            case R.id.ll4:
                hideBottomOptions();
                showCancelDialog();
                break;
            case R.id.llCreateMenu:
            case R.id.ll5:
                hideBottomOptions();
                break;
        }
    }

    public void showCancelDialog() {
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
            tvMsg.setText(R.string.package_cancel_desc);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.delete);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(getStrings(R.string.CANCEL));

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                Map<String, Object> map = new HashMap<>();
                //CustomLog.e("package_id", "" + list.get(selectedPosition).getPackage_id());
                map.put(Constant.KEY_PACKAGE_ID, list.get(selectedPosition).getPackage_id());
                new ApiController(DELETE_URL, map, context, this, REQ_DELETE).execute();
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.CREATE_CONTEST ||
                activity.taskPerformed == Constant.FormType.CREATE_PAGE
                || activity.taskPerformed == Constant.FormType.CREATE_GROUP
                || activity.taskPerformed == Constant.FormType.CREATE_BUSINESS
                ) {
            onBackPressed();
        } else {
            activity.setStatusBarColor(Color.parseColor(Constant.colorPrimary));
        }
    }


    @Override
    public void onStop() {
        activity.setStatusBarColor(Util.manipulateColor(Color.parseColor(Constant.colorPrimary)));
        super.onStop();
    }

    private void updateBottomLayout(int position) {
        selectedPosition = position;
        List<Options> optList = list.get(position).getParams();
        ((ImageView) v.findViewById(R.id.ivCurveBg)).setColorFilter(Color.parseColor(Constant.foregroundColor));

        LinearLayoutCompat llBasic = v.findViewById(R.id.llBottom);
        llBasic.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
        llBasic.removeAllViews();
        llBasic.setVisibility(View.VISIBLE);
        TextView view2 = null;
        //Adding creating createdBy
        for (Options opt : optList) {
            switch (opt.getName()) {
                case "package_description":
                    view2 = (TextView) getLayoutInflater().inflate(R.layout.textview_seeall, (ViewGroup) llBasic, false);
                    view2.setText(opt.getValue());
                    //llBasic.addView(view2);
                    break;

                default:
                    View view1 = getLayoutInflater().inflate(R.layout.item_package_detail, (ViewGroup) llBasic, false);
                    ((TextView) view1.findViewById(R.id.tv1)).setText(opt.getLabel());
                    if ("image".equals(opt.getAction())) {
                        view1.findViewById(R.id.tv2).setVisibility(View.GONE);
                        (view1.findViewById(R.id.ivCross)).setVisibility("0".equals(opt.getValue()) ? View.VISIBLE : View.GONE);
                        (view1.findViewById(R.id.ivCorrect)).setVisibility("0".equals(opt.getValue()) ? View.GONE : View.VISIBLE);
                    } else {
                        ((TextView) view1.findViewById(R.id.tv2)).setText(opt.getValue());
                    }
                    llBasic.addView(view1);
                    break;
            }

        }
        //add description view at last of list
        if (null != view2) {
            llBasic.addView(view2);
        }
        applyTheme(llBasic);
    }

    private void hideFab(FloatingActionButton fab) {
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();

        if (behavior != null) {
            behavior.setAutoHideEnabled(false);
        }

        fab.hide();
    }

    private void showFab(FloatingActionButton fab) {
        fab.show();
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButton.Behavior behavior =
                (FloatingActionButton.Behavior) params.getBehavior();

        if (behavior != null) {
            behavior.setAutoHideEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (v.findViewById(R.id.llCreateMenu).getVisibility() == View.VISIBLE) {
            hideBottomOptions();
        } else {
            super.onBackPressed();
        }
    }

    private void callCreateApi() {
        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(CREATE_URL);
                    request.params.put(Constant.KEY_GET_FORM, 1);
                    if (isMyPackageSelected) {
                        request.params.put(Constant.KEY_EXISTING_PACKAGE_ID, list.get(selectedPosition).getExistingPackageId());
                    } else {
                        request.params.put(Constant.KEY_PACKAGE_ID, list.get(selectedPosition).getPackage_id());
                    }

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
                                        Map<String, Object> map1 = new HashMap<>();
                                        if (isMyPackageSelected) {
                                            map1.put(Constant.KEY_EXISTING_PACKAGE_ID, list.get(selectedPosition).getExistingPackageId());
                                        } else {
                                            map1.put(Constant.KEY_PACKAGE_ID, list.get(selectedPosition).getPackage_id());
                                        }
                                        //map1.put(isMyPackageSelected ? "existing_package_id" : "package_id", list.get(selectedPosition).getPackage_id());
                                        if (resp != null && resp.getResult() != null && resp.getResult().arePackagesAvailabel()) {
                                            openSelectPackage(resp.getResult().getPackages(), resp.getResult().getExistingPackage(), null, rcType);
                                        } else if (resp != null && resp.getResult() != null && resp.getResult().getCategory() != null) {
                                            openSelectCategory(resp.getResult().getCategory(), map1, rcType);
                                        } else {
                                            Dummy vo = new Gson().fromJson(response, Dummy.class);
                                            if (vo != null && vo.getResult() != null && vo.getResult().getFormfields() != null) {
                                                openCreateForm(vo.getResult(), map1);
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
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        switch (eventType) {
            case REQ_DELETE:
                if (data != null) {
                    SuccessResponse resp = new Gson().fromJson("" + data, SuccessResponse.class);
                    if (TextUtils.isEmpty(resp.getError())) {
                        Util.showSnackbar(v, resp.getResult().getMessage());
                        myPackageList.remove(selectedPosition);
                        list.remove(selectedPosition);
                        adapterPhoto.notifyItemRemoved(selectedPosition);
                        if (list.size() == 0) {
                            showHideNoDataLayout(resp.getResult().getMessage());
                        }
                    } else {
                        Util.showSnackbar(v, resp.getErrorMessage());
                    }
                }
                break;
        }
        return false;
    }

    private void showHideNoDataLayout(String message) {
        //v.findViewById(R.id.cl).setVisibility(View.GONE);
        v.findViewById(R.id.llBottom).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(message);
        updateFabIcon();
    }


}

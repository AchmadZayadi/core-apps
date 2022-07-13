package com.sesolutions.ui.packages;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPackageFragment extends BaseFragment implements OnUserClickedListener<Integer, Object> {

    private final int REQ_PACKAGE = 101;
    private final int REQ_DELETE = 102;
    private View v;
    private List<Packages> list;
    // private Map<String, Object> map;
    private String rcType;
    private String selectedScreen;
    private OnUserClickedListener<Integer, Object> listener;
    private int selectedPosition;

    public static MyPackageFragment newInstance(String screenType, String rcType, OnUserClickedListener<Integer, Object> listener) {
        MyPackageFragment frag = new MyPackageFragment();
        frag.listener = listener;
        frag.selectedScreen = screenType;
        frag.rcType = rcType;
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_my_package, container, false);
        return v;
    }


    @Override
    public void initScreenData() {
        setUpModuleData();
        initPackageRecyclerView();

        //new Handler().postDelayed(this::showButtons, 200);

        if (isNetworkAvailable(context)) {
            showBaseLoader(true);
            Map<String, Object> request = new HashMap<>();
            // request.put(Constant.KEY_VIDEO_ID, videoList.get(position).getVideoId());
            new ApiController(PACKAGE_URL, request, context, this, REQ_PACKAGE).execute();
        } else {
            notInternetMsg(v);
        }
    }

    private String CREATE_URL, DELETE_URL, PACKAGE_URL;

    private void setUpModuleData() {
        switch (rcType) {
            case Constant.ResourceType.PAGE:
                DELETE_URL = Constant.URL_PAGE_DELETE_PACKAGE;
                CREATE_URL = Constant.URL_PAGE_CREATE;
                PACKAGE_URL = Constant.URL_PAGE_PACKAGE;
                break;
            case Constant.ResourceType.GROUP:
                DELETE_URL = Constant.URL_GROUP_DELETE_PACKAGE;
                CREATE_URL = Constant.URL_GROUP_CREATE;
                PACKAGE_URL = Constant.URL_GROUP_PACKAGE;
                break;
            case Constant.ResourceType.BUSINESS:
                DELETE_URL = Constant.URL_BUSINESS_DELETE_PACKAGE;
                CREATE_URL = Constant.URL_BUSINESS_CREATE;
                PACKAGE_URL = Constant.URL_BUSINESS_PACKAGE;
                break;
            default:
                //default is contest
                DELETE_URL = Constant.URL_CONTEST_DELETE_PACKAGE;
                CREATE_URL = Constant.URL_CONTEST_CREATE;
                PACKAGE_URL = Constant.URL_CONTEST_PACKAGE;
                break;
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

    private PackageAdapter adapterPhoto;


    private void initPackageRecyclerView() {
        try {
            MultiSnapRecyclerView rvPhotos = (MultiSnapRecyclerView) v.findViewById(R.id.rvPackage);
            list = new ArrayList<>();
            rvPhotos.setHasFixedSize(true);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rvPhotos.setLayoutManager(layoutManager);
            adapterPhoto = new PackageAdapter(list, context, this);
            adapterPhoto.setMyPackage();
            rvPhotos.setAdapter(adapterPhoto);
            rvPhotos.setOnSnapListener(this::updateBottomLayout);
        } catch (Exception e) {
            CustomLog.e(e);
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
                map.put(Constant.KEY_PACKAGE_ID, list.get(selectedPosition).getPackage_id());
                new ApiController(DELETE_URL, map, context, this, REQ_DELETE).execute();
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateBottomLayout(int position) {
        selectedPosition = position;
        List<Options> optList = list.get(position).getParams();
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

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        try {
            switch (eventType) {
                case REQ_DELETE:
                    if (data != null) {
                        SuccessResponse resp = new Gson().fromJson("" + data, SuccessResponse.class);
                        if (TextUtils.isEmpty(resp.getError())) {
                            Util.showSnackbar(v, resp.getResult().getMessage());
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
                case REQ_PACKAGE:
                    hideBaseLoader();

                    if (data != null) {
                        if (null != listener) {
                            listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 0);
                        }
                        CommonResponse resp = new Gson().fromJson("" + data, CommonResponse.class);
                        if (TextUtils.isEmpty(resp.getError())) {
                            String message = new JSONObject("" + data).optJSONObject("result").optString("message");
                            if (!TextUtils.isEmpty(message)) {
                                showHideNoDataLayout(message);
                                Util.showSnackbar(v, message);
                            } else {
                                if (null != resp.getResult().getExistingPackage()) {
                                    v.findViewById(R.id.rlDetail).setBackgroundColor(Color.parseColor(Constant.foregroundColor));
                                    list.clear();
                                    list.addAll(resp.getResult().getExistingPackage());

                                    adapterPhoto.notifyDataSetChanged();
                                    updateBottomLayout(0);

                                    if (null != listener) {
                                        listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, resp.getResult().getTotal());
                                    }
                                }
                            }
                        } else {
                            Util.showSnackbar(v, resp.getErrorMessage());
                        }
                    }
                    break;
            }

        } catch (Exception e) {
            CustomLog.e(e);
            somethingWrongMsg(v);
        }
        return false;
    }

    private void showHideNoDataLayout(String message) {
        v.findViewById(R.id.rlDetail).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.llBottom).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(message);
        if (null != listener)
            listener.onItemClicked(Constant.Events.CLICKED_OPTION, null, -1);
    }

    public void callCreateContestApi() {

        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(CREATE_URL);
                    request.params.put(Constant.KEY_GET_FORM, 1);
                    request.params.put(Constant.KEY_EXISTING_PACKAGE_ID, list.get(selectedPosition).getExistingPackageId());
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
                                        // JSONObject result = new JSONObject(response);
                                        //  CommonResponse resp = new Gson().fromJson(result.getJSONObject("result").toString(), CommonResponse.class);
                                        Map<String, Object> map1 = new HashMap<>();
                                        map1.put(Constant.KEY_EXISTING_PACKAGE_ID, list.get(selectedPosition).getExistingPackageId());
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
                } catch (Exception e) {

                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}

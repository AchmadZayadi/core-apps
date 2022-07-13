package com.sesolutions.ui.video;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.NotificationResponse;
import com.sesolutions.responses.Notifications;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.member.MemberAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class FollowerFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object> {

    private static final String TAG = "FollowerFragment";

    private View v;
    private ProgressBar pb;
    private int resourceId;
    private boolean isLoading;
    private String resourceType;
    private MemberAdapter adapter;
    public RecyclerView recyclerView;
    private List<Notifications> friendList;
    private NotificationResponse.Result result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_follower, container, false);
        new ThemeManager().applyTheme((ViewGroup) v, context);

        initScreenData();
        return v;
    }

    public void initScreenData() {
        CustomLog.e("loading child", "notification");
        init();
        setRecyclerView();
        callNotificationApi(true);
    }

    private void init() {
        friendList = new ArrayList<>();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        pb = v.findViewById(R.id.pb);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(Constant.TITLE_FOLLOWERS);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
       /* ivProfileImage = v.findViewById(R.id.ivProfileImage);
        //bSave = v.findViewById(R.id.bSave);
        v.findViewById(R.id.bChoose).setOnClickListener(this);
        v.findViewById(R.id.bSave).setOnClickListener(this);
        v.findViewById(R.id.tvTerms).setOnClickListener(this);
        v.findViewById(R.id.tvPrivacy).setOnClickListener(this);*/
        //initSlide();
    }

    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new MemberAdapter(friendList, context, this, this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.bRefresh:
                    callNotificationApi(true);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callNotificationApi(boolean showLoader) {
        try {

            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;
                if (showLoader) {
                    showBaseLoader(false);
                } else {
                    pb.setVisibility(View.VISIBLE);
                }
                //showBaseLoader(true);
                try {

                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CHANNEL_FOLLOWERS);
                    request.params.put(Constant.KEY_RESOURCE_ID, resourceId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            pb.setVisibility(View.GONE);
                            isLoading = false;
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {

                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        NotificationResponse resp = new Gson().fromJson(response, NotificationResponse.class);
                                        result = resp.getResult();
                                        if (null != resp.getResult().getNotification())
                                            friendList.addAll(resp.getResult().getNotification());
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
                                    }
                                } else {
                                    notInternetMsg(v);
                                }
                                updateRecyclerView();
                            } catch (
                                    Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    isLoading = false;
                    hideBaseLoader();
                    pb.setVisibility(View.GONE);

                }

            } else {
                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (
                Exception e) {
            isLoading = false;
            hideBaseLoader();
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
        }

    }

    private void updateRecyclerView() {
        isLoading = false;
        updateTitle();
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_FOLLOWER);
        v.findViewById(R.id.llNoData).setVisibility(friendList.size() > 0 ? View.GONE : View.VISIBLE);
    }


    private void updateTitle() {
        if (result.getTotal() > 0) {
            ((TextView) v.findViewById(R.id.tvTitle)).setText(Constant.TITLE_FOLLOWERS + " (" + result.getTotal() + ")");
        }
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                CustomLog.e("getCurrentPage", "" + result.getCurrentPage());
                CustomLog.e("getTotalPage", "" + result.getTotalPage());

                if (result.getCurrentPage() < result.getTotalPage()) {
                    callNotificationApi(false);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        CustomLog.e("pagination", "" + adapter.getItemCount());
    }

    public static FollowerFragment newInstance(int resourceId, String resourceType) {
        FollowerFragment frag = new FollowerFragment();
        frag.resourceId = resourceId;
        frag.resourceType = resourceType;
        return frag;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1){
            case Constant.Events.CLICKED_HEADER_IMAGE:
                goToProfileFragment(postion);
        }
        return false;
    }
}

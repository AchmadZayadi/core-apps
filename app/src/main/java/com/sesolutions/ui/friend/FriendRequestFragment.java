package com.sesolutions.ui.friend;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.NotificationResponse;
import com.sesolutions.responses.Notifications;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.dashboard.MainActivity;
import com.sesolutions.ui.profile.SuggestionViewFragment;
import com.sesolutions.ui.signup.SignUpFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, String>, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public RecyclerView recyclerView;
    private View v;
    private List<Notifications> friendList;
    private FriendRequestAdapter adapter;
    private NotificationResponse.Result result;
    private boolean isLoading;
    private AppCompatButton bRefresh;
    private ProgressBar pb;
    private SwipeRefreshLayout swipeRefreshLayout;
//    private SuggestionViewFragment parent;
    private OnUserClickedListener<Integer, Object> parent;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_friend_request, container, false);

        return v;
    }

//    @Override
//    public void onBackPressed() {
//        if (activity instanceof MainActivity) {
//            ((MainActivity) activity).dashboardFragment.onBackPressed();
//        } else {
//            parent.onBackPressed();
//        }
//    }

    public void initScreenData() {
        applyTheme(v);
        if (SPref.getInstance().isLoggedIn(context)) {
            init();
            setRecyclerView();
            callFriendRequestApi(true, 1);
        } else {
            v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NOT_LOGGED_IN);
        }
    }

    @Override
    public void onRefresh() {
        callFriendRequestApi(false, Constant.REQ_CODE_REFRESH);
    }

    private void init() {
        friendList = new ArrayList<>();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        pb = v.findViewById(R.id.pb);
    }


    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new FriendRequestAdapter(friendList, context, this, this);
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
                case R.id.bRefresh:
                    //  callFriendRequestApi(true);

                    break;

                case R.id.bSignUp:
                    fragmentManager.beginTransaction().replace(R.id.container, new SignUpFragment())
                            .addToBackStack(null)
                            .commit();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callFriendRequestApi(boolean showLoader, int REQ) {
        try {

            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {
                    isLoading = true;
                    if (showLoader) {
                        showBaseLoader(false);
                    } else if (REQ == Constant.REQ_CODE_REFRESH) {
                        result = null;
                        friendList.clear();
                    } else {
                        pb.setVisibility(View.VISIBLE);
                    }

                    HttpRequestVO request = new HttpRequestVO(Constant.URl_FRIEND_REQUEST);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            setRefreshing(swipeRefreshLayout, false);
                            hideBaseLoader();
                            isLoading = false;
                            updateNotificationCount(1);
                            pb.setVisibility(View.GONE);
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {

                                    NotificationResponse resp = new Gson().fromJson(response, NotificationResponse.class);
                                    if (resp.getResult().getTotalPage() > 0) {
                                        result = resp.getResult();
                                        friendList.addAll(resp.getResult().getNotification());
                                    }
                                    updateRecyclerView();
                                } else {
                                    notInternetMsg(v);
                                }
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();
                    isLoading = false;
                    pb.setVisibility(View.GONE);

                }

            } else {
                setRefreshing(swipeRefreshLayout, false);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            pb.setVisibility(View.GONE);
            hideBaseLoader();
            CustomLog.e(e);
        }

    }

    @Override
    public boolean onItemClicked(Integer clickType, String url, int position) {
        switch (clickType) {
            case Constant.Events.MEMBER_ADD:
                callAcceptRejectApi(friendList.get(position).getSubjectId(), url, position);
                break;
            case Constant.Events.CLICKED_HEADER_IMAGE:
                goTo(Constant.GoTo.VIEW_PROFILE, Constant.KEY_ID, friendList.get(position).getSubjectId());
                break;
        }

        return false;

    }

    private void callAcceptRejectApi(int subjectId, String url, final int position) {
        try {

            if (isNetworkAvailable(context)) {

                try {
                    isLoading = true;
                    showBaseLoader(false);
                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_USER_ID, subjectId);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            isLoading = false;
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {
                                    BaseResponse<String> resp = new Gson().fromJson(response, BaseResponse.class);
                                    Util.showSnackbar(v, resp.getResult());
                                    friendList.remove(position);
                                    updateRecyclerView();
                                } else {
                                    notInternetMsg(v);
                                }
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();
                    isLoading = false;

                }

            } else {
                hideBaseLoader();
                isLoading = false;
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            hideBaseLoader();
            CustomLog.e(e);
        }
    }

    private void updateRecyclerView() {
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        isLoading = false;
        if (parent != null) {
            parent.onItemClicked(Constant.Events.SET_LOADED, MenuTab.Dashboard.REQUEST, 1);
        }
//        if (activity instanceof MainActivity) {
//            ((MainActivity) activity).dashboardFragment.isRequestContentLoaded = true;
//        } else {
//            parent.isRequestLoaded = true;
//        }
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_PENDING_REQUEST);
        v.findViewById(R.id.llNoData).setVisibility(friendList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {

                    callFriendRequestApi(false, 1);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        //  CustomLog.e("pagination", "" + adapter.getItemCount());
    }

//    public static FriendRequestFragment newInstance(SuggestionViewFragment parent) {
//        FriendRequestFragment frag = new FriendRequestFragment();
//        frag.parent = parent;
//        return frag;
//    }
    public static FriendRequestFragment newInstance(OnUserClickedListener<Integer, Object> parent) {
        FriendRequestFragment frag = new FriendRequestFragment();
        frag.parent = parent;
        return frag;
    }
}

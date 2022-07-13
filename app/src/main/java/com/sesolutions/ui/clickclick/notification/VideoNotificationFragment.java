package com.sesolutions.ui.clickclick.notification;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.NotificationResponse;
import com.sesolutions.responses.Notifications;
import com.sesolutions.ui.clickclick.ClickClickFragment;
import com.sesolutions.ui.clickclick.me.MeFragment;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.dashboard.MainActivity;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class VideoNotificationFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, String>, SwipeRefreshLayout.OnRefreshListener {

    public RecyclerView recyclerView;
    private View v;
    private List<Notifications> friendList;
    private VideoNotificationAdapter adapter;
    private NotificationResponse.Result result;
    private boolean isLoading;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ProgressBar pb;
    private LinearLayoutManager layoutManager;
    private boolean isHomePressed;

    private OnUserClickedListener<Integer, Object> parent;

    public static VideoNotificationFragment newInstance(OnUserClickedListener<Integer, Object> parent) {
        VideoNotificationFragment frag = new VideoNotificationFragment();
        frag.parent = parent;
        return frag;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_notification_video, container, false);
//        ((MainActivity) activity).changeCurrentFragment();

        initScreenData();
        return v;
    }

    public void initScreenData() {
        applyTheme(v);
        if (SPref.getInstance().isLoggedIn(context)) {
            init();
            setRecyclerView();
            callNotificationApi(true, 1);
        } else {
            v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NOT_LOGGED_IN);
        }
    }

    private void init() {
        friendList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.recyclerView);
        pb = v.findViewById(R.id.pb);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    @Override
    public void onRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        callNotificationApi(false, Constant.REQ_CODE_REFRESH);
    }

    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new VideoNotificationAdapter(friendList, context, this, this);
            recyclerView.setAdapter(adapter);
            // setXtraScrollColor(recyclerView);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    try {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                            if (firstVisiblePosition <= 1 && isHomePressed) {
                                isHomePressed = false;
                                if (((MainActivity) activity).dashboardFragment.unreadCount[3] != 0) {
                                    onRefresh();
                                }
                            }
                        }
                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callNotificationApi(boolean showLoader, int REQ) {
        try {

            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;
                if (showLoader) {
                    showBaseLoader(false);
                } else if (REQ == Constant.REQ_CODE_REFRESH) {
                    result = null;
                    friendList.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    pb.setVisibility(View.VISIBLE);
                }
                //showBaseLoader(true);
                try {

                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_VIDEO_NOTIFICATION);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_TYPE, "tickvideo");
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            setRefreshing(swipeRefreshLayout, false);
                            hideBaseLoader();
                            updateNotificationCount(3);
                            pb.setVisibility(View.GONE);
                            isLoading = false;
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {
                                    NotificationResponse resp = new Gson().fromJson(response, NotificationResponse.class);
                                    result = resp.getResult();
                                    if (null != result.getNotification())
                                        friendList.addAll(result.getNotification());
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
                    isLoading = false;
                    hideBaseLoader();
                    pb.setVisibility(View.GONE);

                }

            } else {
                setRefreshing(swipeRefreshLayout, false);
                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            hideBaseLoader();
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
        }

    }


    private void callMarkRead(final int notificationId) {
        try {
            if (isNetworkAvailable(context)) {
                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_NOTIFICATION_MARK_READ);
                    request.params.put(Constant.KEY_NOTIFICATION_ID, notificationId);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {

                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                            } catch (Exception e) {
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
                setRefreshing(swipeRefreshLayout, false);
                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            hideBaseLoader();
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
        }

    }


    private void updateRecyclerView() {
        isLoading = false;
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        //   paginate.showLoading(false);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_NOTIFICATION);
        v.findViewById(R.id.llNoData).setVisibility(friendList.size() > 0 ? View.GONE : View.VISIBLE);
        if (parent != null) {
            parent.onItemClicked(Constant.Events.SET_LOADED, MenuTab.Dashboard.NOTIFICATION, 1);
        }
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callNotificationApi(false, 1);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer id, String type, int postion) {
        try {
            Notifications vo = friendList.get(postion);
            callMarkRead(vo.getNotificationId());
            friendList.get(postion).setRead(1);
            adapter.notifyItemChanged(postion);
            if (vo.getObjectType().equals("sesvideo_chanel")) {
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, MeFragment.newInstance(true, vo.getObjectId()))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            } else if (vo.getObjectType().equals("video")) {
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, ClickClickFragment.newInstance(vo.getVideo(), true))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    public void scrollToStart() {
        if (null != friendList && friendList.size() > 0) {
            if (layoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                isHomePressed = true;
                recyclerView.smoothScrollToPosition(0);
            } else {
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        }
    }
}

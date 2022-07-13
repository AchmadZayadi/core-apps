package com.sesolutions.ui.message;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.MessageInbox;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.dashboard.MainActivity;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

import org.apache.http.client.methods.HttpPost;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MessageInboxFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<String, String>, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public RecyclerView recyclerView;
    private View v;
    private List<MessageInbox> friendList;
    private MessageInboxAdapter adapter;
    private CommonResponse.Result result;
    private boolean isLoading;
    private ProgressBar pb;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_friend_request, container, false);
        applyTheme(v);
        try {

            if (SPref.getInstance().isLoggedIn(context)) {
                init();
                setRecyclerView();
                callApi(true, 1);
            } else {
                v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NOT_LOGGED_IN);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        friendList = new ArrayList<>();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        pb = v.findViewById(R.id.pb);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        callApi(false, Constant.REQ_CODE_REFRESH);
    }

    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new MessageInboxAdapter(friendList, context, this, this);
            recyclerView.setAdapter(adapter);

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

    private void callApi(boolean showLoader, int REQ) {
        try {

            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;
                if (showLoader) {
                    showBaseLoader(false);
                } else if (REQ == Constant.REQ_CODE_REFRESH) {
                    result = null;
                    friendList.clear();
                } else {
                    pb.setVisibility(View.VISIBLE);
                }

                try {
                    //showBaseLoader(false);
                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_INBOX);
                    // request.params.put(Constant.KEY_IMAGE, filePath);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            isLoading = false;
                            setRefreshing(swipeRefreshLayout, false);
                            updateNotificationCount(2);
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {
                                    ((MainActivity) activity).dashboardFragment.isMessageContentLoaded = true;
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);

                                    if (resp.getResult().getTotalPage() > 0) {
                                        result = resp.getResult();
                                        friendList.addAll(resp.getResult().getMessageList());
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
            isLoading = false;
            pb.setVisibility(View.GONE);
            hideBaseLoader();
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(String conversationId, String isRead, int position) {
        if (Integer.parseInt(isRead) > 0) {
            goToMessageChatFragment(friendList.get(position));
        } else {
            callMarkReadApi(conversationId, Constant.URL_MARK_READ, position);
        }

        return false;
    }

    private void goToMessageChatFragment(MessageInbox vo) {
        Intent intent = new Intent(((MainActivity) activity), ChatActivity.class);
        intent.putExtra(Constant.KEY_DATA, (Serializable) vo);
        context.startActivity(intent);
    }

    private void callMarkReadApi(String conversationId, String url, final int position) {
        try {

            if (isNetworkAvailable(context)) {

                try {
                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_CONVERSATION_ID, conversationId);
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
                                    goToMessageChatFragment(friendList.get(position));
                                    /*BaseResponse<String> resp = new Gson().fromJson(response, BaseResponse.class);
                                    Util.showSnackbar(v, resp.getResult());
                                    friendList.remove(position);
                                    updateRecyclerView();*/
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

                }

            } else {
                hideBaseLoader();
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }

    }

    private void updateRecyclerView() {
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        isLoading = false;
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_SENT_MSG);
        v.findViewById(R.id.llNoData).setVisibility(friendList.size() > 0 ? View.GONE : View.VISIBLE);

    }


    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {

                    callApi(false, 1);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        CustomLog.e("pagination", "" + adapter.getItemCount());
    }
}

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
import com.sesolutions.responses.feed.PeopleSuggestion;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.dashboard.FeedSuggestionAdapter;
import com.sesolutions.ui.profile.SuggestionViewFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.List;

public class SuggestionFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object>, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public RecyclerView recyclerView;
    private View v;
    private List<PeopleSuggestion> friendList;
    private FeedSuggestionAdapter adapter;
    private NotificationResponse.Result result;
    private boolean isLoading;
    private AppCompatButton bRefresh;
    private ProgressBar pb;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SuggestionViewFragment parent;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_friend_request, container, false);
        try {
            //((MainActivity) activity).changeCurrentFragment();
            applyTheme(v);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void onBackPressed() {
        parent.onBackPressed();
    }

    public void initScreenData() {

        init();
        setRecyclerView();
        // callFriendRequestApi(true, 1);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        //callFriendRequestApi(false, Constant.REQ_CODE_REFRESH);
    }

    private void init() {
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        pb = v.findViewById(R.id.pb);
    }


    private void setRecyclerView() {
        try {
            friendList = SPref.getInstance().getSuggestionList(context);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new FeedSuggestionAdapter(friendList, context, this);
            adapter.setSuggestionView(true);
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

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public boolean onItemClicked(Integer clickType, Object url, int position) {
        try {
            switch (clickType) {
                case Constant.Events.MEMBER_REMOVE:
                    friendList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                    break;
                case Constant.Events.MEMBER_ADD:
                    callAcceptRejectApi(friendList.get(position).getUserId(), "" + url, position);
                    break;
                case Constant.Events.PROFILE:
                    goTo(Constant.GoTo.VIEW_PROFILE, Constant.KEY_ID, position);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
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
        parent.isSuggestionLoaded = true;
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_PENDING_REQUEST);
        v.findViewById(R.id.llNoData).setVisibility(friendList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {

                    //callFriendRequestApi(false, 1);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        //  CustomLog.e("pagination", "" + adapter.getItemCount());
    }

    public static SuggestionFragment newInstance(SuggestionViewFragment parent/*, List<PeopleSuggestion> list*/) {
        SuggestionFragment frag = new SuggestionFragment();
        frag.parent = parent;
        return frag;
    }
}

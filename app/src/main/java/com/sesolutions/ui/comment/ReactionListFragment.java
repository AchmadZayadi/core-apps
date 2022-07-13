package com.sesolutions.ui.comment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.LikeData;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReactionListFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object>, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int LOAD_MORE = 100;
    public RecyclerView recyclerView;
    private View v;
    private List<LikeData> friendList;
    private ReactionAdapter adapter;
    private CommonResponse.Result result;
    private boolean isLoading;
    private ProgressBar pb;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OnUserClickedListener<Integer, Object> listener;
    private String type;
    private Map<String, Object> map;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_friend_request, container, false);
        try {
            applyTheme(v);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

  /*  @Override
    public void onBackPressed() {
        parent.onBackPressed();
    }*/

    public void initScreenData() {

        init();
        setRecyclerView();
        callApi(1);
    }

    @Override
    public void onRefresh() {
        callApi(Constant.REQ_CODE_REFRESH);
    }

    private void init() {
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        pb = v.findViewById(R.id.pb);
    }


    private void setRecyclerView() {
        try {
            friendList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new ReactionAdapter(friendList, context, this);
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

    private void callApi(int REQ) {


        if (isNetworkAvailable(context)) {
            try {
                isLoading = true;
                if (REQ == 1) {
                    v.findViewById(R.id.pbCenter).setVisibility(View.VISIBLE);
                } else if (REQ == Constant.REQ_CODE_REFRESH) {
                    result = null;
                    friendList.clear();
                } else {
                    pb.setVisibility(View.VISIBLE);
                }

                HttpRequestVO request = new HttpRequestVO(Constant.URL_USER_REACTION);

                request.params.putAll(map);
                request.params.put(Constant.KEY_TYPE, type);
                request.params.put("is_ajax_content", true);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        setRefreshing(swipeRefreshLayout, false);
                        v.findViewById(R.id.pbCenter).setVisibility(View.GONE);
                        isLoading = false;
                        updateNotificationCount(1);
                        pb.setVisibility(View.GONE);
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {

                                CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                if (resp.getResult().getReactionData() != null) {
                                    wasListEmpty = friendList.size() == 0;
                                    result = resp.getResult();
                                    friendList.addAll(resp.getResult().getReactionData());
                                }
                                updateRecyclerView();
                            } else {
                                notInternetMsg(v);
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                v.findViewById(R.id.pbCenter).setVisibility(View.GONE);
                isLoading = false;
                pb.setVisibility(View.GONE);
                CustomLog.e(e);
            }

        } else {
            setRefreshing(swipeRefreshLayout, false);
            notInternetMsg(v);
        }
    }

    @Override
    public boolean onItemClicked(Integer clickType, Object type, int position) {
        try {
            switch (clickType) {
                case Constant.Events.PROFILE:
                    performClick("" + type, position, null, false);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }


    private void updateRecyclerView() {
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        isLoading = false;
        listener.onItemClicked(Constant.Events.SET_LOADED, type, 1);
        // ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_PENDING_REQUEST);
        //v.findViewById(R.id.llNoData).setVisibility(friendList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {

                    callApi(LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        //  CustomLog.e("pagination", "" + adapter.getItemCount());
    }

    public static ReactionListFragment newInstance(OnUserClickedListener<Integer, Object> listener, Map<String, Object> map, String type) {
        ReactionListFragment frag = new ReactionListFragment();
        frag.listener = listener;
        frag.type = type;
        frag.map = map;
        return frag;
    }
}

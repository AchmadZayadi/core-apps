package com.sesolutions.ui.group_core;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
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
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.event.Discussion;
import com.sesolutions.responses.event.EventResponse;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.events.DiscussionAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoreGroupDiscussionFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>/*, SwipeRefreshLayout.OnRefreshListener*/ {

    public View v;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private String searchKey;
    private EventResponse.Result result;
    private ProgressBar pb;
    private List<Discussion> categoryList;
    private DiscussionAdapter adapter;
    // private SwipeRefreshLayout swipeRefreshLayout;

    private Map<String, Object> map;
    private String url;
    private String resourceType;
    private int mObjectId;

    public static CoreGroupDiscussionFragment newInstance(Map<String, Object> map) {
        CoreGroupDiscussionFragment frag = new CoreGroupDiscussionFragment();
        frag.map = map;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_discussion, container, false);
        applyTheme(v);
        return v;
    }

    private void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
    }

    private void setRecyclerView() {
        try {
            categoryList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new DiscussionAdapter(categoryList, context, this, this);
            adapter.setDiscussion(true);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

                case R.id.cvPost:
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_ID, mObjectId);
                    super.openFormFragment(Constant.FormType.CREATE_DISCUSSTION, map, Constant.URL_CREATE_CORE_GROUP_DISCUSSION);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        getMapValues();
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }

    private void getMapValues() {
        try {
            if (map != null) {
                resourceType = (String) map.get(Constant.KEY_RESOURCES_TYPE);
                url = (String) map.get(Constant.KEY_URI);
                map.remove(Constant.KEY_URI);

                mObjectId = (int) map.get(Constant.KEY_ID);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    v.findViewById(R.id.pbMain).setVisibility(View.VISIBLE);
                }

                try {
                    HttpRequestVO request = new HttpRequestVO(url);
                    if (map != null) {
                        request.params.putAll(map);
                    }
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    if (req == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                hideAllLoaders();

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            categoryList.clear();
                                        }
                                        EventResponse resp = new Gson().fromJson(response, EventResponse.class);
                                        result = resp.getResult();
                                        if (null != result.getDiscussions())
                                            categoryList.addAll(result.getDiscussions());
                                        showHideUpperLayout();
                                        updateAdapter();
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
                    hideAllLoaders();
                }

            } else {
                hideAllLoaders();
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideAllLoaders();
            CustomLog.e(e);
        }
    }

    private void showHideUpperLayout() {
        if (null != result.getPostButton()) {
            v.findViewById(R.id.cvPost).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvPost)).setText(result.getPostButton().getLabel());
            v.findViewById(R.id.cvPost).setOnClickListener(this);
        } else {
            v.findViewById(R.id.cvPost).setVisibility(View.GONE);
        }
    }

    private void hideAllLoaders() {
        isLoading = false;
        hideView(v.findViewById(R.id.pbMain));
        hideView(pb);
    }

   /* public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }*/

    private void updateAdapter() {
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.msg_no_topic);
        v.findViewById(R.id.llNoData).setVisibility(categoryList.size() > 0 ? View.GONE : View.VISIBLE);

    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {
            case Constant.Events.CLICKED_HEADER_IMAGE:
                goToProfileFragment(categoryList.get(postion).getLastposterId());
                break;
            case Constant.Events.MUSIC_MAIN:
                fragmentManager.beginTransaction().replace(R.id.container, CoreGroupDiscussionView.newInstance(categoryList.get(postion).getTopicId())).addToBackStack(null).commit();
                break;

        }
        return false;
    }


}

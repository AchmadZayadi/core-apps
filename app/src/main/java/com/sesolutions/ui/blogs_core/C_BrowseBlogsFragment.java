package com.sesolutions.ui.blogs_core;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.ui.blogs.BlogAdapter;
import com.sesolutions.ui.blogs.BlogHelper;
import com.sesolutions.ui.blogs.C_BlogAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Map;

import static com.sesolutions.utils.Constant.REQ_CODE_REFRESH;

public class C_BrowseBlogsFragment extends C_BlogHelper implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener  {

    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    public String searchKey;
    public CommonResponse.Result result;
    private ProgressBar pb;
    public int loggedinId;
    public SwipeRefreshLayout swipeRefreshLayout;

    public int categoryId;
    public int userId;
    private String txtNoMsg = Constant.MSG_NO_BLOG;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_list_common_offset_refresh, container, false);
        applyTheme(v);
        /*if loggedinid > 0 then this myBlog screen otherwise it is browse blog screen*/
        txtNoMsg = loggedinId > 0 ? Constant.MSG_NO_BLOG_CREATED_YOU : Constant.MSG_NO_BLOG_CREATED;
        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        hiddenPanel = v.findViewById(R.id.hidden_panel);
        hiddenPanel.setOnClickListener(this);
    }

    public void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new C_BlogAdapter(videoList, context, this, this, loggedinId > 0 ? Constant.FormType.TYPE_MY_ALBUMS : Constant.FormType.TYPE_MUSIC_ALBUM);
            //adapter.setLoggedInId(SPref.getInstance().getInt(context, Constant.KEY_LOGGED_IN_ID));
            adapter.setLoggedInId(loggedinId);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onClick(View v) {
//        try {
//            switch (v.getId()) {
//                case R.id.hidden_panel:
//                    hideSlidePanel();
//                    break;
//            }
//        } catch (Exception e) {
//            CustomLog.e(e);
//        }
    }

    public void initScreenData() {
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }

    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {

                    if(req!=REQ_CODE_REFRESH){
                        if (req == REQ_LOAD_MORE) {
                            pb.setVisibility(View.VISIBLE);
                        } else {
                            showBaseLoader(true);
                        }
                    }

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_BLOG_BROWSE);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (loggedinId > 0) {
                        request.params.put(Constant.KEY_USER_ID, loggedinId);

                    }


                    if (!TextUtils.isEmpty(searchKey)) {
                        request.params.put(Constant.KEY_SEARCH, searchKey);
                    } else if (categoryId > 0) {
                        request.params.put(Constant.KEY_CATEGORY_ID, categoryId);
                    }

                    Map<String, Object> map = activity.filteredMap;
                    if (null != map) {
                        request.params.putAll(map);
                    }
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    if (req == REQ_CODE_REFRESH) {
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
                                isLoading = false;
                                setRefreshing(swipeRefreshLayout, false);

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        if (null != parent) {
                                            if (loggedinId == 0) {
                                                parent.onItemClicked(Constant.Events.SET_LOADED, null, 0);
                                            } else {
                                                parent.onItemClicked(Constant.Events.SET_LOADED, null, 2);
                                            }
                                        }
                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        if (req == REQ_CODE_REFRESH) {
                                            videoList.clear();
                                        }
                                        result = resp.getResult();
                                        menuItem = result.getMenus();
                                        if (null != result.getBlogs())
                                            videoList.addAll(result.getBlogs());

                                        updateAdapter();
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
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
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();

                }

            } else {
                isLoading = false;
                setRefreshing(swipeRefreshLayout, false);
                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            setRefreshing(swipeRefreshLayout, false);
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }
    @Override
    public void onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            callMusicAlbumApi(REQ_CODE_REFRESH);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateAdapter() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);


        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoMsg);
        v.findViewById(R.id.llNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);
        if (parent != null) {
            int index = loggedinId != 0 ? 1 : 0;
            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, index, result.getTotal());
        }
    }

    public static C_BrowseBlogsFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId, int categoryId) {
        C_BrowseBlogsFragment frag = new C_BrowseBlogsFragment();
        frag.parent = parent;
        frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        return frag;
    }

    public static C_BrowseBlogsFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId) {
        return newInstance(parent, loggedInId, -1);
    }

    public static C_BrowseBlogsFragment newInstance(int categoryId) {
        return newInstance(null, 0, categoryId);
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
}

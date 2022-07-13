package com.sesolutions.ui.events;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
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
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.videos.Category;
import com.sesolutions.responses.videos.VideoBrowse;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.video.CategoryAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class EventCategoriesFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, String> {

    public View v;
    public EventParentFragment parent;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private String searchKey;
    private com.sesolutions.responses.videos.Result result;
    private ProgressBar pb;
    private List<Category> categoryList;
    private CategoryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static EventCategoriesFragment newInstance(EventParentFragment parent) {
        EventCategoriesFragment frag = new EventCategoriesFragment();
        frag.parent = parent;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_list_common_offset_refresh, container, false);
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
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constant.SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new CategoryAdapter(categoryList, context, this, this, Constant.FormType.CREATE_EVENT);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
                }
            });
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

    public void initScreenData() {
        if (parent != null && !parent.isCategoryLoaded) {
            init();
            setRecyclerView();
            callMusicAlbumApi(1);
        } else if (parent == null) {
            init();
            setRecyclerView();
            callMusicAlbumApi(1);
        }
    }

    private void callMusicAlbumApi(final int req) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else if (req == 1) {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_EVENT_CATEGORY);
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
                                hideLoaders();

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        if (null != parent)
                                            parent.isCategoryLoaded = true;
                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            categoryList.clear();
                                        }
                                        VideoBrowse resp = new Gson().fromJson(response, VideoBrowse.class);
                                        result = resp.getResult();
                                        categoryList.addAll(result.getCategory());

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
                    hideLoaders();
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();
                }
            } else {
                hideLoaders();

                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideLoaders();
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }

    private void updateAdapter() {
        adapter.setPermission(result.getPermission());
        hideLoaders();
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        //  ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_SENT_MSG);
        //  v.findViewById(R.id.llNoData).setVisibility(feedActivityList.size() > 0 ? View.GONE : View.VISIBLE);
        if (parent != null) {
            parent.updateTotal(2, categoryList.size());
        }
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
    public boolean onItemClicked(Integer object1, String object2, int postion) {
        switch (object1) {
            case Constant.Events.MUSIC_MAIN:
                //super. goToCategoryFragment(categoryList.get(postion).getCategoryId(),categoryList.get(postion).getName());
                super.goToCategoryFragment(categoryList.get(postion), 0);
                break;

        }
        return false;
    }


}

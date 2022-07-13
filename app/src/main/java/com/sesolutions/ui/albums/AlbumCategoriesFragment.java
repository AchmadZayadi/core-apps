package com.sesolutions.ui.albums;


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
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.video.CategoryAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class AlbumCategoriesFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, String> {

    public View v;
    public OnUserClickedListener<Integer, Object> listener;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private final int REQ_LOAD_MORE = 2;
    private com.sesolutions.responses.videos.Result result;
    private ProgressBar pb;
    private List<Category> categoryList;
    private CategoryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String selectedScreen;

    public static AlbumCategoriesFragment newInstance(OnUserClickedListener<Integer, Object> parent, String selectedScreen) {
        AlbumCategoriesFragment frag = new AlbumCategoriesFragment();
        frag.listener = parent;
        frag.selectedScreen = selectedScreen;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_list_common_offset_refresh, container, false);
        new ThemeManager().applyTheme((ViewGroup) v, context);
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
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new CategoryAdapter(categoryList, context, this, this, Constant.FormType.TYPE_CATEGORY);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
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
    public void onClick(View v) {
        try {
            switch (v.getId()) {


            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        result = null;
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
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
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_ALBUM_CATEGORIES_BROWSE);
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
        if (null != listener) {
            listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, -1);
            listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
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
                goToCategoryFragment(postion);
                break;

        }
        return false;
    }

    private void goToCategoryFragment(int postion) {
        fragmentManager.beginTransaction()
                .replace(R.id.container
                        , ViewAlbumCategoryFragment.newInstance(categoryList.get(postion).getCategoryId(), categoryList.get(postion).getLabel()))
                .addToBackStack(null)
                .commit();
    }
}

package com.sesolutions.ui.contest;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.contest.ContestResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.ui.customviews.FeedOptionPopup;
import com.sesolutions.ui.customviews.RelativePopupWindow;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

public class ContestManageFragment extends ContestHelper<ContestAdapter> implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {


    public String searchKey;

    public int loggedinId;

    public String txtNoData;
    public SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private ProgressBar pb;
    public boolean isTag;
    private String url;
    public List<Options> filterOptions;


    public static ContestManageFragment newInstance(OnUserClickedListener listener, int loggedInId, int categoryId) {
        ContestManageFragment frag = new ContestManageFragment();
        frag.listener = listener;
        frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        return frag;
    }

    public static ContestManageFragment newInstance(OnUserClickedListener listener, int loggedInId) {
        return newInstance(listener, loggedInId, -1);
    }

    public static ContestManageFragment newInstance(String TYPE, OnUserClickedListener listener) {
        ContestManageFragment frag = newInstance(listener, -1, -1);
        frag.selectedScreen = TYPE;
        return frag;
    }


    public static ContestManageFragment newInstance(int categoryId) {
        return newInstance(null, 0, categoryId);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_refresh, container, false);
        txtNoData = "  ";//Constant.MSG_NO_ALBUM_CREATED;
        applyTheme(v);
        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        // loggedinId = SPref.getInstance().getUserMasterDetail(context).getLoggedinUserId();
        url = Constant.URL_CONTEST_MANAGE;
    }

    public void setRecyclerView() {
        try {
            contestList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            adapter = new ContestAdapter(contestList, context, this, this);
            adapter.setType(selectedScreen);
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void initScreenData() {
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }

    public void callMusicAlbumApi(final int req) {

        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(url); //url will change according to screenType
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                //send menu=1 for fetching filterOptions on My Contest Screen
                request.params.put(Constant.KEY_MENUS, 1);

                if (!TextUtils.isEmpty(mFilter)) {
                    request.params.put("search_filter", mFilter);
                }

                if (req == Constant.REQ_CODE_REFRESH) {
                    request.params.put(Constant.KEY_PAGE, 1);
                } else {
                    request.params.put(Constant.KEY_PAGE, null != result && req != 1 ? result.getNextPage() : 1);
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
                                    if (null != listener) {
                                        listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 0);
                                    }
                                    ContestResponse resp = new Gson().fromJson(response, ContestResponse.class);

                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        contestList.clear();
                                    }

                                    wasListEmpty = contestList.size() == 0;
                                    result = resp.getResult();


                                    if (null != result.getContests())
                                        contestList.addAll(result.getContestList(selectedScreen));

                                    if (result.getMenus() != null) {
                                        if (null != filterOptions) {
                                            filterOptions.clear();
                                        } else {
                                            filterOptions = new ArrayList<>();
                                        }
                                        filterOptions.addAll(result.getMenus());
                                    }
                                    updateAdapter();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                   // goIfPermissionDenied(err.getError());
                                }
                            }

                        } catch (Exception e) {
                            hideBaseLoader();

                            CustomLog.e(e);
                        }
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideBaseLoader();
            }
        } else {

            notInternetMsg(v);
        }


    }

    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }

    private void updateAdapter() {
        hideLoaders();
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(contestList.size() > 0 ? View.GONE : View.VISIBLE);
        if (listener != null) {
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
    public void onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        switch (object1) {
            case Constant.Events.FEED_UPDATE_OPTION:
                int listPosition = Integer.parseInt("" + screenType);
                if (listPosition <= -1) {

                    //this means filter option is clicked
                    //so get Filter value and refreash page
                    if (filterOptions != null && filterOptions.size() > 0)
                        mFilter = filterOptions.get(postion).getName();
                   /* if ("hosts".equals(mFilter) || "lists".equals(mFilter)) {
                        adapter.setSubType(mFilter);
                        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(Constant.SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL));
                    } else {
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    }*/
                    // contestList.clear();
                    //  adapter.notifyDataSetChanged();
                    // recyclerView.setAdapter(adapter);
                    onRefresh();
                }
                break;
        }
        return super.onItemClicked(object1, screenType, postion);
    }

    //show all filter options
    public void onFilterClick(ImageView ivFilter) {
        if (filterOptions != null) {
            try {
                FeedOptionPopup popup = new FeedOptionPopup(v.getContext(), -1, this, filterOptions);
                int vertPos = RelativePopupWindow.VerticalPosition.CENTER;
                int horizPos = RelativePopupWindow.HorizontalPosition.ALIGN_LEFT;
                popup.showOnAnchor(ivFilter, vertPos, horizPos, true);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    }
}

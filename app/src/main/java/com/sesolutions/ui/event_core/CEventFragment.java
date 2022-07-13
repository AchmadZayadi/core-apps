package com.sesolutions.ui.event_core;


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
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.CommonVO;
import com.sesolutions.responses.ErrorResponse;
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

public class CEventFragment extends CEventHelper<CEventAdapter> implements OnLoadMoreListener {

    public String searchKey;

    public int loggedinId;

    public String txtNoData;
    public SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private ProgressBar pb;
    public RecyclerView rvQuotesCategory;
    public boolean isTag;
    public String url;
    private int listId;
    public String SEARCH_KEY;
    public List<Options> filterOptions;

    //O : category
    //1 : sub_category
    //2 : sub_subcategory
    public int categoryLevel;
    public boolean isUnknownCategory;


    public static CEventFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId, int categoryId) {
        CEventFragment frag = new CEventFragment();
        frag.parent = parent;
        frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        return frag;
    }

    public static CEventFragment newInstance(OnUserClickedListener<Integer, Object> parent, int loggedInId) {
        return newInstance(parent, loggedInId, -1);
    }

    public static CEventFragment newInstance(String TYPE, OnUserClickedListener<Integer, Object> parent) {
        CEventFragment frag = newInstance(parent, -1, -1);
        frag.selectedScreen = TYPE;
        return frag;
    }


    public static CEventFragment newInstance(String TYPE, int listId) {
        CEventFragment frag = newInstance(null, -1, -1);
        frag.selectedScreen = TYPE;
        frag.listId = listId;
        return frag;
    }


    public static CEventFragment newInstance(int categoryId) {
        return newInstance(null, 0, categoryId);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_refresh, container, false);
        txtNoData = "  ";
        applyTheme(v);
        return v;
    }

    public void init() {
        try {
            recyclerView = v.findViewById(R.id.recyclerview);
            pb = v.findViewById(R.id.pb);
            hiddenPanel = v.findViewById(R.id.hidden_panel);
            hiddenPanel.setOnClickListener(this);
            txtNoData = getStrings(R.string.MSG_NO_EVENT_FOUND);
            switch (selectedScreen) {

               /* case TYPE_LIST_OG:
                case TYPE_LIST_LATEST:
                case TYPE_LIST_ONGOING:
                case TYPE_LIST_PAST:
                case TYPE_LIST_WEEK:
                case TYPE_LIST_WEEKEND:
                case TYPE_LIST_MONTH:
                case TYPE_LIST_MJE:
                    url = Constant.URL_VIEW_EVENT_LIST;
                    mFilter = selectedScreen;
                    break;*/


                case TYPE_MANAGE:
                    // txtNoData = getStrings(R.string.msg_);
                    loggedinId = SPref.getInstance().getLoggedInUserId(context);
                    url = Constant.URL_CEVENT_BROWSE;
                    break;

             /*   case TYPE_VIEW_CATEGORY:
                    url = Constant.URL_CATEGORY_VIEW_EVENT;
                    break;*/
                default:
                    url = Constant.URL_CEVENT_BROWSE;
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager;
            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            adapter = new CEventAdapter(videoList, context, this, this);
            adapter.setType(selectedScreen);
           /* ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
            recyclerView.addItemDecoration(itemDecoration);*/
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

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else if (req == 1) {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(url); //url will change according to screenType

                    // choose filter key value ,
                    if (!TextUtils.isEmpty(mFilter)) {
                        request.params.put("search_filter", mFilter);
                    } else if (null != activity.filteredMap) {
                        request.params.putAll(activity.filteredMap);
                    }

                    if (selectedScreen != null) {
                        request.params.put(Constant.KEY_FILTER, selectedScreen.replace("sesevent_main_", ""));
                    }
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    if (loggedinId > 0) {
                        request.params.put(Constant.KEY_USER_ID, loggedinId);
                    }

                    if (!TextUtils.isEmpty(searchKey)) {
                        request.params.put(SEARCH_KEY, searchKey);
                    } else if (categoryId > 0) {
                        if (categoryLevel == 0) {
                            request.params.put(Constant.KEY_CATEGORY_ID, categoryId);
                        }
                    } /*else if (isUnknownCategory) {
                        request.params.put(Constant.KEY_CATEGORY_ID, " ");
                    }*/

                    request.params.put(Constant.KEY_PAGE, null != result && req != 1 ? result.getNextPage() : 1);
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
                                isLoading = false;
                                setRefreshing(swipeRefreshLayout, false);
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        if (null != parent) {
                                            parent.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, -1);
                                            //parent.updateLoadStatus(selectedScreen, true);
                                        }
                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        //if screen is refreshed then clear previous data
                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            videoList.clear();
                                        }

                                        wasListEmpty = videoList.size() == 0;
                                        result = resp.getResult();

                                        if (result.getEventCategory() != null) {
                                            CommonVO vo = new CommonVO();
                                            vo.setItemType(1);
                                            vo.setCategory(result.getEventCategory());
                                            videoList.add(vo);
                                        }

                                        if (result.getEventSubCategory() != null) {
                                            CommonVO vo = new CommonVO();
                                            vo.setItemType(2);
                                            vo.setCategoryLevel(categoryLevel);
                                            vo.setSubCategory(result.getEventSubCategory());
                                            videoList.add(vo);
                                        }


                                        //in case of browse list data is coming in key "lists" otherwise in key "events"
                                        if (null != result.getHosts())
                                            videoList.addAll(result.getHosts());
                                        if (null != result.getLists())
                                            videoList.addAll(result.getLists());
                                        if (null != result.getEvents())
                                            videoList.addAll(result.getEvents());

                                       /* if (result.getMenus() != null) {
                                            if (null != filterOptions) {
                                                filterOptions.clear();
                                            } else {
                                                filterOptions = new ArrayList<>();
                                            }
                                            filterOptions.addAll(result.getMenus());
                                        }*/
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
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();
                }
            } else {
                isLoading = false;
                setRefreshing(swipeRefreshLayout, false);

                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            hideLoaders();
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
        hideLoaders();

        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);
        if (parent != null) {
            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
            //parent.updateTotal(selectedScreen, result.getTotal());
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
            case Constant.Events.CLICKED_OPTION:
                Util.showOptionsPopUp((View) screenType, postion, result.getMenus(), this);
                break;
            case Constant.Events.FEED_UPDATE_OPTION:
                int listPosition = Integer.parseInt("" + screenType);
                if (listPosition > -1) {
                    // this means list item option is clicked
                    Options opt = result.getMenus().get(postion);
                    performFeedOptionClick(opt.getName(), listPosition);
                } /*else {
                    //this means filter option is clicked
                    //so get Filter value and refreash page
                    if (filterOptions != null && filterOptions.size() > 0)
                        mFilter = filterOptions.get(postion).getName();
                    if ("hosts".equals(mFilter) || "lists".equals(mFilter)) {
                        adapter.setSubType(mFilter);
                        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(Constant.SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL));
                    } else {
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    }
                    videoList.clear();
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                    onRefresh();
                }*/
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

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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.contest.Contest;
import com.sesolutions.responses.contest.ContestResponse;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.commons.lang.WordUtils;
import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Map;

public class ContestFragment extends ContestHelper<ContestAdapter> implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {


    public String searchKey;
    public int loggedinId;
    public int txtNoData;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    public boolean isLoading;
    public int REQ_LOAD_MORE = 2;
    public ProgressBar pb;
    public String url;

    //O : category
    //1 : sub_category
    //2 : sub_subcategory
    public int categoryLevel;

    //variable used on View Contest -> Entry tab for showing entries of particular contest
    private int contestId;


    public static ContestFragment newInstance(OnUserClickedListener<Integer, Object> listener, int loggedInId, int categoryId) {
        ContestFragment frag = new ContestFragment();
        frag.listener = listener;
        frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        return frag;
    }

    public static ContestFragment newInstance(String TYPE, OnUserClickedListener<Integer, Object> listener) {
        ContestFragment frag = newInstance(listener, -1, -1);
        frag.selectedScreen = TYPE;
        return frag;
    }


    public static ContestFragment newInstance(String TYPE, OnUserClickedListener<Integer,Object> listener, int contestId) {
        ContestFragment frag = newInstance(TYPE, listener);
        frag.contestId = contestId;
        return frag;
    }

    boolean isToolbar=false;
    String type_st="";
    public static ContestFragment newInstance(String TYPE, OnUserClickedListener<Integer,Object> listener, int contestId,boolean isToolbar) {
        ContestFragment frag = newInstance(TYPE, listener);
        frag.contestId = contestId;
        frag.type_st = TYPE;
        frag.isToolbar = isToolbar;
        return frag;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_common_list_refresh, container, false);
        txtNoData = R.string.MSG_NO_CONTEST_CREATED;
        applyTheme(v);

        if (!isToolbar) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);

          String stt=  WordUtils.capitalize(type_st);
          ((TextView) v.findViewById(R.id.tvTitle)).setText(stt);
          v.findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            initScreenData();
        }


        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        switch (selectedScreen) {
            case TYPE_MANAGE:
                loggedinId = SPref.getInstance().getUserMasterDetail(context).getLoggedinUserId();
                url = Constant.URL_CONTEST_MANAGE;
                break;
            case TYPE_ENTRIES:
                url = Constant.URL_ENTRY_BROWSE;
                break;
            case TYPE_CONTEST_ENTRIES:
                url = Constant.URL_CONTEST_ENTRIES;
                break;
            case TYPE_WINNERS:
                url = Constant.URL_WINNER_BROWSE;
                break;
            case TYPE_CATEGORY:
                url = Constant.URL_CONTEST_CATEGORY;
                break;
            case TYPE_VIEW_CATEGORY:
                url = Constant.URL_CONTEST_CATEGORY_VIEW;
                break;
            default:
                url = Constant.URL_CONTEST_BROWSE;
                break;
        }
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

        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1 && contestId < 1) { //contestId < 1 means browse screen otherwise View Screen
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(url); //url will change according to screenType
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                //send menu=1 for fetching filterOptions on My Contest Screen
                if (loggedinId > 0) {
                    request.params.put(Constant.KEY_MENUS, 1);
                }

                if (!TextUtils.isEmpty(searchKey)) {
                    request.params.put(Constant.KEY_SEARCH, searchKey);
                } else {
                    //send filter for fetching filtered contest eg ComingSoon,Ended etc
                    request.params.put(Constant.KEY_FILTER, selectedScreen);
                }

                if (categoryId > 0) {
                    if (categoryLevel == 0) {
                        request.params.put(Constant.KEY_CATEGORY_ID, categoryId);
                    } else if (categoryLevel == 1) {
                        request.params.put("sub_category_id", categoryId);
                    } else if (categoryLevel == 2) {
                        request.params.put("sub_subcategory_id", categoryId);
                    }
                }
                if (contestId > 0) {
                    request.params.put(Constant.KEY_CONTEST_ID, contestId);
                }

                Map<String, Object> map = activity.filteredMap;
                if (null != map) {
                    request.params.putAll(map);
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

                                    if (null != result.getCategory()) {
                                        contestList.add(new Contest(adapter.VT_CATEGORY, result.getCategory()));
                                    }

                                  /*  if (null != result.getCategories()) {
                                        contestList.addAll(result.getContestCategory(adapter.VT_CATEGORIES));
                                    }*/

                                    if (null != result.getEntries()) {
                                        contestList.addAll(result.getEntryList(selectedScreen));
                                    }

                                    if (null != result.getWinners()) {
                                        contestList.addAll(result.getWinnerList(selectedScreen));
                                    }

                                    if (null != result.getContests())
                                        contestList.addAll(result.getContestList(selectedScreen));
                                    updateAdapter();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }
                            }
                        } catch (Exception e) {
                            hideBaseLoader();
                            somethingWrongMsg(v);
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
        showHideNoDataView();
        if (listener != null) {
            listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
        }
    }

    public void showHideNoDataView() {
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(contestList.size() > 0 ? View.GONE : View.VISIBLE);
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
        }catch (Exception e) {
            CustomLog.e(e);
        }
    }

}

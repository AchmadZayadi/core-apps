package com.sesolutions.ui.poll_core;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
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
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.poll.Poll;
import com.sesolutions.responses.poll.PollResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;


import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CMyPollsFragment extends CPollsHelper implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, OnUserClickedListener<Integer, Object> {

    private String txtNoMsg = Constant.MSG_NO_POLL;
    public int txtNoData;
    public String searchKey;
    private static final int REQ_CLOSE = 302;
    public PollResponse.Result result;
    public int loggedinId;
    public int categoryId;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private CMyPollsAdapter myPollsAdapter;
    private ProgressBar pb;
    private Poll poll;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_list_common_offset_refresh, container, false);
        applyTheme(v);
        txtNoMsg = loggedinId > 0 ? Constant.MSG_NO_POLL_CREATED_YOU : Constant.MSG_NO_POLL_CREATED;
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
            myPollsAdapter = new CMyPollsAdapter(videoList, context, this, this);
            myPollsAdapter.setLoggedInId(loggedinId);
            recyclerView.setAdapter(myPollsAdapter);
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

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.hidden_panel:
                    hideSlidePanel();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    //Api call
    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_POLL_BROWSE);
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
                    request.params.put(Constant.KEY_PAGE, null != result && req != 1 ? result.getNextPage() : 1);
                    if (req == Constant.REQ_CODE_REFRESH) {
                        request.params.put(Constant.KEY_PAGE, 1);
                    }
//                    isLoading=false;

//                    setRefreshing(swipeRefreshLayout, false);
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
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
                                            parent.isMypollloaded = true;
                                        }
                                        PollResponse resp = new Gson().fromJson(response, PollResponse.class);
                                        if (req == Constant.REQ_CODE_REFRESH) {
                                            videoList.clear();
                                        }
                                        result = resp.getResult();

                                        if (null != result.getPolls())
                                            videoList.addAll(result.getPolls());

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
//                    isLoading = false;
//                    pb.setVisibility(View.GONE);
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

    private void updateAdapter() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        myPollsAdapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        setRefreshing(swipeRefreshLayout, false);

        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoMsg);
        v.findViewById(R.id.llNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);
        if (parent != null) {
            int index = loggedinId != 0 ? 2 : 0;
            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, index, result.getTotal());
        }
    }

    public static CMyPollsFragment newInstance(CPollParentFragment parent, int loggedInId, int categoryId) {
        CMyPollsFragment frag = new CMyPollsFragment();
        frag.parent = parent;
        frag.loggedinId = loggedInId;
        frag.categoryId = categoryId;
        return frag;
    }

    public static CMyPollsFragment newInstance(CPollParentFragment parent, int loggedInId) {
        return newInstance(parent, loggedInId, -1);

    }

    public static CMyPollsFragment newInstance(int categoryId) {
        return newInstance(null, 0, categoryId);
    }

    @Override
    public void openCViewPollFragment(int pollId) {
        openCCViewPollFragment(pollId);
    }

    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }

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
            hideBaseLoader();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1) {

            case Constant.Events.FEED_UPDATE_OPTION:
                Options vo = videoList.get(Integer.parseInt("" + object2)).getMenus().get(postion);
                int pollId = videoList.get(Integer.parseInt("" + object2)).getPollId();
                performMusicOptionClick(pollId, vo, Integer.parseInt("" + object2), postion);
                break;
        }
        return super.onItemClicked(object1, object2, postion);
    }

    public void goToFormFragment(int pollId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_POLL_ID, pollId);
        // map.put(Constant.KEY_GET_FORM, 1);
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(Constant.FormType.EDIT_CORE_POLL, map, Constant.URL_POLL_EDIT)).addToBackStack(null).commit();
    }

    private void performMusicOptionClick(int pollId, Options vo, int listPosition, int postion) {

        switch (vo.getName()) {
            case Constant.OptionType.EDIT:
                goToFormFragment(pollId);
                break;

            case Constant.OptionType.DELETE:
                showDeleteDialog(videoList.get(listPosition).getPollId(), listPosition);
                break;
            case Constant.OptionType.CLOSE_POLL:
                if (isNetworkAvailable(context)) {
                    Map<String, Object> map = new HashMap<>();
                    map = new HashMap<>();
                    map.put(Constant.KEY_POLL_ID, pollId);
                    // map.put(Constant.KEY_POLL_ID, pollId);
                    if (videoList.get(listPosition).isclosed() == 0) {
                        new ApiController(Constant.URL_POLL_CLOSE, map, context, this, REQ_CLOSE).execute();
                        Util.showSnackbar(v, "Poll Closed");
                        onRefresh();
                    } else {
                        new ApiController(Constant.URL_POLL_CLOSE, map, context, this, REQ_CLOSE).execute();
                        Util.showSnackbar(v, "Poll Opened.");
                        onRefresh();
                    }


                } else {
                    notInternetMsg(v);
                }
        }
    }

    public void showDeleteDialog(final int albumId, final int position) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = (TextView) progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(Constant.MSG_DELETE_POLL);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    Util.showSnackbar(v, "Poll has been succesfully deleted.");
                    callDeleteApi(albumId, position);

                    //callSaveFeedApi( Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);

                }
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi(final int pollId, final int position) {

        try {
            if (isNetworkAvailable(context)) {

                videoList.remove(position);
                myPollsAdapter.notifyItemRemoved(position);
                myPollsAdapter.notifyItemRangeChanged(position, videoList.size());

                try {

                    //  HttpRequestVO request = new HttpRequestVO(Constant.BASE_URL + "album/delete/" + albumId + Constant.POST_URL);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_POLL_DELETE);
                    request.params.put(Constant.KEY_POLL_ID, pollId);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                Util.showSnackbar(v, "Poll has been succesfully deleted.");
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {

                                        // Util.showSnackbar(v, new JSONObject(response).getString("result"));
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

                    hideBaseLoader();

                }

            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {

            CustomLog.e(e);
            hideBaseLoader();
        }
    }


}



package com.sesolutions.ui.storyview;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.story.Result;
import com.sesolutions.responses.story.StoryResponse;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MutedMemberFragment extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    private final int REQ_STORY_MUTE = 670;
    public View v;
    private RecyclerView recyclerView;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private Result result;
    private List<StoryModel> categoryList;
    private StoryViewerAdapter adapter;
    private View pb;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isHighlighting = false;

   /* public static ArchiveFragment newInstance(String rcType) {
        ArchiveFragment fragment = new ArchiveFragment();
        fragment.rcType = rcType;
        return fragment;
    }*/

    @Override
    public void onStart() {
        super.onStart();
        activity.setStatusBarColor(Color.BLACK);
    }

    @Override
    public void onStop() {
        activity.setStatusBarColor(SesColorUtils.getPrimaryDarkColor(context));
        super.onStop();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_muted_member, container, false);
        applyTheme(v);
        initScreenData();
        return v;
    }

    private void setBlackTheme() {
        v.findViewById(R.id.toolbar).setBackgroundColor(Color.BLACK);
        v.findViewById(R.id.swipeRefreshLayout).setBackgroundColor(Color.BLACK);
    }


    private void init() {
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(R.string.story_you_muted);
        recyclerView = v.findViewById(R.id.recyclerView);

        pb = v.findViewById(R.id.pb);

    }


    private void setRecyclerView() {
        try {
            categoryList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            //StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constant.SPAN_COUNT + 1, StaggeredGridLayoutManager.VERTICAL);
            //  recyclerView.setLayoutManager(layoutManager);
            adapter = new StoryViewerAdapter(categoryList, context, this);
            //this will disable blink effect happening on  on_Item_click
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
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
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;

        }
    }

    public void initScreenData() {
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
    }

    private void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }

                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_STORY_MUTED_MEMBERS);
                    request.params.put(Constant.KEY_USER_ID, SPref.getInstance().getLoggedInUserId(context));
                    //request.params.put("userarchivedstories", 1);
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
                                    StoryResponse resp = new Gson().fromJson(response, StoryResponse.class);
                                    if (TextUtils.isEmpty(resp.getError())) {

                                       /* if (req == Constant.REQ_CODE_REFRESH) {
                                            categoryList.clear();
                                        }*/
                                        wasListEmpty = categoryList.size() == 0;
                                        result = resp.getResult();
                                        if (null != result.getViewers()) {
                                            categoryList.addAll(result.getViewers());
                                            // saveHighlightedItem(result.getStories().get(0).getImages());
                                        }
                                        updateAdapter();
                                    } else {
                                        Util.showSnackbar(v, resp.getErrorMessage());
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


    private void hideAllLoaders() {
        isLoading = false;
        hideView(v.findViewById(R.id.pbMain));
        hideView(pb);
        //swipeRefreshLayout.setRefreshing(false);
    }


    private void updateAdapter() {
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.msg_archive_description);
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
    public boolean onItemClicked(Integer object1, Object data, int postion) {
        switch (object1) {

            case Constant.Events.LOAD_MORE:
                onLoadMore();
                break;
            case REQ_STORY_MUTE:
                if (null != data) {
                    StoryResponse res = new Gson().fromJson("" + data, StoryResponse.class);
                    if (res.isSuccess()) {
                        categoryList.get(postion).setOptions(res.getResult().getOption());
                    }
                }
                break;

            case Constant.Events.CLICKED_OPTION:
                /*if (categoryList.get(postion).toggleHighlight()) {
                    unHighlightStory(categoryList.get(postion).getStoryId());
                } else {

                }*/
                if (isNetworkAvailable(context)) {
                    callMuteAPI(postion);
                    categoryList.get(postion).toggleMuteOption(getString(R.string.unmute), getString(R.string.mute));
                    adapter.notifyItemChanged(postion);
                } else {
                    notInternetMsg(v);
                }
                break;
        }
        return false;
    }

    private void callMuteAPI(int position) {
        int muteId = categoryList.get(position).getOptions().getMuteId();
        Map<String, Object> map = new HashMap<>();
        if (muteId > 0) {
            map.put("mute_id", muteId);
            new ApiController(Constant.URL_STORY_UNMUTE, map, context, this, REQ_STORY_MUTE).setExtraKey(position).execute();
        } else {
            map.put(Constant.KEY_USER_ID, categoryList.get(position).getUserId());
            new ApiController(Constant.URL_STORY_MUTE, map, context, this, REQ_STORY_MUTE).execute();
        }
    }
}

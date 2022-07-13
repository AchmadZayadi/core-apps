package com.sesolutions.ui.profile;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.feed.FeedResponse;
import com.sesolutions.ui.dashboard.StaticShare;
import com.sesolutions.ui.dashboard.VideoFeedAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;

import cn.jzvd.JzvdStd;
import cn.jzvd.JzvdStd2;


public class VideoFeedFragment extends FeedFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, OnLoadMoreListener {
    public RecyclerView recycleViewFeedMain;
    public LinearLayoutManager layoutManager;

    private ShimmerFrameLayout mShimmerViewContainer;
    int previouselement=-10;
    private int videoActionId;


    public static VideoFeedFragment newInstance(int videoActionId) {
        VideoFeedFragment frag = new VideoFeedFragment();
        frag.videoActionId = videoActionId;
        return frag;
    }

    public static VideoFeedFragment newInstance(int rcId, String rcType) {
        VideoFeedFragment frag = new VideoFeedFragment();
        frag.resourceId = rcId;
        frag.resourceType = rcType;
        return frag;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        if (v != null) {
            return v;
        }

        try {
            v = inflater.inflate(R.layout.fragment_common_feed, container, false);

            applyTheme();
            mShimmerViewContainer = v.findViewById(R.id.shimmer_view_container);
            mShimmerViewContainer.setVisibility(View.GONE);
            v.findViewById(R.id.rlMain).setBackgroundColor(Color.BLACK);
            //mShimmerViewContainer.startShimmerAnimation();
            initScreenData();
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.setStatusBarColor(Color.BLACK);
    }

    public void initScreenData() {
        super.canCacheData = false;
        pb = v.findViewById(R.id.pb);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            v.findViewById(R.id.ivPip).setVisibility(View.GONE);
            v.findViewById(R.id.ivPip).setOnClickListener(v1 -> getActivity().enterPictureInPictureMode());
        }

        initMainRecyclerView();
        feedActivityList.add(StaticShare.FEED_ACTIVITY);
        adapterFeedMain.notifyItemInserted(0);
        callFeedApi(REQ_CODE_FEED);
    }

    @Override
    public void callFeedApi(final int REQ_CODE) {

        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                HttpRequestVO request = new HttpRequestVO(Constant.URL_FEED_ACTIVITY);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                request.params.put("allvideos", 1);
                request.params.put("action_video_id", videoActionId);

                if (REQ_CODE == REQ_CODE_LOADING_MORE_FEED) {
                    request.params.put(Constant.KEY_NEXT_ID, feedResponse.getResult().getNextid());
                    //request.params.put(Constant.KEY_FILTER_FEED, selectedFeedType);
                    request.params.put(Constant.KEY_CONTENT_COUNTER, feedResponse.getResult().getContentCounter());
                }

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = msg -> {
                    hideBaseLoader();
                    try {
                        String response = (String) msg.obj;
                        //  String response = URLDecoder.decode((String) msg.obj, "UTF-8");
                        if (REQ_CODE == REQ_CODE_REFRESH)
                            updateNotificationCount(0);
                        isLoading = false;
                        setRefreshing(false);
                        CustomLog.e("response_feed_video", "" + response);
                        if (response != null) {
                            /*//always save latest response
                            if (canCacheData && REQ_CODE == REQ_CODE_REFRESH) {
                                SPref.getInstance().saveFeedItems(context, response);
                            }*/

                            feedResponse = new Gson().fromJson(response, FeedResponse.class);
                            initMainRecyclerView();

                            int size = feedActivityList.size();
                            if (null != feedResponse.getResult().getActivity()) {
                                for (int j=0;j<feedResponse.getResult().getActivity().size();j++){
                                       if(feedResponse.getResult().getActivity().get(j).getContentType().equalsIgnoreCase("feed")) {
                                           feedActivityList.add(feedResponse.getResult().getActivity().get(j));
                                       }
                                }

                                int newItems = feedActivityList.size();
                                /*try {

                                    if (AppConfiguration.isAdEnabled) {
                                        for (int i = newItems - 1; i > 0; i--) {
                                            if (i % AppConfiguration.AD_POS == 0) {
                                                feedActivityList.add(size + i, new Activity(Constant.ItemType.GOOGLE_AD));
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    CustomLog.e(e);
                                }*/

                                if (size == 0) {
                                    adapterFeedMain.notifyDataSetChanged();
                                } else {
                                    adapterFeedMain.notifyItemRangeInserted(size, newItems);
                                }
                            }
                            updateFeedMainRecycleview();
                        }

                    } catch (Exception e) {
                        hideBaseLoader();
                        pb.setVisibility(View.GONE);
                        setRefreshing(false);
                        CustomLog.e(e);
                    }

                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                isLoading = false;
                pb.setVisibility(View.GONE);
                hideBaseLoader();
                CustomLog.e(e);
            }

        } else {
            notInternetMsg(v);
        }

    }

 @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        if (isInPictureInPictureMode) {
            v.findViewById(R.id.ivPip).setVisibility(View.GONE);
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            final View child = layoutManager.findViewByPosition(firstVisiblePosition);
            if (null != child) {
                ((RelativeLayout) child.findViewById(R.id.rlFeedHeader)).setVisibility(View.GONE);
                ((JzvdStd) child.findViewById(R.id.videoplayer)).startVideo();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.findViewById(R.id.ivPip).setVisibility(View.GONE);
            }

            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            final View child = layoutManager.findViewByPosition(firstVisiblePosition);
            if (null != child) {
                ((RelativeLayout) child.findViewById(R.id.rlFeedHeader)).setVisibility(View.VISIBLE);
                ((JzvdStd) child.findViewById(R.id.videoplayer)).startVideo();

            }
        }
    }

    @Override
    public void initMainRecyclerView() {
        if (recycleViewFeedMain != null) return;
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        //disable swipeToRefresh in VideoFeed
        swipeRefreshLayout.setEnabled(false);
        //swipeRefreshLayout.setOnRefreshListener(this);

        recycleViewFeedMain = v.findViewById(R.id.recycler_view_feed_main);
        setFeedMainRecycleView();
    }

    @Override
    public void updateComposerUI() {
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void updateFeedMainRecycleview() {
        adapterFeedMain.canPlayFirstVideo(true);
        super.updateFeedMainRecycleview();
    }

    public void setFeedMainRecycleView() {
        try {
            feedActivityList = new ArrayList<>();
           // recycleViewFeedMain.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(context);
            recycleViewFeedMain.setLayoutManager(layoutManager);
            //this will disable blink effect happening on  on_Item_change
            ((SimpleItemAnimator) recycleViewFeedMain.getItemAnimator()).setSupportsChangeAnimations(false);
            adapterFeedMain = new VideoFeedAdapter(feedActivityList, context, this);
            adapterFeedMain.canPlayFirstVideo(true);
            adapterFeedMain.setComposer(composerOption);
            recycleViewFeedMain.setAdapter(adapterFeedMain);
            adapterFeedMain.setLoadListener(this);
            adapterFeedMain.setHome(true);

       //     recycleViewFeedMain.addOnChildAttachStateChangeListener(new ChildAttachStateChangeListener());


          /*  recycleViewFeedMain.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    System.gc();
                    int visibleItemCount = layoutManager.getChildCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    final View child = layoutManager.findViewByPosition(firstVisibleItemPosition);
                   ((JzvdStd) child.findViewById(R.id.videoplayer)).onStatePreparingPlaying();

                }
            });*/





          recycleViewFeedMain.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        // Scrolling up
                    } else {
                        // Scrolling down
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    try {

                        LinearLayoutManager lManager = (LinearLayoutManager) recycleViewFeedMain.getLayoutManager();
                        int firstElementPosition = lManager.findLastVisibleItemPosition();
                        if(firstElementPosition!=previouselement){
                            previouselement =firstElementPosition;
                            Log.e("ElementPostion",""+firstElementPosition);
                            final View child = layoutManager.findViewByPosition(firstElementPosition);
                            if (null != child)
                                ((JzvdStd2) child.findViewById(R.id.videoplayer)).startButton.performClick();
                        }
                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                }
            });

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}

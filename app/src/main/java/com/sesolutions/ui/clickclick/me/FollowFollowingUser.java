package com.sesolutions.ui.clickclick.me;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.videos.Result;
import com.sesolutions.responses.videos.VideoBrowse;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.ui.clickclick.notification.FollowandUnfollow;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;

import static com.sesolutions.utils.URL.POST_URL;

public class FollowFollowingUser extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    public RecyclerView recyclerView;
    private View v;
    public List<Videos> followersList;
    private FollowerAdapter Followeradapter;
    private boolean isLoading;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Result result3;
    private ProgressBar pb;
    private LinearLayoutManager layoutManager;
    private boolean isHomePressed;
    int followers_userid = 0;
    String userTitle = "";

    private OnUserClickedListener<Integer, Object> parent;

    public static FollowFollowingUser newInstance(OnUserClickedListener<Integer, Object> parent) {
        FollowFollowingUser frag = new FollowFollowingUser();
        frag.parent = parent;
        return frag;
    }

    public static FollowFollowingUser newInstance(int userid, String usertitle) {
        FollowFollowingUser frag = new FollowFollowingUser();
        frag.followers_userid = userid;
        frag.userTitle = usertitle;
        return frag;
    }

    private void setFollowersList() {
        try {
            followersList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            Followeradapter = new FollowerAdapter(followersList, context, this, this, Constant.FormType.TYPE_SONGS);
            recyclerView.setAdapter(Followeradapter);
            recyclerView.setNestedScrollingEnabled(false);

           /* recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    try {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                            if (firstVisiblePosition <= 1 && isHomePressed) {
                                isHomePressed = false;
                                //      if (((MainActivity) activity).dashboardFragment.unreadCount[3] != 0) {
                                onRefresh();
                                //    }
                            }
                        }
                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                }
            });*/

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
            {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy)
                {
                    super.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState)
                {
                   //super.onScrollStateChanged(recyclerView, newState);
                    int firstPos=layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (firstPos>0)
                    {
                        swipeRefreshLayout.setEnabled(false);
                    }
                    else {
                        swipeRefreshLayout.setEnabled(true);
                    }
                }
            });


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callFollowApi(int id, int pos) {

        try {
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FOLLOW_MEMBER);

                    request.params.put(Constant.KEY_USER_ID, id);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                Log.e("response", "" + response);
                                FollowandUnfollow followandUnfollow = new Gson().fromJson(response, FollowandUnfollow.class);

                                try {
                                    if (followandUnfollow.getResult().getMember().getFollow().getAction().equalsIgnoreCase("follow")) {
                                        Util.showSnackbar(v, "user unfollow successfully.");
//                                        tvFollowbtn.setText("+ FOLLOW");
                                        GradientDrawable gdr = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.rounded_filled_lover);
                                        gdr.setColor(SesColorUtils.getPrimaryColor(context));
                                        onRefresh();

                                    } else {
                                        Util.showSnackbar(v, "user follow successfully.");
//                                        tvFollowbtn.setText("+ UNFOLLOW");

                                        GradientDrawable gdr = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.rounded_filled_lover);
                                        gdr.setColor(Color.GRAY);
//                                        tvFollowbtn.setBackground(gdr);
//                                        followersList.get(pos).isFollowed = true;
                                        onRefresh();
                                    }
                                    Followeradapter.notifyItemChanged(pos);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    hideBaseLoader();

                                }
                               /* CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        JSONObject json = new JSONObject(response);
                                        String message = json.getString(Constant.KEY_RESULT);
                                        if (message.equalsIgnoreCase("Channel follow successfully.")) {
                                            Util.showSnackbar(v, message);
                                        } else {
                                            Util.showSnackbar(v, message);
                                        }
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                }*/

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_follower_following, container, false);
//        ((MainActivity) activity).changeCurrentFragment();

        v.findViewById(R.id.appBar).setVisibility(View.GONE);
        initScreenData();
        return v;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getActivity().finish();
    }

    public void initScreenData() {
        applyTheme(v);
        if (SPref.getInstance().isLoggedIn(context)) {
            init();
            setFollowersList();
            callFollowersApi(1, 1);
        } else {
            v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NOT_LOGGED_IN);
        }
    }

    private void init() {
        followersList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.recyclerView);
        pb = v.findViewById(R.id.pb);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    @Override
    public void onRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        callFollowersApi(1, 1);
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


    private void updateRecyclerView() {
        isLoading = false;
        Followeradapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        //   paginate.showLoading(false);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.MSG_NO_NOTIFICATION);
        v.findViewById(R.id.llNoData).setVisibility(followersList.size() > 0 ? View.GONE : View.VISIBLE);
       /* if (parent != null) {
            parent.onItemClicked(Constant.Events.SET_LOADED, MenuTab.Dashboard.NOTIFICATION, 1);
        }*/
    }

    @Override
    public void onLoadMore() {
        try {
            if (result3 != null && !isLoading) {
                if (result3.getCurrentPage() < result3.getTotalPage()) {
                    callFollowersApi(1, 1);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void scrollToStart() {
        if (null != followersList && followersList.size() > 0) {
            if (layoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
                isHomePressed = true;
                recyclerView.smoothScrollToPosition(0);
            } else {
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }

        }
    }

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        Log.e("5456", "41211");
        try {
            switch (eventType) {
                case Constant.Events.USER_SELECT:
                    Log.e("user", "profile");
                    goTo(Constant.GoTo.VIEW_PROFILE, Constant.KEY_ID, followersList.get(position).getUserId());
                    break;
                case Constant.Events.FOLLOW_USER:
                    Log.e("user", "" + position);
                    callFollowApi(followersList.get(position).getUserId(), position);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private final int REQ_LOAD_MORE = 2;

    private void callFollowersApi(final int req, int channel) {

        if (isNetworkAvailable(context)) {
            isLoading = true;
            String URL_DATA = "";
            if (userTitle.equalsIgnoreCase("Followers")) {
                URL_DATA = URL.URL_CHANNEL_FOLLOWERS2 + "" + followers_userid + "" + POST_URL;
            } else {
                URL_DATA = URL.URL_CHANNEL_FOLLOWING_USER + "" + followers_userid + "" + POST_URL;
            }


            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showView(v.findViewById(R.id.pbMain));
                }
                HttpRequestVO request = new HttpRequestVO(URL_DATA);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                request.params.put(Constant.KEY_CHANNEL_ID_VIDEO, followers_userid);
             /*       if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideLoaders();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                followersList.clear();
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    showView(v.findViewById(R.id.cvDetail));
                                    VideoBrowse resp = new Gson().fromJson(response, VideoBrowse.class);
                                    result3 = resp.getResult();
                                    if (null != result3.getNotifications()) {
                                        followersList.addAll(result3.getNotifications());
                                    }
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }
                                updateFollowersAdapter();
                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                            v.findViewById(R.id.tvNoData).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
                            if (userTitle.equalsIgnoreCase("Followers")) {
                                ((TextView) v.findViewById(R.id.tvNoData)).setText("No one has followed this user yet.");
                            } else {
                                ((TextView) v.findViewById(R.id.tvNoData)).setText("No one has following this user yet.");
                            }
                            ((TextView) v.findViewById(R.id.tvNoData)).setTextColor(Color.parseColor("#FFFFFF"));
                        }

                        // dialog.dismiss();
                        return true;
                    }
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideLoaders();
            }
        } else {
            isLoading = false;
            pb.setVisibility(View.GONE);
            notInternetMsg(v);
        }
    }

    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
        hideView(v.findViewById(R.id.pbMain));
        hideBaseLoader();
    }


    private void updateFollowersAdapter() {
        isLoading = false;
        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);
        Followeradapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);

        if (userTitle.equalsIgnoreCase("Followers")) {
            ((TextView) v.findViewById(R.id.tvNoData)).setText("No one has followed this user yet.");
        } else {
            ((TextView) v.findViewById(R.id.tvNoData)).setText("No one has following this user yet.");
        }

        v.findViewById(R.id.tvNoData).setVisibility(followersList.size() > 0 ? View.GONE : View.VISIBLE);
        v.findViewById(R.id.llNoData).setVisibility(followersList.size() > 0 ? View.GONE : View.VISIBLE);
        ((TextView) v.findViewById(R.id.tvNoData)).setTextColor(Color.parseColor("#FFFFFF"));

    }


}

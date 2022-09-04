package com.sesolutions.ui.dashboard;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.TextView;

import com.dizcoding.adapterdelegate.DelegatesAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Activity;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.AGvideo.AGVideo;
import com.sesolutions.ui.AGvideo.mediaplayer.MediaExo;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.price.PriceDataResponse;
import com.sesolutions.ui.price.PriceResponse;
import com.sesolutions.ui.price.adapter.PriceHolderAdapterKt;
import com.sesolutions.ui.price.adapter.PriceItemModel;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.ui.storyview.StoryModel;
import com.sesolutions.ui.weather.WeatherDataResponse;
import com.sesolutions.ui.weather.WeatherResponse;
import com.sesolutions.ui.weather.weather.WeatherAdapterKt;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.ChildAttachStateChangeListener;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.jzvd.JZDataSource;
import cn.jzvd.JzvdStd;
import cn.jzvd.JzvdStd2;


/**
 * Created by WarFly on 10/7/2017.
 */

public class HomeFragment extends FeedHelper implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, OnLoadMoreListener {
    RecyclerView recycleViewFeedMain;
    RecyclerView rvWeather;
    RecyclerView rvPrice;
    private ShimmerFrameLayout mShimmerViewContainer;
    public OnUserClickedListener<Integer, Object> parent;
    int highestnumber = 0, previousnumber = 0;
    LinearLayoutManager layoutManagerWeather;
    LinearLayoutManager layoutManagerPrice;
    private DelegatesAdapter<WeatherDataResponse> adapterWeather;
    private DelegatesAdapter<PriceItemModel> adapterPrice;
    WebView webViewWeather;

    public static HomeFragment newInstance(OnUserClickedListener<Integer, Object> parent) {
        HomeFragment frag = new HomeFragment();
        frag.parent = parent;
        return frag;
    }


    int commentpostion = 0;

    @Override
    public void onResume() {
        super.onResume();
        Log.e("BaseActvity", "" + BaseActivity.backcoverchange);
        if (BaseActivity.backcoverchange == Constant.GO_TO_HOMEFRAGMENT) {
            feedActivityList.get(commentpostion).setCommentCount(BaseActivity.commentcount);
            adapterFeedMain.notifyItemChanged(commentpostion);
            BaseActivity.backcoverchange = 0;
            BaseActivity.commentcount = 0;
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        if (v != null) {
            return v;
        }

        try {
            v = inflater.inflate(R.layout.fragment_common_feed, container, false);
            getActivity().getWindow().setStatusBarColor(Color.parseColor(Constant.colorPrimary));
            //setting currentfragment as Dashboard so it can handle drawer open/close logic
            ((MainActivity) activity).changeCurrentFragment();
            applyTheme();
            // appBarLayout = v.findViewById(R.id.appbar);
            // appBarLayout.addOnOffsetChangedListener(this);
            mShimmerViewContainer = v.findViewById(R.id.shimmer_view_container);

            mShimmerViewContainer.startShimmerAnimation();
            // new ThemeManager().applyTheme((ViewGroup) v, context);
           // callApiPrice();
           // callApiWeather();

        } catch (Exception e) {
            CustomLog.e(e);
        }

        return v;
    }


    public void initScreenData() {

        super.canCacheData = true;
        //check if composer options already saved in caceh
        composerOption = SPref.getInstance().getComposerOptions(context);

        //price
        adapterPrice = new DelegatesAdapter<PriceItemModel>(
                PriceHolderAdapterKt.priceHolderAdapter()
        );

        //weather
        adapterWeather = new DelegatesAdapter<WeatherDataResponse>(
                WeatherAdapterKt.weatherAdapter()
        );
        if (null != composerOption) {
            /*if saved then directly fetch feed api  and update composer ui*/
            updateComposerUI();
            callComposerOptionApi(false);
            /* now check if feed items are saved or not if yes then load*/


            feedResponse = SPref.getInstance().getFeedItems(context);
            if (null != feedResponse) {
                setContentLoaded();
                initMainRecyclerView();
                feedActivityList.addAll(feedResponse.getResult().getActivity());
                updateFeedMainRecycleview();
                onRefresh();
            } else {
                callFeedApi(REQ_CODE_FEED);
            }
        } else {
            callComposerOptionApi();
        }
    }


    @Override
    public void callStoryApi() {

        if (isNetworkAvailable(context)) {
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_USER_ID, SPref.getInstance().getLoggedInUserId(context));
            new ApiController(Constant.URL_STORY_BROWSE, map, context, this, REQ_STORY).execute();
        }
    }

    private void initComposer() {
        pb = v.findViewById(R.id.pb);
    }

    int lastpostion = 0;

    @Override
    public void initMainRecyclerView() {
        if (recycleViewFeedMain != null) return;
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);


        recycleViewFeedMain = v.findViewById(R.id.recycler_view_feed_main);


        //webview http://report.matani.id/beranda
        webViewWeather = v.findViewById(R.id.webView);
        //  String postData2 = "username=" + URLEncoder.encode(, "UTF-8") + "&password=" + URLEncoder.encode(my_password, "UTF-8");
        String postData = "auth_token=" + SPref.getInstance().getToken(context);

        webViewWeather.postUrl(Constant.URL_WEATHER_HOME, postData.getBytes());


        rvWeather = v.findViewById(R.id.rv_weather);
        rvPrice = v.findViewById(R.id.rv_price);


        //price
        rvPrice.setHasFixedSize(true);
        layoutManagerPrice = new LinearLayoutManager(context);
        rvPrice.setLayoutManager(layoutManagerPrice);
        rvPrice.setAdapter(adapterPrice);

        //weather
        rvWeather.setHasFixedSize(true);
        layoutManagerWeather = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        rvWeather.setLayoutManager(layoutManagerWeather);
        rvWeather.setAdapter(adapterWeather);


        setFeedMainRecycleView();

        recycleViewFeedMain.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                try {
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        if (recycleViewFeedMain != null) {
                            LinearLayoutManager layoutManager = ((LinearLayoutManager) recycleViewFeedMain.getLayoutManager());

                            final int firstPosition = layoutManager.findFirstVisibleItemPosition();
                            final int lastPosition = layoutManager.findLastVisibleItemPosition();

                            Rect rvRect = new Rect();
                            recycleViewFeedMain.getGlobalVisibleRect(rvRect);

                            highestnumber = 0;
                            previousnumber = 0;

                            for (int i = firstPosition; i <= lastPosition; i++) {
                                Rect rowRect = new Rect();
                                layoutManager.findViewByPosition(i).getGlobalVisibleRect(rowRect);

                                int percentFirst;
                                if (rowRect.bottom >= rvRect.bottom) {
                                    int visibleHeightFirst = rvRect.bottom - rowRect.top;
                                    percentFirst = (visibleHeightFirst * 100) / layoutManager.findViewByPosition(i).getHeight();
                                } else {
                                    int visibleHeightFirst = rowRect.bottom - rvRect.top;
                                    percentFirst = (visibleHeightFirst * 100) / layoutManager.findViewByPosition(i).getHeight();
                                }

                                if (percentFirst > 100) {
                                    percentFirst = 100;
                                }

                                Log.e("percentFirst", "-" + percentFirst);
                                Log.e("highestnumber", "-" + highestnumber);
                                Log.e("postion", "-" + i);

                                if (percentFirst > highestnumber) {
                                    highestnumber = percentFirst;
                                    previousnumber = i;
                                }
                                Log.e("PERCENT_FIRST" + i, "-" + percentFirst);
                            }
                        }
                        final View child = layoutManager.findViewByPosition(previousnumber);
                        try {
                            if (null != child) {
                                if (feedActivityList.get(previousnumber).getAttachment().getAttachmentType().equalsIgnoreCase("video")) {
                                    if (lastpostion != 0) {
                                        feedActivityList.get(previousnumber).getAttachment().set_can_play(false);
                                        adapterFeedMain.notifyItemChanged(lastpostion);
                                    }
                                    feedActivityList.get(previousnumber).getAttachment().set_can_play(true);
                                    adapterFeedMain.notifyItemChanged(previousnumber);
                                    lastpostion = previousnumber;
                                } else {
                                    feedActivityList.get(previousnumber).getAttachment().set_can_play(false);
                                    adapterFeedMain.notifyItemChanged(lastpostion);
                                    lastpostion = 0;
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    try {
                        CustomLog.e(e);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });


    }

    @Override
    public void updateComposerUI() {
        try {
            ((MainActivity) activity).dashboardFragment.setToolbarImage(composerOption.getResult().getUser_image());

            try {
                UserMaster vo = SPref.getInstance().getUserMasterDetail(context);
                vo.setPhotoUrl(composerOption.getResult().getUser_image());
                SPref.getInstance().saveUserMaster(context, vo, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            initComposer();
            SPref.getInstance().saveReactionPluginType(context, composerOption.getResult().getReaction_plugin());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.tvOption1:
                case R.id.llOption1:
                    goToPostFeed(composerOption, 0);
                    break;
                case R.id.tvOption2:
                case R.id.llOption2:
                    goToPostFeed(composerOption, 1);
                    break;
                case R.id.tvOption3:
                case R.id.llOption3:
                    goToPostFeed(composerOption, 2);
                    break;
//                case R.id.llOption4:
//                    startActivity(new Intent(context, FireVideoActivity.class));
//                    break;
                case R.id.ivProfile:
                    goTo(Constant.GoTo.PROFILE, Constant.KEY_ID, SPref.getInstance().getUserMasterDetail(context).getUserId());
                    break;
                case R.id.tvPostSomething:
                    onItemClicked(Constant.Events.CLICKED_POST_SOMETHING, "", 0);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void updateFeedMainRecycleview() {
        if (null != mShimmerViewContainer && mShimmerViewContainer.getVisibility() == View.VISIBLE) {
            mShimmerViewContainer.stopShimmerAnimation();
            mShimmerViewContainer.setVisibility(View.GONE);
            v.findViewById(R.id.rlSimmerMain).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
        }
        super.updateFeedMainRecycleview();
    }


    @Override
    protected void setContentLoaded() {
        if (activity instanceof MainActivity) {
            if (parent != null) {
                parent.onItemClicked(Constant.Events.SET_LOADED, MenuTab.Dashboard.HOME, 0);
                ((MainActivity) activity).dashboardFragment.isHomeContentLoaded = true;
            }
        }
    }

    LinearLayoutManager layoutManager;

    void setFeedMainRecycleView() {
        try {
            feedActivityList = new ArrayList<>();
            adapterFeedMain = new FeedActivityAdapter(feedActivityList, context, this);

            //adding first item for composer
            if (null != composerOption) {
                feedActivityList.add(new Activity(Constant.ItemType.COMPOSER));
                addDummyStory();
                feedActivityList.add(new Activity(Constant.ItemType.FEED_FILTER));
            }

            recycleViewFeedMain.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(context);
            recycleViewFeedMain.setLayoutManager(layoutManager);
            //this will disable blink effect happening on  on_Item_change
            ((SimpleItemAnimator) recycleViewFeedMain.getItemAnimator()).setSupportsChangeAnimations(false);


            adapterFeedMain.setComposer(composerOption);

            recycleViewFeedMain.setAdapter(adapterFeedMain);
            recycleViewFeedMain.addOnChildAttachStateChangeListener(new ChildAttachStateChangeListener());
            adapterFeedMain.setLoadListener(this);
            adapterFeedMain.setHome(true);
           // recycleViewFeedMain.setNestedScrollingEnabled(false);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void addDummyStory() {
        if (AppConfiguration.isStoryEnabled && SPref.getInstance().isLoggedIn(context)) {
            UserMaster userVo = SPref.getInstance().getUserMasterDetail(context);
            List<StoryModel> list = new ArrayList<>();
            list.add(new StoryModel(userVo.getPhotoUrl(), userVo.getDisplayname(), userVo.getUserId()));
            adapterFeedMain.setStories(list);
            feedActivityList.add(new Activity(Constant.ItemType.STORY));
        }
    }

    public void goToComment(int position) {
        //Activity vo = feedActivityList.get(position);
        commentpostion = position;
        Intent intent = new Intent(activity, CommonActivity.class);
        intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_COMMENT);
        intent.putExtra(Constant.KEY_ACTION_ID, feedActivityList.get(position).getActionId());
        String guid = feedActivityList.get(position).getAttributionGuid();
        if (null != guid) {
            intent.putExtra(Constant.KEY_GUID, guid);
        }
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);

    }

    public void showDeleteDialog(final Context context, final int actionId, final Options vo, final int actPosition, final int position) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callSaveFeedApi(REQ_CODE_OPTION_DELETE, Constant.URL_FEED_DELETE, actionId, actPosition, position);
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void callSaveFeedApi(final int reqCode, String url, int actionId, final int actPosition, final int position) {
        try {
            if (isNetworkAvailable(context)) {

                try {
                    // showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(url);
                    // request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_ACTIVITY_ID, actionId);
                    request.params.put(Constant.KEY_ACTION_ID, actionId);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        //  hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            isLoading = false;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                BaseResponse<Object> resp = new Gson().fromJson(response, BaseResponse.class);


                                if (TextUtils.isEmpty(resp.getError())) {
                                    switch (reqCode) {
                                        case REQ_CODE_OPTION_DELETE:
                                            BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                            feedActivityList.remove(actPosition);
                                            adapterFeedMain.notifyItemRemoved(actPosition);
                                            adapterFeedMain.notifyItemRangeChanged(actPosition, feedActivityList.size());
                                            Util.showSnackbar(v, res.getResult());
                                            break;

                                        case REQ_CODE_OPTION_SAVE:
                                            updateOptionText(actPosition, position, "unsave", Constant.TXT_UNSAVE_FEED);
                                            break;
                                        case REQ_CODE_OPTION_UNSAVE:
                                            updateOptionText(actPosition, position, "save", Constant.TXT_SAVE_FEED);
                                            break;
                                        case REQ_CODE_OPTION_COMMENT_DISABLE:
                                            updateOptionText(actPosition, position, "enable_comment", Constant.TXT_ENABLE_COMMENT);
                                            break;

                                        case REQ_CODE_OPTION_COMMENT_ENABLE:
                                            updateOptionText(actPosition, position, "disable_comment", Constant.TXT_DISABLE_COMMENT);
                                            break;
                                        case REQ_CODE_OPTION_HIDE_FEED:
                                            feedActivityList.get(actPosition).setHidden(true);
                                            feedActivityList.get(actPosition).setReported(false);
                                            adapterFeedMain.notifyItemChanged(actPosition);
                                            break;
                                        case REQ_CODE_OPTION_UNDO:
                                            feedActivityList.get(actPosition).setHidden(false);
                                            feedActivityList.get(actPosition).setReported(false);
                                            adapterFeedMain.notifyItemChanged(actPosition);
                                            break;
                                        case REQ_CODE_OPTION_REPORT:
                                            feedActivityList.get(actPosition).setHidden(true);
                                            feedActivityList.get(actPosition).setReported(true);
                                            adapterFeedMain.notifyItemChanged(actPosition);
                                            break;
                                    }
                                } else {
                                    Util.showSnackbar(v, resp.getErrorMessage());
                                }
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        // dialog.dismiss();
                        return true;
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);
                } catch (Exception e) {
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                    hideBaseLoader();
                }

            } else {
                isLoading = false;
                pb.setVisibility(View.GONE);
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    private void updateOptionText(int actPosition, int position, String name, String value) {
        CustomLog.e("values", actPosition + "___" + position + "___");
        feedActivityList.get(actPosition).getOptions().get(position).setValue(value);
        feedActivityList.get(actPosition).getOptions().get(position).setName(name);
        adapterFeedMain.notifyItemChanged(actPosition);
    }

    @Override
    public void onLoadMore() {
        try {
            CustomLog.e("feedActivityload", "load");
            if (!isLoading && !feedResponse.getResult().getEndOfFeed()) {
                pb.setVisibility(View.GONE);
                callFeedApi(REQ_CODE_LOADING_MORE_FEED);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        callFeedApi(REQ_CODE_REFRESH_DATA);
        updateNotificationCount(0);
    }

    public void scrollToPosition(int index) {
        recycleViewFeedMain.smoothScrollToPosition(index);
    }

    public void scrollToStart() {
        if (null != feedActivityList && feedActivityList.size() > 0) {
            View visibleChild = recycleViewFeedMain.getChildAt(0);
            int positionOfChild = recycleViewFeedMain.getChildAdapterPosition(visibleChild);
            if (positionOfChild > 0) {
                recycleViewFeedMain.smoothScrollToPosition(0);
            } else {
                onRefresh();
            }
        }
    }

    public void updateComposerProfileImage(String url) {
        try {
            //TODO
            adapterFeedMain.setComposerProfileImage(url);
            adapterFeedMain.notifyItemChanged(0);
            //  Util.showImageWithGlide(ivProfileCompose, url, context, R.drawable.placeholder_3_2);
        } catch (Exception e) {
            //IGNORE THIS ERROR
        }
    }

    public void updateFeedItem(int itemPosition) {
        try {
            adapterFeedMain.canPlayFirstVideo(false);
            adapterFeedMain.notifyItemChanged(itemPosition);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    void callApiWeather() {

        if (isNetworkAvailable(context)) {

            try {

                HttpRequestVO request = new HttpRequestVO("http://integrate.matani.id/home-cuaca.php");


                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;


                            if (response != null) {


                                WeatherResponse weatherResponse = new Gson().fromJson(response, WeatherResponse.class);

                                if (weatherResponse.getError().getMessage() == null) {
                                    adapterWeather.submitList(weatherResponse.getCuaca());
                                    CustomLog.d("masuksini", "cuaca");
                                } else {
                                    rvWeather.setVisibility(View.GONE);
                                    CustomLog.d("masuksini", "cuaca2");
                                }

                            } else {
                                somethingWrongMsg(v);
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
                CustomLog.e(e);
            }
        } else {
            hideBaseLoader();
            notInternetMsg(v);
        }
    }

    void callApiPrice() {

        if (isNetworkAvailable(context)) {

            try {

                HttpRequestVO request = new HttpRequestVO("http://integrate.matani.id/home-harga.php");


                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideBaseLoader();
                        try {
                            hideBaseLoader();
                            String response = (String) msg.obj;

                            PriceResponse priceItemModel = new Gson().fromJson(response, PriceResponse.class);
                            if (response != null) {

                                if (priceItemModel.getError().getMessage() == null) {
                                    // adapterPrice.submitList(priceItemModel.getHarga());

                                    rvPrice.setVisibility(View.VISIBLE);
                                } else {
                                    rvPrice.setVisibility(View.GONE);
                                    CustomLog.d("masuksini", "harga2");
                                }


                            } else {
                                somethingWrongMsg(v);
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
                CustomLog.e(e);
            }
        } else {
            hideBaseLoader();
            notInternetMsg(v);
        }
    }
}

package com.sesolutions.ui.courses.lecture;


import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.transition.Transition;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.http.VideoDownloadController;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.videos.ResultView;
import com.sesolutions.responses.videos.VideoView;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.responses.videos.ViewVideo;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.courses.adapters.LectureViewAdapter;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.customviews.InsideWebViewClient;
import com.sesolutions.ui.customviews.VideoEnabledWebChromeClient;
import com.sesolutions.ui.customviews.VideoEnabledWebView;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.ui.music_album.AddToPlaylistFragment;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sesolutions.utils.URL.URL_LECTURE_DELETE;

public class ViewLectureFragment extends CommentLikeHelper implements View.OnClickListener, OnLoadMoreListener, PopupMenu.OnMenuItemClickListener {

    public static final int UPDATE_UPPER_LAYOUT = 101;
    public static final int REQ_LIKE = 102;
    public static final int REQ_FAVORITE = 103;
    public static final int REQ_RESELECTED = 104;
    public RecyclerView recyclerView;
    public boolean isLoading;
    public int REQ_LOAD_MORE = 2;
    public ResultView result;
    public ProgressBar pb;
    public ViewVideo videoVo;

    public List<Videos> videoList;
    public LectureViewAdapter adapter;

    public ImageView ivPipMode;
    public ImageView ivUserImage;
    public TextView tvAlbumTitle;
    public TextView tvUserTitle;
    public TextView tvAlbumDate;
    //public TextView tvAlbumDetail;


    public int videoId;
    public NestedScrollView mScrollView;
    public VideoEnabledWebView webView;
    public VideoEnabledWebChromeClient webChromeClient;
    public boolean isLoggedIn;
    public ImageView ivDesc;
    public TextView tvDesc;
    public String rcType;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_lecture_view, container, false);
        applyTheme(v);
        cText2 = Color.parseColor(Constant.text_color_2);
        init();
        setRecyclerView();
        callMusicAlbumApi(1);
        callBottomCommentLikeApi(videoId, Constant.ResourceType.LECTURE, Constant.URL_VIEW_COMMENT_LIKE);
        return v;
    }

    public void hideShowAddTo(boolean canShow) {
        v.findViewById(R.id.llAddTo).setVisibility(canShow ? View.GONE : View.GONE);
        v.findViewById(R.id.ivShare).setVisibility(canShow ? View.GONE : View.GONE);
        v.findViewById(R.id.llAddTo).setVisibility(canShow ? View.GONE : View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            activity.setStatusBarColor(Color.BLACK);
            if (activity.taskPerformed == Constant.FormType.KEY_EDIT_VIDEO) {
                activity.taskPerformed = 0;
                callMusicAlbumApi(1);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onStop() {
        activity.setStatusBarColor(Util.manipulateColor(Color.parseColor(Constant.colorPrimary)));
        super.onStop();
    }

    private void setCookie() {
        try {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.setCookie(Constant.BASE_URL, getCookie(), value -> {
                    String cookie = cookieManager.getCookie(Constant.BASE_URL);
                    CookieManager.getInstance().flush();
                    CustomLog.d("cookie", "cookie ------>" + cookie);
                    setupWebView();
                });
            } else {
                cookieManager.setCookie(Constant.BASE_URL, getCookie());
                new Handler().postDelayed(this::setupWebView, 700);
                CookieSyncManager.getInstance().sync();
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void init() {

        try {
            iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            webView = v.findViewById(R.id.wbVideo);
            //  ((TextView) v.findViewById(R.id.tvTitle)).setText("");
            recyclerView = v.findViewById(R.id.recyclerview);
            ivPipMode = v.findViewById(R.id.ivPipMode);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ivPipMode.setVisibility(View.VISIBLE);
                ivPipMode.setOnClickListener(v -> getActivity().enterPictureInPictureMode());
            }
            ivUserImage = v.findViewById(R.id.ivUserImage);
            tvAlbumTitle = v.findViewById(R.id.tvAlbumTitle);
            setCookie();
            cGrey = Color.parseColor(Constant.text_color_2);
            cPrimary = Color.parseColor(Constant.colorPrimary);

            tvUserTitle = v.findViewById(R.id.tvUserTitle);
            tvUserTitle.setOnClickListener(this);
            ivUserImage.setOnClickListener(this);
            tvAlbumDate = v.findViewById(R.id.tvAlbumDate);
            ivDesc = v.findViewById(R.id.ivDesc);
            tvDesc = v.findViewById(R.id.tvDesc);

            ivDesc.setColorFilter(Color.parseColor(Constant.text_color_2));
            pb = v.findViewById(R.id.pb);
            v.findViewById(R.id.cvDetail).setOnClickListener(this);

            v.findViewById(R.id.ivBack).setOnClickListener(this);
//            v.findViewById(R.id.ivShare).setOnClickListener(this);
            v.findViewById(R.id.ivOption).setOnClickListener(this);
            v.findViewById(R.id.ivOption).setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);

            v.findViewById(R.id.ivStar1).setOnClickListener(this);
            v.findViewById(R.id.ivStar2).setOnClickListener(this);
            v.findViewById(R.id.ivStar3).setOnClickListener(this);
            v.findViewById(R.id.ivStar4).setOnClickListener(this);
            v.findViewById(R.id.ivStar5).setOnClickListener(this);

            v.findViewById(R.id.llLike).setOnClickListener(this);
            v.findViewById(R.id.llComment).setOnClickListener(this);
            v.findViewById(R.id.llFavorite).setOnClickListener(this);
            v.findViewById(R.id.llLater).setOnClickListener(this);
            v.findViewById(R.id.llAddTo).setOnClickListener(this);
            v.findViewById(R.id.llLike).setVisibility(View.GONE);
            v.findViewById(R.id.llComment).setVisibility(View.GONE);
            v.findViewById(R.id.llFavorite).setVisibility(View.GONE);
            v.findViewById(R.id.llLater).setVisibility(View.GONE);
            v.findViewById(R.id.llAddTo).setVisibility(View.GONE);


            hideShowAddToButton(rcType);

            mScrollView = v.findViewById(R.id.mScrollView);

            mScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
                View view = mScrollView.getChildAt(mScrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView.getScrollY()));

                if (diff == 0) {
                    // your pagination code
                    loadMore();
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void hideShowAddToButton(String rcType) {
        if (null == rcType) return;
        switch (rcType) {
            case Constant.ResourceType.SES_EVENT_VIDEO:
            case Constant.ResourceType.BUSINESS_VIDEO:
            case Constant.ResourceType.GROUP_VIDEO:
            case Constant.ResourceType.PAGE_VIDEO:
                hideShowAddTo(false);
                break;
            default:
                boolean isCorePlugin = ModuleUtil.getInstance().isCorePlugin(context, rcType);
                hideShowAddTo(!isCorePlugin);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (!webChromeClient.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public void setupWebView() {
        try {
            // Save the web view
            webView = v.findViewById(R.id.wbVideo);
            webView.setDownloadListener(new VideoDownloadController(context));
            // Initialize the VideoEnabledWebChromeClient and set event handlers
            View nonVideoLayout = v.findViewById(R.id.rlNonVideo); // Your own view, read class comments
            ViewGroup videoLayout = v.findViewById(R.id.videoLayout); // Your own view, read class comments
            //noinspection all
            View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
            webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
            {
                // Subscribe to standard events, such as onProgressChanged()...
                @Override
                public void onProgressChanged(WebView view, int progress) {
                    // Your code...
                }
            };
            webChromeClient.setOnToggledFullscreen(fullscreen -> {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    activity.getWindow().setAttributes(attrs);
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                } else {
                    WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    activity.getWindow().setAttributes(attrs);
                    //noinspection all
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            });
            webView.setWebChromeClient(webChromeClient);
            webView.setBackgroundColor(Color.BLACK);
            webView.setWebViewClient(new InsideWebViewClient());


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void playVideo(String url) {
        if ("external".equals(videoVo.getType())) {
            webView.loadUrl(url);
            return;
        }
        if (ModuleUtil.getInstance().isCorePlugin(context, rcType)) {
            webView.loadUrl(url);
            return;
        }
        if ("iframely".equals(videoVo.getType())) {
            switch (rcType) {
                case Constant.ResourceType.PAGE_VIDEO:
                    url = Constant.URL_PAGE_VIDEO_PLAY_URL;
                    break;
                case Constant.ResourceType.GROUP_VIDEO:
                    url = Constant.URL_GROUP_VIDEO_PLAY_URL;
                    break;
                case Constant.ResourceType.BUSINESS_VIDEO:
                    url = Constant.URL_BUSINESS_VIDEO_PLAY_URL;
                    break;

            }
            webView.loadUrl(url + videoVo.getLectureId() + "?restApi=Sesapi");
        } else {
            webView.loadUrl(url);
        }
    }

    public void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new LectureViewAdapter(videoList, context, this, this, Constant.FormType.TYPE_SONGS);
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void updateUpperLayout() {
        try {
            setRatingStars(-1);
            if (TextUtils.isEmpty(videoVo.getDescription())) {
                ivDesc.setVisibility(View.GONE);
            } else {
                tvDesc.setText(videoVo.getDescription());
            }
            ((TextView) v.findViewById(R.id.tvViewCount)).setText(videoVo.getViewCount() + (videoVo.getViewCount() == 1 ? " View" : " Views"));
            tvAlbumTitle.setText(videoVo.getTitle());
            Util.showImageWithGlide(ivUserImage, videoVo.getOwner().getImages(), context, R.drawable.placeholder_square);
            //  Util.showImageWithGlide(ivCoverPhoto, videoVo.getCover().getMain(), context, R.drawable.placeholder_square);

            tvUserTitle.setText("Instructor : " + videoVo.getOwner().getTitle());
            tvAlbumDate.setText(Util.changeDateFormat(context, videoVo.getCreationDate()));
            ((TextView) v.findViewById(R.id.tvLater)).setTextColor(videoVo.getHasWatchlater() ? cPrimary : cText1);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void setRatingStars(int newRating) {
        if (-1 != newRating) {
            if (videoVo.getRating().getCode() != 100) {
                Util.showSnackbar(v, videoVo.getRating().getMessage());
            } else {
                videoVo.getRating().setTotalRatingAverage(newRating);
            }
        }
        if (videoVo.canShowRating()) {
            v.findViewById(R.id.llStar).setVisibility(View.VISIBLE);
            Drawable dFilledStar = ContextCompat.getDrawable(context, R.drawable.star_filled);
            float rating = videoVo.getRating().getTotalRatingAverage();
            if (rating > 0) {
                ((ImageView) v.findViewById(R.id.ivStar1)).setImageDrawable(dFilledStar);
                if (rating > 1) {
                    ((ImageView) v.findViewById(R.id.ivStar2)).setImageDrawable(dFilledStar);
                    if (rating > 2) {
                        ((ImageView) v.findViewById(R.id.ivStar3)).setImageDrawable(dFilledStar);
                        if (rating > 3) {
                            ((ImageView) v.findViewById(R.id.ivStar4)).setImageDrawable(dFilledStar);
                            if (rating > 4) {
                                ((ImageView) v.findViewById(R.id.ivStar5)).setImageDrawable(dFilledStar);
                            }
                        }
                    }
                }
            }
        } else {
            v.findViewById(R.id.llStar).setVisibility(View.GONE);
        }
    }


    //TODO Uncomment this code if favorite is not resoleved


    @Override
    public void onPermissionError() {
        v.findViewById(R.id.llReaction).setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        try {

            if (webView != null) {
                webView.onPause();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            //CustomLog.e("onResume", "onResume");
            v.findViewById(R.id.rl1).setVisibility(View.VISIBLE);
            if (webView != null) {
                webView.onResume();
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void unfilledAllStar() {
        Drawable dUnfilledStar = ContextCompat.getDrawable(context, R.drawable.star_unfilled);
        ((ImageView) v.findViewById(R.id.ivStar1)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar2)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar3)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar4)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar5)).setImageDrawable(dUnfilledStar);
    }

    public void showHideOptionIcon() {
        try {
            v.findViewById(R.id.ivOption).setVisibility((result.getLecture().getMenus() != null && result.getLecture().getMenus().size() > 0) ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateAdapter() {
        showHideOptionIcon();
        try {
            askForPermission(permissionlistener, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            isLoading = false;
            pb.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            runLayoutAnimation(recyclerView);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(R.string.EMPTY);
            v.findViewById(R.id.tvNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

    @Override
    public void updateFavorite() {

        try {
            if (stats.getIsFavourite()) {
                stats.decreamentFavourite();
            } else {
                ((TextView) v.findViewById(R.id.tvFavorite)).setTextColor(cPrimary);
                stats.increamentFavourite();
            }
            //toggle favourite
            stats.setIsFavourite(!stats.getIsFavourite());

            ((TextView) v.findViewById(R.id.tvFavorite)).setTextColor(stats.getIsFavourite() ? cPrimary : cText1);
            ((TextView) v.findViewById(R.id.tvFavorite)).setText(stats.getFavouriteCount() + " " + (stats.getFavouriteCount() == 1 ? getStrings(R.string.TXT_FAVORITE) : getStrings(R.string.TXT_FAVORITES)));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        try {
            switch (view.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
                case R.id.ivShare:
                    showShareDialog(videoVo.getShare());
                    break;
                case R.id.cvDetail:
                    Transition transition;
                    if (tvDesc.getVisibility() != View.VISIBLE) {
                        tvDesc.setVisibility(View.VISIBLE);
                        tvAlbumDate.setVisibility(View.VISIBLE);
                        ivDesc.setRotation(270);
                        /*  } */
                    } else {
                        tvDesc.setVisibility(View.GONE);
                        tvAlbumDate.setVisibility(View.GONE);
                        ivDesc.setRotation(90);
                    }
                    break;

                case R.id.ivOption:
                    showPopup(result.getLecture().getMenus(), view, 10, this);
                    break;

                case R.id.llLater:
                    if (isNetworkAvailable(context)) {
                        videoVo.toggleWatchLater();
                        ((TextView) v.findViewById(R.id.tvLater)).setTextColor(videoVo.getHasWatchlater() ? cText1 : cPrimary);
                        String url = Constant.URL_VIDEO_WATCH_LATER;
                        switch (rcType) {
                            case Constant.ResourceType.SES_EVENT_VIDEO:
                                url = Constant.URL_EVENT_VIDEO_WATCH_LATER;
                                break;
                            case Constant.ResourceType.BUSINESS_VIDEO:
                                url = Constant.URL_BUSINESS_VIDEO_WATCH_LATER;
                                break;
                            case Constant.ResourceType.GROUP_VIDEO:
                                url = Constant.URL_GROUP_VIDEO_WATCH_LATER;
                                break;
                            case Constant.ResourceType.PAGE_VIDEO:
                                url = Constant.URL_PAGE_VIDEO_WATCH_LATER;
                                break;
                        }
                        callLaterApi(videoVo.getLectureId(), url, videoVo.getHasWatchlater());
                    } else {
                        notInternetMsg(v);
                    }
                    break;
                case R.id.tvUserTitle:
                case R.id.ivUserImage:
                    goTo(Constant.GoTo.PROFILE, Constant.KEY_ID, videoVo.getOwnerId());
                    break;

                case R.id.llAddTo:
                    goToAddToPlaylistFragment();
                    break;
                case R.id.ivStar1:
                    if (isLoggedIn()) {
                        callRatingApi(1);
                        unfilledAllStar();
                        setRatingStars(1);
                    }
                    break;
                case R.id.ivStar2:
                    if (isLoggedIn()) {
                        callRatingApi(2);
                        unfilledAllStar();
                        setRatingStars(2);

                    }
                    break;
                case R.id.ivStar3:
                    if (isLoggedIn()) {
                        callRatingApi(3);
                        unfilledAllStar();
                        setRatingStars(3);

                    }
                    break;
                case R.id.ivStar4:
                    if (isLoggedIn()) {
                        callRatingApi(4);
                        unfilledAllStar();
                        setRatingStars(4);

                    }
                    break;
                case R.id.ivStar5:
                    if (isLoggedIn()) {
                        callRatingApi(5);
                        unfilledAllStar();
                        setRatingStars(5);
                    }
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private boolean isLoggedIn() {
        if (!isLoggedIn) {
            isLoggedIn = SPref.getInstance().isLoggedIn(context);
        }
        return isLoggedIn;
    }


    /*private void showPopup(List<Options> menus, View v, int idPrefix) {
        PopupMenu menu = new PopupMenu(context, v);
        for (int index = 0; index < menus.size(); index++) {
            Options s = menus.get(index);
            menu.getMenu().add(1, idPrefix + index + 1, index + 1, s.getLabel());
        }
        menu.show();
        menu.setOnMenuItemClickListener(this);
    }*/


    private void callRatingApi(int rating) {
        if (videoVo.getRating().getCode() != 100) {
            //Util.showSnackbar(v, videoVo.getRating().getMessage());
            return;
        }

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    String type = Constant.ResourceType.VIDEO;
                    String url = Constant.URL_RATE_VIDEO;

                    //resource_type and url are different for different plugin ,so change as per screen
                    if (Constant.ResourceType.SES_EVENT_VIDEO.equals(rcType)) {
                        type = rcType;
                        url = Constant.URL_EVENT_VIDEO_RATE;
                    } else if (Constant.ResourceType.PAGE_VIDEO.equals(rcType)) {
                        type = rcType;
                        url = Constant.URL_PAGE_VIDEO_RATE;
                    } else if (Constant.ResourceType.GROUP_VIDEO.equals(rcType)) {
                        type = rcType;
                        url = Constant.URL_GROUP_VIDEO_RATE;
                    } else if (Constant.ResourceType.BUSINESS_VIDEO.equals(rcType)) {
                        type = rcType;
                        url = Constant.URL_BUSINESS_VIDEO_RATE;
                    }
                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_RATING, rating);
                    request.params.put(Constant.KEY_RESOURCE_ID, videoVo.getLectureId());
                    request.params.put(Constant.KEY_VIDEO_ID, videoVo.getLectureId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, type);
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

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        JSONObject res = new JSONObject(response);
                                        if (res.get("result") instanceof JSONObject) {
                                            String result = res.getJSONObject("result").getString("message");
                                            Util.showSnackbar(v, result);
                                        } else {
                                            Util.showSnackbar(v, res.getString("result"));
                                        }
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

    private void callRemoveImageApi(String url) {
        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_RESOURCE_ID, videoVo.getLectureId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.ResourceType.VIDEO);
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

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                        callMusicAlbumApi(UPDATE_UPPER_LAYOUT);
                                        Util.showSnackbar(v, res.getResult());
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

    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else if (req != UPDATE_UPPER_LAYOUT) {
                        showBaseLoader(false);
                    }

                    String url = Constant.URL_LECTURE_VIEW;
                    String type = Constant.ResourceType.VIDEO;
                    if (!TextUtils.isEmpty(rcType)) {
                        switch (rcType) {
                            case Constant.ResourceType.SES_EVENT_VIDEO:
                                url = Constant.URL_EVENT_VIDEO_VIEW;
                                type = rcType;
                                break;
                            case Constant.ResourceType.PAGE_VIDEO:
                                url = Constant.URL_PAGE_VIDEO_VIEW;
                                type = rcType;
                                break;
                            case Constant.ResourceType.BUSINESS_VIDEO:
                                url = Constant.URL_BUSINESS_VIDEO_VIEW;
                                type = rcType;
                                break;
                            case Constant.ResourceType.GROUP_VIDEO:
                                url = Constant.URL_GROUP_VIDEO_VIEW;
                                type = rcType;
                                break;
                        }
                    }
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                    request.params.put(Constant.KEY_LECTURE_ID, videoId);

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

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        showView(mScrollView);
                                        VideoView resp = new Gson().fromJson(response, VideoView.class);
                                        if (req != REQ_LOAD_MORE) {
                                            videoList.clear();
                                        }
                                        result = resp.getResult();
                                        if (req == UPDATE_UPPER_LAYOUT) {
                                            videoVo = result.getLecture();
                                            updateUpperLayout();
                                        } else {
                                            videoVo = result.getLecture();
                                            if (videoVo.getIframeurl() != null) {
                                                playVideo(videoVo.getIframeurl());
                                                updateUpperLayout();
                                                updateAdapter();
                                            } else {
                                                Util.showSnackbar(v, videoVo.getStatusMessage());
                                                onBackPressed();
                                            }
                                        }
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


    public static ViewLectureFragment newInstance(int albumId, String rcType) {
        ViewLectureFragment frag = new ViewLectureFragment();
        frag.videoId = albumId;
        frag.rcType = rcType;
        return frag;
    }

    public static ViewLectureFragment newInstance(int albumId) {
        ViewLectureFragment frag = new ViewLectureFragment();
        frag.videoId = albumId;
        return frag;
    }

    @Override
    public void onLoadMore() {
    }

    public void loadMore() {
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try {
            int itemId = item.getItemId();
            Options opt = null;

            itemId = itemId - 10;
            opt = result.getLecture().getMenus().get(itemId - 1);


            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    goToFormFragment();
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog();
                    break;
                case Constant.OptionType.REPORT:
                    goToReportFragment();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private void goToFormFragment() {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_LECTURE_ID, videoVo.getLectureId());
        String url = Constant.URL_LECTURE_EDIT;

        fragmentManager.beginTransaction().replace(R.id.container,
                FormFragment.newInstance(Constant.FormType.KEY_EDIT_LECTURE, map, url))
                .addToBackStack(null)
                .commit();

    }

    private void goToAddToPlaylistFragment() {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_VIDEO_ID, videoVo.getLectureId());
        map.put(Constant.KEY_MODULE, Constant.VALUE_MODULE_VIDEO);
        fragmentManager.beginTransaction().replace(R.id.container,
                AddToPlaylistFragment.newInstance(Constant.FormType.ADD_VIDEO, map,
                        Constant.URL_CREATE_VIDEO_PLAYLIST))
                .addToBackStack(null).commit();

    }

    private void goToReportFragment() {
//        String module = Constant.ResourceType.VIDEO;
//        switch (rcType) {
//            case Constant.ResourceType.SES_EVENT_VIDEO:
//                module = Constant.ResourceType.SES_EVENT_VIDEO;
//                break;
//            case Constant.ResourceType.PAGE_VIDEO:
//                module = Constant.ResourceType.PAGE_VIDEO;
//                break;
//            case Constant.ResourceType.GROUP_VIDEO:
//                module = Constant.ResourceType.GROUP_VIDEO;
//                break;
//            case Constant.ResourceType.BUSINESS_VIDEO:
//                module = Constant.ResourceType.BUSINESS_VIDEO;
//                break;
//        }
        String guid = "courses_lecture" + "_" + videoVo.getLectureId();
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid)).addToBackStack(null).commit();
    }

    public void showDeleteDialog() {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION_VIDEO);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                callDeleteApi();

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callDeleteApi() {

        try {
            if (isNetworkAvailable(context)) {

                showBaseLoader(false);
                HttpRequestVO request = new HttpRequestVO(URL_LECTURE_DELETE);
//              request.params.put(key, value);
                request.params.put(Constant.KEY_LECTURE_ID, videoId);
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;


                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    JSONObject json = new JSONObject(response);
                                    activity.taskPerformed = Constant.TASK_ALBUM_DELETED;
                                    onBackPressed();
                                    String message = json.getJSONObject(Constant.KEY_RESULT).getString("success_message");
                                    Util.showSnackbar(v, message);
                                    //VideoBrowse resp = new Gson().fromJson(response, VideoBrowse.class);
                                    // updateAdapter();
                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
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


            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {

            CustomLog.e(e);
            hideBaseLoader();
        }
    }

    public void showImageRemoveDialog(boolean isCover, String msg, final String url) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            ((TextView) progressDialog.findViewById(R.id.tvDialogText)).setText(msg);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(isCover ? R.string.TXT_REMOVE_COVER : R.string.TXT_REMOVE_PHOTO);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callRemoveImageApi(url);
                    //callSaveFeedApi(REQ_CODE_OPTION_DELETE, Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);

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

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        try {
            if (object1 == Constant.Events.MUSIC_MAIN) {
                videoId = videoList.get(postion).getlecture_id();
                callMusicAlbumApi(REQ_RESELECTED);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onItemClicked(object1, object2, postion);
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {

        if (isInPictureInPictureMode) {
            v.findViewById(R.id.rl1).setVisibility(View.GONE);
            v.findViewById(R.id.ivPipMode).setVisibility(View.GONE);
            v.findViewById(R.id.mScrollView).setVisibility(View.GONE);
            webView.onResume();
        } else {
            v.findViewById(R.id.rl1).setVisibility(View.VISIBLE);
            v.findViewById(R.id.mScrollView).setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.findViewById(R.id.ivPipMode).setVisibility(View.VISIBLE);
            }
        }
    }
}
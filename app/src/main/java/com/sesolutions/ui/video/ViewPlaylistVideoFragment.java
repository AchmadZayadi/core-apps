package com.sesolutions.ui.video;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.http.VideoDownloadController;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.videos.ResultView;
import com.sesolutions.responses.videos.VideoView;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.responses.videos.ViewVideo;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.customviews.InsideWebViewClient;
import com.sesolutions.ui.customviews.VideoEnabledWebChromeClient;
import com.sesolutions.ui.customviews.VideoEnabledWebView;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewPlaylistVideoFragment extends CommentLikeHelper implements View.OnClickListener, OnLoadMoreListener {

    private static final String TAG = "ViewPlaylistVideo";

    private static final int REQ_LIKE = 102;
    private static final int REQ_FAVORITE = 103;
    private static final int REQ_DELETE_VIDEO = 104;
    private static final int UPDATE_UPPER_LAYOUT = 101;

    private ProgressBar pb;
    private boolean isLoading;
    private ResultView result;
    private ViewVideo videoVo;
    private int REQ_LOAD_MORE = 2;
    private RecyclerView recyclerView;

    public List<Videos> videoList;
    public VideoViewAdapter adapter;

    // public View v;
    // public List<Albums> videoList;
    // public AlbumAdapter adapter;

    public ImageView ivUserImage;
    public TextView tvAlbumTitle;
    public TextView tvVideoDetail;
    public TextView tvUserTitle;
    public TextView tvAlbumDate;

    private String url;
    private int playlistId;
    private Typeface iconFont;
    private VideoEnabledWebView webView;
    private NestedScrollView mScrollView;
    private VideoEnabledWebChromeClient webChromeClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_video_playlist_view, container, false);
        applyTheme(v);
        init();
        setRecyclerView();
        callMusicAlbumApi(1);

        return v;
    }

    @Override
    public void onPause() {

        try {
            CustomLog.e("onPause", "onPause");
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
            CustomLog.e("onResume", "onResume");
            if (webView != null) {
                webView.onResume();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void init() {


        try {
            iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            //   webView = v.findViewById(R.id.wbVideo);
            recyclerView = v.findViewById(R.id.recyclerview);
            ivUserImage = v.findViewById(R.id.ivUserImage);
            tvAlbumTitle = v.findViewById(R.id.tvAlbumTitle);
            tvVideoDetail = v.findViewById(R.id.tvVideoDetail);
            tvVideoDetail.setTypeface(iconFont);
            setupWebView();
            tvUserTitle = v.findViewById(R.id.tvUserTitle);
            tvAlbumDate = v.findViewById(R.id.tvAlbumDate);

            pb = v.findViewById(R.id.pb);
            v.findViewById(R.id.ivBack).setOnClickListener(this);

            mScrollView = v.findViewById(R.id.mScrollView);

            mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    View view = mScrollView.getChildAt(mScrollView.getChildCount() - 1);

                    int diff = (view.getBottom() - (mScrollView.getHeight() + mScrollView
                            .getScrollY()));

                    if (diff == 0) {
                        // your pagination code
                        loadMore();
                    }
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setupWebView() {
        try {
            // Save the web view
            webView = v.findViewById(R.id.wbVideo);
            webView.setDownloadListener(new VideoDownloadController(context));
            webView.setBackgroundColor(Color.BLACK);
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
            webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
                @Override
                public void toggledFullscreen(boolean fullscreen) {
                    // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                    if (fullscreen) {
                        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
                        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                        attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                        activity.getWindow().setAttributes(attrs);
                        //noinspection all
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    } else {
                        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
                        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                        attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                        activity.getWindow().setAttributes(attrs);
                        //noinspection all
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            });
            webView.setWebChromeClient(webChromeClient);
            // Call private class InsideWebViewClient
            webView.setWebViewClient(new InsideWebViewClient());

            // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site
            //  webView.loadUrl("http://m.youtube.com");

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void playVideo(String url) {
        webView.loadUrl(url);
    }

    private void setRecyclerView() {
        try {
            videoList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new VideoViewAdapter(videoList, context, this, this, Constant.FormType.TYPE_SONGS);
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateUpperLayout() {
        try {

            tvAlbumTitle.setText(videoVo.getTitle());
            //Util.showImageWithGlide123(ivUserImage, videoVo.getUserImage(), context, R.drawable.placeholder_square);
            Util.showImageWithGlide123(ivUserImage, ""+result.getPlaylist().getImages().getMain(),  R.drawable.dummy_profile);
            Log.e("Video taa",""+result.getPlaylist().getImages().getMain());
            tvUserTitle.setText(videoVo.getUserTitle());
            tvAlbumDate.setText(Util.changeDateFormat(context, videoVo.getCreationDate()));
            tvVideoDetail.setText(getVideoDetail(videoVo, false));
        } catch (Exception e) {
            CustomLog.e(e);
        }


    }

    /* public void showHideOptionIcon() {
         try {
             v.findViewById(R.id.option).setVisibility((result.getMenus() != null && result.getMenus().size() > 0) ? View.VISIBLE : View.GONE);
         } catch (Exception e) {
             CustomLog.e(e);asda
         }
     }
 */
    private void updateAdapter() {
        //  showHideOptionIcon();
        try {
            askForPermission();
            isLoading = false;
            pb.setVisibility(View.GONE);
            adapter.setOwner(videoVo.getOwnerId() == result.getLoggedinUserId());
            //  swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();
            runLayoutAnimation(recyclerView);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_SONG_ALBUM);
            v.findViewById(R.id.tvNoData).setVisibility(videoList.size() > 0 ? View.GONE : View.VISIBLE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void askForPermission() {
        try {
            new TedPermission(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(Constant.MSG_PERMISSION_DENIED)
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // Toast.makeText(How_It_Works_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            try {

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };


    private void showShareDialog(String msg) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_three);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);
            AppCompatButton bShareIn = progressDialog.findViewById(R.id.bCamera);
            AppCompatButton bShareOut = progressDialog.findViewById(R.id.bGallary);
            bShareOut.setText(Constant.TXT_SHARE_OUTSIDE);
            bShareIn.setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);
            bShareIn.setText(Constant.TXT_SHARE_INSIDE + AppConfiguration.SHARE);
            bShareIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    shareInside(videoVo.getShare(), true);
                }
            });

            bShareOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    shareOutside(videoVo.getShare());
                }
            });

            progressDialog.findViewById(R.id.bLink).setVisibility(View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View view) {
        super.onClick(view);
        try {
            switch (view.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivShare:
                    showShareDialog(Constant.TXT_SHARE_FEED);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.setStatusBarColor(Color.BLACK);
    }

    @Override
    public void onStop() {
        super.onStop();
        activity.setStatusBarColor(Util.manipulateColor(Color.parseColor(Constant.colorPrimary)));
    }

    private void callRemoveImageApi(final int position, String url) {
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                showBaseLoader(true);
                try {
                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_VIDEO_ID, videoList.get(position).getVideoId());
                    request.params.put(Constant.KEY_RESOURCE_ID, playlistId);
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
                                        videoList.remove(position);
                                        adapter.notifyItemRemoved(position);

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

    private void callMusicAlbumApi(final int req) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else if (req != UPDATE_UPPER_LAYOUT) {
                        showBaseLoader(true);
                    }
                    if (null == url) {
                        url = Constant.URL_VIDEO_VIEW_PLAYLIST;
                        resourceType = Constant.ResourceType.VIDEO;
                    } else {
                        resourceType = Constant.ResourceType.VIDEO_CHANNEL;
                    }
                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);

                    request.params.put(Constant.KEY_PLAYLIST_ID, playlistId);
                    request.params.put(Constant.KEY_RESOURCE_ID, playlistId);
                    request.params.put(Constant.KEY_RESOURCES_TYPE, resourceType);

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
                                        result = resp.getResult();
                                        if (req == UPDATE_UPPER_LAYOUT) {
                                            videoVo = result.getPlaylist();
                                            updateUpperLayout();
                                        } else {
                                            if (null != result.getVideos() && result.getVideos().size() > 0) {
                                                videoList.addAll(result.getVideos());
                                                if (req != REQ_LOAD_MORE) {
                                                    playVideo(videoList.get(0).getIframeURL());
                                                }
                                            }
                                            if (null != result.getPlaylist())
                                                videoVo = result.getPlaylist();

                                            updateUpperLayout();
                                            updateAdapter();
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


    public static ViewPlaylistVideoFragment newInstance(int albumId, String url, ViewVideo videoVo) {
        ViewPlaylistVideoFragment frag = new ViewPlaylistVideoFragment();
        frag.playlistId = albumId;
        frag.url = url;
        frag.videoVo = videoVo;
        return frag;
    }

    @Override
    public void onLoadMore() {

    }

    public void loadMore() {
        /*try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }*/
    }

    /*public void showDeleteDialog() {
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
            TextView tvMsg = (TextView) progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
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
*/
    public void showImageRemoveDialog(String msg, final int position, final String url) {
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
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callRemoveImageApi(position, url);
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
    public void onBackPressed() {
        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
        if (!webChromeClient.onBackPressed()) {
            /*if (webView.canGoBack())
            {
                webView.goBack();
            }
            else
            {*/
            // Standard back button implementation (for example this could close the app)
            super.onBackPressed();
            // }
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        if (object1 == Constant.Events.MUSIC_MAIN) {
            playVideo(videoList.get(postion).getIframeURL());
        } else if (object1 == Constant.Events.DELETE_PLAYLIST) {
            String deleteUrl = null != result.getPlaylist() ? Constant.URL_DELETE_PLAYLIST_VIDEO : Constant.URL_DELETE_CHANNEL_VIDEO;
            showImageRemoveDialog(Constant.MSG_DELETE_CONFIRMATION_VIDEO, postion, deleteUrl);
        }
        return false;
    }

}

package com.sesolutions.ui.quotes;


import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.http.VideoDownloadController;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.quote.Quote;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.customviews.InsideWebViewClient;
import com.sesolutions.ui.customviews.VideoEnabledWebChromeClient;
import com.sesolutions.ui.customviews.VideoEnabledWebView;
import com.sesolutions.ui.music_album.FormFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPhotoQuoteFragment extends CommentLikeHelper implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int UPDATE_UPPER_LAYOUT = 101;

    private boolean isLoading;

    private CommonResponse.Result result;
    private Quote album;
    private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;
    private NestedScrollView mScrollView;
    //public View v;
    // public List<Albums> videoList;
    // public AlbumAdapter adapter;
    public ImageView ivCoverPhoto;
    public ImageView ivUserImage;
    //public TextView tvAlbumTitle;
    public TextView tvUserTitle;
    public TextView tvAlbumDate;
    public TextView tvAlbumDetail;
    protected TextView tvDesc;
    protected TextView tvTags;
    protected TextView tvCategory;
    protected TextView tvQuoteBy;

    private int albumId;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView tvTitle;
    private boolean openComment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvSource;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setHasOptionsMenu(true);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_photo_quote, container, false);
        try {
            applyTheme(v);
            init();
            callMusicAlbumApi(1);
            callBottomCommentLikeApi(albumId, Constant.ResourceType.QUOTE, Constant.URL_VIEW_COMMENT_LIKE);
            if (openComment) {
                //change value of openComment otherwise it prevents coming back from next screen
                openComment = false;
                goToCommentFragment(albumId, Constant.ResourceType.QUOTE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void init() {
        try {
            iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            //  ((TextView) v.findViewById(R.id.tvTitle)).setText(" ");
            initCollapsingToolbar();
            ivCoverPhoto = v.findViewById(R.id.ivCoverPhoto);
            mScrollView = v.findViewById(R.id.mScrollView);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvSource = (TextView) v.findViewById(R.id.tvSource);

            tvUserTitle = v.findViewById(R.id.tvUserTitle);
            ivUserImage = v.findViewById(R.id.ivUserImage);
            tvAlbumDate = v.findViewById(R.id.tvAlbumDate);
            tvAlbumDetail = v.findViewById(R.id.tvAlbumDetail);
            tvDesc = v.findViewById(R.id.tvDesc);
            tvTags = v.findViewById(R.id.tvTags);
            tvCategory = v.findViewById(R.id.tvCategory);
            tvQuoteBy = v.findViewById(R.id.tvQuoteBy);

            // v.findViewById(R.id.ivBack).setOnClickListener(this);
            //  v.findViewById(R.id.ivShare).setOnClickListener(this);
            //  v.findViewById(R.id.ivOption).setOnClickListener(this);

          /*v.findViewById(R.id.ivStar1).setOnClickListener(this);
            v.findViewById(R.id.ivStar2).setOnClickListener(this);
            v.findViewById(R.id.ivStar3).setOnClickListener(this);
            v.findViewById(R.id.ivStar4).setOnClickListener(this);
            v.findViewById(R.id.ivStar5).setOnClickListener(this);*/
            v.findViewById(R.id.llLike).setOnClickListener(this);
            v.findViewById(R.id.llComment).setOnClickListener(this);
            v.findViewById(R.id.llFavorite).setOnClickListener(this);

            ((TextView) v.findViewById(R.id.ivUserTitle)).setTypeface(iconFont);
            ((TextView) v.findViewById(R.id.ivAlbumDate)).setTypeface(iconFont);
            ((TextView) v.findViewById(R.id.ivUserTitle)).setText(Constant.FontIcon.USER);
            ((TextView) v.findViewById(R.id.ivAlbumDate)).setText(Constant.FontIcon.CALENDAR);
            tvAlbumDetail.setTypeface(iconFont);

            swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);
            /*AppBarLayout appBarLayout = v.findViewById(R.id.appbar);
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    swipeRefreshLayout.setEnabled(verticalOffset == 0);
                }
            });*/
            v.findViewById(R.id.llFavorite).setVisibility(View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private void initCollapsingToolbar() {

        AppBarLayout appBarLayout = null;
        try {
            Toolbar toolbar = v.findViewById(R.id.toolbar);
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null)
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
            collapsingToolbar.setTitle(" ");
            collapsingToolbar.setContentScrimColor(Color.parseColor(Constant.colorPrimary));

            appBarLayout = v.findViewById(R.id.appbar);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        //endregion
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {


            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                /*if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }*/
                if (/*scrollRange +*/ verticalOffset == 0) {
                    if (album != null) {
                        swipeRefreshLayout.setEnabled(true);
                        tvTitle.setVisibility(View.GONE);
                        //collapsingToolbar.setTitle(album.getTitle());
                    }
                    //  isShow = true;
                } else /*if (isShow)*/ {
                    swipeRefreshLayout.setEnabled(false);
                    tvTitle.setVisibility(View.VISIBLE);
                    //     collapsingToolbar.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    //  isShow = false;
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_menu, menu);
        //    menuDotItem = menu.findItem(R.id.option);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void showHideOptionIcon() {
        try {
            getActivity().findViewById(R.id.option).setVisibility((result.getMenus() != null && result.getMenus().size() > 0) ? View.VISIBLE : View.GONE);
            getActivity().findViewById(R.id.share).setVisibility((album != null && null != album.getShare()) ? View.VISIBLE : View.GONE);
        } catch (NullPointerException e) {
            CustomLog.e(e);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.share:
                    showShareDialog(Constant.TXT_SHARE_FEED);
                    break;
                case R.id.option:
                    View vItem = getActivity().findViewById(R.id.option);
                    showPopup(result.getMenus(), vItem, 10);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateUpperLayout() {
        try {
            showHideOptionIcon();
            //  ((TextView) v.findViewById(R.id.tvTitle)).setText(album.getTitle());
            collapsingToolbar.setTitle(album.getQuotetitle());
            //  setRatingStars();
            // tvAlbumTitle.setText(album.getTitle());

            if (album.getMediatype() == 2) {
                setupWebView();
            } else if (album.getImages() != null) {
                Util.showImageWithGlide(ivCoverPhoto, album.getImages().getMain(), context, R.drawable.placeholder_square);
            } else {
                //  too.setContentScrimColor(Color.parseColor(Constant.colorPrimary));
                v.findViewById(R.id.toolbar).setBackgroundColor(Color.parseColor(Constant.colorPrimary));
                v.findViewById(R.id.rlQuoteMedia).setVisibility(View.GONE);
                // toolbar.setTitle(album.getTitle());
            }

            Util.showImageWithGlide(ivUserImage, album.getUserImageUrl(), context, R.drawable.placeholder_square);

            tvUserTitle.setText(album.getUserTitle());
            tvAlbumDate.setText(Util.changeDateFormat(context,album.getCreationDate()));
            String detail = Constant.EMPTY;
            detail += "\uf164 " + album.getLikeCount()
                    + "  \uf075 " + album.getCommentCount()
                    + "  \uf06e " + album.getViewCount();
            tvAlbumDetail.setText(detail);
            if (!TextUtils.isEmpty(album.getCategoryTitle())) {
                tvCategory.setText("- " + album.getCategoryTitle());
            } else {
                tvCategory.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(album.getQuotetitle())) {
                tvQuoteBy.setText(album.getQuotetitle());
            } else {
                tvQuoteBy.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(album.getSource())) {
                tvSource.setText(album.getSource());
            } else {
                tvSource.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(album.getTitle())) {
                tvDesc.setVisibility(View.GONE);
            } else {
                tvDesc.setVisibility(View.VISIBLE);
                //tvDesc.setText(album.getTitle());
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    tvDesc.setText(Html.fromHtml(album.getTitle(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    tvDesc.setText(Html.fromHtml(album.getTitle()));
                }
                tvDesc.setMovementMethod(LinkMovementMethod.getInstance());
            }

            if (null != result.getTags()) {
                tvTags.setVisibility(View.VISIBLE);
                tvTags.setText(addClickableTaggs(result.getTags()));
                tvTags.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                tvTags.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    public void onBackPressed() {
        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
        if (null != webChromeClient && !webChromeClient.onBackPressed()) {
            /*if (webView.canGoBack())
            {
                webView.goBack();
            }
            else
            {*/
            // Standard back button implementation (for example this could close the app)
            super.onBackPressed();
            // }
        } else if (null == webChromeClient) {
            super.onBackPressed();
        }
    }

    private void setupWebView() {
        try {
            // Save the web view
            webView = v.findViewById(R.id.wbVideo);
            webView.setVisibility(View.VISIBLE);
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
            webView.setBackgroundColor(Color.BLACK);
            // Call private class InsideWebViewClient
            webView.setWebViewClient(new InsideWebViewClient());
            playVideo(album.getCode());

            // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site
            //  webView.loadUrl("http://m.youtube.com");


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void playVideo(String url) {
        // url = "http://mobileapps.socialenginesolutions.com/public/video/f0/9f/0df8ca4b7ae1d71d957a004cd7b75e";
        //webView.loadUrl(url);
        webView.loadData(url, "text/html", null);
    }

    @Override
    public void onPause() {
        try {
            CustomLog.e("QUOTE", "onPause");
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
            CustomLog.e("QUOTE", "onResume");
            if (webView != null) {
                webView.onResume();
            }
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



   /* private void setRatingStars() {
        v.findViewById(R.id.llStar).setVisibility(View.VISIBLE);
        Drawable dFilledStar = ContextCompat.getDrawable(context, R.drawable.star_filled);
        float rating = album.getIntRating();//.getTotalRatingAverage();
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

    }*/

   /* private void unfilledAllStar() {
        Drawable dUnfilledStar = ContextCompat.getDrawable(context, R.drawable.star_unfilled);
        ((ImageView) v.findViewById(R.id.ivStar1)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar2)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar3)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar4)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar5)).setImageDrawable(dUnfilledStar);

    }*/


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
                    shareInside(album.getShare(), true);

                }
            });

            bShareOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    shareOutside(album.getShare());
                }
            });

            progressDialog.findViewById(R.id.bLink).setVisibility(View.GONE);
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
                    showShareDialog(Constant.TXT_SHARE_FEED);
                    break;

                case R.id.ivOption:
                    showPopup(result.getMenus(), view, 10);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void showPopup(List<Options> menus, View v, int idPrefix) {
        try {
            PopupMenu menu = new PopupMenu(context, v);
            for (int index = 0; index < menus.size(); index++) {
                Options s = menus.get(index);
                menu.getMenu().add(1, idPrefix + index + 1, index + 1, s.getLabel());
            }
            menu.show();
            menu.setOnMenuItemClickListener(this);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

  /*  private void callRemoveImageApi(String url) {


        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_ARTICLE_ID, album.getArticleId());
                    request.params.put(Constant.KEY_RESOURCE_ID, album.getArticleId());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, album.getResourceType());
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
                    hideBaseLoader();

                }

            } else {
                isLoading = false;
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            CustomLog.e(e);
            hideBaseLoader();
        }
    }*/

    private void callMusicAlbumApi(final int req) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req != Constant.REQ_CODE_REFRESH) {
                        showBaseLoader(false);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_VIEW_QUOTE);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_QUOTE_ID, albumId);
             /*       if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/
                    // request.params.put("blog", albumId);

                    request.params.put(Constant.KEY_PAGE, 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                swipeRefreshLayout.setRefreshing(false);
                                String response = (String) msg.obj;
                                isLoading = false;

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        showView(mScrollView);
                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        result = resp.getResult();
                                        album = result.getQuote();
                                        updateUpperLayout();

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
                    hideBaseLoader();

                }

            } else {
                isLoading = false;
                notInternetMsg(v);
            }

        } catch (Exception e) {
            isLoading = false;
            CustomLog.e(e);
            hideBaseLoader();
        }
    }


    public static ViewPhotoQuoteFragment newInstance(int albumId, boolean openComment) {
        ViewPhotoQuoteFragment frag = new ViewPhotoQuoteFragment();
        frag.albumId = albumId;
        frag.openComment = openComment;
        return frag;
    }

    public static ViewPhotoQuoteFragment newInstance(int albumId) {
        return ViewPhotoQuoteFragment.newInstance(albumId, false);
    }

    private void callDeleteApi() {
        try {
            if (isNetworkAvailable(context)) {
                try {
                    //  HttpRequestVO request = new HttpRequestVO(Constant.BASE_URL + "album/delete/" + albumId + Constant.POST_URL);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_DELETE_QUOTE);
                    request.params.put(Constant.KEY_QUOTE_ID, albumId);

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
                                        activity.taskPerformed = Constant.TASK_ALBUM_DELETED;
                                        onBackPressed();
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try {
            int itemId = item.getItemId();
            Options opt;
            itemId = itemId - 10;
            opt = result.getMenus().get(itemId - 1);


            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    goToFormFragment(album.getQuoteId());
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog();
                    break;
                case Constant.OptionType.REPORT:
                    goToReportFragment(Constant.ResourceType.QUOTE + "_" + album.getQuoteId());
                    break;

              /*  case Constant.OptionType.CHANGE_PHOTO:
                case Constant.OptionType.UPLOAD_PHOTO:
                    gToAlbumImage(Constant.URL_EDIT_ARTICLE_PHOTO, album.getArticleImages().getMain(), Constant.TITLE_EDIT_ARTICLE_PHOTO);
                    break;*/
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    public void goToFormFragment(int blogId) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_QUOTE_ID, blogId);
        // map.put(Constant.KEY_GET_FORM, 1);
        fragmentManager.beginTransaction().replace(R.id.container, FormFragment.newInstance(Constant.FormType.EDIT_QUOTE, map, Constant.URL_EDIT_QUOTE)).addToBackStack(null).commit();
    }

    public void showDeleteDialog() {
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
            tvMsg.setText(Constant.MSG_DELETE_CONFIRMATION_QUOTE);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(Constant.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi();
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

    @Override
    public void onStart() {
        super.onStart();
        /*if (activity.taskPerformed == Constant.TASK_IMAGE_UPLOAD) {
            activity.taskPerformed = 0;
            Util.showImageWithGlide(ivCoverPhoto, activity.stringValue, context, R.drawable.placeholder_square);
        } else */
        if (activity.taskPerformed == Constant.FormType.EDIT_QUOTE) {
            activity.taskPerformed = 0;
            onRefresh();
            //     callMusicAlbumApi(1);
        }
    }

    @Override
    public void onRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.isRefreshing();
        }
        callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
    }



  /*  private void gToAlbumImage(String url, String main, String title) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_ARTICLE_ID, album.getArticleId());
        fragmentManager.beginTransaction()
                .replace(R.id.container, AlbumImageFragment.newInstance(title, url, main, map))
                .addToBackStack(null)
                .commit();
    }*/


    /*public void showImageRemoveDialog(boolean isCover, String msg, final String url) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            progressDialog.setContentView(R.layout.dialog_message_two);
            ((TextView) progressDialog.findViewById(R.id.tvDialogText)).setText(msg);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(isCover ? Constant.TXT_REMOVE_COVER : Constant.TXT_REMOVE_PHOTO);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.CANCEL);

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
    }*/

   /* public void askForPermission(String permission) {
        try {
            new TedPermission(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(Constant.MSG_PERMISSION_DENIED)
                    .setPermissions(permission, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/

}

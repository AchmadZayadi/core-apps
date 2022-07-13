package com.sesolutions.ui.articles;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.camerahelper.CameraActivity;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.blogs.Blog;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.speech.Speech;
import com.sesolutions.speech.TextToSpeechCallback;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.customviews.NestedWebView;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.ui.music_album.AlbumImageFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;


public class ViewArticleFragment extends ArticleHelper implements View.OnClickListener, OnLoadMoreListener, PopupMenu.OnMenuItemClickListener, TextToSpeechCallback {

    private static final int UPDATE_UPPER_LAYOUT = 101;
    public static final int CAMERA_PIC_REQUEST = 7080;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private CommonResponse.Result result;
    private Blog album;
    //  private NestedScrollView mScrollView;
    //public View v;
    // public List<Albums> videoList;
    // public AlbumAdapter adapter;
    public ImageView ivCoverPhoto;
    public ImageView ivUserImage;
    //public TextView tvAlbumTitle;
    public TextView tvUserTitle;
    public TextView tvAlbumDate;
    public TextView tvAlbumDetail;
    private NestedWebView webview;

    private int albumId;
    private boolean isCameraOptionSelected;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView tvTitle;
    private boolean openComment;
    private MenuItem menuDotItem;
    private Bundle bundle;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!openComment && bundle != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivCoverPhoto.setTransitionName(bundle.getString(Constant.Trans.IMAGE));
            tvTitle.setTransitionName(bundle.getString(Constant.Trans.TEXT));
            tvTitle.setText(bundle.getString(Constant.Trans.IMAGE));
            try {
                Glide.with(context)
                        .setDefaultRequestOptions(new RequestOptions().dontAnimate().dontTransform())
                        .load(bundle.getString(Constant.Trans.IMAGE_URL))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                CustomLog.e("onLoadFailed", "onLoadFailed");
                                startPostponedEnterTransition();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                CustomLog.e("onResourceReady", "onResourceReady");
                                //  ivAlbumImage.setImageDrawable(resource);
                                startPostponedEnterTransition();
                                return false;
                            }
                        })
                        .into(ivCoverPhoto);
            } catch (Exception e) {
                CustomLog.e(e);
            }
            //    Util.showImageWithGlide(ivAlbumImage, bundle.getString(Constant.Trans.IMAGE_URL), context);
        } /*else {

        }*/
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setHasOptionsMenu(true);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_blog, container, false);
        applyTheme();
        Speech.init(context, context.getPackageName());
        init();
        callMusicAlbumApi(1);
        callBottomCommentLikeApi(albumId, Constant.ResourceType.ARTICLE, Constant.URL_VIEW_COMMENT_LIKE);
        if (openComment) {
            //change value of openComment otherwise it prevents coming back from next screen
            openComment = false;
            goToCommentFragment(albumId, Constant.ResourceType.ARTICLE);
        }
        return v;
    }


    @Override
    public void onStartPlaying() {
        pulsator.start();
    }

    @Override
    public void onCompleted() {
        pulsator.stop();
    }

    @Override
    public void onError() {
        Util.showSnackbar(pulsator,getString(R.string.msg_tts_content_invalid));
        pulsator.stop();
    }

    @Override
    public void init() {
        super.init();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        //  ((TextView) v.findViewById(R.id.tvTitle)).setText(" ");
        initCollapsingToolbar();
        ivCoverPhoto = v.findViewById(R.id.ivCoverPhoto);
        //  mScrollView = v.findViewById(R.id.mScrollView);
        v.findViewById(R.id.llContent).setBackgroundColor(Color.parseColor(Constant.foregroundColor));
        tvTitle = v.findViewById(R.id.tvTitle);
        setCookie();

        tvUserTitle = v.findViewById(R.id.tvUserTitle);
        ivUserImage = v.findViewById(R.id.ivUserImage);
        tvAlbumDate = v.findViewById(R.id.tvAlbumDate);
        tvAlbumDetail = v.findViewById(R.id.tvAlbumDetail);

        v.findViewById(R.id.ivStar1).setOnClickListener(this);
        v.findViewById(R.id.ivStar2).setOnClickListener(this);
        v.findViewById(R.id.ivStar3).setOnClickListener(this);
        v.findViewById(R.id.ivStar4).setOnClickListener(this);
        v.findViewById(R.id.ivStar5).setOnClickListener(this);

        v.findViewById(R.id.llLike).setOnClickListener(this);
        v.findViewById(R.id.llComment).setOnClickListener(this);
        v.findViewById(R.id.llFavorite).setOnClickListener(this);

        ((TextView) v.findViewById(R.id.ivUserTitle)).setTypeface(iconFont);
        ((TextView) v.findViewById(R.id.ivAlbumDate)).setTypeface(iconFont);
        ((TextView) v.findViewById(R.id.ivUserTitle)).setText(Constant.FontIcon.USER);
        ((TextView) v.findViewById(R.id.ivAlbumDate)).setText(Constant.FontIcon.CALENDAR);
        tvAlbumDetail.setTypeface(iconFont);

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

    private void initCollapsingToolbar() {
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        collapsingToolbar.setContentScrimColor(Color.parseColor(Constant.colorPrimary));

        AppBarLayout appBarLayout = v.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    if (album != null) {
                        tvTitle.setVisibility(View.GONE);
                        collapsingToolbar.setTitle(album.getTitle());
                    }
                    isShow = true;
                } else if (isShow) {
                    tvTitle.setVisibility(View.VISIBLE);
                    collapsingToolbar.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(AppConfiguration.IS_BLOG_TTS_EBANBLED ? R.menu.view_menu_share : R.menu.view_menu, menu);
        menuDotItem = menu.findItem(R.id.option);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void showHideOptionIcon() {
        try {
            getActivity().findViewById(R.id.option).setVisibility((result.getMenus() != null && result.getMenus().size() > 0) ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            // CustomLog.e(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.share:
                    showShareDialog(album.getShare());
                    break;
                case R.id.speak:
                    if (pulsator.isStarted()) {
                        Speech.getInstance().stopTextToSpeech();
                        pulsator.stop();
                    } else {
                        Speech.getInstance().say(album.getBody(), this);
                    }
                    //TTSSpeakDialogFragment.newInstance(album.getTitle(), album.getBody()).show(activity.getSupportFragmentManager(), "tts");
                    break;
                case R.id.option:
                    View vItem = getActivity().findViewById(R.id.option);
                    showPopup(result.getMenus(), vItem, 10, this);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupWebView() {
        try {
            webview = v.findViewById(R.id.webview);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setBuiltInZoomControls(false);
            webview.getSettings().setSupportZoom(false);
            webview.setNestedScrollingEnabled(false);
            //webView.setWebViewClient(new WebViewFragment.browser(progress));

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void updateUpperLayout() {
        try {
            showHideOptionIcon();
            ((TextView) v.findViewById(R.id.tvTitle)).setText(album.getTitle());
//            collapsingToolbar.setTitle(album.getTitle());
            setRatingStars();

            //    tvAlbumTitle.setText(album.getTitle());
            Util.showImageWithGlide(ivCoverPhoto, album.getArticleImages().getMain(), context/*, R.drawable.placeholder_square*/);
            Util.showImageWithGlide(ivUserImage, album.getUserImage(), context, R.drawable.placeholder_square);

            tvUserTitle.setText(album.getOwnerTitle());
            tvAlbumDate.setText(Util.changeDateFormat(context, album.getCreationDate()));

            tvAlbumDetail.setText(getDetail(album));
            webview.loadDataWithBaseURL(Constant.BASE_URL, album.getRawBody(), "text/html", "UTF-8",null);
            if (menuDotItem.isVisible() && result.getMenus() == null) {
                menuDotItem.setVisible(false);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void setRatingStars() {
        //tampilin icon bintang
      //  v.findViewById(R.id.llStar).setVisibility(View.VISIBLE);
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

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        try {
            switch (view.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callRemoveImageApi(String url) {


        try {
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
    }

    private void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req != UPDATE_UPPER_LAYOUT) {
                        showView(v.findViewById(R.id.pbMain));
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.BASE_URL + "article/" + albumId + Constant.POST_URL);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
             /*       if (!TextUtils.isEmpty(searchKey))
                        request.params.put(Constant.KEY_SEARCH, searchKey);*/
                    // request.params.put("blog", albumId);
                    if (req == UPDATE_UPPER_LAYOUT) {
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getCurrentPage() : 1);
                    } else {
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideView(v.findViewById(R.id.pbMain));
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        showView(v.findViewById(R.id.cvDetail));
                                        CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                        result = resp.getResult();

                                        album = result.getArticle();
                                        updateUpperLayout();

                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
                                    }
                                }

                            } catch (Exception e) {
                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    CustomLog.e(e);
                    hideView(v.findViewById(R.id.pbMain));
                }
            } else {
                notInternetMsg(v);
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public static ViewArticleFragment newInstance(int albumId, boolean openComment) {
        ViewArticleFragment frag = new ViewArticleFragment();
        frag.albumId = albumId;
        frag.openComment = openComment;
        return frag;
    }

    public static ViewArticleFragment newInstance(int albumId, Bundle bundle) {
        ViewArticleFragment frag = new ViewArticleFragment();
        frag.albumId = albumId;
        frag.bundle = bundle;
        return frag;
    }


    public static ViewArticleFragment newInstance(int albumId) {
        return ViewArticleFragment.newInstance(albumId, false);
    }

    @Override
    public void onLoadMore() {
    }

    public void loadMore() {
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
    public boolean onMenuItemClick(MenuItem item) {
        try {
            int itemId = item.getItemId();
            Options opt;
            itemId = itemId - 10;
            opt = result.getMenus().get(itemId - 1);


            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    goToFormFragment(album.getArticleId());
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog(VIEW_BLOG_DELETE, albumId, 0);
                    break;
                case Constant.OptionType.REPORT:
                    goToReportFragment();
                    break;

                case Constant.OptionType.CHANGE_PHOTO:
                case Constant.OptionType.UPLOAD_PHOTO:
                    gToAlbumImage(Constant.URL_EDIT_ARTICLE_PHOTO, album.getArticleImages().getMain(), Constant.TITLE_EDIT_ARTICLE_PHOTO);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.TASK_IMAGE_UPLOAD) {
            activity.taskPerformed = 0;
            Util.showImageWithGlide(ivCoverPhoto, activity.stringValue, context, R.drawable.placeholder_square);
        } else if (activity.taskPerformed == Constant.FormType.TYPE_ARTICLE_EDIT) {
            activity.taskPerformed = 0;
            callMusicAlbumApi(1);
        }
    }

    /*@Override
    public void onStop() {
        if (pulsator.isStarted()) {
            Speech.getInstance().stopTextToSpeech();
            pulsator.stop();
        }
        Speech.getInstance().shutdown();
        super.onStop();
    }
*/
    private void goToReportFragment() {
        String guid = album.getResourceType() + "_" + album.getArticleId();
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid)).addToBackStack(null).commit();
    }

    private void gToAlbumImage(String url, String main, String title) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_ARTICLE_ID, album.getArticleId());
        fragmentManager.beginTransaction()
                .replace(R.id.container, AlbumImageFragment.newInstance(title, url, main, map))
                .addToBackStack(null)
                .commit();
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
            bCamera.setText(isCover ? Constant.TXT_REMOVE_COVER : Constant.TXT_REMOVE_PHOTO);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(Constant.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callRemoveImageApi(url);

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

    public void askForPermission(String permission) {
        try {
            new TedPermission(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(Constant.MSG_PERMISSION_DENIED)
                    .setPermissions(permission, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            try {
                if (isCameraOptionSelected) {
                    takeImageFromCamera();
                } else {
                    showImageChooser();
                }
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };

    public void showImageChooser() {
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setActivityTheme(R.style.FilePickerTheme)
                .showFolderView(true)
                .enableImagePicker(true)
                .enableVideoPicker(false)
                .pickPhoto(this);
    }

    public void takeImageFromCamera() {
        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SeSolutions/";
        String imageName = Constant.IMAGE_NAME + Util.getCurrentdate(Constant.TIMESTAMP);

        File dir = new File(imagePath);
        try {
            if (dir.mkdir()) {
            } else {
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        Intent cameraIntent = new Intent(activity, CameraActivity.class);
        cameraIntent.putExtra("path", imagePath);
        cameraIntent.putExtra("name", imageName);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }


    /**
     * camera activity call back
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CustomLog.e("onActivityResult", "requestCode : " + requestCode + " resultCode : " + resultCode);
        try {
            switch (requestCode) {
                case FilePickerConst.REQUEST_CODE_PHOTO:
                    if (resultCode == -1 && data != null) {
                        List<String> photoPaths = new ArrayList<>(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS));
                        //  setImage(photoPaths.get(0));


                    }
                    break;
                case CAMERA_PIC_REQUEST:
                    if (resultCode == -1) {
                        //setImage(Constant.path);
                        List<String> photoPaths = new ArrayList<String>();
                        photoPaths.add(Constant.path);
                        CustomLog.d("CAMERA_PIC_REQUEST", Constant.path);

                    }
                    break;
            }

             /*  if (requestCode == CAMERA_PIC_REQUEST && resultCode == -1) {
                CustomLog.e("on", "requestCode : " + requestCode + " resultCode : " + resultCode);
             if (requestCode == Constant.SELECT_PICTURE) {
                    CustomLog.e("inner", "requestCode : " + requestCode + " resultCode : " + resultCode);
                    // pic image from gallery
                    Uri selectedImageUri = intentdata.getData();
                    Util.FCopy(image_path_source_temp + imageName, getPath(selectedImageUri));
                }
                // CheckOrient();
                // takeImage.setImageBitmap(Image_BMP);
                //   takeImage.setScaleType(ImageView.ScaleType.FIT_XY);/*CENTER_CROP
        }*/

        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

}

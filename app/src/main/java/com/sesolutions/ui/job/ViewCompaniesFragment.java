package com.sesolutions.ui.job;


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
import android.text.TextUtils;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.camerahelper.CameraActivity;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.responses.CommonResponse3;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.jobs.JobsResponse;
import com.sesolutions.responses.page.PageResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.customviews.NestedWebView;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.ui.message.MessageDashboardViewPagerAdapter;
import com.sesolutions.ui.music_album.AlbumImageFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
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

public class ViewCompaniesFragment extends JobHelper implements View.OnClickListener, OnLoadMoreListener, PopupMenu.OnMenuItemClickListener, TabLayout.OnTabSelectedListener {

    private static final int UPDATE_UPPER_LAYOUT = 101;
    public static final int CAMERA_PIC_REQUEST = 7080;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private CommonResponse3.Result result;
    private JobsResponse jobsResponse;
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
    AppBarLayout appBar;
    private int JobId;
    String company_image="";
    private boolean isCameraOptionSelected;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView tvTitle;
    private boolean openComment;
    private View vItem;
    private Bundle bundle;
    TextView jobtitle,companyname,expriense,aboutcompanydes;
    ImageView companyimageid;

    private AppCompatImageView ivFbShare;
    private AppCompatImageView ivWhatsAppShare;
    private AppCompatImageView ivImageShare;
    private AppCompatImageView ivSaveFeed;

    public Drawable dSave;
    public  Drawable dUnsave;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    /*    if (!openComment && bundle != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivCoverPhoto.setTransitionName(bundle.getString(Constant.Trans.IMAGE));
            tvTitle.setTransitionName(bundle.getString(Constant.Trans.TEXT));
            tvTitle.setText(bundle.getString(Constant.Trans.IMAGE));
            try {
                Glide.with(context)
                        .setDefaultRequestOptions(new RequestOptions().dontAnimate().dontTransform().centerCrop())
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
        } *//*else {

        }*/
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setHasOptionsMenu(true);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_view_company, container, false);
        applyTheme(v);

        init();
        callMusicAlbumApi(1);




        /*callMusicAlbumApi(1);
        callBottomCommentLikeApi(albumId, Constant.ResourceType.BLOG, Constant.URL_VIEW_COMMENT_LIKE);
        if (openComment) {
            //change value of openComment otherwise it prevents coming back from next screen
            openComment = false;
            goToCommentFragment(albumId, Constant.ResourceType.BLOG);
        }*/

        return v;
    }

    public MessageDashboardViewPagerAdapter adapter;



    public void init() {
        super.init();

        //  setCookie();
      /*  iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        //  ((TextView) v.findViewById(R.id.tvTitle)).setText(" ");
        initCollapsingToolbar();
        ivCoverPhoto = v.findViewById(R.id.ivCoverPhoto);*/
        appBar = v.findViewById(R.id.appBar);
        companyimageid = v.findViewById(R.id.companyimageid);
        this.dSave = ContextCompat.getDrawable(context, R.drawable.ic_save);
        this.dUnsave = ContextCompat.getDrawable(context, R.drawable.ic_save_filled);

        //  mScrollView = v.findViewById(R.id.mScrollView);
      /*  v.findViewById(R.id.llContent).setBackgroundColor(Color.parseColor(Constant.foregroundColor));
        tvTitle = v.findViewById(R.id.tvTitle);*/
        appBar.setVisibility(View.VISIBLE);
        jobtitle = v.findViewById(R.id.jobtitle);
        companyname = v.findViewById(R.id.companyname);
        aboutcompanydes = v.findViewById(R.id.aboutcompanydes);
        ivFbShare = v.findViewById(R.id.ivFbShare);
        ivWhatsAppShare = v.findViewById(R.id.ivWhatsAppShare);
        ivImageShare = v.findViewById(R.id.ivImageShare);
        ivSaveFeed = v.findViewById(R.id.ivSaveFeed);

        expriense = v.findViewById(R.id.expriense);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        ((TextView)v.findViewById(R.id.tvTitle)).setText(getStrings(R.string.companies_data));

     /*
        ivUserImage = v.findViewById(R.id.ivUserImage);
        tvAlbumDate = v.findViewById(R.id.tvAlbumDate);
        tvAlbumDetail = v.findViewById(R.id.tvAlbumDetail);

        //  v.findViewById(R.id.ivShare).setOnClickListener(this);
        //  v.findViewById(R.id.ivOption).setOnClickListener(this);

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
        tvAlbumDetail.setTypeface(iconFont);*/

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


    Menu menueItem=null;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_menu_share, menu);
        menueItem=menu;
        menueItem.getItem(1).setIcon(R.drawable.ses_speak);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.share:
                    showShareDialog(album.getShare());
                    break;
                case R.id.speak:
                    if(!isSubscribe){
                        callsubscribeuser(album.getBlogId());
                    }else {
                        callUnsubscribeuser(album.getBlogId());
                    }
                    break;
                case R.id.option:
                    vItem = getActivity().findViewById(R.id.option);
                    showPopup(result.getMenus(), vItem, 10, this);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void showHideOptionIcon() {
        try {
            getActivity().findViewById(R.id.option).setVisibility((result.getMenus() != null && result.getMenus().size() > 0) ? View.VISIBLE : View.GONE);
        } catch (Exception ignore) {
        }
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
              //          ((TextView) v.findViewById(R.id.tvTitle)).setText(album.getTitle());
              //            collapsingToolbar.setTitle(album.getTitle());
              //    tvAlbumTitle.setText(album.getTitle());
         //   Util.showImageWithGlide(ivCoverPhoto, jobsResponse.getBlogImages().getMain(), context/*, R.drawable.placeholder_square*/);
         //   Util.showImageWithGlide(ivUserImage, album.getUserImage(), context, R.drawable.placeholder_square);

            companyname.setTypeface(iconFont);
            companyname.setTypeface(iconFont);
            jobtitle.setText(""+jobsResponse.getCompany_name());
            aboutcompanydes.setText(""+jobsResponse.getCompany_description());

            companyname.setVisibility(View.VISIBLE);

            if(jobsResponse!=null && jobsResponse.getExperience()!=null && jobsResponse.getLocation()!=null){
                companyname.setText(Constant.FontIcon.SHOTCASE+"  "+jobsResponse.getExperience()+"  "+Constant.FontIcon.MAP_MARKER+" "+jobsResponse.getLocation());
            }else   if(jobsResponse!=null && jobsResponse.getExperience()!=null ){
                companyname.setText(Constant.FontIcon.SHOTCASE+"  "+jobsResponse.getExperience());
            }else if(jobsResponse!=null && jobsResponse.getLocation()!=null){
                companyname.setText(Constant.FontIcon.MAP_MARKER+" "+jobsResponse.getLocation());
            }else {
                companyname.setVisibility(View.GONE);
            }
            expriense.setText("Posted on "+Util.changeDateFormat(context,jobsResponse.getCreationDate()));


            ivFbShare.setOnClickListener(v ->
                    onItemClicked(Constant.Events.SHARE_FEED, jobsResponse.getShare(), 1));
            ivWhatsAppShare.setOnClickListener(v ->
                    onItemClicked(Constant.Events.SHARE_FEED, jobsResponse.getShare(), 2));
            ivImageShare.setOnClickListener(v ->
                    onItemClicked(Constant.Events.SHARE_FEED, jobsResponse.getShare(), 3));


            ivSaveFeed.setOnClickListener(v -> {
                try {
                    if(jobsResponse.getShortcut_save().getShortcut_id()>0){
                        if(jobsResponse.getShortcut_save().isIs_saved()){
                            showBaseLoader(false);
                            callFeedEventApi(REQ_CODE_OPTION_SAVE, Constant.URL_FEED_REMOVESHOIRTCUT, jobsResponse.getShortcut_save().getResource_id(),jobsResponse.getShortcut_save().getShortcut_id());
                        }else {
                            showBaseLoader(false);
                            callFeedEventApi(REQ_CODE_OPTION_UNSAVE, Constant.URL_FEED_ADDSHOIRTCUT, jobsResponse.getShortcut_save().getResource_id(),jobsResponse.getShortcut_save().getShortcut_id());
                        }
                    }else {
                        showBaseLoader(false);
                        callFeedEventApi(REQ_CODE_OPTION_UNSAVE, Constant.URL_FEED_ADDSHOIRTCUT, jobsResponse.getShortcut_save().getResource_id(),0);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    showBaseLoader(false);
                    callFeedEventApi(REQ_CODE_OPTION_UNSAVE, Constant.URL_FEED_ADDSHOIRTCUT, jobsResponse.getShortcut_save().getResource_id(),0);
                }
            });


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


   /* private void unfilledAllStar() {
        Drawable dUnfilledStar = ContextCompat.getDrawable(context, R.drawable.star_unfilled);
        ((ImageView) v.findViewById(R.id.ivStar1)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar2)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar3)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar4)).setImageDrawable(dUnfilledStar);
        ((ImageView) v.findViewById(R.id.ivStar5)).setImageDrawable(dUnfilledStar);

    }*/

    @Override
    public void onClick(View view) {
        super.onClick(view);
        try {
            switch (view.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;

                case R.id.ivShare:
                    showShareDialog(album.getShare());
                    break;

                case R.id.ivOption:
                    showPopup(result.getMenus(), view, 10, this);
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void callRemoveImageApi(String url) {


        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_BLOG_ID, album.getBlogId());
                    request.params.put(Constant.KEY_RESOURCE_ID, album.getBlogId());
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

    boolean isSubscribe=false;

    private void callMusicAlbumApi(final int req) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;


                try {
                    if (req == 1) {
                        showView(v.findViewById(R.id.pbMain));
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.BASE_URL + "/sesjob/index/company-view/" + Constant.POST_URL);
                  //  HttpRequestVO request = new HttpRequestVO(Constant.BASE_URL + "/sesjob/index/job-view/" + Constant.POST_URL);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_COMPANY_ID, ""+JobId);

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
                                        CommonResponse3 resp = new Gson().fromJson(response, CommonResponse3.class);
                                        result = resp.getResult();
                                        jobsResponse = result.getJobs();
                                        updateUpperLayout();
                                        //setupViewPager();
                                        Log.e("company_image",""+company_image);
                                        Util.showImageWithGlide(companyimageid, company_image, context, R.drawable.placeholder_square);
                                      /*  try {
                                            if(result.getBlog().subscribe!=null){
                                                if(result.getBlog().subscribe.label.equalsIgnoreCase("Unsubscribe")){
                                                    isSubscribe=true;
                                                    menueItem.getItem(1).setIcon(R.drawable.ses_speak);
                                                }else {
                                                    isSubscribe=false;
                                                    menueItem.getItem(1).setIcon(R.drawable.unsubscribe_11102021);
                                                }
                                            }else {
                                                isSubscribe=false;
                                            }
                                        }catch (Exception ex){
                                            ex.printStackTrace();
                                        }*/

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
                    hideView(v.findViewById(R.id.pbMain));
                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public static ViewCompaniesFragment newInstance(int albumId,String company_image, boolean comment) {
        ViewCompaniesFragment frag = new ViewCompaniesFragment();
        frag.JobId = albumId;
        frag.company_image = company_image;
        frag.openComment = comment;
        return frag;
    }

    public static ViewCompaniesFragment newInstance(int albumId, Bundle bundle,String company_image) {
        ViewCompaniesFragment frag = new ViewCompaniesFragment();
        frag.JobId = albumId;
        frag.bundle = bundle;
        frag.company_image = company_image;
        return frag;
    }


    public static ViewCompaniesFragment newInstance(int albumId,String company_image) {
        return ViewCompaniesFragment.newInstance(albumId, company_image,false);
    }

    @Override
    public void onLoadMore() {
    }

   /* public void loadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        try {
            int itemId = item.getItemId();
            Options opt;
            itemId = itemId - 10;
            opt = result.getMenus().get(itemId - 1);


            switch (opt.getName()) {
                case Constant.OptionType.EDIT:
                    goToFormFragment(album.getBlogId());
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog(VIEW_BLOG_DELETE, JobId, 0);
                    break;
                case Constant.OptionType.REPORT:
                    goToReportFragment();
                    break;

                case Constant.OptionType.CHANGE_PHOTO:
                case Constant.OptionType.UPLOAD_PHOTO:
                    gToAlbumImage(Constant.URL_EDIT_BLOG_PHOTO, album.getBlogImages().getMain(), Constant.TITLE_EDIT_BLOG_PHOTO);
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
        } else if (activity.taskPerformed == Constant.FormType.TYPE_BLOG_EDIT) {
            activity.taskPerformed = 0;
            callMusicAlbumApi(1);
        }

    }

    private void goToReportFragment() {
        String guid = album.getResourceType() + "_" + album.getBlogId();
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid)).addToBackStack(null).commit();
    }

    private void gToAlbumImage(String url, String main, String title) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_BLOG_ID, album.getBlogId());
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
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
            // Toast.makeText(How_It_Works_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
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
        // fimg = new File(image_path_source_temp + imageName);
        // Uri uri = Uri.fromFile(fimg);
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


    public void showImageDialog(String msg) {
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
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    isCameraOptionSelected = true;
                    askForPermission(Manifest.permission.CAMERA);
                    // takeImageFromCamera();
                }
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    isCameraOptionSelected = false;
                    askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //showImageChooser();
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
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

    private void callsubscribeuser(final int blogId) {

        try {
            if (isNetworkAvailable(context)) {

                try {
                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_BLOG_SUBSCRIBE);
                    request.params.put(Constant.KEY_BLOG_ID, blogId);
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
                                isSubscribe=true;
                                menueItem.getItem(1).setIcon(R.drawable.ses_speak);
                                   if (response != null) {
                                       ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                       Util.showSnackbar(v, err.getErrorMessage());
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
    private void callUnsubscribeuser(final int blogId) {

        try {
            if (isNetworkAvailable(context)) {

                try {
                    showBaseLoader(false);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_BLOG_UNSUBSCRIBE);
                    request.params.put(Constant.KEY_BLOG_ID, blogId);
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
                                isSubscribe=false;
                                menueItem.getItem(1).setIcon(R.drawable.unsubscribe_11102021);
                              if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    Util.showSnackbar(v, err.getErrorMessage());
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
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    public void callFeedEventApi(final int reqCode, String url, int resid,int shortcutid) {
        try {
            if (isNetworkAvailable(context)) {

                try {
                    HttpRequestVO request = new HttpRequestVO(url);
                    // request.params.put(Constant.KEY_ACTIVITY_ID, actionId);
                    request.params.put("resource_id", resid);
                    request.params.put("resource_type", "sesjob_company");
                    request.params.put("shortcut_id", shortcutid);

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
                                PageResponse resp = new Gson().fromJson(response, PageResponse.class);
                                jobsResponse.getShortcut_save().setIs_saved(!jobsResponse.getShortcut_save().isIs_saved());

                                if (TextUtils.isEmpty(resp.getError())) {
                                    switch (reqCode) {
                                        case REQ_CODE_OPTION_SAVE:
                                            hideBaseLoader();
                                            try {
                                                ivSaveFeed.setImageDrawable(dSave);
                                            }catch (Exception ex){
                                                ex.printStackTrace();
                                                ivSaveFeed.setVisibility(View.GONE);
                                            }
                                            //    updateOptionText(actPosition,  "save", Constant.TXT_UNSAVE_FEED,0);
                                            break;
                                        case REQ_CODE_OPTION_UNSAVE:
                                            hideBaseLoader();

                                            try {
                                                ivSaveFeed.setImageDrawable(dUnsave);
                                            }catch (Exception ex){
                                                ex.printStackTrace();
                                                ivSaveFeed.setVisibility(View.GONE);
                                            }
                                            //    updateOptionText(actPosition,  "unsave", Constant.TXT_SAVE_FEED,resp.getResult().getShortcut_id23());
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



}

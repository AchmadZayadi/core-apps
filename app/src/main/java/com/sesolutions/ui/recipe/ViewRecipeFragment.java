package com.sesolutions.ui.recipe;


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
import com.sesolutions.responses.feed.Options;
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

public class ViewRecipeFragment extends RecipeHelper implements View.OnClickListener, OnLoadMoreListener, PopupMenu.OnMenuItemClickListener {

    private static final int UPDATE_UPPER_LAYOUT = 101;
    public static final int CAMERA_PIC_REQUEST = 7080;
    private CommonResponse.Result result;
    public ImageView ivCoverPhoto;
    public ImageView ivUserImage;
    public TextView tvUserTitle;
    public TextView tvAlbumDate;
    public TextView tvAlbumDetail;
    private NestedWebView webview;

    private int albumId;
    private boolean isCameraOptionSelected;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView tvTitle;
    private boolean openComment;
    private View vItem;
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
                        .setDefaultRequestOptions(new RequestOptions().dontAnimate().dontTransform().centerCrop())
                        .load(bundle.getString(Constant.Trans.IMAGE_URL))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                startPostponedEnterTransition();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
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
        init();
        callMusicAlbumApi(1);
        callBottomCommentLikeApi(albumId, Constant.ResourceType.RECIPE, Constant.URL_VIEW_COMMENT_LIKE);
        if (openComment) {
            //change value of openComment otherwise it prevents coming back from next screen
            openComment = false;
            goToCommentFragment(albumId, Constant.ResourceType.RECIPE);
        }
        return v;
    }

    public void init() {
        super.init();
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        //  ((TextView) v.findViewById(R.id.tvTitle)).setText(" ");
        initCollapsingToolbar();

        ivCoverPhoto = v.findViewById(R.id.ivCoverPhoto);
        //  mScrollView = v.findViewById(R.id.mScrollView);
        v.findViewById(R.id.llContent).setBackgroundColor(Color.parseColor(Constant.foregroundColor));
        tvTitle = v.findViewById(R.id.tvTitle);

        //    tvAlbumTitle = v.findViewById(R.id.tvAlbumTitle);
        setupWebView();

        tvUserTitle = v.findViewById(R.id.tvUserTitle);
        ivUserImage = v.findViewById(R.id.ivUserImage);
        tvAlbumDate = v.findViewById(R.id.tvAlbumDate);
        tvAlbumDetail = v.findViewById(R.id.tvAlbumDetail);
        // v.findViewById(R.id.ivBack).setOnClickListener(this);
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
        tvAlbumDetail.setTypeface(iconFont);

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
        inflater.inflate(R.menu.view_menu_share, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            switch (item.getItemId()) {
                case R.id.share:
                    showShareDialog(Constant.TXT_SHARE_FEED);
                    break;
                case R.id.option:
                    vItem = getActivity().findViewById(R.id.option);
                    showPopup(result.getMenus(), vItem, 10);
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
        } catch (Exception e) {
            // CustomLog.e(e);
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
            ((TextView) v.findViewById(R.id.tvTitle)).setText(album.getTitle());
//            collapsingToolbar.setTitle(album.getTitle());
            setRatingStars();

            //    tvAlbumTitle.setText(album.getTitle());
            Util.showImageWithGlide(ivCoverPhoto, album.getRecipeImages().getMain(), context/*, R.drawable.placeholder_square*/);
            Util.showImageWithGlide(ivUserImage, album.getUserImage(), context, R.drawable.placeholder_square);

            tvUserTitle.setText(album.getOwnerTitle());
            tvAlbumDate.setText(Util.changeDateFormat(context,album.getCreationDate()));

            tvAlbumDetail.setText(getDetail(album));
            webview.loadData(album.getRawBody(), "text/html", "UTF-8");

        } catch (Exception e) {
            CustomLog.e(e);
        }


    }

    private void setRatingStars() {
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
    }

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
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_three);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);
            AppCompatButton bShareIn = progressDialog.findViewById(R.id.bCamera);
            AppCompatButton bShareOut = progressDialog.findViewById(R.id.bGallary);
            bShareOut.setText(Constant.TXT_SHARE_OUTSIDE);
            bShareIn.setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);
            bShareIn.setText(Constant.TXT_SHARE_INSIDE + AppConfiguration.SHARE);
            bShareIn.setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);
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

    private void callRemoveImageApi(String url) {


        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {
                    HttpRequestVO request = new HttpRequestVO(url);

                    request.params.put(Constant.KEY_RECIPE_ID, album.getRecipeId());
                    request.params.put(Constant.KEY_RESOURCE_ID, album.getRecipeId());
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

    private void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {

                try {
                    if (req == 1) {
                        showView(v.findViewById(R.id.pbMain));
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_RECIPE_VIEW);
                    request.params.put(Constant.KEY_RECIPE_ID, albumId);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
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
                                        album = result.getRecipe();
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
                    hideView(v.findViewById(R.id.pbMain));
                }
            } else {
                notInternetMsg(v);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public static ViewRecipeFragment newInstance(int albumId, boolean comment) {
        ViewRecipeFragment frag = new ViewRecipeFragment();
        frag.albumId = albumId;
        frag.openComment = comment;
        return frag;
    }

    public static ViewRecipeFragment newInstance(int albumId, Bundle bundle) {
        ViewRecipeFragment frag = new ViewRecipeFragment();
        frag.albumId = albumId;
        frag.bundle = bundle;
        return frag;
    }


    public static ViewRecipeFragment newInstance(int albumId) {
        return ViewRecipeFragment.newInstance(albumId, false);
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
                    goToFormFragment(album.getRecipeId());
                    break;
                case Constant.OptionType.DELETE:
                    showDeleteDialog(VIEW_DELETE, albumId, 0);
                    break;
                case Constant.OptionType.REPORT:
                    goToReportFragment();
                    break;

                case Constant.OptionType.CHANGE_PHOTO:
                case Constant.OptionType.UPLOAD_PHOTO:
                    gToAlbumImage(Constant.URL_EDIT_RECIPE_PHOTO, album.getRecipeImages().getMain(), getStrings(R.string.TITLE_EDIT_BLOG_PHOTO));
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
        } else if (activity.taskPerformed == Constant.FormType.TYPE_RECIPE_EDIT) {
            activity.taskPerformed = 0;
            callMusicAlbumApi(1);
        }

    }

    private void goToReportFragment() {
        String guid = album.getResourceType() + "_" + album.getRecipeId();
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid)).addToBackStack(null).commit();
    }

    private void gToAlbumImage(String url, String main, String title) {
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.KEY_RECIPE_ID, album.getRecipeId());
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

}

package com.sesolutions.ui.events;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import com.sesolutions.http.ApiController;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.event.HostResponse;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.customviews.NestedWebView;
import com.sesolutions.ui.dashboard.ReportSpamFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FlowLayout;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import droidninja.filepicker.FilePickerBuilder;

public class OffSiteHostViewFragment extends CommentLikeHelper implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private static final int UPDATE_UPPER_LAYOUT = 101;
    public static final int CAMERA_PIC_REQUEST = 7080;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    private HostResponse.Result result;


    public ImageView ivUserImage;
    //public TextView tvAlbumTitle;
    public TextView tvUserTitle;

    private NestedWebView webview;

    private int albumId;
    private boolean isCameraOptionSelected;
    private CollapsingToolbarLayout collapsingToolbar;
    private boolean openComment;
    private View vItem;
    private Bundle bundle;
    /* private String hostName;
     private String imageUrl;*/
    private int mHostId;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!openComment && bundle != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivUserImage.setTransitionName(bundle.getString(Constant.Trans.IMAGE));
            // tvTitle.setTransitionName(bundle.getString(Constant.Trans.TEXT));
            // tvTitle.setText(bundle.getString(Constant.Trans.IMAGE));
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
                        .into(ivUserImage);
            } catch (Exception e) {
                CustomLog.e(e);
            }
            //    Util.showImageWithGlide(ivAlbumImage, bundle.getString(Constant.Trans.IMAGE_URL), context);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        setHasOptionsMenu(true);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_host_view, container, false);
        applyTheme(v);
        init();
        callApi(true);
        callBottomCommentLikeApi(mHostId, Constant.ResourceType.SES_EVENT_HOST, Constant.URL_VIEW_COMMENT_LIKE);
        if (openComment) {
            //change value of openComment otherwise it prevents coming back from next screen
            openComment = false;
            goToCommentFragment(mHostId, Constant.ResourceType.SES_EVENT_HOST);
        }
        return v;
    }

    private void callApi(boolean showLoader) {
        if (isNetworkAvailable(context)) {
            if (showLoader)
                showBaseLoader(true);
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_HOST_ID, mHostId);

            new ApiController(Constant.URL_HOST_VIEW, map, context, this, -1).execute();
        } else {
            notInternetMsg(v);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (activity.taskPerformed == Constant.FormType.EDIT_HOST) {
            activity.taskPerformed = 0;
            callApi(false);
        }
    }

    private void init() {
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        //  ((TextView) v.findViewById(R.id.tvTitle)).setText(" ");

        //  mScrollView = v.findViewById(R.id.mScrollView);
        //  v.findViewById(R.id.llContent).setBackgroundColor(Color.parseColor(Constant.foregroundColor));
        TextView tvTitle = v.findViewById(R.id.tvTitle);
        tvTitle.setText(" ");
        // v.findViewById(R.id.ivShare).setOnClickListener(this);
        v.findViewById(R.id.ivSearch).setOnClickListener(this);
        ((ImageView) v.findViewById(R.id.ivSearch)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.vertical_dots));
        v.findViewById(R.id.ivSearch).setRotation(90);
        //    tvAlbumTitle = v.findViewById(R.id.tvAlbumTitle);
        setupWebView();

        tvUserTitle = v.findViewById(R.id.tvUser);
        ivUserImage = v.findViewById(R.id.ivUser);

        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.cvPost).setOnClickListener(this);
        // v.findViewById(R.id.ivShare).setOnClickListener(this);
        //  v.findViewById(R.id.ivOption).setOnClickListener(this);

       /* v.findViewById(R.id.ivStar1).setOnClickListener(this);
        v.findViewById(R.id.ivStar2).setOnClickListener(this);
        v.findViewById(R.id.ivStar3).setOnClickListener(this);
        v.findViewById(R.id.ivStar4).setOnClickListener(this);
        v.findViewById(R.id.ivStar5).setOnClickListener(this);
*/
        v.findViewById(R.id.llLike).setOnClickListener(this);
        v.findViewById(R.id.llComment).setOnClickListener(this);
        v.findViewById(R.id.llFavorite).setOnClickListener(this);

        v.findViewById(R.id.ivFb).setOnClickListener(this);
        v.findViewById(R.id.ivWeb).setOnClickListener(this);
        v.findViewById(R.id.ivTwitter).setOnClickListener(this);
        v.findViewById(R.id.ivGoogle).setOnClickListener(this);


    }

    public void showHideOptionIcon() {
        try {
            // v.findViewById(R.id.ivShare).setVisibility(View.VISIBLE);
            v.findViewById(R.id.ivSearch).setVisibility((result.getMenus() != null && result.getMenus().size() > 0) ? View.VISIBLE : View.GONE);
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
            v.findViewById(R.id.rlMain).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(result.getHostName());

//            collapsingToolbar.setTitle(album.getTitle());
            // setRatingStars();

            //  Util.showImageWithGlide(ivCoverPhoto, album.getBlogImages().getMain(), context/*, R.drawable.placeholder_square*/);
            Util.showImageWithGlide(ivUserImage, result.getImage(), context, R.drawable.placeholder_square);

            tvUserTitle.setText(result.getHostName());
            //tvAlbumDate.setText(Util.changeDateFormat(context,album.getCreationDate()));

            webview.loadData(result.getDescription(), "text/html", "UTF-8");

            setUserDetail();
            v.findViewById(R.id.cvPost).setOnClickListener(this);
            updateFollowLayout();

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void updateFollowLayout() {
        if (result.canFollow()) {
            v.findViewById(R.id.cvPost).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvPost)).setText(result.isContentFollow() ? R.string.unfollow : R.string.follow);
            ((ImageView) v.findViewById(R.id.ivPost)).setImageDrawable(ContextCompat.getDrawable(context, result.isContentFollow() ? R.drawable.unfollow : R.drawable.follow));
        } else {
            v.findViewById(R.id.cvPost).setVisibility(View.GONE);
        }
    }

    private void setUserDetail() {
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);

        FlowLayout flStats = v.findViewById(R.id.flStats);
        flStats.removeAllViews();

        if (!TextUtils.isEmpty(result.getHostedEvent())) {
            View view2 = getLayoutInflater().inflate(R.layout.layout_stats_text, flStats, false);
            ((TextView) view2.findViewById(R.id.tvStats)).setText(Constant.FontIcon.CALENDAR);
            ((TextView) view2.findViewById(R.id.tvStats)).setTypeface(iconFont);
            ((TextView) view2.findViewById(R.id.tvText)).setText(result.getHostedEvent());
            flStats.addView(view2);
        }
        //add follow count

        if (!TextUtils.isEmpty(result.getFollowCount())) {
            View view2 = getLayoutInflater().inflate(R.layout.layout_stats_text, flStats, false);
            ((TextView) view2.findViewById(R.id.tvStats)).setText(Constant.FontIcon.MEMBERS);
            ((TextView) view2.findViewById(R.id.tvStats)).setTypeface(iconFont);
            ((TextView) view2.findViewById(R.id.tvText)).setText(result.getFollowCount());
            flStats.addView(view2);
        }


        //add view count
        if (!TextUtils.isEmpty(result.getViewCount())) {
            View view1 = getLayoutInflater().inflate(R.layout.layout_stats_text, flStats, false);
            ((TextView) view1.findViewById(R.id.tvStats)).setText(Constant.FontIcon.VIEWS);
            ((TextView) view1.findViewById(R.id.tvStats)).setTypeface(iconFont);
            ((TextView) view1.findViewById(R.id.tvText)).setText(result.getViewCount());
        }


        //add favourite count
        if (!TextUtils.isEmpty(result.getFavourite_count())) {
            View view3 = getLayoutInflater().inflate(R.layout.layout_stats_text, flStats, false);
            ((TextView) view3.findViewById(R.id.tvStats)).setText(Constant.FontIcon.HEART);
            ((TextView) view3.findViewById(R.id.tvStats)).setTypeface(iconFont);
            ((TextView) view3.findViewById(R.id.tvText)).setText(result.getFavourite_count());
            flStats.addView(view3);
        }

        //add host email
        if (!TextUtils.isEmpty(result.getHost_email())) {
            View view4 = getLayoutInflater().inflate(R.layout.layout_stats_text, flStats, false);
            ((TextView) view4.findViewById(R.id.tvStats)).setText(Constant.FontIcon.MAIL);
            ((TextView) view4.findViewById(R.id.tvStats)).setTypeface(iconFont);
            ((TextView) view4.findViewById(R.id.tvText)).setText(result.getHost_email());
            flStats.addView(view4);
        }

        //add host phone
        if (!TextUtils.isEmpty(result.getHost_phone())) {
            View view = getLayoutInflater().inflate(R.layout.layout_stats_text, flStats, false);
            ((TextView) view.findViewById(R.id.tvStats)).setText(Constant.FontIcon.PHONE);
            ((TextView) view.findViewById(R.id.tvStats)).setTypeface(iconFont);
            ((TextView) view.findViewById(R.id.tvText)).setText(result.getHost_phone());
            flStats.addView(view);
        }

        v.findViewById(R.id.ivFb).setVisibility(!TextUtils.isEmpty(result.getFacebook_url()) ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.ivWeb).setVisibility(!TextUtils.isEmpty(result.getWebsite_url()) ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.ivTwitter).setVisibility(!TextUtils.isEmpty(result.getTwitter_url()) ? View.VISIBLE : View.GONE);
        v.findViewById(R.id.ivGoogle).setVisibility(!TextUtils.isEmpty(result.getGoogleplus_url()) ? View.VISIBLE : View.GONE);


        applyTheme(flStats);
    }

    private void showShareDialog(String msg) {
        try {

            final Share share = new Share();
            share.setTitle(result.getHostName());
            share.setDescription(result.getDescription());
            share.setImageUrl(result.getImage());

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
                    shareInside(share, true);
                }
            });

            bShareOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    shareOutside(share);
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
                case R.id.cvPost:
                    if (isNetworkAvailable(context)) {
                        Map<String, Object> req = new HashMap<>();
                        req.put(Constant.KEY_ID, mHostId);
                        req.put(Constant.KEY_TYPE, Constant.ResourceType.SES_EVENT_HOST);
                        if (result.isContentFollow()) {
                            req.put("contentId", result.getFollow_id());
                        }

                        result.toggleFollow();
                        updateFollowLayout();

                        new ApiController(Constant.URL_HOST_FOLLOW, req, context, this, -3).execute();
                    } else {
                        notInternetMsg(v);
                    }
                    break;

                case R.id.ivShare:
                    showShareDialog(Constant.TXT_SHARE_FEED);
                    break;

                case R.id.ivSearch:
                    showPopup(result.getMenus(), view, 10);
                    break;

                case R.id.ivFb:
                    openWebView(result.getFacebook_url(), result.getHostName());
                    break;
                case R.id.ivWeb:
                    openWebView(result.getWebsite_url(), result.getHostName());
                    break;
                case R.id.ivTwitter:
                    openWebView(result.getTwitter_url(), result.getHostName());
                    break;
                case R.id.ivGoogle:
                    openWebView(result.getGoogleplus_url(), result.getHostName());
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


    public static OffSiteHostViewFragment newInstance(int mHostId) {
        OffSiteHostViewFragment frag = new OffSiteHostViewFragment();
        // frag.hostName = vo.getHostName();
        // frag.imageUrl = vo.getImage();
        frag.mHostId = mHostId;
        frag.openComment = false;
        return frag;
    }

   /* public static OffSiteHostViewFragment newInstance(int albumId, Bundle bundle) {
        OffSiteHostViewFragment frag = new OffSiteHostViewFragment();
        frag.albumId = albumId;
        frag.bundle = bundle;
        return frag;
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
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_HOST_ID, mHostId);
                    openFormFragment(Constant.FormType.EDIT_HOST, map, Constant.URL_HOST_EDIT);
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

    public void showDeleteDialog() {
        try {

            final Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_HOST_ID, mHostId);

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
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_HOST);
            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    new ApiController(Constant.URL_HOST_DELETE, map, context, OffSiteHostViewFragment.this, -2).execute();

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


    private void goToReportFragment() {
        String guid = Constant.ResourceType.SES_EVENT_HOST + "_" + mHostId;
        fragmentManager.beginTransaction().replace(R.id.container, ReportSpamFragment.newInstance(guid)).addToBackStack(null).commit();
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

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        switch (object1) {
            case -1:
                hideBaseLoader();
                String response = "" + screenType;
                if (null != response) {
                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                    if (err.isSuccess()) {
                        HostResponse resp = new Gson().fromJson(response, HostResponse.class);
                        result = resp.getResult();
                        updateUpperLayout();

                    } else {
                        Util.showSnackbar(v, err.getErrorMessage());
                        goIfPermissionDenied(err.getError());
                    }
                } else {
                    somethingWrongMsg(v);
                }
                break;
            case -3:
                hideBaseLoader();
                try {
                    response = "" + screenType;
                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                    if (err.isSuccess()) {
                        result.setFollow_id(new JSONObject(response).optJSONObject("result").optInt("follow_id"));
                    } else {
                        Util.showSnackbar(v, err.getErrorMessage());
                        //in case of error revert follow value changes
                        result.toggleFollow();
                        updateFollowLayout();
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                    somethingWrongMsg(v);
                    //in case of error revert follow value changes
                    result.toggleFollow();
                    updateFollowLayout();
                }
                break;
            case -2:

                break;
        }
        return super.onItemClicked(object1, screenType, postion);
    }


}

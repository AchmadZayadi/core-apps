package com.sesolutions.ui.clickclick;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.SnapHelper;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.animate.Techniques;
import com.sesolutions.http.ApiController;
import com.sesolutions.http.HttpImageRequestHandler;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.listeners.onLoadCommentsListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.FeedLikeResponse;
import com.sesolutions.responses.comment.CommentData;
import com.sesolutions.responses.comment.CommentResponse;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.videos.Result;
import com.sesolutions.responses.videos.VideoBrowse;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.clickclick.notification.FollowandUnfollow;
import com.sesolutions.ui.clickclick.notification.TrendingAdapter;
import com.sesolutions.ui.comment.EditDialogFragment;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.customviews.CircularProgressBar;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.ui.video.VideoHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.sesolutions.utils.VibratorUtils;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.jzvd.Jzvd2;
import cn.jzvd.JzvdStd2;


public class FollowingFragment extends VideoHelper implements View.OnClickListener, OnLoadMoreListener, onLoadCommentsListener {

    private RecyclerView rvTiktok;
    private AdapterClickClickRecyclerView mAdapter;
    private ViewPagerLayoutManager mViewPagerLayoutManager;
    private int mCurrentPosition = -1;
    private boolean isLoading;
    public int REQ_LOAD_MORE = 2;
    private BottomSheetDialog dialog;
    private int videoPos = 0;
    private static final int REQ_LIKE = 100;
    private static final int REQ_CODE_EDIT = 125;
    public String searchKey;
    public Result result;
    public Result CreatorsResult;
    public AppCompatImageView ivPost;
    public OnUserClickedListener<Integer, Object> parent;
    public com.sesolutions.responses.comment.Result commentResult;
    public ProgressBar pb;
    public int loggedinId;
    public List<Videos> profileList;
    public RecyclerView recycleViewInfo;
    public int videoListSize;
    public AppCompatTextView tvCommentCount;
    public RecyclerView commentRecyclerView;
    public ClickClickCommentAdapter commentAdapter;
    int RESULTPROFILEVIEW=930;
    private List<CommentData> commentList;
    public TextInputEditText etBody;
    public int creatorsSize;
    private int currentpos;
    public ProgressBar pbHeaderProgress;
    private int totComments;
    private boolean currentVideo = false;
    public TrendingAdapter trendingAdapter;
    ProgressBar progressbarId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_following, container, false);
        applyTheme(v);
        initScreenData();
        return v;
    }

    public void init() {
        videoList = new ArrayList<>();
        rvTiktok = v.findViewById(R.id.recyclerview);
        progressbarId = v.findViewById(R.id.progressbarId);
        progressbarId.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mCurrentPosition!=0){
            autoPlayVideo(mCurrentPosition);
        }
    }

    Share sharelink_data=null;
    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        switch (object1) {
            case Constant.Events.PROFILE:
                goTo(Constant.GoTo.VIEW_PROFILE, Constant.KEY_ID, postion);
                break;
            case Constant.Events.TTS_POPUP_CLOSED:
                profileList.remove(postion);
                trendingAdapter.notifyItemRemoved(postion);
                break;

            case Constant.Events.SHARE_FEED2:
                Log.e("Data","share");
                sharelink_data=videoList.get(postion).getShare();
                askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                break;

            case Constant.Events.CONTENT_EDIT:
                if (null != screenType) {
                    callEditCommentAPI((String) screenType, postion);
                }
                break;
            case Constant.Events.DELETE_COMMENT:
                showDeleteDialog(commentList.get(postion).getCommentId(), postion);
                break;
            case Constant.Events.TICK_COMMENT_EDIT:
                EditDialogFragment.newInstance(this, postion, commentList.get(postion)).show(fragmentManager, "comment");
                break;
            case Constant.Events.COMMENT:
//                goTo(Constant.GoTo.TICK_COMMENT, Constant.KEY_RESOURCE_ID, postion);
                showBottomSheetDialog();
                break;
            case Constant.Events.TICK_GO_TO_CHANNEL:
                /*Intent intent2 = new Intent(getActivity(), CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.TICK_VIEW_CHANNEL3);
                int channelid=videoList.get(postion).getOwnerId();
                if(channelid!=0){
                    intent2.putExtra(Constant.KEY_CHANNEL_ID, channelid);
                    startActivity(intent2);
                }*/
                Intent intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.TICK_VIEW_CHANNEL3);
                intent2.putExtra(Constant.KEY_CHANNEL_ID, postion);
                startActivityForResult(intent2,RESULTPROFILEVIEW);

                break;
            case Constant.Events.TICK_GO_TO_CHANNEL2:
                Intent intent3 = new Intent(getActivity(), CommonActivity.class);
                intent3.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.TICK_VIEW_CHANNEL3);
                intent3.putExtra(Constant.KEY_CHANNEL_ID, profileList.get(postion).getOwnerId());
                startActivity(intent3);
                break;
            case Constant.Events.MEMBER_FOLLOW2:
                callFollowApi(postion);
                break;
            case Constant.Events.MEMBER_FOLLOW:
                callFollowApi2(postion);
                break;
            case Constant.Events.TICK_VIDEO_LIKE:
                callLikeApi(REQ_LIKE, postion, Constant.URL_MUSIC_LIKE, Integer.parseInt(screenType.toString()));
                break;
            case Constant.Events.SHARE_FEED:
                showShareDialog(videoList.get(postion).getShare());
                break;
            case Constant.Events.REPORT:
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                goToReport(commentList.get(postion).getCommentId());
                break;
            case Constant.Events.LIKE_COMMENT:
                if (Integer.parseInt("" + screenType) > -1) {
                    callLikeUnlikeApi(Constant.URL_LIKE_COMMENT, commentList.get(postion).getCommentId(), postion);
                } else {
                    callLikeUnlikeApi(Constant.URL_UNLIKE_COMMENT, commentList.get(postion).getCommentId(), postion);
                }
                break;
        }
        return super.onItemClicked(object1, screenType, postion);
    }

    public void askForPermission(String permission) {
        try {
            new TedPermission(context)
                    .setPermissionListener(permissionlistener)
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
                showShareDialog(sharelink_data);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };



    private ProgressDialog pDialog;
    private String videoPath;
    public void showShareDialog(final Share share) {
        try {
            if (null != bottomSheetDialog && bottomSheetDialog.isShowing()) {
                bottomSheetDialog.dismiss();
            }

            View view = getLayoutInflater().inflate(R.layout.bottomsheet_share2, null);

            bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
//            bottomSheetDialog = ProgressDialog.show(context, "", "", true);
//            progressDialog.setCanceledOnTouchOutside(true);
//            progressDialog.setCancelable(true);
            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            new ThemeManager().applyTheme((ViewGroup) bottomSheetDialog.findViewById(R.id.rlDialogMain), context);

            UserMaster userVo = SPref.getInstance().getUserMasterDetail(context);
            Util.showImageWithGlide(view.findViewById(R.id.ivProfile), userVo.getPhotoUrl(), context);
            ((TextView) view.findViewById(R.id.tvTitleName)).setText(userVo.getDisplayname());
            ((AppCompatImageView) view.findViewById(R.id.ivExpand)).setVisibility(View.GONE);

            AppCompatEditText etShare = bottomSheetDialog.findViewById(R.id.etShare);
            MaterialButton bShareIn = bottomSheetDialog.findViewById(R.id.bShare);
            AppCompatTextView tvShareMore = bottomSheetDialog.findViewById(R.id.tvShareMore);
            // boolean isLoggedIn = SPref.getInstance().isLoggedIn(context);
            bShareIn.setVisibility(SPref.getInstance().isLoggedIn(context) ? View.VISIBLE : View.GONE);
            view.findViewById(R.id.llShareWhatsapp).setVisibility(View.VISIBLE);
            view.findViewById(R.id.llInstagram).setVisibility(View.GONE);
            view.findViewById(R.id.llDownload).setVisibility(View.VISIBLE);
            if ("1".equals(share.getSetting())) {
                bShareIn.setVisibility(View.VISIBLE);
                tvShareMore.setVisibility(View.GONE);
            }

            tvShareMore.setText(R.string.TXT_SHARE_OUTSIDE);
//            bShareIn.setText(getString(R.string.txt_share_on, AppConfiguration.SHARE));

            bottomSheetDialog.show();
            bShareIn.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
//                shareInside(share, true);
                callShareSubmitApi(share, etShare.getText().toString());
            });

//            view.findViewById(R.id.llSendToMessage).setOnClickListener(v -> {
//                bottomSheetDialog.dismiss();
//                goToComposeMessageFragment();
//            });

            view.findViewById(R.id.llShareMore).setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                shareOutside(share);
            });
            view.findViewById(R.id.llShareWhatsapp).setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                boolean installed = appInstalledOrNot("com.whatsapp");
                if(installed) {
                    videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/videos/" + videoList.get(videoPos).getTitle() + ".mp4";
                    downloadVideo(videoList.get(videoPos).getIframeURL(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/videos/",
                            videoList.get(videoPos).getTitle() + ".mp4", 0);
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse("https://play.google.com/store/apps/details?id=" + "com.whatsapp"));
                    try {
                        startActivity(new Intent(intent)
                                .setPackage("com.whatsapp"));
                    } catch (android.content.ActivityNotFoundException exception) {
                        startActivity(intent);
                    }
                }


            });
            view.findViewById(R.id.llDownload).setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/videos/" + videoList.get(videoPos).getTitle() + ".mp4";
                downloadVideo(videoList.get(videoPos).getIframeURL(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/videos/",
                        videoList.get(videoPos).getTitle() + ".mp4", 1);
            });
            view.findViewById(R.id.llInstagram).setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/videos/" + videoList.get(videoPos).getTitle() + ".mp4";
                downloadVideo(videoList.get(videoPos).getIframeURL(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/videos/",
                        videoList.get(videoPos).getTitle() + ".mp4", 2);
            });



        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void downloadVideo(String url, String dirPath, String fileName, int type) {
        int downloadId = PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        try {
                            pDialog = ProgressDialog.show(activity, "", "", true);
                            pDialog.setCancelable(false);
                            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            pDialog.setContentView(R.layout.dialog_progress_text);
                            ((TextView) pDialog.findViewById(R.id.tvText)).setTextColor(Color.WHITE);
                            CircularProgressBar circularProgressBar = pDialog.findViewById(R.id.cpb);
                            circularProgressBar.setColor(Color.parseColor(Constant.colorPrimary));
                            circularProgressBar.setBackgroundColor(Color.parseColor(Constant.menuButtonActiveTitleColor.replace("#", "#67")));
                            circularProgressBar.setProgressWithAnimation(0, 0); // Default duration = 1500ms
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                        ((Activity) activity).runOnUiThread(() -> {
                            try {
                                if (null != pDialog) {


                                    double ratio = progress.currentBytes / (double) progress.totalBytes;
                                    DecimalFormat percentFormat = new DecimalFormat("#.#%");

                                    Log.e("per:-", "" + percentFormat);

                                    ((TextView) pDialog.findViewById(R.id.tvText)).setText("" + percentFormat.format(ratio));
                                    Log.e("" + progress.currentBytes, "" + progress.totalBytes);
                                    ((CircularProgressBar) pDialog.findViewById(R.id.cpb)).setProgressWithAnimation((float) ratio, 1800);
                                }
                            } catch (Exception e) {
                                CustomLog.e(e);
                            }
                        });

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        if (type == 0) {
                            Util.showSnackbar(v, "Processed");
                            if (pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                            shareVideoWhatsApp();
                        } else if (type == 1) {
                            Util.showSnackbar(v, "Video Downloaded successfully..");

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                showNotification(videoPath);
                            }
                            videoPath = null;
                            if (pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                        } else if (type == 2) {
                            Util.showSnackbar(v, "Processed");
                            if (pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                            shareVideoInstagram();
                        }


                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
    }
    public void shareVideoWhatsApp() {
        File file = new File(videoPath);
        Uri uri = Uri.fromFile(file);
        videoPath = null;
        Intent videoshare = new Intent(Intent.ACTION_SEND);
        videoshare.setType("*/*");
        videoshare.setPackage("com.whatsapp");
        videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        videoshare.putExtra(Intent.EXTRA_STREAM, uri);
        videoshare.putExtra(Intent.EXTRA_TEXT, "This video has been uploaded to SNS - Advanced, install SNS - Advanced to enjoy more videos!");
        try {
            startActivity(videoshare);
        } catch (android.content.ActivityNotFoundException ex) {
            Util.showSnackbar(v, "Whatsapp has not been installed.");
        }
    }
    public void shareVideoInstagram() {
        File file = new File(videoPath);
        Uri uri = Uri.fromFile(file);
        videoPath = null;
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("video/*");
        sharingIntent.setPackage("com.instagram.android");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            startActivity(Intent.createChooser(sharingIntent, "Share Video "));
        } catch (android.content.ActivityNotFoundException ex) {
            Util.showSnackbar(v, "Instagram has not been installed.");
        }
    }
    public void showNotification(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        File file = new File(path); // set your audio path
        intent.setDataAndType(Uri.fromFile(file), "video/*");
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        int notifyID = 1;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channels);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = null;
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            Notification notification = new Notification.Builder(context)
                    .setContentTitle("Video Downloaded Successfully.")
                    .setContentText(videoList.get(videoPos).getTitle() + " has been downloaded successfully, click here to view.")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId(CHANNEL_ID)
                    .setContentIntent(pIntent)
                    .build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager mNotificationManager =
                    (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(notifyID, notification);


        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "0");
            NotificationManager notifManager = (NotificationManager) activity.getSystemService
                    (Context.NOTIFICATION_SERVICE);

            builder.setContentTitle("Video Downloaded Successfully.")
                    .setSmallIcon(R.mipmap.ic_launcher) // required
                    .setContentText(videoList.get(videoPos).getTitle() + " has been downloaded successfully, click here to view.")  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pIntent)
                    .setGroupSummary(true);

            Notification notification = builder.build();
            notifManager.notify(notifyID, notification);
        }

    }

    public void goToReport(int id) {
        String guid = Constant.ResourceType.COMMENT + "_" + id;
        Intent intent2 = new Intent(activity, CommonActivity.class);
        intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.REPORT_COMMENT);
        intent2.putExtra(Constant.KEY_GUID, guid);
        startActivity(intent2);
    }

    private void callLikeUnlikeApi(final String url, int commentId, final int position) {

        if (isNetworkAvailable(context)) {
            try {


                HttpRequestVO request = new HttpRequestVO(url);
                request.params.put("subjectid", videoList.get(videoPos).getVideoId());
                request.params.put("sbjecttype", "video");

                request.params.put(Constant.KEY_TYPE, 1);
                request.params.put(Constant.KEY_COMMENT_ID, commentId);

                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = msg -> {
                    try {
                        String response = (String) msg.obj;
                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            CommentResponse comResp = new Gson().fromJson(response, CommentResponse.class);

                            if (TextUtils.isEmpty(comResp.getError())) {
                                FeedLikeResponse res = new Gson().fromJson(response, FeedLikeResponse.class);
                                commentList.get(position).toggleLike();
                                commentAdapter.notifyItemChanged(position);
                                autoPlayVideo(videoPos);
                            } else {
                                Util.showSnackbar(v, comResp.getErrorMessage());
                                //revert the changes made in case og any error
                                commentList.get(position).toggleLike();
                                commentAdapter.notifyItemChanged(position);

                            }
                        }

                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception ignore) {
            }
        } else {
            notInternetMsg(v);
        }
    }

    public void showDeleteDialog(final int commentId, final int position) {
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
            tvMsg.setText(R.string.MSG_DELETE_COMMENT_CONFIRMATION);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.dismiss();
                    callDeleteApi(commentList.get(position).getCommentId(), position);

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

    private void setCommentsRecyclerView() {
        commentList = new ArrayList<>();
        commentRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        commentRecyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) commentRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        commentAdapter = new ClickClickCommentAdapter(commentList, context, this, this);
        commentRecyclerView.setAdapter(commentAdapter);
    }

    private void callDeleteApi(int commentId, final int position) {

        if (isNetworkAvailable(context)) {
            try {
                HttpRequestVO request = new HttpRequestVO(Constant.URL_DELETE_COMMENT);
                request.params.put(Constant.KEY_RESOURCE_ID, videoList.get(videoPos).getVideoId());
                request.params.put(Constant.KEY_COMMENT_ID, commentId);
                request.params.put(Constant.KEY_RESOURCES_TYPE, "video");


                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        try {


                            String response = (String) msg.obj;

                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                // response = response.replace("â\u0080\u0099", "'");
                                BaseResponse<Object> comResp = new Gson().fromJson(response, BaseResponse.class);
                                //  result = comResp.getResult();

                                if (TextUtils.isEmpty(comResp.getError())) {
                                    BaseResponse<String> res = new Gson().fromJson(response, BaseResponse.class);
                                    commentList.remove(position);
                                    String Comment = commentList.size() == 1 ? "comment" : "Komentar";
                                    tvCommentCount.setText(commentList.size() - 1 + " " + Comment);
                                    commentAdapter.notifyItemRemoved(position);
                                    commentAdapter.notifyDataSetChanged();
                                    Util.showSnackbar(v, res.getResult());
                                    // adapter.notifyItemChanged(position);
                                    totComments = commentList.size();
                                    if (totComments == 1) {
                                        tvCommentCount.setText(totComments + " Komentar");
                                    } else {
                                        tvCommentCount.setText(totComments + " Komentar");
                                    }
                                } else {
                                    Util.showSnackbar(v, comResp.getErrorMessage());
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

            } catch (Exception ignore) {

            }
        } else {
            notInternetMsg(v);
        }
    }

    public void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_create, null);

        dialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        commentRecyclerView = view.findViewById(R.id.recyclerView);
        pbHeaderProgress = view.findViewById(R.id.pbHeaderProgress);
        ivPost = view.findViewById(R.id.ivPost);
        pbHeaderProgress.setVisibility(View.VISIBLE);
        tvCommentCount = view.findViewById(R.id.tvCommentCount);

        setCommentsRecyclerView();

        callCommentsApi(1);
        //Recyclerview


        //Edit Comment
        etBody = view.findViewById(R.id.etBody);
        etBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = etBody.getText().toString();
                if (text.startsWith(" "))
                    etBody.setText(text.trim());

                if (etBody.getText().length() > 0) {
                    CustomLog.e("text:", "" + s.toString());
                    ivPost.setVisibility(View.VISIBLE);
                } else {
                    ivPost.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        view.findViewById(R.id.ivPost).setOnClickListener(v1 -> {
            submitCommentIfValid();
        });

    }

    private void submitCommentIfValid() {
        boolean isValid = false;
        Map<String, Object> params = new HashMap<>();
        String body = Objects.requireNonNull(etBody.getText()).toString();
        if (!TextUtils.isEmpty(body)) {
            params.put("body", body);
            isValid = true;
        }
        closeKeyboard();
        if (isValid) {
            callCreateCommentApi(params);
        } else {
            VibratorUtils.vibrate(context);
            startAnimation(ivPost, Techniques.SHAKE, 400);
        }
    }

    @Override
    public void onLoadMoreComments() {
        try {
            if (commentResult != null && !isLoading) {
                if (commentResult.getCurrentPage() < commentResult.getTotalPage()) {
                    callCommentsApi(REQ_LOAD_MORE);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callCreateCommentApi(final Map<String, Object> params) {

        if (isNetworkAvailable(context)) {
            final boolean[] isDummyCommentAdded = {false};
            try {
                if (params.containsKey("body")) {

                    UserMaster userVo = SPref.getInstance().getUserMasterDetail(context);
                    etBody.setText(Constant.EMPTY);
                    isDummyCommentAdded[0] = true;
                    commentList.add(0, new CommentData(
                            (String) params.get("body"),
                            userVo.getDisplayname(),
                            userVo.getPhotoUrl(),
                            Util.getCurrentdate(Constant.DATE_FROMAT_FEED),
                            true));
                    commentRecyclerView.smoothScrollToPosition(0);
                    showBaseLoader(false);
                    totComments = commentList.size();
                    if (totComments == 1) {
                        tvCommentCount.setText(totComments + " comment");
                    } else {
                        tvCommentCount.setText(totComments + " comments");
                    }
                } else {
                    showBaseLoader(true);
                }

                HttpRequestVO request = new HttpRequestVO(Constant.URL_CREATE_COMMENT);

                request.params.putAll(params);
                request.params.put(Constant.KEY_RESOURCE_ID, videoList.get(videoPos).getVideoId());
                request.params.put(Constant.KEY_ACTIVITY_ID, videoList.get(videoPos).getVideoId());
                request.params.put(Constant.KEY_RESOURCES_TYPE, "video");


                //request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = msg -> {
                    hideBaseLoader();
                    try {
                        String response = (String) msg.obj;
                        isLoading = false;
                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            // response = response.replace("â\u0080\u0099", "'");
                            BaseResponse<Object> comResp = new Gson().fromJson(response, BaseResponse.class);
                            //  result = comResp.getResult();

                            if (TextUtils.isEmpty(comResp.getError())) {

                                String itemComment = new JSONObject(response).getJSONObject("result").getJSONObject("comment_data").toString();
                                CommentData vo = new Gson().fromJson(itemComment, CommentData.class);

                                if (isDummyCommentAdded[0]) {
                                    commentList.get(0).updateObject(vo);
                                    videoList.get(videoPos).increaseCommentCount();
//                                    mAdapter.notifyItemChanged(videoPos);
                                    commentAdapter.notifyDataSetChanged();
                                } else {
                                    etBody.setText(Constant.EMPTY);
                                    commentList.add(0, vo);
                                    commentRecyclerView.smoothScrollToPosition(0);
                                    commentAdapter.notifyDataSetChanged();
                                }
                            } else {
                                Util.showSnackbar(v, comResp.getErrorMessage());
                            }
                        }

                    } catch (Exception e) {
                        hideBaseLoader();

                        CustomLog.e(e);
                    }

                    return true;
                };
                new HttpImageRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideBaseLoader();
            }
        } else {
            notInternetMsg(v);
        }
    }

    private void callFollowApi(final int position2) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FOLLOW_MEMBER);

                    request.params.put(Constant.KEY_USER_ID, profileList.get(position2).getUserId());
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                Log.e("folow response",""+response);

                                FollowandUnfollow followandUnfollow=new Gson().fromJson(response, FollowandUnfollow.class);
                                try {
                                    if(followandUnfollow.getResult().getMember().getFollow().getAction().equalsIgnoreCase("follow")){
                                        profileList.get(position2).setIsUserChannelFollow(false);
                                        trendingAdapter.notifyItemChanged(position2,profileList);
                                    }else {
                                      //  Util.showSnackbar(v, "user follow successfully.");
                                        profileList.get(position2).setIsUserChannelFollow(true);
                                        trendingAdapter.notifyItemChanged(position2,profileList);
                                        recycleViewInfo.smoothScrollToPosition(position2+1);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
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

    private void callFollowApi2(final int position) {

        showBaseLoader(false);
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CHANNEL_FOLLOW);
                    request.params.put(Constant.KEY_RESOURCE_ID, videoList.get(position).getChannelID());
                    request.params.put(Constant.KEY_CHANNEL_ID, videoList.get(position).getChannelID());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, "sesvideo_chanel");
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
                                        String message = json.getString(Constant.KEY_RESULT);
                                        if (message.equalsIgnoreCase("Channel follow successfully.")) {
                                            Util.showSnackbar(v, message);
                                        } else {
                                            Util.showSnackbar(v, message);
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

    private void callLikeApi(final int REQ_CODE, final int position, String url, final int vo) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {


                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    String resourceType = Constant.ResourceType.VIDEO;
                    request.params.put(Constant.KEY_RESOURCE_ID, vo);
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

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        JSONObject json = new JSONObject(response);
                                        Boolean islike = json.getJSONObject(Constant.KEY_RESULT).getBoolean("is_like");
                                        if (islike) {
                                            videoList.get(position).setContentLike(true);
                                        } else {
                                            videoList.get(position).setContentLike(false);
                                        }
//                                        mAdapter.notifyItemChanged(position);
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

    public void setRecyclerView() {
        try {
            mAdapter = new AdapterClickClickRecyclerView(videoList, getContext(), this, this, true);
            mViewPagerLayoutManager = new ViewPagerLayoutManager(getContext(), OrientationHelper.VERTICAL);
            rvTiktok.setLayoutManager(mViewPagerLayoutManager);
            rvTiktok.setAdapter(mAdapter);
            if (videoListSize > 0) {
                ((TextView) v.findViewById(R.id.tvNoData)).setText("No videos available right now.");
                v.findViewById(R.id.llNoData).setVisibility(View.GONE);
                mViewPagerLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
                    @Override
                    public void onInitComplete() {
                        //自动播放第一条
                        autoPlayVideo(0);
                    }

                    @Override
                    public void onPageRelease(boolean isNext, int position) {
                        if (mCurrentPosition == position) {
                            Jzvd2.releaseAllVideos();
                        }
                    }

                    @Override
                    public void onPageSelected(int position, boolean isBottom) {
                        if (mCurrentPosition == position) {
                            return;
                        }
                        autoPlayVideo(position);
                        mCurrentPosition = position;
                        videoPos = position;
                    }
                });

                rvTiktok.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                    @Override
                    public void onChildViewAttachedToWindow(View view) {

                    }

                    @Override
                    public void onChildViewDetachedFromWindow(View view) {
                        Jzvd2 jzvd = view.findViewById(R.id.videoplayer);
                        if (jzvd != null && Jzvd2.CURRENT_JZVD != null &&
                                jzvd.jzDataSource.containsTheUrl(Jzvd2.CURRENT_JZVD.jzDataSource.getCurrentUrl())) {
                            if (Jzvd2.CURRENT_JZVD != null && Jzvd2.CURRENT_JZVD.screen != Jzvd2.SCREEN_FULLSCREEN) {
                                Jzvd2.releaseAllVideos();
                            }
                        }
                    }
                });
            } else {
                rvTiktok.setVisibility(View.GONE);
                ((TextView) v.findViewById(R.id.tvNoData)).setText("No videos available right now.");
                v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void autoPlayVideo(int postion) {
        if (rvTiktok == null || rvTiktok.getChildAt(0) == null) {
            return;
        }
        JzvdStdClickClick player = rvTiktok.getChildAt(0).findViewById(R.id.videoplayer);
        if (player != null) {
            player.startVideoAfterPreloading();
        }
    }

    public void initScreenData() {
        callMusicAlbumApi(1);
        init();
//        setRecyclerView();
    }

    private void setRecyclerViewProfileInfo() {
        try {
            recycleViewInfo = v.findViewById(R.id.rvCategories);

            if (null != CreatorsResult.getCreators() && CreatorsResult.getCreators().size() > 0) {
              //  recycleViewInfo.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
                profileList = new ArrayList<>();
                profileList.addAll(CreatorsResult.getCreators());
                recycleViewInfo.setHasFixedSize(true);
                SnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(recycleViewInfo);
                LinearLayoutManager linearLayoutManager
                        = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                recycleViewInfo.setLayoutManager(linearLayoutManager);
                trendingAdapter = new TrendingAdapter(profileList, context, this, this);
                recycleViewInfo.setAdapter(trendingAdapter);
                final View child = linearLayoutManager.findViewByPosition(0);
                if (null != child) {
                    ((JzvdStd2) child.findViewById(R.id.videoplayer)).onStatePlaying();
                }
                recycleViewInfo.addOnScrollListener(new RecyclerView.OnScrollListener() {


                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (dx == 0 && dy == 0) {
                            final View child = linearLayoutManager.findViewByPosition(0);
                            if (null != child)
                                ((JzvdStd2) child.findViewById(R.id.videoplayer)).startButton.performClick();
                        }
                    }

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        try {
                        /*if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                            // Do something
                        } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                            // Do something
                        } else */
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                                // Do something
                                //GridLayoutManager layoutManager = ((GridLayoutManager)mRecyclerView.getLayoutManager());
                                int firstVisiblePosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                                final View child = linearLayoutManager.findViewByPosition(firstVisiblePosition);
                                if (null != child)
                                    ((JzvdStd2) child.findViewById(R.id.videoplayer)).startButton.performClick();
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                    }
                });

            } else {
                recycleViewInfo.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    if (req == REQ_LOAD_MORE) {
                        pb.setVisibility(View.VISIBLE);
                    } else {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_TICK_BROWSE);
                    Map<String, Object> map = activity.filteredMap;
                    if (null != map) {
                        request.params.putAll(map);
                    }
                    if (!TextUtils.isEmpty(searchKey)) {
                        request.params.put(Constant.KEY_SEARCH, searchKey);
                    }

                    if (loggedinId > 0) {
                        request.params.put(Constant.KEY_USER_ID, loggedinId);
                    }
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
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
                                        VideoBrowse resp = new Gson().fromJson(response, VideoBrowse.class);
                                        result = resp.getResult().getFollowing().getResult();
                                        CreatorsResult = resp.getResult();
                                        if (result.getVideos().size() > 0) {
                                            ((AppCompatTextView) v.findViewById(R.id.tvTrending)).setVisibility(View.GONE);
                                            ((AppCompatTextView) v.findViewById(R.id.tvTrending2)).setVisibility(View.GONE);
                                            videoList.addAll(result.getVideos());
                                            videoListSize = videoList.size();
                                            setRecyclerView();
                                            updateAdapter();
                                        } else {
                                            ((AppCompatTextView) v.findViewById(R.id.tvTrending)).setVisibility(View.VISIBLE);
                                            ((AppCompatTextView) v.findViewById(R.id.tvTrending2)).setVisibility(View.VISIBLE);
                                            setRecyclerViewProfileInfo();
                                            updateCreators();
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

    @Override
    public void onBackPressed() {
        if (Jzvd2.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    private void updateAdapter() {
        isLoading = false;
//        pb.setVisibility(View.GONE);
        //  swipeRefreshLayout.setRefreshing(false);

        if(adapter!=null){
            adapter.notifyDataSetChanged();
            runLayoutAnimation(rvTiktok);
        }

        ((TextView) v.findViewById(R.id.tvNoData)).setText("No videos available right now.");
        v.findViewById(R.id.llNoData).setVisibility(videoListSize > 0 ? View.GONE : View.VISIBLE);
        if (null != listener) {
            listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, -1);
            listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
        }


    }

    private void updateCreators() {
        trendingAdapter.notifyDataSetChanged();
      //  runLayoutAnimation(recycleViewInfo);
    }

    public static ClickClickFragment newInstance(OnUserClickedListener<Integer, Object> parent, String selectedScreen) {
        ClickClickFragment frag = new ClickClickFragment();
        frag.listener = parent;
        frag.selectedScreen = selectedScreen;
        return frag;
    }

    @Override
    public void onLoadMore() {
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
    public void onPause() {
        super.onPause();
        Jzvd2.releaseAllVideos();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callCommentsApi(final int req) {

        if (isNetworkAvailable(context)) {
            isLoading = true;

            try {
                HttpRequestVO request = new HttpRequestVO(Constant.URL_GET_COMMENT);
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                request.params.put(Constant.KEY_RESOURCE_ID, videoList.get(videoPos).getVideoId());
                // request.params.put(Constant.KEY_RESOURCES_TYPE, Constant.VALUE_RESOURCES_TYPE);
                request.params.put(Constant.KEY_RESOURCES_TYPE, "video");
                if (req == REQ_LOAD_MORE) {
                    request.params.put(Constant.KEY_PAGE, null != commentResult ? commentResult.getNextPage() : 1);
                } else {
                    request.params.put(Constant.KEY_PAGE, 1);
                }
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = msg -> {
                    hideBaseLoader();
                    try {
                        String response = (String) msg.obj;
                        isLoading = false;
                        pbHeaderProgress.setVisibility(View.GONE);
                        CustomLog.e("response_comments", "" + response);
                        if (response != null) {
                            CommentResponse comResp = new Gson().fromJson(response, CommentResponse.class);
                            commentResult = comResp.getResult();

                            if (TextUtils.isEmpty(comResp.getError())) {
                                if (null != comResp.getResult().getCommentData()) {
                                    commentList.addAll(comResp.getResult().getCommentData());
                                    totComments = commentList.size();
                                }
//                                if (result.getCurrentPage() == result.getTotalPage() && req == REQ_LOAD_MORE) {
//                                    Collections.reverse(commentList);
//                                }
                                tvCommentCount.setVisibility(View.VISIBLE);
                                if (totComments == 1) {
                                    tvCommentCount.setText(comResp.getResult().getTotal() + " comment");
                                } else {
                                    tvCommentCount.setText(comResp.getResult().getTotal() + " comments");
                                }

                                commentAdapter.notifyDataSetChanged();

                                runLayoutAnimation(commentRecyclerView);
                            } else {
                                Util.showSnackbar(v, comResp.getErrorMessage());
                                goIfPermissionDenied(comResp.getError());
                            }
                        }
                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception e) {
                hideBaseLoader();
            }
        } else {
            notInternetMsg(v);
        }
    }

    private void callEditCommentAPI(String value, int postion) {

        if (isNetworkAvailable(context)) {
            commentList.get(postion).setBody(value);
            commentAdapter.notifyItemChanged(postion);
            Map<String, Object> map = new HashMap<>();
            map.put(Constant.KEY_RESOURCES_TYPE, "video");
            map.put(Constant.KEY_RESOURCE_ID, videoList.get(videoPos).getVideoId());
            map.put(Constant.KEY_BODY, value);
            map.put(Constant.KEY_COMMENT_ID, commentList.get(postion).getCommentId());

            new ApiController(Constant.URL_EDIT_COMMENT, map, context, this, REQ_CODE_EDIT).setExtraKey(postion).execute();
        } else {
            notInternetMsg(v);
        }
    }
}

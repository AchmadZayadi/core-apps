package com.sesolutions.ui.clickclick;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.room.util.FileUtil;

import com.danikula.videocache.HttpProxyCacheServer;
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
import com.ixuea.android.downloader.DownloadService;
import com.ixuea.android.downloader.callback.DownloadListener;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.android.downloader.exception.DownloadException;
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
import com.sesolutions.ui.clickclick.me.OtherFragment;
import com.sesolutions.ui.clickclick.music.SimillerACtivity;
import com.sesolutions.ui.clickclick.notification.FollowandUnfollow;
import com.sesolutions.ui.comment.EditDialogFragment;
import com.sesolutions.ui.common.BaseResponse;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.common.MainApplication;
import com.sesolutions.ui.customviews.CircularProgressBar;
import com.sesolutions.ui.customviews.VideoEnabledWebView;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import cn.jzvd.Jzvd2;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ClickClickFragment extends VideoHelper implements View.OnClickListener, OnLoadMoreListener, onLoadCommentsListener {

    private RecyclerView rvTiktok;
    private AdapterClickClickRecyclerView mAdapter;
    private ViewPagerLayoutManager mViewPagerLayoutManager;
    private int mCurrentPosition = -1;
    private int videoPos = 0;
    private boolean isLoading;
    public int REQ_LOAD_MORE = 2;
    public String searchKey;
    private static final int REQ_LIKE = 100;
    public Result result;
    public com.sesolutions.responses.comment.Result commentResult;
    public OnUserClickedListener<Integer, Object> parent;
    public ProgressBar pb;
    public int loggedinId;
    private LinearLayout llFavorite;
    public int videoListSize;
    public boolean fromNotification = false;
    public boolean fromD = false;
    public boolean isPos = false;
    public boolean isSearch = false;
    private static final int REQ_CODE_EDIT = 125;
    public int position;
    public int ActPos;
    private HttpProxyCacheServer proxy;
    private String videoPath;
    private BottomSheetDialog dialog;
    public int startposition;
    public VideoEnabledWebView webView;
    public TextInputEditText etBody;
    public RecyclerView commentRecyclerView;
    public ProgressBar pbHeaderProgress;
    public AppCompatTextView tvCommentCount;
    public AppCompatImageView ivPost;
    public ClickClickCommentAdapter commentAdapter;
    private List<CommentData> commentList;
    private JzvdStdClickClick player;
    private int currentpos;
    private int totComments;
    private boolean currentVideo = false;
    int menuTitleActiveColor;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_tiktok, container, false);
        menuTitleActiveColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
        applyTheme(v);
        initScreenData();
        //setRecyclerView();
        return v;
    }




   @Override
    public void onResume() {
        super.onResume();
        if(mCurrentPosition!=0){
            autoPlayVideo(mCurrentPosition);
        }
       if(activity.currentFragment!=null){
           Log.e("CurrentFragmentNowD",""+activity.currentFragment);
       }
    }




    public static ClickClickFragment newInstance(List<Videos> list, Boolean fromdiscover) {
        ClickClickFragment fragment = new ClickClickFragment();
        fragment.videoList = list;
        fragment.fromD = fromdiscover;
        return fragment;
    }

    public static ClickClickFragment newInstance(List<Videos> list, Boolean fromdiscover, int currentpos, boolean currentVideo) {
        ClickClickFragment fragment = new ClickClickFragment();
        fragment.videoList = list;
        fragment.fromD = fromdiscover;
         fragment.currentpos = currentpos;
        fragment.currentVideo = currentVideo;
        return fragment;
    }

    public static ClickClickFragment newInstance(Videos video, Boolean fromNotification) {
        ClickClickFragment fragment = new ClickClickFragment();
        fragment.video = video;
        fragment.fromNotification = fromNotification;
        return fragment;
    }

    public static ClickClickFragment newInstance(List<Videos> list, Boolean fromdiscover, Boolean isPos, int pos) {
        ClickClickFragment fragment = new ClickClickFragment();
                            fragment.videoList = list;
        fragment.fromD = fromdiscover;
        fragment.isPos = isPos;
        fragment.position = pos;
        return fragment;
    }

    public static ClickClickFragment newInstance(Boolean fromdiscover, Boolean isSearch, int pos) {
        ClickClickFragment fragment = new ClickClickFragment();
        fragment.fromD = fromdiscover;
        fragment.isSearch = isSearch;
        fragment.ActPos = pos;
        return fragment;
    }

    public void init() {
        if (!fromD) {
            videoList = new ArrayList<>();
            commentList = new ArrayList<>();
        }
        rvTiktok = v.findViewById(R.id.recyclerview);
    }

    public void pause() {
        if (player != null) {
            player.pauseVideo();
        }
    }

    public void play() {
        if (player != null) {
            player.resumeVideo();
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
                        hideBaseLoader();
                        String response = (String) msg.obj;
                        CustomLog.e("repsonse1", "" + response);
                        if (response != null) {
                            CommentResponse comResp = new Gson().fromJson(response, CommentResponse.class);

                            if (TextUtils.isEmpty(comResp.getError())) {
                                FeedLikeResponse res = new Gson().fromJson(response, FeedLikeResponse.class);
                                Log.e("error",""+res.getError());
                                commentList.get(position).toggleLike();
                                commentAdapter.notifyItemChanged(position);
                               // autoPlayVideo(videoPos);
                            } else {
                                Util.showSnackbar(v, comResp.getErrorMessage());
                                //revert the changes made in case og any error
                                commentList.get(position).toggleLike();
                                commentAdapter.notifyItemChanged(position);

                            }
                        }

                    } catch (Exception e) {
                        CustomLog.e(e);
                        hideBaseLoader();
                    }
                    return true;
                };
                new HttpRequestHandler(activity, new Handler(callback)).run(request);

            } catch (Exception ignore) {
                hideBaseLoader();
            }
        } else {
            hideBaseLoader();
            notInternetMsg(v);
        }
    }

    Share sharelink_data=null;
    int RESULTPROFILEVIEW=930;
    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {

        switch (object1) {
            case Constant.Events.PROFILE:
                goTo(Constant.GoTo.VIEW_PROFILE, Constant.KEY_ID, postion);
                break;
            case Constant.Events.COMMENT:
                showBottomSheetDialog(postion);
                break;
            case Constant.Events.TICK_VIDEO_LIKE:
                 callLikeApi(REQ_LIKE, postion, Constant.URL_MUSIC_LIKE, Integer.parseInt(screenType.toString()));
                break;
            case Constant.Events.SHARE_FEED2:
                Log.e("Data","share");
                sharelink_data=videoList.get(postion).getShare();
               // askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                try {
                    showShareDialog(sharelink_data);
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                break;
            case Constant.Events.TICK_GO_TO_CHANNEL:
                Intent intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.TICK_VIEW_CHANNEL3);
                intent2.putExtra(Constant.KEY_CHANNEL_ID, postion);
                startActivityForResult(intent2,RESULTPROFILEVIEW);
                break;
            case Constant.Events.MEMBER_FOLLOW:
                Videos message_display= (Videos) screenType;
                callFollowApi(postion);
                for(int i=0;i<videoList.size();i++){
                    Log.e("id",""+videoList.get(i).getVideoId());
                    Log.e("id2",""+message_display.getVideoId());
                    if(videoList.get(i).getOwnerId() == message_display.getOwnerId()){
                        videoList.get(i).setIsUserChannelFollow(true);
                        Log.e("postion",""+i);
                        mAdapter.notifyItemInserted(position);
                      }
                }
                break;
            case Constant.Events.DELETE_COMMENT:
                showDeleteDialog(commentList.get(postion).getCommentId(), postion);
                break;
            case Constant.Events.REPORT:
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                goToReport(commentList.get(postion).getCommentId());
                break;

            case Constant.Events.TICK_COMMENT_EDIT:
                EditDialogFragment.newInstance(this, postion, commentList.get(postion)).show(fragmentManager, "comment");
                break;

            case Constant.Events.CONTENT_EDIT:
                if (null != screenType) {
                    callEditCommentAPI((String) screenType, postion);
                }
                break;

            case Constant.Events.ADD_TO_CART:
                //Util.showSnackbar(v, "Song Name: " + videoList.get(postion).getSong().gettitle());
                    int CAMERA_VIDEO_REQUEST = 7080;
                    try {
                        Intent intent=new Intent(context, SimillerACtivity.class);
                        if(videoList.get(postion).getSong().gettitle()!=null){
                            intent.putExtra("song_name",""+videoList.get(postion).getSong().gettitle());
                        }
                        intent.putExtra("song_url",""+videoList.get(postion).getSong().getUrl());
                        intent.putExtra("song_id",videoList.get(postion).getSongId());
                        intent.putExtra("Video_id",videoList.get(postion).getVideoId());
                        intent.putExtra("MP4_image",videoList.get(postion).getImages().getMain());
                        intent.putExtra("Username",videoList.get(postion).getUser_username());
                        if(videoList.get(postion).getSong().getImages()!=null){
                            intent.putExtra("musicimage",videoList.get(postion).getSong().getImages().getMain());
                        }else {
                            intent.putExtra("musicimage","");
                        }
                        intent.putExtra("sharetitle",videoList.get(postion).getShare().getTitle());
                        intent.putExtra("shareDescritpion",videoList.get(postion).getShare().getDescription());
                        intent.putExtra("shareImageUrl",videoList.get(postion).getShare().getImageUrl());
                        intent.putExtra("shareeUrl",videoList.get(postion).getShare().getUrl());
                        startActivityForResult(intent,CAMERA_VIDEO_REQUEST);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                break;
            case Constant.Events.LIKE_COMMENT:
                Log.e("lok","20102020");
                showBaseLoader(false);
                if (Integer.parseInt("" + screenType) > -1) {
                    callLikeUnlikeApi(Constant.URL_LIKE_COMMENT, commentList.get(postion).getCommentId(), postion);
                } else {
                    callLikeUnlikeApi(Constant.URL_UNLIKE_COMMENT, commentList.get(postion).getCommentId(), postion);
                }
                break;
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


            if(OtherFragment.followtag==5001){
                for(int i=0;i<videoList.size();i++){
                    if(videoList.get(i).getOwnerId() == videoList.get(videoPos).getOwnerId()){
                        videoList.get(i).setIsUserChannelFollow(true);
                    }
                }
                mAdapter.notifyDataSetChanged();
                mAdapter.notifyItemInserted(videoPos);
            }else if(OtherFragment.followtag==5010) {
                for(int i=0;i<videoList.size();i++){
                    if(videoList.get(i).getOwnerId() == videoList.get(videoPos).getOwnerId()){
                        videoList.get(i).setIsUserChannelFollow(false);
                    }
                }
                mAdapter.notifyDataSetChanged();
                mAdapter.notifyItemInserted(videoPos);
            }



    }

    private ProgressDialog pDialog;
    String fileName_path="";
    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            // your code
            Toast.makeText(context,"Successfull data ......",Toast.LENGTH_SHORT).show();

           /* File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName_path);
            if(file.exists()) {
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
            }*/

        }
    };


    public void onDownloadStart(String url,String contentDescription,String mimetype) {
        try {
                /*
                    DownloadManager.Request
                        This class contains all the information necessary to request a new download.
                        The URI is the only required parameter. Note that the default download
                        destination is a shared volume where the system might delete your file
                        if it needs to reclaim space for system use. If this is a problem,
                        use a location on external storage (see setDestinationUri(Uri).
                */
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                /*
                    void allowScanningByMediaScanner ()
                        If the file to be downloaded is to be scanned by MediaScanner, this method
                        should be called before enqueue(Request) is called.
                */
            request.allowScanningByMediaScanner();

                /*
                    DownloadManager.Request setNotificationVisibility (int visibility)
                        Control whether a system notification is posted by the download manager
                        while this download is running or when it is completed. If enabled, the
                        download manager posts notifications about downloads through the system
                        NotificationManager. By default, a notification is shown only
                        when the download is in progress.

                        It can take the following values: VISIBILITY_HIDDEN, VISIBILITY_VISIBLE,
                        VISIBILITY_VISIBLE_NOTIFY_COMPLETED.

                        If set to VISIBILITY_HIDDEN, this requires the permission
                        android.permission.DOWNLOAD_WITHOUT_NOTIFICATION.

                    Parameters
                        visibility int : the visibility setting value
                    Returns
                        DownloadManager.Request this object
                */
            request.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                /*
                    DownloadManager
                        The download manager is a system service that handles long-running HTTP
                        downloads. Clients may request that a URI be downloaded to a particular
                        destination file. The download manager will conduct the download in the
                        background, taking care of HTTP interactions and retrying downloads
                        after failures or across connectivity changes and system reboots.
                */

                /*
                    String guessFileName (String url, String contentDisposition, String mimeType)
                        Guesses canonical filename that a download would have, using the URL
                        and contentDisposition. File extension, if not defined,
                        is added based on the mimetype

                    Parameters
                        url String : Url to the content
                        contentDisposition String : Content-Disposition HTTP header or null
                        mimeType String : Mime-type of the content or null

                    Returns
                        String : suggested filename
                */
            fileName_path = URLUtil.guessFileName(url, contentDescription, mimetype);

                /*
                    DownloadManager.Request setDestinationInExternalPublicDir
                    (String dirType, String subPath)

                        Set the local destination for the downloaded file to a path within
                        the public external storage directory (as returned by
                        getExternalStoragePublicDirectory(String)).

                        The downloaded file is not scanned by MediaScanner. But it can be made
                        scannable by calling allowScanningByMediaScanner().

                    Parameters
                        dirType String : the directory type to pass to
                                         getExternalStoragePublicDirectory(String)
                        subPath String : the path within the external directory, including
                                         the destination filename

                    Returns
                        DownloadManager.Request this object

                    Throws
                        IllegalStateException : If the external storage directory cannot be
                                                found or created.
                */
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName_path);
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            DownloadManager dManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);



                /*
                    long enqueue (DownloadManager.Request request)
                        Enqueue a new download. The download will start automatically once the
                        download manager is ready to execute it and connectivity is available.

                    Parameters
                        request DownloadManager.Request : the parameters specifying this download

                    Returns
                        long : an ID for the download, unique across the system. This ID is used
                               to make future calls related to this download.
                */
            dManager.enqueue(request);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void downloadImage(String videourl,String fileName) {
        try {
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videourl));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);
            String str = fileName;
            //  request.setTitle(Constant.DOWNLOADING_IMAGE);
            // request.setDescription("Downloading " + "Image" + ".png");
            Log.e("Filename",""+fileName);
            request.setVisibleInDownloadsUi(true);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/" + getStrings(R.string.app_name).replace(" ", "") + "/" + str + Util.getCurrentdate(Constant.TIMESTAMP) + ".mp4");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            downloadManager.enqueue(request);

            Util.showSnackbar(v, "Video saved successfully.");
        } catch (Exception e) {
            CustomLog.e(e);
        }
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

                        activity.runOnUiThread(() -> {
                            try {
                                if (null != pDialog) {


                                    double ratio = progress.currentBytes / (double) progress.totalBytes;
                                    DecimalFormat percentFormat= new DecimalFormat("#%");

                                    Log.e("per:-",""+percentFormat);

                                    ((TextView) pDialog.findViewById(R.id.tvText)).setText(""+percentFormat.format(ratio));
                                    Log.e(""+progress.currentBytes,""+progress.totalBytes);
                                    ((CircularProgressBar) pDialog.findViewById(R.id.cpb)).setProgressWithAnimation((float) ratio*100, 1800);
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
                        Log.e("Messge",""+error.toString());
                        Log.e("Messge1",""+error.getServerErrorMessage());
                        Log.e("Messge2",""+error.getConnectionException());

                    }
                });
    }

    int CREATE_FILE_REQUEST_CODE=9807;

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
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/videos/" + videoList.get(videoPos).getTitle() + ".mp4";
                        downloadVideo(videoList.get(videoPos).getIframeURL(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/videos/",
                                videoList.get(videoPos).getTitle() + ".mp4", 0);
                    }else {
                        DownloadVideFile(0,videoList.get(videoPos).getTitle());
                    }

                }
              else{
                  try {
                      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                          Intent intent = new Intent(Intent.ACTION_VIEW)
                                  .setData(Uri.parse("https://play.google.com/store/apps/details?id=" + "com.whatsapp"));
                          try {
                              startActivity(new Intent(intent)
                                      .setPackage("com.whatsapp"));
                          } catch (android.content.ActivityNotFoundException exception) {
                              startActivity(intent);
                          }
                      }else {
                          DownloadVideFile(0,videoList.get(videoPos).getTitle());
                      }
                  }catch (Exception ex){
                      ex.printStackTrace();
                  }


              }


            });
            view.findViewById(R.id.llDownload).setOnClickListener(v -> {
                bottomSheetDialog.dismiss();

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/videos/" + videoList.get(videoPos).getTitle() + ".mp4";
                    downloadVideo(videoList.get(videoPos).getIframeURL(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/videos/",
                            videoList.get(videoPos).getTitle() + ".mp4", 1);
                }else {
                    DownloadVideFile(1,videoList.get(videoPos).getTitle());
                }

            });
            view.findViewById(R.id.llInstagram).setOnClickListener(v -> {
                bottomSheetDialog.dismiss();

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/videos/" + videoList.get(videoPos).getTitle() + ".mp4";
                    downloadVideo(videoList.get(videoPos).getIframeURL(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/videos/",
                            videoList.get(videoPos).getTitle() + ".mp4", 2);
                }else {
                    DownloadVideFile(2,videoList.get(videoPos).getTitle());
                }


            });



        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void DownloadVideFile(int type,String filenamenew) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        com.ixuea.android.downloader.callback.DownloadManager downloadManager = DownloadService.getDownloadManager(context.getApplicationContext());
        //create download info set download uri and save path.
        targetFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filenamenew+"_"+timeStamp+".mp4");
        Log.e("Absolute Path",""+targetFile.getAbsolutePath());
        DownloadInfo downloadInfo = new DownloadInfo.Builder().setUrl(videoList.get(videoPos).getIframeURL())
                .setPath(targetFile.getAbsolutePath())
                .build();
        //set download callback.
        downloadInfo.setDownloadListener(new DownloadListener() {

            @Override
            public void onStart() {
                Log.e("Prepare downloading","Prepare downloading:");
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

            @Override
            public void onWaited() {
                Log.e("Waiting","Waiting:");

            }

            @Override
            public void onPaused() {
                Log.e("Continue","Continue:");
            }

            @Override
            public void onDownloading(long progress, long size) {

                Log.e(""+size,":"+progress);

                activity.runOnUiThread(() -> {
                    try {
                        if (null != pDialog) {
                            double ratio = progress / (double) size;
                            DecimalFormat percentFormat= new DecimalFormat("#%");

                            Log.e("per:-",""+percentFormat);

                            ((TextView) pDialog.findViewById(R.id.tvText)).setText(""+percentFormat.format(ratio));
                            Log.e(""+progress,""+size);
                            ((CircularProgressBar) pDialog.findViewById(R.id.cpb)).setProgressWithAnimation((float) ratio*100, 1800);
                        }
                    } catch (Exception e) {
                        CustomLog.e(e);
                    }
                });
            }

            @Override
            public void onRemoved() {
                Log.e("Continue","Continue:");
            }

            @Override
            public void onDownloadSuccess() {
                Log.e("Download succes","success:");

                videoPath=targetFile.getAbsolutePath();
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
            public void onDownloadFailed(DownloadException e) {
                Log.e("Download fail:","Download fail:");
            }


        });

        //submit download info to download manager.
        downloadManager.download(downloadInfo);
    }

    File targetFile = null ;

    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName="Myname_"+timeStamp+".mp4";

        File photo = new File(Environment.DIRECTORY_DOWNLOADS,  imageFileName);

        return photo;
    }



    public static String getVideoContentUriFromFilePath(Context ctx, String filePath) {

        ContentResolver contentResolver = ctx.getContentResolver();
        String videoUriStr = null;
        long videoId = -1;
        Log.d("first log","Loading file " + filePath);

        // This returns us content://media/external/videos/media (or something like that)
        // I pass in "external" because that's the MediaStore's name for the external
        // storage on my device (the other possibility is "internal")
        Uri videosUri = MediaStore.Video.Media.getContentUri("external");

        Log.d("second log","videosUri = " + videosUri.toString());

        String[] projection = {MediaStore.Video.VideoColumns._ID};

        // TODO This will break if we have no matching item in the MediaStore.
        Cursor cursor = contentResolver.query(videosUri, projection, MediaStore.Video.VideoColumns.DATA + " LIKE ?", new String[] { filePath }, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(projection[0]);
        videoId = cursor.getLong(columnIndex);

        Log.d("third log","Video ID is " + videoId);
        cursor.close();
        if (videoId != -1 ) videoUriStr = videosUri.toString() + "/" + videoId;
        return videoUriStr;
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

    public void showNotification(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        File file = new File(path); // set your audio path
        Uri apkURI = FileProvider.getUriForFile(
                context,
                context.getApplicationContext()
                        .getPackageName() + ".provider", file);
        intent.setDataAndType(apkURI, "video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

       // intent.setDataAndType(Uri.fromFile(file), "video/*");
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
            NotificationCompat.Builder builder2 = new NotificationCompat.Builder(context, "0");
            NotificationManager notifManager = (NotificationManager) activity.getSystemService
                    (Context.NOTIFICATION_SERVICE);

            builder2.setContentTitle("Video Downloaded Successfully.")
                    .setSmallIcon(R.mipmap.ic_launcher) // required
                    .setContentText(videoList.get(videoPos).getTitle() + " has been downloaded successfully, click here to view.")  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pIntent)
                    .setGroupSummary(true);

            Notification notification = builder2.build();
            notifManager.notify(notifyID, notification);
        }

    }

    public void shareVideoWhatsApp() {
        File file = new File(videoPath);
       // Uri uri = Uri.fromFile(file);

     //   Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, file);

        Uri uri = FileProvider.getUriForFile(
                context,
                context.getApplicationContext()
                        .getPackageName() + ".provider", file);

        videoPath = null;
        Intent videoshare = new Intent(Intent.ACTION_SEND);
        videoshare.setType("*/*");
        videoshare.setPackage("com.whatsapp");
        videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        videoshare.putExtra(Intent.EXTRA_STREAM, uri);
        videoshare.putExtra(Intent.EXTRA_TEXT, "This video has been uploaded to  Vavci App, install  Vavci App to enjoy more videos!");
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
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
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
                                    String Comment = commentList.size() == 1 ? "comment" : "comments";
                                    tvCommentCount.setText(commentList.size() - 1 + " " + Comment);
                                    commentAdapter.notifyItemRemoved(position);
                                    commentAdapter.notifyDataSetChanged();
                                    Util.showSnackbar(v, res.getResult());
                                    // adapter.notifyItemChanged(position);
                                    totComments = commentList.size();
                                    if (totComments == 1) {
                                        tvCommentCount.setText(totComments + " comment");
                                    } else {
                                        tvCommentCount.setText(totComments + " comments");
                                    }
                                    videoList.get(videoPos).decreaseCommentCount();
                                    mAdapter.notifyItemChanged(videoPos);


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

    public void setRecyclerView() {
        try {


            mAdapter = new AdapterClickClickRecyclerView(videoList, getContext(), this, this, false);
            mViewPagerLayoutManager = new ViewPagerLayoutManager(getContext(), OrientationHelper.VERTICAL);
            rvTiktok.setLayoutManager(mViewPagerLayoutManager);
            rvTiktok.setAdapter(mAdapter);

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

            if (currentVideo) {
                rvTiktok.scrollToPosition(currentpos);
            }

            if (videoListSize > 0 || fromD || fromNotification) {

                ((TextView) v.findViewById(R.id.tvNoData)).setText("No videos available right now.");
                v.findViewById(R.id.llNoData).setVisibility(View.GONE);

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

        player = rvTiktok.getChildAt(0).findViewById(R.id.videoplayer);
        if (player != null) {
            player.startVideoAfterPreloading();

        }
    }


    public void initScreenData() {
        if (fromNotification) {
            init();
            videoList.add(video);
            setRecyclerView();
        } else {
            init();
            callMusicAlbumApi(1);
        }

//        setRecyclerView();
    }


    private void setCommentsRecyclerView() {
        commentList = new ArrayList<>();
        commentRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        commentRecyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) commentRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        commentAdapter = new ClickClickCommentAdapter(commentList, context, this, this);
        commentRecyclerView.setAdapter(commentAdapter);
        commentAdapter.notifyDataSetChanged();
    }

    TextView tvNoData_vid;
    RelativeLayout llNoData_vid;
    public void showBottomSheetDialog(int position_comment) {
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_create, null);

        dialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();

        /*dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                adapter.notifyDataSetChanged();
            }
        });*/


        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED);

        llNoData_vid = view.findViewById(R.id.llNoData);
        tvNoData_vid = view.findViewById(R.id.tvNoData);
        commentRecyclerView = view.findViewById(R.id.recyclerView);
        pbHeaderProgress = view.findViewById(R.id.pbHeaderProgress);
        ivPost = view.findViewById(R.id.ivPost);
        pbHeaderProgress.setVisibility(View.VISIBLE);
        tvCommentCount = view.findViewById(R.id.tvCommentCount);

        ivPost.setColorFilter(menuTitleActiveColor);
        // menuTitleActiveColor
        setCommentsRecyclerView();
        callCommentsApi(1);
        ivPost.setColorFilter(menuTitleActiveColor);

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
            submitCommentIfValid(position_comment);
        });

    }

    private void submitCommentIfValid(int position_comment) {
        boolean isValid = false;
        Map<String, Object> params = new HashMap<>();
        String body = Objects.requireNonNull(etBody.getText()).toString();
        if (!TextUtils.isEmpty(body)) {
            params.put("body", body);
            isValid = true;
        }
        closeKeyboard();
        if (isValid) {
            callCreateCommentApi(params,position_comment);
        } else {
            VibratorUtils.vibrate(context);
            startAnimation(ivPost, Techniques.SHAKE, 400);
        }
    }

    private void callCreateCommentApi(final Map<String, Object> params,int position_comment) {

        if (isNetworkAvailable(context)) {
            final boolean[] isDummyCommentAdded = {false};
            try {
                if (params.containsKey("body")) {

                    UserMaster userVo = SPref.getInstance().getUserMasterDetail(context);
                    etBody.setText(Constant.EMPTY);
                    isDummyCommentAdded[0] = true;
                   /* commentList.add(commentList.size(), new CommentData(
                            "Loading...",
                            userVo.getDisplayname(),
                            userVo.getPhotoUrl(),
                            Util.getCurrentdate(Constant.DATE_FROMAT_FEED),
                            true));
                    commentRecyclerView.smoothScrollToPosition(commentList.size());*/
                    showBaseLoader(true);
                  /*  totComments = commentList.size();
                    if (totComments == 1) {
                        tvCommentCount.setText(totComments + " comment");
                    } else {
                        tvCommentCount.setText(totComments + " comments");
                    }*/
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

                                    commentList.add(commentList.size(),vo);
                                    commentRecyclerView.smoothScrollToPosition(commentList.size());
                                  //  commentList.get(commentList.size() - 1).updateObject(vo);
                                    videoList.get(videoPos).increaseCommentCount();

                                    mAdapter.notifyItemChanged(videoPos);
                                    commentAdapter.notifyDataSetChanged();
                                    totComments = commentList.size();
                                    if (totComments == 1) {
                                        tvCommentCount.setText(totComments + " comment");
                                    } else {
                                        tvCommentCount.setText(totComments + " comments");
                                    }

                                } else {
                                    etBody.setText(Constant.EMPTY);
                                    commentList.add(0, vo);
                                    commentRecyclerView.smoothScrollToPosition(0);
                                    commentAdapter.notifyDataSetChanged();
                                }

                               // videoList.get(videoPos).setCommentCount(totComments);
                               // mAdapter.notifyItemChanged(videoPos);

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



    @Override
    public void onBackPressed() {
        if (Jzvd2.backPress()) {
            return;
        }
        onBackPressednew();
    }

    public void onBackPressednew() {
        try {
            closeKeyboard();
            if (getParentFragment() != null) {
                activity.currentFragment.onBackPressed();
                Log.e("parent2","parent");
            } else if (fragmentManager.getBackStackEntryCount() > 1) {
                fragmentManager.popBackStack();
                Log.e("backstack2","parent");
            } else {
                Log.e("finish2","not parent");
                activity.supportFinishAfterTransition();
                Log.e("CurrentFragment2",""+activity.currentFragment);
                Log.e("getParentFragment2",""+getParentFragment());

            }
        } catch (Exception e) {
            CustomLog.e(e);
            activity.finishAffinity();
        }
    }


    private void updateAdapter() {
        isLoading = false;
        if (isSearch) {
            rvTiktok.smoothScrollToPosition(startposition);
//        pb.setVisibility(View.GONE)
        }
        //  swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(rvTiktok);
        ((TextView) v.findViewById(R.id.tvNoData)).setText("No videos available right now.");
        v.findViewById(R.id.llNoData).setVisibility(videoListSize > 0 ? View.GONE : View.VISIBLE);
        if (null != listener) {
            listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, -1);
            listener.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
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
                                        boolean islike = json.getJSONObject(Constant.KEY_RESULT).getBoolean("is_like");
                                        videoList.get(position).setContentLike(islike);
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

    public static ClickClickFragment newInstance(OnUserClickedListener<Integer, Object> parent, String selectedScreen) {
        ClickClickFragment frag = new ClickClickFragment();
        frag.listener = parent;
        frag.selectedScreen = selectedScreen;
        return frag;
    }

    private void callFollowApi(final int position) {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {

                    HttpRequestVO request = new HttpRequestVO(Constant.URL_FOLLOW_MEMBER);
                    //request.params.put(Constant.KEY_RESOURCE_ID, videoList.get(position).getChannelID());
                 //   request.params.put(Constant.KEY_CHANNEL_ID, videoList.get(position).getChannelID());

                    //request.params.put(Constant.KEY_RESOURCES_TYPE, "sesvideo_chanel");

                    request.params.put(Constant.KEY_USER_ID, videoList.get(position).getOwnerId());
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;
                                Log.e("response",""+response);

                                FollowandUnfollow followandUnfollow=new Gson().fromJson(response, FollowandUnfollow.class);

                                try {
                                    if(followandUnfollow.getResult().getMember().getFollow().getAction().equalsIgnoreCase("follow")){
                                        Util.showSnackbar(v, "user unfollow successfully.");
                                    }else {
                                        Util.showSnackbar(v, "user follow successfully.");
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
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


    private class AsyncCaller extends AsyncTask<Context, Void, Void> {

        @Override
        protected Void doInBackground(Context... params) {
            loadvideos();
            return null;
        }


        void loadvideos() {
            for (int i = 1; i < videoListSize; i++) {
                URL url = null;
                try {
                    proxy = ((MainApplication) getApplicationContext()).getProxy(getApplicationContext());
                    url = new URL(proxy.getProxyUrl(videoList.get(i).getIframeURL(), true));

                    InputStream inputStream = url.openStream();
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    int length = 0;
                    while ((length = inputStream.read(buffer)) != -1) {
                        //nothing to do
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

        }

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
                                if (result.getCurrentPage() == result.getTotalPage() && req == REQ_LOAD_MORE) {
                                    Collections.reverse(commentList);
                                }
                                tvCommentCount.setVisibility(View.VISIBLE);
                                if (totComments == 1) {
                                    tvCommentCount.setText(comResp.getResult().getTotal() + " comment");
                                } else {
                                    tvCommentCount.setText(comResp.getResult().getTotal() + " comments");
                                }

                                    tvNoData_vid.setText("Be the first one to comment.");
                                    llNoData_vid.setVisibility(totComments > 0 ? View.GONE : View.VISIBLE);
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

    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(context)) {
                isLoading = true;
                try {
                    if (req == REQ_LOAD_MORE) {
//                        pb.setVisibility(View.VISIBLE);
                    } else {
                        showBaseLoader(true);
                    }
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_TICK_FORYOU);
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
                                        result = resp.getResult();
                                        if (!fromD) {
                                            videoList.addAll(result.getVideos());
                                            videoListSize = videoList.size();
                                            new AsyncCaller().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
                                            if (isSearch) {
                                                for (int i = 0; i < videoListSize; i++) {
                                                    if (videoList.get(i).getVideoId() == ActPos) {
                                                        startposition = i;
                                                    }
                                                }
                                            }
                                        }
                                        if (req != REQ_LOAD_MORE) {
                                            setRecyclerView();

                                        }
//                                        updateAdapter();
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
          //  pb.setVisibility(View.GONE);
            CustomLog.e(e);
            hideBaseLoader();
        }
    }

}

package com.sesolutions.ui.resume;

import android.Manifest;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.gson.Gson;
import com.ixuea.android.downloader.DownloadService;
import com.ixuea.android.downloader.callback.DownloadListener;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.android.downloader.exception.DownloadException;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.NotificationResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.customviews.CircularProgressBar;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyPreviewList extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MyresumeList";

    private View v;
    private int resourceId;
    private boolean isLoading;
    private String resourceType;
    private PreviewAdapter adapter;
    public RecyclerView recyclerView;
    private List<PreviewModel.ResultBean.TemplateIdBean> friendList;
    private NotificationResponse.Result result;
    com.google.android.material.floatingactionbutton.FloatingActionButton fabAdd;
    TextView previewid;
    int selectpostion=1;
    Button buttonview,buttonviewPreview;
    String resumePath="";
    public SwipeRefreshLayout swipeRefreshLayout;

    public static final int PERMISSION_CONSTANT = 1059;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_resumelistpreview, container, false);
        new ThemeManager().applyTheme((ViewGroup) v, context);
        previewid=v.findViewById(R.id.previewid);
        buttonview=v.findViewById(R.id.buttonview);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        buttonviewPreview=v.findViewById(R.id.buttonviewPreview);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CONSTANT);
        }

        initScreenData();

        buttonview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO}, PERMISSION_CONSTANT);
                } else {

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        resumePath="";
                        showBaseLoader(false);
                        String downloadurl= Constant.BASE_URL+"eresume/index/generateresume?restApi=Sesapi&sesapi_version=3.1&sesapi_platform=2&language=id&debug=1&auth_token="+ SPref.getInstance().getToken(context)+"&resume_id="+resumeid+"&template_id="+selectpostion;
                        resumePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/Resume/" + titlerd+"_"+resumeid +"_"+selectpostion+ ".pdf";
                        downloadVideo(downloadurl, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/Resume/", titlerd+"_"+resumeid +"_"+selectpostion+".pdf", 1,titlerd+"_"+selectpostion);
                    }else {
                        resumePath="";
                        showBaseLoader(false);
                        String downloadurl= Constant.BASE_URL+"eresume/index/generateresume?restApi=Sesapi&sesapi_version=3.1&sesapi_platform=2&language=id&debug=1&auth_token="+ SPref.getInstance().getToken(context)+"&resume_id="+resumeid+"&template_id="+selectpostion;
                        resumePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/Resume/" + titlerd+"_"+resumeid +"_"+selectpostion+ ".pdf";
                        DownloadVideFile(2,titlerd+"_"+resumeid +"_"+selectpostion+ ".pdf",downloadurl);
                    }

                 }
            }
        });

        buttonviewPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String werurl= Constant.BASE_URL+"resumes/preview/"+resumeid+"/template_id/"+selectpostion+"?removeSiteHeaderFooter=true";
                Intent intent = new Intent(activity, CommonActivity.class);
                intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_WEBVIEW);
                intent.putExtra(Constant.KEY_URI, werurl);
                intent.putExtra(Constant.KEY_TITLE, "Preview");
                startActivity(intent);
            }
        });


        previewid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String werurl= Constant.BASE_URL+"resumes/preview/"+resumeid+"/template_id/"+selectpostion;
                Intent intent = new Intent(activity, CommonActivity.class);
                intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_WEBVIEW);
                intent.putExtra(Constant.KEY_URI, werurl);
                intent.putExtra(Constant.KEY_TITLE, "Preview");
                startActivity(intent);
            }
        });
        return v;
    }

    int resumeid=0;
    String titlerd="";

    boolean isRefreash=false;
    @Override
    public void onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            isRefreash=true;
            callNotificationApi(false,resumeid);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void initScreenData() {
        CustomLog.e("loading child", "notification");
        init();
        setRecyclerView();
        callNotificationApi(true,resumeid);
    }

    private void init() {
        friendList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.recyclerView);
        fabAdd = v.findViewById(R.id.fabAdd);
        ((TextView) v.findViewById(R.id.tvTitle)).setText("Choose Template");
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.fabAdd).setOnClickListener(this);
        v.findViewById(R.id.fabAdd).setOnClickListener(this);
        v.findViewById(R.id.fabAdd).setVisibility(View.GONE);
       /* ivProfileImage = v.findViewById(R.id.ivProfileImage);
        //bSave = v.findViewById(R.id.bSave);
        v.findViewById(R.id.bChoose).setOnClickListener(this);
        v.findViewById(R.id.bSave).setOnClickListener(this);
        v.findViewById(R.id.tvTerms).setOnClickListener(this);
        v.findViewById(R.id.tvPrivacy).setOnClickListener(this);*/
        //initSlide();

    }

    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(Constant.SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new PreviewAdapter(friendList, context, this, this);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    @Override
    //@OnClick({R.id.bSignIn, R.id.bSignUp})
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.ivBack:
                    onBackPressed();
                    break;
//                case R.id.ivOptionAddnew:
                  //updatetitlte("",false,0);
                 //   break;

                case R.id.bRefresh:
                    callNotificationApi(true,resumeid);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callNotificationApi(boolean showLoader,int resumid) {
        try {

            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;
                if(showLoader){
                    showBaseLoader(false);
                }


                //showBaseLoader(true);
                try {

                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.RESUME_PREVIEW);
                  //  request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put(Constant.KEY_GET_FORM, "1");
                    request.params.put(Constant.KEY_RESUME_ID,resumid);

                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            isLoading = false;
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {
                                    swipeRefreshLayout.setRefreshing(false);
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        PreviewModel resp = new Gson().fromJson(response, PreviewModel.class);
                                        friendList.clear();
                                        if (null != resp.getResult().getTemplate_id())
                                            friendList.addAll(resp.getResult().getTemplate_id());
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
                                    }
                                } else {
                                    notInternetMsg(v);
                                }
                                updateRecyclerView();
                            } catch (
                                    Exception e) {
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
                notInternetMsg(v);
            }

        } catch (
                Exception e) {
            isLoading = false;
            hideBaseLoader();
            CustomLog.e(e);
        }

    }

    private void updateRecyclerView() {
        isLoading = false;
        //updateTitle();
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        ((TextView) v.findViewById(R.id.tvNoData)).setText(Constant.MSG_NO_RESUME);
        v.findViewById(R.id.llNoData).setVisibility(friendList.size() > 0 ? View.GONE : View.VISIBLE);
    }


    private void updateTitle() {
        if (result.getTotal() > 0) {
            ((TextView) v.findViewById(R.id.tvTitle)).setText(Constant.TITLE_FOLLOWERS + " (" + result.getTotal() + ")");
        }
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                CustomLog.e("getCurrentPage", "" + result.getCurrentPage());
                CustomLog.e("getTotalPage", "" + result.getTotalPage());

                if (result.getCurrentPage() < result.getTotalPage()) {
                    callNotificationApi(false,resumeid);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        CustomLog.e("pagination", "" + adapter.getItemCount());
    }

    public static MyPreviewList newInstance(int resourceId, String title) {
        MyPreviewList frag = new MyPreviewList();
        frag.resumeid = resourceId;
        frag.titlerd = title;
        return frag;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1){
            case Constant.Events.CLICKED_HEADER_TITLE:
              //  gotoResumeBuilder(postion);
                friendList= (List<PreviewModel.ResultBean.TemplateIdBean>) object2;
                selectpostion=postion+1;
                adapter.notifyDataSetChanged();
                break;



        }
        return false;
    }



    private ProgressDialog pDialog;
    public void downloadVideo(String url, String dirPath, String fileName, int type,String titlename) {
        int downloadId2 = PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        try {
                            hideBaseLoader();
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
                        Log.e("pause","pause");
                        hideBaseLoader();

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        Log.e("onCancel","onCancel");
                        hideBaseLoader();

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


                        hideBaseLoader();
                        Util.showSnackbar(v, "Resume Downloaded successfully..");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showNotification(resumePath,titlename);
                        }
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                        }



                    }

                    @Override
                    public void onError(Error error) {
                        Log.e("onMessageCancel",""+error.getServerErrorMessage());
                        Log.e("onMessageCancel",""+error.toString());
                        Log.e("onMessageCancel",""+error.getResponseCode());
                        if(error.getResponseCode()==0){
                            downloadVideo(url, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/Resume/", titlerd+"_"+resumeid +"_"+selectpostion+".pdf", 1,titlerd+"_"+selectpostion);
                        }

                    }
                });
    }
  /*  public void downloadVideo2(String url, String dirPath, String fileName, int type,String titlename) {
        PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {


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

                        });

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                    }

                    @Override
                    public void onError(Error error) {
                        Log.e("onMessageCancel",""+error.getServerErrorMessage());

                    }
                });
    }
  */

    public void showNotification(String path,String title) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        File file = new File(path); // set your audio path
        Uri apkURI = FileProvider.getUriForFile(
                context,
                context.getApplicationContext()
                        .getPackageName() + ".provider", file);
        intent.setDataAndType(apkURI, "application/pdf");
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
                    .setContentTitle("Resume Downloaded Successfully.")
                    .setContentText(title + " has been downloaded successfully, click here to view.")
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

            builder2.setContentTitle("Resume Downloaded Successfully.")
                    .setSmallIcon(R.mipmap.ic_launcher) // required
                    .setContentText(title + " has been downloaded successfully, click here to view.")  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pIntent)
                    .setGroupSummary(true);

            Notification notification = builder2.build();
            notifManager.notify(notifyID, notification);
        }

    }

    private void DownloadVideFile(int type,String filenamenew,String urlst) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        com.ixuea.android.downloader.callback.DownloadManager downloadManager = DownloadService.getDownloadManager(context.getApplicationContext());
        //create download info set download uri and save path.
        targetFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filenamenew+"_"+timeStamp+".mp4");
        Log.e("Absolute Path",""+targetFile.getAbsolutePath());
        DownloadInfo downloadInfo = new DownloadInfo.Builder().setUrl(urlst)
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
                hideBaseLoader();
                resumePath=targetFile.getAbsolutePath();
                Util.showSnackbar(v, "Resume Downloaded successfully..");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    showNotification(resumePath,filenamenew);
                }
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
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

}

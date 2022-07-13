package com.sesolutions.ui.resume;

import android.Manifest;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import java.util.Objects;


public class MyresumeList extends BaseFragment implements View.OnClickListener, OnLoadMoreListener, OnUserClickedListener<Integer, Object>, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MyresumeList";

    private View v;
    private int resourceId;
    private boolean isLoading;
    private String resourceType;
    private ResumeAdapter adapter;
    public RecyclerView recyclerView;
    private List<ResumeMoel.ResultBean.ResumesBean> friendList;
    private List<resumedashordmodel.ResultBean.DashboardoptionsBean> tabslist;
    private NotificationResponse.Result result;
    com.google.android.material.floatingactionbutton.FloatingActionButton fabAdd;
    ImageView ivOptionAddnew;

    String resumePath="";
    public SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_resumelist, container, false);
        new ThemeManager().applyTheme((ViewGroup) v, context);
        initScreenData();
        return v;
    }

    public void initScreenData() {
        CustomLog.e("loading child", "notification");

        init();
        setRecyclerView();
        callNotificationApi(true,true);
        calldashboardtabs();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CONSTANT);
        }

    }

    Boolean isRefreash=false;

    @Override
    public void onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            isRefreash=true;
            callNotificationApi(true,false);
         } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void init() {
        friendList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.recyclerView);
        ivOptionAddnew = v.findViewById(R.id.ivOptionAddnew);
        fabAdd = v.findViewById(R.id.fabAdd);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(Constant.TITLE_Resume);

        ((LinearLayoutCompat) v.findViewById(R.id.llOption)).setVisibility(View.VISIBLE);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
        v.findViewById(R.id.fabAdd).setOnClickListener(this);
        v.findViewById(R.id.fabAdd).setOnClickListener(this);
        v.findViewById(R.id.fabAdd).setVisibility(View.GONE);

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

       /* ivProfileImage = v.findViewById(R.id.ivProfileImage);
        //bSave = v.findViewById(R.id.bSave);
        v.findViewById(R.id.bChoose).setOnClickListener(this);
        v.findViewById(R.id.bSave).setOnClickListener(this);
        v.findViewById(R.id.tvTerms).setOnClickListener(this);
        v.findViewById(R.id.tvPrivacy).setOnClickListener(this);*/
        //initSlide();

        ivOptionAddnew.setOnClickListener(this);
    }

    private void setRecyclerView() {
        try {
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new ResumeAdapter(friendList, context, this, this);
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
              //  case R.id.fabAdd:
                case R.id.ivOptionAddnew:
                    updatetitlte("",false,0);
                    break;

                case R.id.bRefresh:
                    callNotificationApi(true,true);
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callNotificationApi(boolean showLoader,boolean loaderenable) {
        try {

            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                isLoading = true;
                if(loaderenable)
                showBaseLoader(false);

                //showBaseLoader(true);
                try {

                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.CREDIT_RESUMEBUILDER);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

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
                                        ResumeMoel resp = new Gson().fromJson(response, ResumeMoel.class);
                                        friendList.clear();

                                        try {
                                            if(resp.getResult().getCanCreate()==1){
                                                ivOptionAddnew.setVisibility(View.VISIBLE);
                                            }else {
                                                ivOptionAddnew.setVisibility(View.GONE);
                                            }
                                        }catch (Exception ex){
                                            ex.printStackTrace();
                                        }
                                        if (null != resp.getResult().getResumes())
                                            friendList.addAll(resp.getResult().getResumes());
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                     //   goIfPermissionDenied(err.getError());
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
    private void calldashboardtabs() {
        try {

            if (isNetworkAvailable(context)) {
                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.RESUME_PRE_DASHBOARD);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonsedash", "" + response);
                                if (response != null) {

                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        resumedashordmodel resp = new Gson().fromJson(response, resumedashordmodel.class);
                                        tabslist=new ArrayList<>();
                                        if (null != resp.getResult().getDashboardoptions())
                                            tabslist.addAll(resp.getResult().getDashboardoptions());
                                    } else {
                                        Util.showSnackbar(v, err.getErrorMessage());
                                        goIfPermissionDenied(err.getError());
                                    }
                                }
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
                    e.printStackTrace();
                 }

            }

        } catch (
                Exception e) {
            e.printStackTrace();
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
                    callNotificationApi(false,true);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        CustomLog.e("pagination", "" + adapter.getItemCount());
    }

    public static MyresumeList newInstance(int resourceId, String resourceType) {
        MyresumeList frag = new MyresumeList();
        frag.resourceId = resourceId;
        frag.resourceType = resourceType;
        return frag;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1){
            case Constant.Events.CLICKED_HEADER_TITLE:
                ResumeMoel.ResultBean.ResumesBean resumesBean471= (ResumeMoel.ResultBean.ResumesBean) object2;
                gotoResumeBuilder(postion,resumesBean471.getTitle(),tabslist);
                break;
            case Constant.Events.CLICKED_HEADER_EDIT:
                ResumeMoel.ResultBean.ResumesBean resumesBean= (ResumeMoel.ResultBean.ResumesBean) object2;
                updatetitlte(resumesBean.getTitle(),true,postion);
                break;
            case Constant.Events.CLICKED_HEADER_DELETE:
                showDeleteDialog(context,postion);
                break;

            case Constant.Events.CLICKED_HEADER_SEEMORE:
                ResumeMoel.ResultBean.ResumesBean resumesBean47= (ResumeMoel.ResultBean.ResumesBean) object2;
               gotoResumeBuilder(postion,resumesBean47.getTitle(),tabslist);

                break;
            case Constant.Events.CLICKED_PREVIEW_RESUME:
                ResumeMoel.ResultBean.ResumesBean resumesBean44= (ResumeMoel.ResultBean.ResumesBean) object2;

                gotoPreviewBuilder(postion,resumesBean44.getTitle());
                break;
            case Constant.Events.CLICKED_PREVIEW_DOWNLOAD:

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CONSTANT);
                } else {

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                        ResumeMoel.ResultBean.ResumesBean resumesBean3= (ResumeMoel.ResultBean.ResumesBean) object2;
                        resumePath="";
                        showBaseLoader(false);
                        String downloadurl= Constant.BASE_URL+"eresume/index/download-resume?restApi=Sesapi&sesapi_version=3.1&sesapi_platform=2&language=id&debug=1&auth_token="+ SPref.getInstance().getToken(context)+"&resume_id="+postion;
                        resumePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/Resume/" + resumesBean3.getTitle()+postion + ".pdf";
                        downloadVideo(downloadurl, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/Resume/", resumesBean3.getTitle()+postion + ".pdf", 1,resumesBean3.getTitle());
                    }else {
                        ResumeMoel.ResultBean.ResumesBean resumesBean3= (ResumeMoel.ResultBean.ResumesBean) object2;
                        resumePath="";
                        showBaseLoader(false);
                        String downloadurl= Constant.BASE_URL+"eresume/index/download-resume?restApi=Sesapi&sesapi_version=3.1&sesapi_platform=2&language=id&debug=1&auth_token="+ SPref.getInstance().getToken(context)+"&resume_id="+postion;
                        resumePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/Resume/" + resumesBean3.getTitle()+postion + ".pdf";
                        DownloadVideFile(2,resumesBean3.getTitle()+"_"+postion+ ".pdf",downloadurl);
                    }

                }
                break;
        }
        return false;
    }
    public static final int PERMISSION_CONSTANT = 1059;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CONSTANT:

                break;
        }
    }

    private AlertDialog.Builder dialog;
    private void updatetitlte(String title,Boolean editable,int resumeid) {
        dialog = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.resume_title_layout,null);
        EditText edittitle=view.findViewById(R.id.edittitle);
        TextView titleview=view.findViewById(R.id.titleview);
        Button updateUserBtn = view.findViewById(R.id.updateUserBtn);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);

        if(editable){
            edittitle.setText(title);
            titleview.setText("Edit Resume");
            updateUserBtn.setText("Save Changes");
        }else {
            titleview.setText("Create Resume");
            edittitle.setHint("Title");
            updateUserBtn.setText("Create Resume");
        }
        AlertDialog alertDialog = dialog.create();
        alertDialog.setView(view);
        updateUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edittitle.getText().toString().length()>0){
                    alertDialog.dismiss();
                    if(editable){
                        calltitleresume(false,edittitle.getText().toString(), Constant.CREDIT_RESUMEBUILDER_TiTLE_EDIT,resumeid);
                    }else {
                        calltitleresume(false,edittitle.getText().toString(), Constant.CREDIT_RESUMEBUILDER_TiTLE,0);
                    }

                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void calltitleresume(boolean showLoader,String title,String url,int resumeid) {
        try {

            if (isNetworkAvailable(context)) {
                 showBaseLoader(false);
               try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put(Constant.KEY_TITLE, title);
                   if(url.equalsIgnoreCase(Constant.CREDIT_RESUMEBUILDER_TiTLE_EDIT)){
                       request.params.put("resume_id", resumeid);
                   }
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());


                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            try {
                                String response = (String) msg.obj;
                                CustomLog.e("repsonse", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        callNotificationApi(true,false);
                                    } else {
                                        hideBaseLoader();
                                        Util.showSnackbar(v, err.getErrorMessage());
                                    }
                                } else {
                                    notInternetMsg(v);
                                }
                            } catch (
                                    Exception e) {
                                CustomLog.e(e);
                            }
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

        } catch (
                Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }

    }

    public void showDeleteDialog(final Context context, final int resumeid33) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_RESUME);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.delete);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                calldeletetitle(false, Constant.CREDIT_RESUMEBUILDER_DELETE,resumeid33);

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }



    private void calldeletetitle(boolean showLoader,String url,int resumeid) {
        try {

            if (isNetworkAvailable(context)) {
                 showBaseLoader(false);
               try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put("resume_id", resumeid);
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                         //   hideBaseLoader();
                            callNotificationApi(true,false);

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

        } catch (
                Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }

    }


    private ProgressDialog pDialog;
    public void downloadVideo(String url, String dirPath, String fileName, int type,String titlename) {
        int downloadId = PRDownloader.download(url, dirPath, fileName)
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
                        hideBaseLoader();
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
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
                        hideBaseLoader();
                    }
                });
    }

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

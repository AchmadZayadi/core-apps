package com.sesolutions.ui.resume;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
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
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.videos.Result;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.ui.customviews.CircularProgressBar;
import com.sesolutions.ui.video.VideoHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.sesolutions.utils.Constant.EDIT_CHANNEL_ME;

public class ResumeProjectFragment extends VideoHelper implements View.OnClickListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

        private RecyclerView recyclerView;
        private boolean isLoading;
        private final int REQ_LOAD_MORE = 2;
        public String searchKey;
        public Result result;
        private ProgressBar pb;
        public String txtNoData = Constant.MSG_NO_PROJECT;
        public SwipeRefreshLayout swipeRefreshLayout;
        com.sesolutions.ui.resume.WorkProjectAdapter WorkProjectAdapter;
        public List<Resume_project.ResultBean.ProjectsBean> workexperincelist;





    /*@Override
    public void onResume() {
        super.onResume();
        Log.e("TASK RESUME",""+Constant.backresume);
        if(Constant.backresume==Constant.FormType.CREATE_RESUME_PROJECT || Constant.backresume==Constant.FormType.CREATE_RESUME_PROJECT_EDIT){
            Constant.backresume=0;
            if(Constant.resumeid!=0){
                resumeid=Constant.resumeid;
                initScreenData();
                Constant.resumeid=0;
            }
        }
    }*/

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
            // activity.setTitle(title);
            if (v != null) {
                return v;
            }
            v = inflater.inflate(R.layout.fragment_list_common_offset_refresh, container, false);
            applyTheme(v);


            return v;
        }

        public void init() {
            recyclerView = v.findViewById(R.id.recyclerview);
            pb = v.findViewById(R.id.pb);
        }

        public void setRecyclerView() {
            try {
                workexperincelist = new ArrayList<>();
                recyclerView.setHasFixedSize(true);
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);
                WorkProjectAdapter = new WorkProjectAdapter(workexperincelist, context, this, this);
                recyclerView.setAdapter(WorkProjectAdapter);
                swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
                swipeRefreshLayout.setOnRefreshListener(this);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }


        @Override
        //@OnClick({R.id.bSignIn, R.id.bSignUp})
        public void onClick(View v) {
            try {
                switch (v.getId()) {
                }
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        public void initScreenData() {
            if(Constant.resumeid!=0){
                resumeid= Constant.resumeid;
                Constant.resumeid=0;
            }
            init();
            setRecyclerView();
            result = null;
            callMusicAlbumApi(1);
        }


        public void callMusicAlbumApi(final int req) {
            try {
                if (isNetworkAvailable(context)) {
                    isLoading = true;
                    try {
                      if(!isdelete){
                          if (req != Constant.REQ_CODE_REFRESH)
                              showBaseLoader(true);
                      }else {
                          isdelete=false;
                      }


                        HttpRequestVO request = new HttpRequestVO(Constant.CREDIT_RESUME_PROJECT);
                        Map<String, Object> map = activity.filteredMap;
                        if (null != map) {
                            request.params.putAll(map);
                        }
                        if (!TextUtils.isEmpty(searchKey)) {
                            request.params.put(Constant.KEY_SEARCH, searchKey);
                        }
                        request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                        request.params.put("resume_id", resumeid);
                        request.params.put(Constant.KEY_PAGE, null != result ? result.getNextPage() : 1);
                        if (req == Constant.REQ_CODE_REFRESH) {
                            request.params.put(Constant.KEY_PAGE, 1);
                        }
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
                                    setRefreshing(swipeRefreshLayout, false);
                                    CustomLog.e("repsonse1", "" + response);
                                    if (response != null) {
                                        ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                        if (TextUtils.isEmpty(err.getError())) {
                                            workexperincelist.clear();
                                            try {
                                                Resume_project resp = new Gson().fromJson(response, Resume_project.class);
                                                if(resp.getResult().getProjects()!=null)
                                                workexperincelist.addAll(resp.getResult().getProjects());
                                            }catch (Exception ex){
                                                ex.printStackTrace();
                                            }


                                            updateAdapter();
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
                    setRefreshing(swipeRefreshLayout, false);

                    pb.setVisibility(View.GONE);
                    notInternetMsg(v);
                }

            } catch (Exception e) {
                hideLoaders();
                CustomLog.e(e);
                hideBaseLoader();
            }
        }

        public void hideLoaders() {
            isLoading = false;
            setRefreshing(swipeRefreshLayout, false);
            pb.setVisibility(View.GONE);
        }

        private void updateAdapter() {
            hideLoaders();

            WorkProjectAdapter = new WorkProjectAdapter(workexperincelist, context, this, this);
            recyclerView.setAdapter(WorkProjectAdapter);
            WorkProjectAdapter.notifyDataSetChanged();
            runLayoutAnimation(recyclerView);
            ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
            v.findViewById(R.id.llNoData).setVisibility(workexperincelist.size() > 0 ? View.GONE : View.VISIBLE);
            v.findViewById(R.id.tvNoData).setVisibility(workexperincelist.size() > 0 ? View.GONE : View.VISIBLE);

        }

        int resumeid;
        public static ResumeProjectFragment newInstance(OnUserClickedListener<Integer, Object> parent, String selectedScreen, int resumid) {
            ResumeProjectFragment frag = new ResumeProjectFragment();
            frag.listener = parent;
            frag.selectedScreen = selectedScreen;
            frag.resumeid = resumid;
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
        public void onRefresh() {
            try {
                if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(true);
                }
                init();
                setRecyclerView();
                callMusicAlbumApi(Constant.REQ_CODE_REFRESH);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }


    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        switch (object1){
            case Constant.Events.SUCCESS:
                callMusicAlbumApi(1);
                break;
            case Constant.Events.CLICKED_HEADER_EDIT:
                Resume_project.ResultBean.ProjectsBean resumesBean= (Resume_project.ResultBean.ProjectsBean) object2;
              //  updatetitlte(resumesBean.getTitle(),true,postion);
                Intent intent = new Intent(activity, CommonActivity.class);
                intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.CREATE_RESUME_PROJECT_EDIT);
                intent.putExtra(Constant.KEY_ID, resumeid);
                intent.putExtra(Constant.KEY_PROJECT_ID, resumesBean.getProject_id());
                startActivityForResult(intent, EDIT_CHANNEL_ME);



                break;
            case Constant.Events.CLICKED_HEADER_DELETE:
                showDeleteDialog(context,postion);

                break;
            case Constant.Events.CLICKED_HEADER_SEEMORE:
                Resume_project.ResultBean.ProjectsBean resumesBean2= (Resume_project.ResultBean.ProjectsBean) object2;
                //  updatetitlte(resumesBean.getTitle(),true,postion);
                Intent intent2 = new Intent(activity, CommonActivity.class);
                intent2 = new Intent(activity, CommonActivity.class);
                intent2.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GO_TO_WEBVIEW);
                intent2.putExtra(Constant.KEY_URI, resumesBean2.getProject_url());
                intent2.putExtra(Constant.KEY_TITLE, resumesBean2.getTitle());
                startActivity(intent2);

                break;
            case Constant.Events.CLICKED_PREVIEW_DOWNLOAD:
                Resume_project.ResultBean.ProjectsBean resumesBean6= (Resume_project.ResultBean.ProjectsBean) object2;

                String DownloadUrl="";
                for(int k=0;k<resumesBean6.getMenus().size();k++){
                    if(resumesBean6.getMenus().get(k).getName().equalsIgnoreCase("download")){
                        DownloadUrl=resumesBean6.getMenus().get(k).getImage_url();
                    }
                }
                resumePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/Project/" + resumesBean6.getTitle()+"_"+resumeid+ ".pdf";
                downloadVideo(DownloadUrl, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/Project/", resumesBean6.getTitle()+"_"+resumeid + ".pdf", 1,resumesBean6.getTitle());

                break;


        }
        return false;
    }

    public void showDeleteDialog(final Context context, final int projectid) {
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
            tvMsg.setText(R.string.MSG_DELETE_CONFIRMATION_PROD);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(R.string.delete);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(R.string.CANCEL);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                calldeletetitle(false, Constant.CREDIT_RESUME_DELETEPROJECT,projectid);

            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> progressDialog.dismiss());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    boolean isdelete=false;
    private void calldeletetitle(boolean showLoader,String url,int project_id) {
        try {

            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {

                    HttpRequestVO request = new HttpRequestVO(url);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.params.put("project_id", project_id);
                    request.requestMethod = HttpPost.METHOD_NAME;
                    request.headres.put(Constant.KEY_COOKIE, getCookie());

                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                        //   hideBaseLoader();
                            isdelete=true;
                            init();
                            setRecyclerView();
                            callMusicAlbumApi(1);

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


    String resumePath="";
    private ProgressDialog pDialog;
    public void downloadVideo(String url, String dirPath, String fileName, int type,String titlename) {
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
                                    DecimalFormat percentFormat = new DecimalFormat("#.#%");

                                    android.util.Log.e("per:-", "" + percentFormat);

                                    ((TextView) pDialog.findViewById(R.id.tvText)).setText("" + percentFormat.format(ratio));
                                    android.util.Log.e("" + progress.currentBytes, "" + progress.totalBytes);
                                    //   String progress1=percentFormat.format(ratio);
                                    try {
                                        ((CircularProgressBar) pDialog.findViewById(R.id.cpb)).setProgressWithAnimation(Float.parseFloat( percentFormat.format(ratio*100)), 1800);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

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

                        Util.showSnackbar(v, "Document downloaded successfully.");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showNotification(resumePath,titlename);
                        }
                        if (pDialog.isShowing()) {
                            pDialog.dismiss();
                        }



                    }

                    @Override
                    public void onError(Error error) {

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
                    .setContentTitle("Document downloaded successfully.")
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

            builder2.setContentTitle("Document downloaded successfully.")
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

}

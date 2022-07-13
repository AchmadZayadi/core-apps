package com.sesolutions.ui.clickclick.music;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
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
import com.sesolutions.responses.music.Albums;
import com.sesolutions.responses.music.MusicView;
import com.sesolutions.responses.music.ResultView;
import com.sesolutions.ui.clickclick.ActivityClickClick;
import com.sesolutions.ui.clickclick.discover.DiscoverAdapter;
import com.sesolutions.ui.customviews.CircularProgressBar;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.sesolutions.utils.URL.URL_ALL_MUSIC;
import static com.sesolutions.utils.URL.URL_MUSIC_FAV;

public class AddMusicFragment extends AddMusicHelper<DiscoverAdapter> implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public AppCompatTextView etStoreSearch;
    public String selectedScreen = "";
    public int size;
    public String searchKey;
    public int loggedinId;
    public int txtNoData;
    public SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recyclerView;
    public AddMusicAdapter adapter;
    private boolean isLoading;
    private int REQ_LOAD_MORE = 2;
    public ProgressBar pb;
    public RecyclerView rvQuotesCategory;
    public boolean isTag;
    public OnUserClickedListener<Integer, Object> parent;
    //variable used when called from page view -> associated
    private int mPageId;
    public ResultView result;
    private int selectedTab = 0;
    private List<Albums> profileList;
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    public RelativeLayout rlSearchFilter;
    public RecyclerView recycleViewInfo;
    public MusicCategoryAdapter musicCategoryAdapter;
    private boolean isPlaying = false;
    private int playingId;
    ProgressBar progress_bar_loding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_add_music_list, container, false);
        progress_bar_loding=v.findViewById(R.id.progress_bar_loding);
        applyTheme(v);
        initScreenData();

        return v;
    }

    public void init() {
        recyclerView = v.findViewById(R.id.recyclerview);
        pb = v.findViewById(R.id.pb);
        txtNoData = R.string.MSG_NO_ALBUM_MUSIC_SEARCH;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object screenType, int postion) {
        try {
            switch (object1) {
                case Constant.Events.MUSIC_FAVOURITE:
                    callLikeApi(postion);
                    break;
                case Constant.Events.MENU_MAIN:
                    for (int i = 0; i < profileList.size(); i++) {
                        profileList.get(i).setSelected(false);
                    }
                    musicCategoryAdapter.notifyDataSetChanged();
                    changeRecyclerView(postion);
                    break;
                case Constant.Events.ADD_MUSIC:
                    handler.removeCallbacksAndMessages(null);
                    Constant.songObj = null;
                    ((AddMusicActivity) activity).hideMusicLayout();
                    Constant.songObj = (com.sesolutions.responses.music.Albums) screenType;
                    Constant.musicid = postion;
                    Constant.songtitle = Constant.songObj.getTitle();
                    Constant.songPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/" + Constant.songObj.getTitle() + ".mp3";

                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    break;
                case Constant.Events.MUSIC_FAB_PLAY:
                    ((AddMusicActivity) activity).hideMusicLayout();
                    for (int i = 0; i < albumsList.size(); i++) {
                        albumsList.get(i).setPlaying(false);
                    }
                    isPlaying = true;
                    playingId = albumsList.get(postion).getMusicid();
                    albumsList.get(postion).setPlaying(true);
                    progress_bar_loding.setVisibility(View.VISIBLE);
                    progress_bar_loding.setClickable(false);
                    Runnable runnable2 = new Runnable() {
                        @Override
                        public void run() {
                            progress_bar_loding.setVisibility(View.GONE);
                            progress_bar_loding.setClickable(true);
                         }
                    };
                    new Handler().postDelayed(runnable2, 2000);

                    playMusic(albumsList.get(postion));
                    adapter.notifyDataSetChanged();
                    String duration = (String) screenType;
                    int foo = Integer.parseInt(duration);
                    handler.removeCallbacksAndMessages(null);
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < albumsList.size(); i++) {
                                albumsList.get(i).setPlaying(false);
                            }
                            isPlaying = false;
                            progress_bar_loding.setVisibility(View.GONE);
                            progress_bar_loding.setClickable(true);
                            ((AddMusicActivity) activity).stopMusicPlayer();
                            adapter.notifyDataSetChanged();
                        }
                    }, foo * 1000);
                    break;
                case Constant.Events.PRIVACY_CHANGED:
                    ((AddMusicActivity) activity).hideMusicLayout();
                    for (int i = 0; i < albumsList.size(); i++) {
                        albumsList.get(i).setPlaying(false);
                    }
                    isPlaying = false;
                    ((AddMusicActivity) activity).pause();
                    progress_bar_loding.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
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
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                            downloadSong(Constant.songObj.getSongUrl(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/",
                                    Constant.songObj.getTitle() + ".mp3");
                        }else {
                            DownloadSong(0,Constant.songObj.getTitle() ,Constant.songObj.getSongUrl());
                        }


                    }
                };
                new Handler().postDelayed(runnable, 100);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };

    File targetFile = null ;

    private void DownloadSong(int type,String filenamenew,String urlfile) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        com.ixuea.android.downloader.callback.DownloadManager downloadManager = DownloadService.getDownloadManager(context.getApplicationContext());
        //create download info set download uri and save path.
        targetFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), filenamenew+"_"+timeStamp+".mp4");
         DownloadInfo downloadInfo = new DownloadInfo.Builder().setUrl(urlfile)
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

                Constant.songPath=targetFile.getAbsolutePath();
                try {
                    ((AddMusicActivity) activity).stopMusicPlayer();
                    if (getActivity() != null) {
                        pDialog.dismiss();
                        getActivity().finish();
                    }
                }catch (Exception e){
                    e.printStackTrace();
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



    private ProgressDialog pDialog;

    public void downloadSong(String url, String dirPath, String fileName) {
        int downloadId = PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                       // Util.showSnackbar(v, "loading music");
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
                                    DecimalFormat percentFormat= new DecimalFormat("#.#%");

                                    Log.e("per:-",""+percentFormat);

                                    ((TextView) pDialog.findViewById(R.id.tvText)).setText(""+percentFormat.format(ratio));
                                    Log.e(""+progress.currentBytes,""+progress.totalBytes);
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
                        try {
                            ((AddMusicActivity) activity).stopMusicPlayer();
                            if (getActivity() != null) {
                                pDialog.dismiss();
                                getActivity().finish();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(Error error) {
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }




    public void changeRecyclerView(int pos) {
        try {
            selectedTab = pos;
            profileList.get(pos).setSelected(true);
            albumsList.clear();
            albumsList.addAll(result.getCategories().get(pos).getMusics().getResults());
            for (int i = 0; i < albumsList.size(); i++) {
                if (isPlaying && albumsList.get(i).getMusicid() == playingId) {
                    albumsList.get(i).setPlaying(true);
                } else {
                    albumsList.get(i).setPlaying(false);
                }
            }
            updateAdapter(true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callLikeApi(final int position) {

        try {
            if (isNetworkAvailable(context)) {
                try {

                    HttpRequestVO request = new HttpRequestVO(URL_MUSIC_FAV);

                    request.params.put(Constant.KEY_RESOURCE_ID, albumsList.get(position).getMusicid());
                    request.params.put(Constant.KEY_RESOURCES_TYPE, "tickvideo_music");
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse1", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    JSONObject json = new JSONObject(response);
                                    String message = json.getString(Constant.KEY_RESULT);
                                    if (message.equalsIgnoreCase("Item Favourite Successfully")) {
                                        albumsList.get(position).setContentFavourite(true);
                                    } else {
                                        albumsList.get(position).setContentFavourite(false);
                                    }
                                    adapter.notifyItemChanged(position);
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

    public void setRecyclerView(boolean fromSearch) {
        try {
            albumsList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            adapter = new AddMusicAdapter(albumsList, context, this, this);
            adapter.setType(selectedScreen);
           /* ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
            recyclerView.addItemDecoration(itemDecoration);*/
            recyclerView.setAdapter(adapter);
            if (!fromSearch) {
                swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
                swipeRefreshLayout.setOnRefreshListener(this);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void initScreenData() {
        init();
        setRecyclerView(false);
        callMusicAlbumApi(1, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.etStoreSearch:
//                goToSearchFragment()
                fragmentManager.beginTransaction().replace(R.id.container, new SearchAddMusicFragment()).addToBackStack(null).commit();
                break;
        }
    }

    public void callMusicAlbumApi(final int req, boolean fromDiscover) {


        if (isNetworkAvailable(context)) {
            isLoading = true;
            try {
                if (req == REQ_LOAD_MORE) {
                    pb.setVisibility(View.VISIBLE);
                } else if (req == 1) {
                    showBaseLoader(true);
                }
                HttpRequestVO request = new HttpRequestVO(URL_ALL_MUSIC); //url will change according to screenType
                request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                if (loggedinId > 0) {
                    request.params.put(Constant.KEY_USER_ID, loggedinId);
                }

                // used when this screen called from page view -> associated
                if (mPageId > 0) {
                    request.params.put(Constant.KEY_PAGE_ID, mPageId);
                }// used when this screen called from page view -> associated
                    /*if (categoryId > 0) {
                        request.params.put(Constant.KEY_CATEGORY_ID, categoryId);
                    }*/

                if (!TextUtils.isEmpty(searchKey)) {
                    request.params.put("title", searchKey);
                } else if (categoryId > 0) {
                    request.params.put(Constant.KEY_CATEGORY_ID, categoryId);
                }

                Map<String, Object> map = activity.filteredMap;
                if (null != map) {
                    request.params.putAll(map);
                }
                request.params.put(Constant.KEY_PAGE, null != result && req != 1 ? result.getNextPage() : 1);
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
                                    if (null != parent) {
                                        parent.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 1);
                                    }
                                    MusicView resp = new Gson().fromJson(response, MusicView.class);
                                    //if screen is refreshed then clear previous data
                                    if (req == Constant.REQ_CODE_REFRESH) {
                                        albumsList.clear();
                                    }
                                    wasListEmpty = albumsList.size() == 0;
                                    result = resp.getResult();


                                    if (null != result.getCategories() && result.getCategories().size()>0) {
                                        if (selectedTab > 0) {
                                            if (null != result.getCategories().get(selectedTab).getMusics().getResults()) {
                                                albumsList.addAll(result.getCategories().get(selectedTab).getMusics().getResults());
                                            }
                                        } else {
                                            albumsList.addAll(result.getCategories().get(0).getMusics().getResults());
                                        }

                                        if (fromDiscover) {
                                            setRecyclerViewProfileInfo();
                                            if (selectedTab > 0) {
                                                profileList.get(selectedTab).setSelected(true);
                                                recycleViewInfo.scrollToPosition(selectedTab);
                                            } else {
                                                profileList.get(0).setSelected(true);
                                            }
                                        }
                                        updateAdapter(fromDiscover);
                                    }else {
                                            ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
                                            v.findViewById(R.id.llNoData).setVisibility(View.VISIBLE);
                                    }


                                } else {
                                    Util.showSnackbar(v, err.getErrorMessage());
                                    goIfPermissionDenied(err.getError());
                                }
                            }

                        } catch (Exception e) {
                            hideBaseLoader();
                            CustomLog.e(e);
                            somethingWrongMsg(v);
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
    }


    public void hideLoaders() {
        isLoading = false;
        setRefreshing(swipeRefreshLayout, false);
        pb.setVisibility(View.GONE);
    }

    public void updateAdapter(boolean fromdiscover) {
        hideLoaders();
        if (fromdiscover) {
            musicCategoryAdapter.notifyDataSetChanged();
        }
        for (int i = 0; i < albumsList.size(); i++) {
            if (isPlaying && albumsList.get(i).getMusicid() == playingId) {
                albumsList.get(i).setPlaying(true);
            } else {
                albumsList.get(i).setPlaying(false);
            }
        }
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
        if (fromdiscover) {
            runLayoutAnimation(recycleViewInfo);
        }
        ((TextView) v.findViewById(R.id.tvNoData)).setText(txtNoData);
        v.findViewById(R.id.llNoData).setVisibility(albumsList.size() > 0 ? View.GONE : View.VISIBLE);
        if (parent != null) {
            parent.onItemClicked(Constant.Events.UPDATE_TOTAL, selectedScreen, result.getTotal());
        }
    }

    private void setRecyclerViewProfileInfo() {
        try {
            recycleViewInfo = v.findViewById(R.id.rvCategories);

            if (null != result.getCategories() && result.getCategories().size() > 0) {
                recycleViewInfo.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
                profileList = new ArrayList<>();
                profileList.addAll(result.getCategories());
                recycleViewInfo.setHasFixedSize(true);
                recycleViewInfo.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                musicCategoryAdapter = new MusicCategoryAdapter(profileList, context, this);
                recycleViewInfo.setAdapter(musicCategoryAdapter);
                recycleViewInfo.setNestedScrollingEnabled(false);
            } else {
                recycleViewInfo.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onLoadMore() {
        try {
            if (result != null && !isLoading) {
                if (result.getCurrentPage() < result.getTotalPage()) {
                    callMusicAlbumApi(REQ_LOAD_MORE, true);
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        CustomLog.e("AddmysucFragment", "show");
        ((AddMusicActivity) activity).showTop();
    }

    @Override
    public void onRefresh() {
        try {
            if (null != swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            callMusicAlbumApi(Constant.REQ_CODE_REFRESH, true);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }
}

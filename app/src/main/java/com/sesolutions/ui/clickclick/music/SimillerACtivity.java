package com.sesolutions.ui.clickclick.music;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.http.ParserCallbackInterface;
import com.sesolutions.listeners.OnLoadMoreListener;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.Video;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.ui.clickclick.ClickClickFragment;
import com.sesolutions.ui.clickclick.CreateClickClick;
import com.sesolutions.ui.clickclick.discover.VideoResponse;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.customviews.CircularProgressBar;
import com.sesolutions.ui.page.CreateEditPageFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sesolutions.ui.dashboard.ApiHelper.REQ_CODE_VIDEO;
import static com.sesolutions.utils.Constant.Events.MUSIC_MAIN;
import static com.sesolutions.utils.Constant.URL_SIMMILER_MUSIC;

public class SimillerACtivity extends BaseActivity implements OnUserClickedListener, OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, ParserCallbackInterface, MediaPlayer.OnCompletionListener{

    int Song_id=0;
    String songName="",VideoUrl="",Username="",song_url="",song_img="";
    String sharetitle="",shareDescritpion="",shareImageUrl="",shareeUrl="";
    Context context;
    private RecyclerView recyclerView;
    public MeAdapterMusic adapter;
    RelativeLayout usethissoundid;
    public List<Videos> albumsList;
    public VideoResponse.Result result2;
    TextView tvTitle,username,tvNoData,videocount,textviewid;
    ImageView ivBack,playicon,imageviewbtn;
    SwipeRefreshLayout swipeRefreshLayout;
    Share share_data;
    AppBarLayout appBar;
    boolean isplay=false;
    boolean isFirstTimeplay=true;
    ImageView ivAlbumImage;
    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();;
  //  private SeekBar songProgressBar;
    ProgressBar songProgressBar;
    RelativeLayout savevideorl;
    TextView musictimeid;

    @Override
    protected void onResume() {
        super.onResume();
        isFirstTimeplay=true;
    }

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();
            try {
                int sec= (int) (currentDuration/1000);
                int minute_new=sec/60;
                int new_sec=sec-(minute_new*60);

                if(minute_new>0){
                    musictimeid.setText(minute_new+":"+new_sec);
                }else {
                    if(new_sec>9){
                        musictimeid.setText("00"+":"+new_sec);
                    }else {
                        musictimeid.setText("00"+":0"+new_sec);
                    }
                }
                long percentage=0l;
                if(currentDuration>0){
                    percentage= (long) ((currentDuration*100/totalDuration));
                }

                songProgressBar.setProgress((int) percentage);

            }catch (NullPointerException ex){
                ex.printStackTrace();
            }
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similler_a_ctivity);
        context=this;
        recyclerView = findViewById(R.id.recyclerview);
        songProgressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        ivBack = findViewById(R.id.ivBack);
        savevideorl = findViewById(R.id.savevideorl);
        musictimeid = findViewById(R.id.musictimeid);
        imageviewbtn = findViewById(R.id.imageviewbtn);
        tvTitle = findViewById(R.id.tvTitledew);
        tvNoData = findViewById(R.id.tvNoData);
        username = findViewById(R.id.kk);
        usethissoundid = findViewById(R.id.usethissoundid);
        appBar = findViewById(R.id.appBar);
        playicon = findViewById(R.id.playicon);
        videocount = findViewById(R.id.videocount);
        ivAlbumImage = findViewById(R.id.ivAlbumImage);
        textviewid = findViewById(R.id.textviewid);
        Song_id=getIntent().getIntExtra("song_id",0);
        VideoUrl=getIntent().getStringExtra("MP4_image");
        songName=getIntent().getStringExtra("song_name");
        Username=getIntent().getStringExtra("Username");
        song_url=getIntent().getStringExtra("song_url");

        Constant.musicid = Song_id;
        Constant.songPath = song_url;
        Constant.songtitle = songName;

        Albums albums=new Albums();
        albums.setSongUrl(song_url);
        albums.setSongId(Song_id);
        albums.setTitle(songName);
        Constant.songObj=albums;

        fragmentManager = getSupportFragmentManager();
        sharetitle=getIntent().getStringExtra("sharetitle");
        shareDescritpion=getIntent().getStringExtra("shareDescritpion");
        shareImageUrl=getIntent().getStringExtra("shareImageUrl");
        shareeUrl=getIntent().getStringExtra("shareeUrl");
        if(getIntent().hasExtra("musicimage")){
            song_img=getIntent().getStringExtra("musicimage");
        }

        mp = new MediaPlayer();

        mp.setOnCompletionListener(this);




        if(song_img!=null && song_img.length()>0){
            Util.showImageWithGlide(ivAlbumImage,VideoUrl, context, R.drawable.default_song_img);
        }else {
            Glide.with(this).load(R.drawable.default_song_img).into(ivAlbumImage);
        }

        GradientDrawable drawable = (GradientDrawable) usethissoundid.getBackground();
        drawable.setColor(Color.parseColor(Constant.outsideButtonBackgroundColor));

        textviewid.setTextColor(Color.parseColor(Constant.outsideButtonTitleColor));
        imageviewbtn.setColorFilter(Color.parseColor(""+ Constant.outsideButtonTitleColor), android.graphics.PorterDuff.Mode.MULTIPLY);


        appBar.setVisibility(View.VISIBLE);
        findViewById(R.id.tvTitle).setVisibility(View.GONE);


        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setNestedScrollingEnabled(false);

        tvTitle.setText(""+songName);

        if(Username!= null && !Username.equalsIgnoreCase("null") && Username.length()>0){
            username.setText(""+Username);
            username.setVisibility(View.VISIBLE);
        }else {
            username.setVisibility(View.GONE);
        }


        appBar.setBackgroundColor(Color.parseColor(""+ Constant.backgroundColor));
        ivBack.setColorFilter(Color.parseColor(""+ Constant.text_color_1), android.graphics.PorterDuff.Mode.MULTIPLY);
         tvTitle.setTextColor(Color.parseColor(""+ Constant.text_color_1));
        swipeRefreshLayout.setOnRefreshListener(this);

     //   Util.downloadFile(context,song_url,songName,""+Song_id);



        setRecyclerView();
        callVideosApi();

        usethissoundid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            }
        });

        savevideorl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Albums albums=new Albums();
                albums.setSongUrl(song_url);
                albums.setSongId(Song_id);
                albums.setTitle(songName);
                String extension= Util.getMimeType(song_url);
                Constant.songPath=Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/"+songName+""+Song_id + "."+extension;
                File filedata=new File(Constant.songPath);
                if(!filedata.exists()){
                    //   Constant.songPath=song_url;
                    downloadSong2(song_url, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/",
                            songName+""+Song_id + "."+extension,albums);

                }else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        showNotification(Constant.songPath,songName);
                    }

                    Toast.makeText(SimillerACtivity.this,"Music saved successfully",Toast.LENGTH_SHORT).show();
                }
            }
        });


        playicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Albums albums=new Albums();
                albums.setSongUrl(song_url);
                albums.setSongId(Song_id);
                albums.setTitle(songName);
                String extension= Util.getMimeType(song_url);

                Constant.songObj = (Albums) albums;
                Constant.musicid = Song_id;
                Constant.songPath = song_url;

                Constant.songPath=Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/"+songName+""+Song_id + "."+extension;
                File filedata=new File(Constant.songPath);
                if(filedata.exists()){
                    if(mp.isPlaying()){
                        if(mp!=null){
                            mp.pause();
                            // Changing button image to play button
                            playicon.setImageResource(R.drawable.ic_play_arrow_white);
                            playicon.setColorFilter(Color.parseColor("#000000"));
                        }
                    }else{
                        if(isFirstTimeplay){
                            playSong(Constant.songPath);
                            isFirstTimeplay=false;
                            playicon.setImageResource(R.drawable.ic_pause_24dp_white);
                            playicon.setColorFilter(Color.parseColor("#000000"));

                        }else {
                            if(mp!=null){
                                mp.start();
                                // Changing button image to pause button
                                playicon.setImageResource(R.drawable.ic_pause_24dp_white);
                                playicon.setColorFilter(Color.parseColor("#000000"));
                            }
                        }
                    }
                }else {

                    playicon.setClickable(false);
                    downloadSong21(song_url, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/",
                            songName+""+Song_id + "."+extension,albums);

                   /* if(mp.isPlaying()){
                        if(mp!=null){
                            mp.pause();
                            // Changing button image to play button
                            playicon.setImageResource(R.drawable.ic_play_arrow_white);
                            playicon.setColorFilter(Color.parseColor("#000000"));
                        }
                    }else{
                        if(isFirstTimeplay){
                            playSong(song_url);
                            isFirstTimeplay=false;
                            playicon.setImageResource(R.drawable.ic_pause_24dp_white);
                            playicon.setColorFilter(Color.parseColor("#000000"));

                        }else {
                            if(mp!=null){
                                mp.start();
                                // Changing button image to pause button
                                playicon.setImageResource(R.drawable.ic_pause_24dp_white);
                                playicon.setColorFilter(Color.parseColor("#000000"));
                            }
                        }
                    }*/
                }




            /*    if(isFirstTimeplay){
                    isplay=true;
                    playicon.setImageResource(R.drawable.ic_pause_24dp_white);
                    songPicked(albums);
                    isFirstTimeplay=false;
                    playicon.setColorFilter(Color.parseColor("#000000"));
                }else {
                    if(isplay){
                        isplay=false;
                        playicon.setImageResource(R.drawable.ic_play_arrow_white);
                        playicon.setColorFilter(Color.parseColor("#000000"));
                        pause();
                    }else {
                        isplay=true;
                        playicon.setImageResource(R.drawable.ic_pause_24dp_white);
                        playicon.setColorFilter(Color.parseColor("#000000"));
                        start();
                    }
                }*/

              /*  Constant.songPath=Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/"+songName+""+Song_id + "."+extension;
                File filedata=new File(Constant.songPath);
                if(!filedata.exists()){
                 //   Constant.songPath=song_url;
                    downloadSong2(song_url, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/",
                            songName+""+Song_id + "."+extension,albums);

                }else {
                    if(isFirstTimeplay){
                        isplay=true;
                        playicon.setImageResource(R.drawable.ic_pause_24dp_white);
                        songPicked(albums);
                        isFirstTimeplay=false;
                        playicon.setColorFilter(Color.parseColor("#000000"));
                    }else {
                        if(isplay){
                            isplay=false;
                            playicon.setImageResource(R.drawable.ic_play_arrow_white);
                            playicon.setColorFilter(Color.parseColor("#000000"));
                            pause();
                        }else {
                            isplay=true;
                            playicon.setImageResource(R.drawable.ic_pause_24dp_white);
                            playicon.setColorFilter(Color.parseColor("#000000"));
                            start();
                        }
                    }
                }*/



            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    finish();

            }
        });


    }


    public void  playSong(String songpath){
        // Play song
        try {
            mp.reset();
            try {
                mp.setDataSource(songpath);
                mp.prepare();
                mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Displaying Song title
          /*  String songTitle = songsList.get(songIndex).get("songTitle");
            songTitleLabel.setText(songTitle);

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.btn_pause);*/

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void setRecyclerView() {
        try {
            albumsList = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new MeAdapterMusic(albumsList, context, this, this, Constant.FormType.TYPE_SONGS);
            recyclerView.setAdapter(adapter);
            recyclerView.setNestedScrollingEnabled(false);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void callVideosApi() {

        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                showBaseLoader(false);
                try {
                    HttpRequestVO request = new HttpRequestVO(URL_SIMMILER_MUSIC);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put("music_id", ""+Song_id);
                    request.params.put("page", "1");
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            Log.e("String Response",""+msg.toString());
                            String response = (String) msg.obj;
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    VideoResponse resp = new Gson().fromJson(response, VideoResponse.class);
                                    result2 = resp.getResult();
                                    albumsList.clear();

                                    if (null != result2.getVideos()) {
                                        albumsList.addAll(result2.getVideos());
                                        if(albumsList!=null && albumsList.size()>0){
                                            tvNoData.setVisibility(View.GONE);
                                            if(albumsList.size()==1){
                                                videocount.setText(albumsList.size()+" video");
                                            }else {
                                                videocount.setText(albumsList.size()+" videos");
                                            }
                                            videocount.setVisibility(View.VISIBLE);
                                        }else {
                                            tvNoData.setVisibility(View.VISIBLE);
                                            videocount.setVisibility(View.GONE);
                                        }

                                    }
                                    swipeRefreshLayout.setRefreshing(false);
                                    adapter.notifyDataSetChanged();
                                } else {
                                   Util.showToast(context, err.getErrorMessage());
                                    tvNoData.setVisibility(View.VISIBLE);
                                }
                            }
                            return true;
                        }
                    };
                    new HttpRequestHandler(this, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();
                    swipeRefreshLayout.setRefreshing(false);
                }

            } else {
                 Util.showToast(this, "Not connected to Internet");
                swipeRefreshLayout.setRefreshing(false);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            hideBaseLoader();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onLoadMore() {

    }

    public FragmentManager fragmentManager;

    @Override
    public void onBackPressed() {
        try {
            closeKeyboard();
            appBar.setBackgroundColor(Color.parseColor(""+ Constant.backgroundColor));
            ivBack.setColorFilter(Color.parseColor(""+ Constant.text_color_1), android.graphics.PorterDuff.Mode.MULTIPLY);

            super.onBackPressed();
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    @Override
    public boolean onItemClicked(Object eventType, Object data, int position) {
        try {
             switch ((Integer)eventType) {
                case MUSIC_MAIN:

                    appBar.setBackgroundColor(Color.parseColor("#000000"));
                    ivBack.setColorFilter(Color.parseColor("#ffffff"), android.graphics.PorterDuff.Mode.MULTIPLY);
                     //  fragmentManager.beginTransaction().replace(R.id.container, ClickClickFragment.newInstance(albumsList, true, true, postion)).addToBackStack(null).commit();
                    fragmentManager.beginTransaction()
                            .replace(R.id.containernew, ClickClickFragment.newInstance(albumsList,
                                    true, position, true))
                            .commit();


                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return false;
    }



    @Override
    public void onRefresh() {
        try {

            swipeRefreshLayout.setRefreshing(false);
            callVideosApi();
        } catch (Exception e) {
            CustomLog.e(e);
        }
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
                        String extension= Util.getMimeType(song_url);

                        String filepath=Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/"+songName+""+Song_id + "."+extension;

                        File file = new File(filepath);
                        if(!file.exists())
                        {
                            downloadSong(song_url, Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/",
                                    songName+""+Song_id + "."+extension);
                        }else {

                            Constant.musicid=Song_id;
                            Constant.songPath=Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/"+songName+""+Song_id + "."+extension;
                            Constant.songtitle=songName;
                            takeVideoFromCamera();
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


    private static final int CAMERA_VIDEO_REQUEST = 5687;
    private void takeVideoFromCamera() {
        // fimg = new File(image_path_source_temp + imageName);
        // Uri uri = Uri.fromFile(fimg);
        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SeSolutions/";
        String imageName = Constant.IMAGE_NAME + Util.getCurrentdate(Constant.TIMESTAMP);

        /* File dir = new File(imagePath);
        try {
            if (dir.mkdir()) {
            } else {
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }*/

        Constant.videoUri = null;
        MediaMetadataRetriever mtt=new MediaMetadataRetriever();
        mtt.setDataSource(Constant.songPath);
        String duration=mtt.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        isplay=false;
        playicon.setImageResource(R.drawable.ic_play_arrow_white);


        Intent cameraIntent = new Intent(this, CreateClickClick.class);
        cameraIntent.putExtra("path", imagePath);
        cameraIntent.putExtra("name", imageName);
        cameraIntent.putExtra("songPathSelect", true);
        cameraIntent.putExtra("record_video", true);
        cameraIntent.putExtra("music_duration", ""+duration);
        startActivityForResult(cameraIntent, CAMERA_VIDEO_REQUEST);
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
                            pDialog = ProgressDialog.show(context, "", "", true);
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

                        runOnUiThread(() -> {
                            try {
                                if (null != pDialog) {


                                    double ratio = progress.currentBytes / (double) progress.totalBytes;
                                    DecimalFormat percentFormat= new DecimalFormat("#%");

                                    android.util.Log.e("per:-",""+percentFormat);

                                    ((TextView) pDialog.findViewById(R.id.tvText)).setText(""+percentFormat.format(ratio));
                                    android.util.Log.e(""+progress.currentBytes,""+progress.totalBytes);
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
                        try {
                            if (this != null) {
                                pDialog.dismiss();
                                Constant.musicid=Song_id;
                                String extension= Util.getMimeType(song_url);
                                Constant.songPath=Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/"+songName+""+Song_id + "."+extension;

                                Constant.songtitle=songName;
                                takeVideoFromCamera();

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





    public void downloadSong2(String url, String dirPath, String fileName, Albums albums2) {
        int downloadId = PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        // Util.showSnackbar(v, "loading music");
                        try {
                            pDialog = ProgressDialog.show(context, "", "", true);
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

                        runOnUiThread(() -> {
                            try {
                                if (null != pDialog) {

                                    double ratio = progress.currentBytes / (double) progress.totalBytes;
                                    DecimalFormat percentFormat= new DecimalFormat("#%");
                                    android.util.Log.e("per:-",""+percentFormat);
                                    ((TextView) pDialog.findViewById(R.id.tvText)).setText(""+percentFormat.format(ratio));
                                    android.util.Log.e(""+progress.currentBytes,""+progress.totalBytes);
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

                        pDialog.dismiss();

                      /*  if(isFirstTimeplay){
                            isplay=true;
                            playicon.setImageResource(R.drawable.ic_pause_24dp_white);
                           // songPicked(albums2);
                            isFirstTimeplay=false;
                            playicon.setColorFilter(Color.parseColor("#ffffff"));
                        }else {
                            if(isplay){
                                isplay=false;
                                playicon.setImageResource(R.drawable.ic_play_arrow_white);
                                playicon.setColorFilter(Color.parseColor("#ffffff"));
                          //      pause();
                            }else {
                                isplay=true;
                                playicon.setImageResource(R.drawable.ic_pause_24dp_white);
                                playicon.setColorFilter(Color.parseColor("#ffffff"));
                             //   start();
                            }
                        }*/

                        Toast.makeText(SimillerACtivity.this,"Music saved successfully",Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            showNotification(Constant.songPath,fileName);
                        }

                    }
                    @Override
                    public void onError(Error error) {
                    }
                });
    }

    public void downloadSong21(String url, String dirPath, String fileName, Albums albums2) {
        int downloadId = PRDownloader.download(url, dirPath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        // Util.showSnackbar(v, "loading music");
                        try {
                          showBaseLoader(false);
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
                        hideBaseLoader();
                        playicon.setClickable(true);
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {



                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                        hideBaseLoader();
                        playicon.setClickable(true);
                        String extension= Util.getMimeType(song_url);
                        Constant.songPath=Environment.getExternalStorageDirectory().getAbsolutePath() + "/Vavci/"+songName+""+Song_id + "."+extension;
                        File filedata=new File(Constant.songPath);
                        if(filedata.exists()){
                            if(mp.isPlaying()){
                                if(mp!=null){
                                    mp.pause();
                                    // Changing button image to play button
                                    playicon.setImageResource(R.drawable.ic_play_arrow_white);
                                    playicon.setColorFilter(Color.parseColor("#000000"));
                                }
                            }else{
                                if(isFirstTimeplay){
                                    playSong(Constant.songPath);
                                    isFirstTimeplay=false;
                                    playicon.setImageResource(R.drawable.ic_pause_24dp_white);
                                    playicon.setColorFilter(Color.parseColor("#000000"));

                                }else {
                                    if(mp!=null){
                                        mp.start();
                                        // Changing button image to pause button
                                        playicon.setImageResource(R.drawable.ic_pause_24dp_white);
                                        playicon.setColorFilter(Color.parseColor("#000000"));
                                    }
                                }
                            }
                        }

                    }
                    @Override
                    public void onError(Error error) {
                        hideBaseLoader();
                        playicon.setClickable(true);
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
        intent.setDataAndType(apkURI, "video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // intent.setDataAndType(Uri.fromFile(file), "video/*");
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        int notifyID = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channels);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = null;
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            Notification notification = new Notification.Builder(context)
                    .setContentTitle("Audio Music File Downloaded Successfully.")
                    .setContentText(title + " has been downloaded successfully, click here to view.")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setChannelId(CHANNEL_ID)
                    .setContentIntent(pIntent)
                    .build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(notifyID, notification);
        } else {
            NotificationCompat.Builder builder2 = new NotificationCompat.Builder(context, "0");
            NotificationManager notifManager = (NotificationManager) getSystemService
                    (Context.NOTIFICATION_SERVICE);
            builder2.setContentTitle("Audio Music File Downloaded Successfully.")
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

    private String imageFilePath;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CustomLog.e("onActivityResult", "requestCode : " + requestCode + " resultCode : " + resultCode);
        try {
            switch (requestCode) {
                case CAMERA_VIDEO_REQUEST:

                    String selectedImagePath = null;
                    //ImagePath will use to upload video
                    if (Constant.path != null) {
                        selectedImagePath = Constant.path;
                        CustomLog.e("VIDEO_REQUEST", "" + selectedImagePath);
                    }

                    if (selectedImagePath != null) {
                        List<String> photoPaths = new ArrayList<>();
                        photoPaths.add(selectedImagePath);
//                        Constant.videoUri = selectedImageUri;
                        imageFilePath = selectedImagePath;
                        onResponseSuccess(REQ_CODE_VIDEO, photoPaths);
                        //  videoView.setVideoPath(selectedImagePath);
                    }
//                    Util.showToast(getApplicationContext(), "Video Recorded successfully.");
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public Video videoDetail;
    @Override
    public void onResponseSuccess(int reqCode, Object result) {
        try {
            switch (reqCode) {
                case REQ_CODE_VIDEO:
                    if (result != null) {
                        String filePath = ((List<String>) result).get(0);
                        videoDetail = new Video();
                        videoDetail.setFromDevice(true);
                        videoDetail.setSrc(filePath);
                        CustomLog.e("video src", "" + videoDetail.getSrc());
                        Map<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_SONG_ID, Constant.musicid);
                        map.put(Constant.KEY_VIDEO, videoDetail.getSrc());
                        map.put("moduleName", "sesvideo");
                        map.put("not_merge_song", "1");
                        Constant.path = null;
                        Constant.musicid = 0;
                        getSupportFragmentManager().beginTransaction().replace(R.id.containernew, CreateEditPageFragment.newInstance(Constant.FormType.CREATE_TICK_CUSTOM, map, Constant.URL_TICK_CREATE, null, false)).addToBackStack(null).commitAllowingStateLoss();
//                        submit();
                        break;
                    }
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {
    }

    public void shareOutside(String title,String description,String imageurl,String urlData) {
        // UrlParams urlParams = sharelist.getUrlParams();
        try {
            final Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            if (!TextUtils.isEmpty(urlData)) {
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, urlData);
                startActivityForResult(Intent.createChooser(sharingIntent, getString(R.string.MSG_SHARE_VIA)), 25);
            } else if (TextUtils.isEmpty(imageurl)) {
                sharingIntent.setType("text/plain");
                if (TextUtils.isEmpty(description)) {
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, title);
                } else {
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, description);
                }
                startActivityForResult(Intent.createChooser(sharingIntent, getString(R.string.MSG_SHARE_VIA)), 25);
            } else {
                        showBaseLoader(true);
                Glide.with(context).asBitmap()
                        .load(imageurl)//"https://www.google.es/images/srpr/logo11w.png")
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                try {
                                    hideBaseLoader();
                                    sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    sharingIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));
                                    sharingIntent.setType("image/*");
                                    //sharingIntent.setType("image/*");
                                    startActivityForResult(Intent.createChooser(sharingIntent, getString(R.string.MSG_SHARE_VIA)), 25);
                                } catch (Exception e) {
                                    CustomLog.e(e);
                                    startActivityForResult(Intent.createChooser(sharingIntent, getString(R.string.MSG_SHARE_VIA)), 25);
                                }

                            }
                        });
           }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            //  File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            //FileOutputStream out = null;
            try {
                // out = new FileOutputStream(file);
                // bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(
                        context.getContentResolver(), bmp, "Image", null);
               /* try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                bmpUri = Uri.parse(path);
                /*if (Build.VERSION.SDK_INT < 24) {
                    bmpUri = Uri.fromFile(file);
                } else {
                    bmpUri = Uri.parse(file.getPath());
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return bmpUri;
    }



    @Override
    protected void onPause() {
        super.onPause();
          if(mp.isPlaying()){
              mp.pause();
              playicon.setImageResource(R.drawable.ic_play_arrow_white);
              playicon.setColorFilter(Color.parseColor("#000000"));
          }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        songProgressBar.setProgress(0);
        mp.reset();
        isFirstTimeplay=true;
        playicon.setImageResource(R.drawable.ic_play_arrow_white);
        playicon.setColorFilter(Color.parseColor("#000000"));
    }


}
package com.sesolutions.ui.clickclick;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.SessionType;
import com.otaliastudios.cameraview.Size;
import com.sesolutions.R;
import com.sesolutions.animate.Techniques;
import com.sesolutions.animate.YoYo;
import com.sesolutions.camerahelper.Control;
import com.sesolutions.camerahelper.ControlView;
import com.sesolutions.http.ParserCallbackInterface;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Video;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.clickclick.music.AddMusicActivity;
import com.sesolutions.ui.common.MainApplication;
import com.sesolutions.ui.customviews.AnimationAdapter;
import com.sesolutions.ui.musicplayer.MusicService;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;


public class CreateClickClick extends AppCompatActivity implements View.OnClickListener, ControlView.Callback, ParserCallbackInterface, MediaController.MediaPlayerControl, OnUserClickedListener<Integer, Object> {

    private CameraView camera;
    private ViewGroup controlPanel;
    public ProgressDialog progressDialog;
    public ProgressDialog progressDialog2;
    public ProgressDialog progressDialog3;
    public Video videoDetail;
    public static final int REQ_CODE_VIDEO = 2;
    private boolean mCapturingPicture;
    private boolean mCapturingVideo;
    private ProgressBar mProgressBar;
    private int i = 0;
    private int timer = 15;
    private int timer212 = 15;
    private ViewTooltip viewTooltip;
    private CountDownTimer progressBarTimer;
    private CountDownTimer startCountdownTimer;
    private int startTimer;
    ArrayList<String> selectedImageList;
    // To show stuff in the callback
    private Size mCaptureNativeSize;
    private long mCaptureTime;
    private static final int ADD_MUSIC = 7999;
    private static final int UPLOAD = 9999;
    public static final int PERMISSION_CONSTANT = 1054;
    //variables used to set media path and name
    private String path;
    private String imageName;
    private TextView tvSongName;
    private ImageView ivMusic;
    public boolean fromGallery = false;
    //variable used to find action type {IMAGE_CAPTURE,VIDEO_RECORD}
    private boolean hasToRecordVideo;

    ImageView ic_pauseid;
    File file1_re, file2_re, file3_re, file4_re, file5_re, file6_re;
    int Pause_counter = 1;
    boolean pause1 = true, pause2 = false, pause3 = false, pause4 = false, pause5 = false;
    public CardView cvMusicMain;
    private TextView tvSongTitle;
    private ImageView ivSongImage;
    Boolean play_music=false;

//    ArrayList<EpVideo> epVideos =  new  ArrayList<>();

    int menuTitleActiveColor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.create_tiktok);
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE);
        ic_pauseid = findViewById(R.id.ic_pauseid);
        path = getIntent().getStringExtra("path");
        imageName = getIntent().getStringExtra("name");
        hasToRecordVideo = getIntent().getBooleanExtra("record_video", false);
        menuTitleActiveColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
        camera = findViewById(R.id.camera);
        camera.setSessionType(SessionType.VIDEO);
       // ((AppCompatImageView) findViewById(R.id.captureVideo)).setColorFilter(menuTitleActiveColor);

        GradientDrawable drawable = (GradientDrawable) findViewById(R.id.captureVideo).getBackground();
        drawable.setColor(menuTitleActiveColor);
        ic_pauseid.setColorFilter(menuTitleActiveColor);



        try {
            timer212=Integer.parseInt(duration_song)/1000;
        }catch (Exception e){
            e.printStackTrace();
        }



        ic_pauseid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Pause_counter == 1) {
                    if(musicselected)
                    pause();

                    findViewById(R.id.toggleCamera).setVisibility(View.GONE);
                    Pause_counter = 100;
                    pause1 = false;
                    pause2 = true;
                    pause3 = false;
                    pause4 = false;
                    pause5 = false;
                    ic_pauseid.setImageResource(R.drawable.ic_baseline_play);
                    ic_pauseid.setColorFilter(menuTitleActiveColor);
                    if (progressBarTimer != null) {
                        progressBarTimer.cancel();
                    }
                    camera.stopCapturingVideo();
                }
                else if (Pause_counter == 100) {
                    Pause_counter = 200;
                    ic_pauseid.setImageResource(R.drawable.ic_baseline_pause);
                    ic_pauseid.setColorFilter(menuTitleActiveColor);
                    mCapturingVideo = false;
                    if(musicselected)
                    start();
                    captureVideo();
                }
                else if (Pause_counter == 200) {
                    findViewById(R.id.toggleCamera).setVisibility(View.GONE);
                    Pause_counter = 300;
                    pause1 = false;
                    pause2 = false;
                    pause3 = true;
                    pause4 = false;
                    pause5 = false;
                    ic_pauseid.setImageResource(R.drawable.ic_baseline_play);
                    ic_pauseid.setColorFilter(menuTitleActiveColor);
                    if (progressBarTimer != null) {
                        progressBarTimer.cancel();
                    }
                    if(musicselected)
                    pause();
                    camera.stopCapturingVideo();
                } else if (Pause_counter == 300) {
                    Pause_counter = 400;
                    ic_pauseid.setImageResource(R.drawable.ic_baseline_pause);
                    ic_pauseid.setColorFilter(menuTitleActiveColor);
                    mCapturingVideo = false;
                    if(musicselected)
                    start();
                    captureVideo();
                } else if (Pause_counter == 400) {
                    findViewById(R.id.toggleCamera).setVisibility(View.GONE);
                    Pause_counter = 500;
                    pause1 = false;
                    pause2 = false;
                    pause3 = false;
                    pause4 = true;
                    pause5 = false;
                    ic_pauseid.setImageResource(R.drawable.ic_baseline_play);
                    ic_pauseid.setColorFilter(menuTitleActiveColor);
                    if (progressBarTimer != null) {
                        progressBarTimer.cancel();
                    }
                    if(musicselected)
                    pause();
                    camera.stopCapturingVideo();
                } else if (Pause_counter == 500) {
                    Pause_counter = 600;
                    ic_pauseid.setImageResource(R.drawable.ic_baseline_pause);
                    ic_pauseid.setColorFilter(menuTitleActiveColor);
                    mCapturingVideo = false;
                    if(musicselected)
                    start();
                    captureVideo();
                } else if (Pause_counter == 600) {
                    findViewById(R.id.toggleCamera).setVisibility(View.GONE);
                    Pause_counter = 700;
                    pause1 = false;
                    pause2 = false;
                    pause3 = false;
                    pause4 = false;
                    pause5 = true;
                    ic_pauseid.setImageResource(R.drawable.ic_baseline_play);
                    ic_pauseid.setColorFilter(menuTitleActiveColor);
                    if (progressBarTimer != null) {
                        progressBarTimer.cancel();
                    }
                    if(musicselected)
                    pause();
                    camera.stopCapturingVideo();
                } else if (Pause_counter == 700) {
                    Pause_counter = 800;
                    ic_pauseid.setImageResource(R.drawable.ic_baseline_pause);
                    ic_pauseid.setColorFilter(menuTitleActiveColor);
                    mCapturingVideo = false;
                    if(musicselected)
                    start();
                    captureVideo();
                }

            }
        });
        camera.addCameraListener(new CameraListener() {
            public void onCameraOpened(CameraOptions options) {
                if (galleryfrom != 100) {
                    onOpened();
                }
            }

            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
                onVideoRecorded(video);
            }
        });

        tvSongTitle = findViewById(R.id.tvSongTitle);
        cvMusicMain = findViewById(R.id.cvMusicMain);
        findViewById(R.id.edit).setOnClickListener(this);
        findViewById(R.id.ivGallery).setOnClickListener(this);
        findViewById(R.id.llGallery).setOnClickListener(this);
        findViewById(R.id.ivCross).setOnClickListener(this);
        findViewById(R.id.ivMusic).setOnClickListener(this);
        findViewById(R.id.capturePhoto).setOnClickListener(this);
        findViewById(R.id.tv15).setOnClickListener(this);
        findViewById(R.id.tv60).setOnClickListener(this);
        findViewById(R.id.captureVideo).setOnClickListener(this);
        inittooltip();
        findViewById(R.id.toggleCamera).setOnClickListener(this);
        findViewById(R.id.toggleFlash).setOnClickListener(this);
        findViewById(R.id.toggleTimer).setOnClickListener(this);

        camera.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    CustomLog.e("TEST", "onDoubleTap");
                    if(!play_music){
                        toggleCamera();
                    }
                     return super.onDoubleTap(e);
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CustomLog.e("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        tvSongName = findViewById(R.id.tvSongName);
        ivMusic = findViewById(R.id.ivMusic);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setProgress(i);
    //    ivMusic.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        if (hasToRecordVideo) {
            findViewById(R.id.capturePhoto).setVisibility(View.GONE);
        } else {
            findViewById(R.id.captureVideo).setVisibility(View.GONE);
        }

        controlPanel = findViewById(R.id.controls);
        ViewGroup group = (ViewGroup) controlPanel.getChildAt(0);
        Control[] controls = Control.values();
        for (Control control : controls) {
            ControlView view = new ControlView(this, control, this);
            group.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        controlPanel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
                b.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });


        if(getIntent().hasExtra("songPathSelect")){
          Util.showToast(getApplicationContext(), "Music Selected: " + Constant.songtitle);
            tvSongName.setVisibility(View.VISIBLE);
            ivMusic.setVisibility(View.VISIBLE);
            tvSongName.setText("" + Constant.songtitle);
            musicselected=true;
            duration_song = getIntent().getStringExtra("music_duration");
            try {
                timer212=Integer.parseInt(duration_song)/1000;
            }catch (Exception e){
                e.printStackTrace();
            }
          }

    }

    private void inittooltip(){
        ViewTooltip
                .on(this, findViewById(R.id.captureVideo))
                .corner(30)
                .clickToHide(true)
                .textColor(Color.WHITE)
                .color(menuTitleActiveColor)
                .position(ViewTooltip.Position.TOP)
                .text("Click to Record.")
                .show();

    }
    private void message(String content, boolean important) {
        int length = important ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast.makeText(this, content, length).show();
    }

    private void onOpened() {
        ViewGroup group = (ViewGroup) controlPanel.getChildAt(0);
        for (int i = 0; i < group.getChildCount(); i++) {
            ControlView view = (ControlView) group.getChildAt(i);
            view.onCameraOpened(camera);
        }
    }

    protected void openVideoPicker(boolean videoSelected) {
        isVideoSelected = videoSelected;
        askForPermission(Manifest.permission.CAMERA);
    }

/*

    private void mergeVideos() {

        epVideos.add(new EpVideo (file2)); // Video 1
        epVideos.add(new EpVideo (file1)); // Video 2
        EpEditor. OutputOption outputOption =new EpEditor.OutputOption(Constant.path);
        outputOption.setWidth(720);
        outputOption.setHeight(1280);
        outputOption.frameRate = 25 ;
        outputOption.bitRate = 10 ;
        EpEditor.merge(epVideos, outputOption, new  OnEditorListener() {
            @Override
            public  void  onSuccess () {
                Log.d("Status","Success");

            }

            @Override
            public  void  onFailure () {

            }

            @Override
            public  void  onProgress ( float  progress ) {
                // Get processing progress here
                Log.d("Progress",""+progress);
            }
        });

    }
*/


    boolean musicselected=false;
    String duration_song="";
    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //    super.onActivityResult(requestCode, resultCode, data);
        CustomLog.e("onActivityResult", "requestCode : " + requestCode + " resultCode : " + resultCode);
        try {
            switch (requestCode) {
                case FilePickerConst.REQUEST_CODE_PHOTO:
                    if (resultCode == -1 && data != null) {
                        List<String> photoPaths = new ArrayList<>(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                        for (String path : photoPaths) {
                            if (path.endsWith(".mp4")) {
                                Constant.videoUri = Uri.fromFile(new File(photoPaths.get(0)));
                                onResponseSuccess(REQ_CODE_VIDEO, photoPaths);
                            }
                        }
                    }
                    break;
                case UPLOAD:
                    if (resultCode == Activity.RESULT_OK) {
                        String result=data.getStringExtra("result");
                        Log.e("data",""+result);
                        if(result.equalsIgnoreCase("OK")){
                            Log.e("2222","dhjdh");
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result","OK");
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }else {
                            Log.e("1111","dhjdh");
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result","CANSEL");
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    }else {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result","CANSEL");
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }


                   /* if (resultCode == Activity.RESULT_OK) {
                        finish();
                    } else {
                        if (fromGallery) {
                            openVideoPicker(true);
                        } else {
                            finish();
                        }
                    }*/
                    break;
                case ADD_MUSIC:
                    if (Constant.musicid == 0) {
                        Util.showToast(getApplicationContext(), "No music selected.");
                        musicselected=false;
                    } else {
                        Util.showToast(getApplicationContext(), "Music Selected: " + Constant.songtitle);
                        tvSongName.setVisibility(View.VISIBLE);
                        ivMusic.setVisibility(View.VISIBLE);
                        tvSongName.setText("" + Constant.songtitle);
                        musicselected=true;
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        mmr.setDataSource(Constant.songPath);
                        duration_song = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        mmr.release();
                        try {
                            timer212=Integer.parseInt(duration_song)/1000;
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    int galleryfrom = 0;

    @Override
    public void onResponseSuccess(int reqCode, Object result) {
        try {
            switch (reqCode) {
                case REQ_CODE_VIDEO:
                    if (result != null) {
                        galleryfrom = 100;
                        String filePath = ((List<String>) result).get(0);
                        videoDetail = new Video();
                        videoDetail.setFromDevice(true);
                        videoDetail.setSrc(filePath);
                        onVideo(new File(filePath));
                        break;
                    }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {

    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getApplication().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public String saveImageToCard(byte[] jpeg) {
        // fimg = new File(image_path_source_temp + imageName);
        // Uri uri = Uri.fromFile(fimg);
        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SeSolutions/";
        String imageName = Constant.IMAGE_NAME + Util.getCurrentdate(Constant.TIMESTAMP);


        try {
            if (new File(imagePath).mkdir()) {
            } else {
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

        String path = imagePath + imageName + ".jpg";
        File photo = new File(imagePath, imageName + ".jpg");
       /* if (photo.exists()) {
            photo.delete();
        }*/

        try {
            FileOutputStream fos = new FileOutputStream(photo.getPath());

            fos.write(jpeg);
            fos.close();
        } catch (IOException e) {
            CustomLog.e("PictureDemo", "Exception in photoCallback", e);
        }

        return path;

    }

    private File getFilePath() {

      //  String imagePath =getApplicationContext() Environment.getExternalStorageDirectory().getAbsolutePath() + "/SeSolutions/";
        String imagePath =getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/SeSolutions/";
        String imageName = "";
        if (Pause_counter == 200) {
            imageName = Constant.IMAGE_NAME + Util.getCurrentdate(Constant.TIMESTAMP) + "file2";
        } else if (Pause_counter == 400) {
            imageName = Constant.IMAGE_NAME + Util.getCurrentdate(Constant.TIMESTAMP) + "file3";
        } else {
            imageName = Constant.IMAGE_NAME + Util.getCurrentdate(Constant.TIMESTAMP) + "file1";
        }

        try {
            if (new File(imagePath).mkdir()) {
            } else {
            }
        } catch (Exception e) {
            CustomLog.e(e);
            return null;
        }

        return new File(imagePath, imageName + ".mp4");

    }




    private void onVideo(File video) {
        mCapturingVideo = false;
        if (galleryfrom == 100) {
            camera.stop();
        } else {
            updateRecordingIcon();
        }
        Constant.musicid=0;

        Intent intent = new Intent(CreateClickClick.this, ClickClickPreview2.class);
        intent.putExtra("EXTRA_VIDEO_PATH", Uri.fromFile(video).toString());
        intent.putExtra("video", Uri.fromFile(video));
        intent.putExtra("videouri", video.getPath());
        Constant.path = video.getPath();
        startActivityForResult(intent, UPLOAD);

//        finish();
    }


    File combined_1st, combined_2nd;

    private void onVideoRecorded(File video) {
        mCapturingVideo = false;
     //   updateRecordingIcon();
        if (Pause_counter == 100) {
            file1_re = video;
        } else if (Pause_counter == 300) {
            file2_re = video;
        } else if (Pause_counter == 500) {
            file3_re = video;
        } else if (Pause_counter == 700) {
            file4_re = video;
        } else {
            file5_re = video;
        //    epVideos=new ArrayList<>();
            Runnable runnable4 = new Runnable() {
                @Override
                public void run() {
                  /*  Intent intent = new Intent(CreateClickClick.this, ClickClickPreview.class);
                    intent.putExtra("EXTRA_VIDEO_PATH", Uri.fromFile(new File(Constant.path)).toString());
                    intent.putExtra("video", Uri.fromFile(new File(Constant.path)));
                    startActivityForResult(intent, UPLOAD);*/

                    Intent intent = new Intent(CreateClickClick.this, ClickClickPreview2.class);
                    intent.putExtra("EXTRA_VIDEO_PATH", Uri.fromFile(video).toString());
                    intent.putExtra("video", Uri.fromFile(video));
                    intent.putExtra("videouri", video.getPath());
                    Constant.path = video.getPath();
                    startActivityForResult(intent, UPLOAD);

                }};
            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    if (pause1) {
                        Constant.path = file5_re.getPath();
                        new Handler().postDelayed(runnable4, 100);
                        //  finish();
                    } else if (pause2 && file1_re!=null) {
                        int file_size1 = Integer.parseInt(String.valueOf(file1_re.length()/1024));
                        int file_size5 = Integer.parseInt(String.valueOf(file5_re.length()/1024));
                        if(file_size1>500 && file_size5>500){
                           Constant.path = appendTwoVideos(file1_re.getPath(), file5_re.getPath(), 1);
                        }else if(file_size1>500 && file_size5<500){
                           Constant.path = file1_re.getPath();
                          //  epVideos.add(new EpVideo (file1_re.getPath())); // Video 1
                         }else {
                           Constant.path = file5_re.getPath();
                           // epVideos.add(new EpVideo (file5_re.getPath())); // Video 1
                        }
                        new Handler().postDelayed(runnable4, 500);
                        //   finish();
                    } else if (pause3 && file1_re!=null && file2_re!=null) {
                        int file_size1 = Integer.parseInt(String.valueOf(file1_re.length()/1024));
                        int file_size2 = Integer.parseInt(String.valueOf(file2_re.length()/1024));
                        int file_size5 = Integer.parseInt(String.valueOf(file5_re.length()/1024));

                        if(file_size1>500 && file_size2>500 && file_size5>500){
                            Constant.path = appendTwoVideos2(file1_re.getPath(), file2_re.getPath(), file5_re.getPath(), 1);
                        }else if(file_size1>500 && file_size2<500 && file_size5>500){
                            Constant.path = appendTwoVideos(file1_re.getPath(), file5_re.getPath(), 1);
                        }else {
                           Constant.path = appendTwoVideos(file2_re.getPath(), file5_re.getPath(), 1);
                        }

                        new Handler().postDelayed(runnable4, 800);
                      //  finish();
                    } else if (pause4 && file1_re!=null && file2_re!=null && file3_re!=null) {
                        int file_size1 = Integer.parseInt(String.valueOf(file1_re.length()/1024));
                        int file_size2 = Integer.parseInt(String.valueOf(file2_re.length()/1024));
                        int file_size3 = Integer.parseInt(String.valueOf(file3_re.length()/1024));
                        int file_size5 = Integer.parseInt(String.valueOf(file5_re.length()/1024));
                        if(file_size1>500 && file_size2>500 && file_size3>500) {
                             Constant.path = appendTwoVideos3(file1_re.getPath(), file2_re.getPath(), file3_re.getPath(), file5_re.getPath(), 1);
                        }
                        else if(file_size1>500 && file_size2<500 && file_size3>500){
                          Constant.path = appendTwoVideos2(file1_re.getPath(), file3_re.getPath(), file5_re.getPath(), 1);
                        }else if(file_size1<500 && file_size2>500 && file_size3>500){


                            Constant.path = appendTwoVideos2(file2_re.getPath(), file3_re.getPath(), file5_re.getPath(), 1);
                        }else if(file_size1>500 && file_size2>500 && file_size3<500){
                            Constant.path = appendTwoVideos2(file1_re.getPath(), file2_re.getPath(), file5_re.getPath(), 1);
                        }else {
                            Constant.path = file5_re.getPath();
                        }

                        new Handler().postDelayed(runnable4, 900);
                       // finish();
                    } else if (pause5 && file1_re!=null && file2_re!=null && file3_re!=null && file4_re!=null) {
                        int file_size1 = Integer.parseInt(String.valueOf(file1_re.length()/1024));
                        int file_size2 = Integer.parseInt(String.valueOf(file2_re.length()/1024));
                        int file_size3 = Integer.parseInt(String.valueOf(file3_re.length()/1024));
                        int file_size4 = Integer.parseInt(String.valueOf(file4_re.length()/1024));
                        int file_size5 = Integer.parseInt(String.valueOf(file5_re.length()/1024));
                        if(file_size1>500 && file_size2>500 && file_size3>500 && file_size4>500 && file_size5>500) {
                            Constant.path = appendTwoVideos4(file1_re.getPath(), file2_re.getPath(), file3_re.getPath(), file4_re.getPath(), file5_re.getPath(), 1);
                        }
                        else if(file_size1>500 && file_size2<500 && file_size3>500){
                            Constant.path = appendTwoVideos2(file1_re.getPath(), file3_re.getPath(), file5_re.getPath(), 1);
                        }else if(file_size1<500 && file_size2>500 && file_size3>500){
                            Constant.path = appendTwoVideos2(file2_re.getPath(), file3_re.getPath(), file5_re.getPath(), 1);
                        }else if(file_size1>500 && file_size2>500 && file_size3<500){
                            Constant.path = appendTwoVideos2(file1_re.getPath(), file2_re.getPath(), file5_re.getPath(), 1);
                        }else {
                            Constant.path = file5_re.getPath();
                        }

                        new Handler().postDelayed(runnable4, 1200);
                       //  finish();
                    } else {
                        int file_size5 = Integer.parseInt(String.valueOf(file5_re.length()/1024));
                        if(file_size5>10){
                            Constant.path = file5_re.getPath();
                        }
                        new Handler().postDelayed(runnable4, 1500);

                        //finish();
                    }



                }
            };
            new Handler().postDelayed(runnable, 400);
        }
    }

    private String appendTwoVideos(String firstVideoPath, String secondVideoPath, int finatstage) {
        try {
            Movie[] inMovies = new Movie[2];

            inMovies[0] = MovieCreator.build(firstVideoPath);
            inMovies[1] = MovieCreator.build(secondVideoPath);

            List<Track> videoTracks = new LinkedList<>();
            List<Track> audioTracks = new LinkedList<>();

            for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                }
            }

            //This will append video files together in a sequence they were added. Output will be a single video file.


            Movie result = new Movie();

            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }

            BasicContainer out = (BasicContainer) new DefaultMp4Builder().build(result);

            @SuppressWarnings("resource")
            FileChannel fc = new RandomAccessFile(Environment.getExternalStorageDirectory() + "/vavcivideo" + Util.getCurrentdate(Constant.TIMESTAMP) + ".mp4", "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/vavcivideo" + Util.getCurrentdate(Constant.TIMESTAMP) + ".mp4";
        return mFileName;
    }

    private String appendTwoVideos2(String firstVideoPath, String secondVideoPath, String thirdVideoPath, int finatstage) {
        try {
            Movie[] inMovies = new Movie[3];

            inMovies[0] = MovieCreator.build(firstVideoPath);
            inMovies[1] = MovieCreator.build(secondVideoPath);
            inMovies[2] = MovieCreator.build(thirdVideoPath);

            List<Track> videoTracks = new LinkedList<>();
            List<Track> audioTracks = new LinkedList<>();

            for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                }
            }

            Movie result = new Movie();

            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }

            BasicContainer out = (BasicContainer) new DefaultMp4Builder().build(result);

            @SuppressWarnings("resource")
            FileChannel fc = new RandomAccessFile(Environment.getExternalStorageDirectory() + "/vavcivideo" + Util.getCurrentdate(Constant.TIMESTAMP) + ".mp4", "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/vavcivideo" + Util.getCurrentdate(Constant.TIMESTAMP) + ".mp4";
        return mFileName;
    }

    private String appendTwoVideos3(String firstVideoPath, String secondVideoPath, String thirdVideoPath, String fourthVideoPath, int finatstage) {
        try {
            Movie[] inMovies = new Movie[4];

            inMovies[0] = MovieCreator.build(firstVideoPath);
            inMovies[1] = MovieCreator.build(secondVideoPath);
            inMovies[2] = MovieCreator.build(thirdVideoPath);
            inMovies[3] = MovieCreator.build(fourthVideoPath);

            List<Track> videoTracks = new LinkedList<>();
            List<Track> audioTracks = new LinkedList<>();

            for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                }
            }

            Movie result = new Movie();

            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }

            BasicContainer out = (BasicContainer) new DefaultMp4Builder().build(result);

            @SuppressWarnings("resource")
            FileChannel fc = new RandomAccessFile(Environment.getExternalStorageDirectory() + "/vavcivideo" + Util.getCurrentdate(Constant.TIMESTAMP) + ".mp4", "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/vavcivideo" + Util.getCurrentdate(Constant.TIMESTAMP) + ".mp4";
        return mFileName;
    }

    private String appendTwoVideos4(String firstVideoPath, String secondVideoPath, String thirdVideoPath, String fourthVideoPath, String fifthVideoPath, int finatstage) {
        try {
            Movie[] inMovies = new Movie[5];

            inMovies[0] = MovieCreator.build(firstVideoPath);
            inMovies[1] = MovieCreator.build(secondVideoPath);
            inMovies[2] = MovieCreator.build(thirdVideoPath);
            inMovies[3] = MovieCreator.build(fourthVideoPath);
            inMovies[4] = MovieCreator.build(fifthVideoPath);

            List<Track> videoTracks = new LinkedList<>();
            List<Track> audioTracks = new LinkedList<>();

            for (Movie m : inMovies) {
                for (Track t : m.getTracks()) {
                    if (t.getHandler().equals("soun")) {
                        audioTracks.add(t);
                    }
                    if (t.getHandler().equals("vide")) {
                        videoTracks.add(t);
                    }
                }
            }

            Movie result = new Movie();

            if (audioTracks.size() > 0) {
                result.addTrack(new AppendTrack(audioTracks
                        .toArray(new Track[audioTracks.size()])));
            }
            if (videoTracks.size() > 0) {
                result.addTrack(new AppendTrack(videoTracks
                        .toArray(new Track[videoTracks.size()])));
            }

            BasicContainer out = (BasicContainer) new DefaultMp4Builder().build(result);

            @SuppressWarnings("resource")
            FileChannel fc = new RandomAccessFile(Environment.getExternalStorageDirectory() + "/vavcivideo" + Util.getCurrentdate(Constant.TIMESTAMP) + ".mp4", "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/vavcivideo" + Util.getCurrentdate(Constant.TIMESTAMP) + ".mp4";
        return mFileName;
    }


    private File getFilePath(int finatstage) {

        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SeSolutions/";
        String imageName = "";
        if (finatstage == 1) {
            imageName = Constant.IMAGE_NAME + Util.getCurrentdate(Constant.TIMESTAMP) + "combined1";
        } else {
            imageName = Constant.IMAGE_NAME + Util.getCurrentdate(Constant.TIMESTAMP) + "combined2";
        }

        try {
            if (new File(imagePath).mkdir()) {
            } else {
            }
        } catch (Exception e) {
            CustomLog.e(e);
            return null;
        }

        return new File(imagePath, imageName + ".mp4");

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit:
                edit();
                break;
            case R.id.capturePhoto:
                capturePhoto();
                break;
            case R.id.tv15:
                timer = 15;
                ((TextView) findViewById(R.id.tv15)).setBackgroundResource(R.drawable.circle_timer);
                ((TextView) findViewById(R.id.tv15)).setTypeface(null, Typeface.BOLD);
                ((TextView) findViewById(R.id.tv60)).setBackgroundResource(0);
                ((TextView) findViewById(R.id.tv60)).setTypeface(null, Typeface.NORMAL);
                Util.showToast(getApplicationContext(), "Timer set to: " + timer + " seconds.");
                break;
            case R.id.tv60:
                timer = 60;
                ((TextView) findViewById(R.id.tv60)).setBackgroundResource(R.drawable.circle_timer);
                ((TextView) findViewById(R.id.tv60)).setTypeface(null, Typeface.BOLD);
                ((TextView) findViewById(R.id.tv15)).setTypeface(null, Typeface.NORMAL);
                ((TextView) findViewById(R.id.tv15)).setBackgroundResource(0);
                Util.showToast(getApplicationContext(), "Timer set to: " + timer + " seconds.");
                break;
            case R.id.captureVideo:
                getPermission();

                break;
            case R.id.toggleCamera:
                toggleCamera();
                break;
            case R.id.toggleTimer:
                showTimerDialog();
                break;
            case R.id.toggleFlash:
                toggleFlash();
                break;
            case R.id.ivCross:
                showBackDialog();
                break;
            case R.id.ivMusic:
                openMusicChooser();
                break;
            case R.id.ivGallery:
            case R.id.llGallery:
                fromGallery = true;
                if (progressBarTimer != null) {
                    progressBarTimer.cancel();
                }

                openVideoPicker(true);
                break;
        }
    }

    private void captureVideoData() {
        if (mCapturingVideo) {
            if (progressBarTimer != null) {
                progressBarTimer.cancel();
            }
           if(musicSrv!=null && musicselected){
                musicSrv.removeListener(Constant.Listener.COMMON);
                hideMusicLayout();
                stopMusicPlayer();
            }
           if(camera.isCapturingVideo()){
               camera.stopCapturingVideo();
           }else {
               Runnable runnable = new Runnable() {
                   @Override
                   public void run() {
                       if (pause2 && file1_re!=null) {
                           Constant.path = file1_re.getPath();
                           finish();
                       }
                       else if (pause3 && file1_re!=null && file2_re!=null) {
                           int file_size1 = Integer.parseInt(String.valueOf(file1_re.length()/1024));
                           int file_size5 = Integer.parseInt(String.valueOf(file2_re.length()/1024));
                           if(file_size1>500 && file_size5>500){
                               Constant.path = appendTwoVideos(file1_re.getPath(), file2_re.getPath(), 1);
                               finish();
                           }else if(file_size1>500 && file_size5<500){
                               Constant.path = file1_re.getPath();
                               finish();
                           }else {
                               Constant.path = file2_re.getPath();
                               finish();
                           }

                       }
                       else if (pause4 && file1_re!=null && file2_re!=null && file3_re!=null) {
                           int file_size1 = Integer.parseInt(String.valueOf(file1_re.length()/1024));
                           int file_size3 = Integer.parseInt(String.valueOf(file3_re.length()/1024));
                           int file_size2 = Integer.parseInt(String.valueOf(file2_re.length()/1024));

                           if(file_size1>500 && file_size2>500 && file_size3>500){
                               Constant.path = appendTwoVideos2(file1_re.getPath(), file2_re.getPath(), file3_re.getPath(), 1);
                           }else if(file_size1>500 && file_size2<500 && file_size3>500){
                               Constant.path = appendTwoVideos(file1_re.getPath(), file3_re.getPath(), 1);
                           }else {
                               Constant.path = appendTwoVideos(file2_re.getPath(), file3_re.getPath(), 1);
                           }

                           finish();
                       }
                       else if (pause5 && file1_re!=null && file2_re!=null && file3_re!=null && file4_re!=null) {

                           int file_size1 = Integer.parseInt(String.valueOf(file1_re.length()/1024));
                           int file_size3 = Integer.parseInt(String.valueOf(file3_re.length()/1024));
                           int file_size4 = Integer.parseInt(String.valueOf(file4_re.length()/1024));
                           int file_size2 = Integer.parseInt(String.valueOf(file2_re.length()/1024));

                           if(file_size1>500 && file_size2>500 && file_size3>500 && file_size4>500 ) {
                               Constant.path = appendTwoVideos3(file1_re.getPath(), file2_re.getPath(), file3_re.getPath(), file4_re.getPath(), 1);
                           }
                           else if(file_size1>500 && file_size2<500 && file_size3>500  && file_size4>500){
                               Constant.path = appendTwoVideos2(file1_re.getPath(), file3_re.getPath(), file4_re.getPath(), 1);
                           }else if(file_size1<500 && file_size2>500 && file_size3>500  && file_size4>500){
                               Constant.path = appendTwoVideos2(file2_re.getPath(), file3_re.getPath(), file4_re.getPath(), 1);
                           }else if(file_size1>500 && file_size2>500 && file_size3<500 && file_size4>500){
                               Constant.path = appendTwoVideos2(file1_re.getPath(), file2_re.getPath(), file4_re.getPath(), 1);
                           }else {
                               Constant.path = file1_re.getPath();
                           }
                             finish();
                       }
                       else {
                           Constant.path = file1_re.getPath();
                           finish();
                       }

                   }
               };
               new Handler().postDelayed(runnable, 400);
           }

         } else {
            if (startTimer > 0) {
                startAfterTimer(startTimer);
            } else {
                if(musicselected){
                    songPicked(Constant.songObj);
                }
                captureVideo();
            }
        }


    }


    public void getPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CONSTANT);
        } else {
            if (Pause_counter == 1) {
                captureVideoData();
                updateRecordingIcon();
            } else {
                Pause_counter = 1020;
                mCapturingVideo = true;
                captureVideoData();
                updateRecordingIcon();
            }

            if(musicselected){
                ivMusic.setVisibility(View.VISIBLE);
            }else {
                ivMusic.setVisibility(View.GONE);
            }
            play_music=true;
        }

    }


    public void openMusicChooser() {
        Intent intent = new Intent(CreateClickClick.this, AddMusicActivity.class);
        intent.putExtra(Constant.DESTINATION_FRAGMENT, Constant.GoTo.ADD_MUSIC);
        startActivityForResult(intent, ADD_MUSIC);
    }

    public void showBackDialog() {
        try {
            if (null != progressDialog3 && progressDialog3.isShowing()) {
                progressDialog3.dismiss();
            }
            progressDialog3 = ProgressDialog.show(CreateClickClick.this, "", "", true);
            progressDialog3.setCanceledOnTouchOutside(true);
            progressDialog3.setCancelable(true);
            progressDialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog3.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog3.findViewById(R.id.rlDialogMain), CreateClickClick.this);
            TextView tvMsg = progressDialog3.findViewById(R.id.tvDialogText);
            tvMsg.setText(R.string.MSG_STOP_TIKTOK);

            AppCompatButton bCamera = progressDialog3.findViewById(R.id.bCamera);
            bCamera.setText(R.string.YES);
            AppCompatButton bGallary = progressDialog3.findViewById(R.id.bGallary);
            bGallary.setText(R.string.NO);

            progressDialog3.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog3.dismiss();
                if (progressBarTimer != null) {
                    progressBarTimer.cancel();
                }
                onBackPressed();
            });

            progressDialog3.findViewById(R.id.bGallary).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog3.dismiss();

                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public boolean isVideoSelected = true;
    private final PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // Toast.makeText(How_It_Works_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            try {
                if (isVideoSelected) {
                    //showing thumbnail only if video selected from gallery
                    showVideoChooser();
//                    fetchVideo();
                }

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };

    private void showVideoChooser() {

        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(selectedImageList != null ? selectedImageList : new ArrayList<>())
                .setActivityTheme(R.style.FilePickerTheme)
                .showFolderView(false)
                .enableVideoPicker(true)
                .enableImagePicker(false)
                .pickPhoto(this);
    }

    public void askForPermission(String permission) {
        try {
            new TedPermission(getApplicationContext())
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(Constant.MSG_PERMISSION_DENIED)
                    .setPermissions(permission, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showTimerDialog() {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(CreateClickClick.this, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_tiktok_timer);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), CreateClickClick.this);
            progressDialog.findViewById(R.id.threeS).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTimer = 3;
                    Util.showToast(getApplicationContext(), "Timer set to: " + startTimer);
                    progressDialog.dismiss();
                    //callSaveFeedApi( Constant.URL_FEED_DELETE, actionId, vo, actPosition, position);
                }
            });

            progressDialog.findViewById(R.id.tenS).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTimer = 10;
                    Util.showToast(getApplicationContext(), "Timer set to: " + startTimer);
                    progressDialog.dismiss();
                }
            });

            progressDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTimer = 0;
                    Util.showToast(getApplicationContext(), "Timer Reset.");
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    int remainingtime = 0, old_timer = 0;

    public void startTimer(int timer) {
        mProgressBar.setVisibility(View.VISIBLE);
        int original_timer = 0;

        if (Pause_counter == 1) {
            original_timer = timer;
        } else if (Pause_counter == 200) {
            original_timer = (timer+2) - old_timer;
        } else if (Pause_counter == 400) {
            original_timer = (timer+2) - old_timer;
        } else if (Pause_counter == 600) {
            original_timer = (timer+2) - old_timer;
        } else if (Pause_counter == 800) {
            original_timer = (timer+2) - old_timer;
        }
        //Do what you want
        int finalOriginal_timer = original_timer;
        progressBarTimer = new CountDownTimer(finalOriginal_timer * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                old_timer = i-1;
                i++;
                mProgressBar.setProgress((int) i * 100 / (timer * 1000 / 1000));
            }

            @Override
            public void onFinish() {
                i++;
                mProgressBar.setProgress(100);
                camera.stopCapturingVideo();
                if(musicselected && musicSrv!=null){
                    musicSrv.removeListener(Constant.Listener.COMMON);
                    hideMusicLayout();
                    stopMusicPlayer();
                }

            }
        };
        progressBarTimer.start();
    }

    @Override
    public void onBackPressed() {
        BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
        if (b.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            b.setState(BottomSheetBehavior.STATE_HIDDEN);
            return;
        }
        super.onBackPressed();
    }

    public void showBaseLoader(boolean isCancelable) {
        try {
            progressDialog = ProgressDialog.show(getApplicationContext(), "", "", true);
            progressDialog.setCancelable(isCancelable);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_tiktok);
            // new showBaseLoaderAsync(context).execute();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void edit() {
        BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
        b.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void capturePhoto() {
        if (mCapturingPicture) return;
        mCapturingPicture = true;
        mCaptureTime = System.currentTimeMillis();
        mCaptureNativeSize = camera.getPictureSize();
        message("Capturing picture...", false);
        camera.capturePicture();
    }

    private void startAfterTimer(int afterTimer) {
        progressDialog2 = ProgressDialog.show(CreateClickClick.this, "", "", true);
        progressDialog2.setCancelable(false);
        progressDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog2.setContentView(R.layout.progress_tiktok);

        ((TextView) findViewById(R.id.tvCountDown)).setVisibility(View.VISIBLE);
        startCountdownTimer = new CountDownTimer(afterTimer * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                CustomLog.e("time_left", "seconds remaining: " + millisUntilFinished / 1000);
                ((TextView) findViewById(R.id.tvCountDown)).setText("" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                //Do what you want
                progressDialog2.dismiss();
                ((TextView) findViewById(R.id.tvCountDown)).setVisibility(View.GONE);
                captureVideo();
            }
        };
        startCountdownTimer.start();
    }

    public void showLayout(Boolean show) {
        if (!show) {
            findViewById(R.id.ivCross).setVisibility(View.GONE);
          //  findViewById(R.id.ivMusic).setVisibility(View.GONE);
            findViewById(R.id.toggleCamera).setVisibility(View.VISIBLE);
            findViewById(R.id.toggleTimer).setVisibility(View.GONE);
            findViewById(R.id.toggleFlash).setVisibility(View.GONE);
            findViewById(R.id.ivGallery).setVisibility(View.GONE);
            findViewById(R.id.llGallery).setVisibility(View.GONE);
        } else {
            findViewById(R.id.ivCross).setVisibility(View.VISIBLE);
         //   findViewById(R.id.ivMusic).setVisibility(View.VISIBLE);
            findViewById(R.id.toggleCamera).setVisibility(View.VISIBLE);
            findViewById(R.id.toggleTimer).setVisibility(View.VISIBLE);
            findViewById(R.id.toggleFlash).setVisibility(View.VISIBLE);
            findViewById(R.id.llGallery).setVisibility(View.VISIBLE);
            findViewById(R.id.ivGallery).setVisibility(View.VISIBLE);
        }
    }

    private void captureVideo() {
        showLayout(false);

        if(musicselected){
            findViewById(R.id.ivMusic).setVisibility(View.VISIBLE);
            startTimer(timer212);
        }else {
            findViewById(R.id.ivMusic).setVisibility(View.GONE);
            startTimer(timer);
        }


        findViewById(R.id.toggleCamera).setVisibility(View.INVISIBLE);
        ((LinearLayout) findViewById(R.id.llTimer)).setVisibility(View.INVISIBLE);
        if (camera.getSessionType() != SessionType.VIDEO) {
            message("Can't record video while session type is 'picture'.", false);
            return;
        }
        if (mCapturingPicture || mCapturingVideo) return;
        mCapturingVideo = true;
        //  message("Recording for 8 seconds...", true);
        camera.startCapturingVideo(getFilePath());

        ic_pauseid.setVisibility(View.VISIBLE);
        ic_pauseid.setImageResource(R.drawable.ic_baseline_pause);
        ic_pauseid.setColorFilter(menuTitleActiveColor);

    }

    //SES CHANGE changing button icon
    private void updateRecordingIcon() {
        ((AppCompatImageView) findViewById(R.id.captureVideo)).setImageDrawable(ContextCompat.getDrawable(this, mCapturingVideo ? R.drawable.ic_baseline_stop : R.drawable.cam_ic_video));

    }

    private void toggleCamera() {
        if (mCapturingPicture) return;
        switch (camera.toggleFacing()) {
            case BACK:
                message("Switched to back camera!", false);
                ((AppCompatImageView) findViewById(R.id.toggleFlash)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_flash));
                ((AppCompatImageView) findViewById(R.id.toggleFlash)).setVisibility(View.VISIBLE);
                break;

            case FRONT:
                message("Switched to front camera!", false);
                ((AppCompatImageView) findViewById(R.id.toggleFlash)).setVisibility(View.GONE);
                break;
        }
    }

    private void toggleFlash() {
        if (mCapturingPicture) return;
        switch (camera.getFlash()) {
            case OFF:
                camera.setFlash(Flash.TORCH);
                ((AppCompatImageView) findViewById(R.id.toggleFlash)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_thunder));
                break;
            case TORCH:
                camera.setFlash(Flash.OFF);
                ((AppCompatImageView) findViewById(R.id.toggleFlash)).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_flash));
                break;
        }
    }


    @Override
    public boolean onValueChanged(Control control, Object value, String name) {
        if (!camera.isHardwareAccelerated() && (control == Control.WIDTH || control == Control.HEIGHT)) {
            if ((Integer) value > 0) {
                message("This device does not support hardware acceleration. " +
                        "In this case you can not change width or height. " +
                        "The view will act as WRAP_CONTENT by default.", true);
                return false;
            }
        }
        control.applyValue(camera, value);
        BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
        b.setState(BottomSheetBehavior.STATE_HIDDEN);
//        message("Changed " + control.getName() + " to " + name, false);
        return true;
    }

    //region Boilerplate

    @Override
    protected void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.destroy();
        if (progressBarTimer != null) {
            progressBarTimer.cancel();
        }
        if (startCountdownTimer != null) {
            startCountdownTimer.cancel();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_CONSTANT:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPermission();
                }

                break;
            default:
                boolean valid = true;
                for (int grantResult : grantResults) {
                    valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
                }
                if (valid && !camera.isStarted()) {
                    camera.start();
                }

                break;

        }
    }


    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        try {

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public void pause() {
        playbackPaused = true;
        if(musicSrv!=null && musicselected){
            musicSrv.pausePlayer();
        }

    }

    @Override
    public void seekTo(int pos) {
        if(musicselected){
            musicSrv.seek(Util.progressToTimer(pos, getDuration()));
        }

    }

    @Override
    public void start() {
        if(musicselected){
           try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            musicSrv.go();
        }
    }


    public int getCurrentPosition() {
        if (musicSrv != null && musicBound && musicSrv.isPng())
            return musicSrv.getPosn();
        else return 0;
    }

    @Override
    public int getDuration() {
        if (musicSrv != null && musicBound && musicSrv.isPng())
            return musicSrv.getDur();
        else return 0;
    }

    @Override
    public boolean isPlaying() {
        if (musicSrv != null && musicBound)
            return musicSrv.isPng();
        return false;
    }

    public int getCurrentSongId() {
        if (musicSrv != null && musicBound)
            return musicSrv.getCurrentSongId();
        return 0;
    }


    //connect to the service

    private MusicService musicSrv;
    private boolean musicBound = true;//false;
    private Albums pendingSong;
    private boolean paused = false, playbackPaused = false;

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(new ArrayList<Albums>());
            musicBound = true;
            //  musicSrv.removeAllListeners();
            musicSrv.setProgressListener(Constant.Listener.COMMON, CreateClickClick.this);
            ((MainApplication) getApplication()).setMusicService(musicSrv);
            if (null != pendingSong) {
                playSong(pendingSong);
            }
        }

        public void playSong(Albums song) {
            int position = musicSrv.updateSongList(song);
            musicSrv.setSong(position - 1);
            musicSrv.playLoadedSong();
            if (playbackPaused) {
                //    setController();
                playbackPaused = false;
            }
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    private void initService() {
        try {
            musicSrv = ((MainApplication) getApplication()).onStart(musicConnection);
            if (musicSrv != null && (musicSrv.isPng() || musicSrv.isBuffering())) {
                //  musicSrv.removeAllListeners();
                musicSrv.setProgressListener(Constant.Listener.COMMON, this);
                songPicked(Constant.songObj);

            } else {
                hideMusicLayout();
                //ok
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void hideMusicLayout() {

        try {
            if (cvMusicMain.getVisibility() != View.VISIBLE) return;
            Techniques technique = Techniques.values()[Techniques.SLIDE_OUT_DOWN];
            YoYo.with(technique)
                    .duration(200)
                    //  .repeat(1)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .withListener(new AnimationAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            cvMusicMain.setVisibility(View.GONE);
                            // mLayout.addPanelSlideListener(PostFeedFragment.this);
                        }
                    })
                    .playOn(cvMusicMain);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    //user song select
    public void songPicked(Albums song) {
        if (null != musicSrv) {
            //   boolean isSongPending = false;
            pendingSong = null;
            playSong(song);
            //  controller.show(0);
        } else {
            //   boolean isSongPending = true;
            pendingSong = song;
            initService();
        }
        // showSongDetail(song);
    }

    public void showSongDetail(Albums currentSong) {
        tvSongTitle.setText(currentSong.getTitle());
        Glide.with(this).load(currentSong.getImageUrl()).into(ivSongImage);
    }

    public void playSong(Albums song) {
        int position = musicSrv.updateSongList(song);
        musicSrv.setSong(position - 1);
        musicSrv.playLoadedSong();

        if (playbackPaused) {
            //    setController();
            playbackPaused = false;
        }
    }


    public void stopMusicPlayer() {
        musicSrv.callListeners(Constant.Events.STOP, "", 0);
        musicSrv = null;
        ((MainApplication) getApplication()).stopMusic();
    }



}

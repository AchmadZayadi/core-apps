package com.sesolutions.camerahelper;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Toast;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.SessionType;
import com.otaliastudios.cameraview.Size;
import com.sesolutions.R;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.io.File;
import java.io.FileOutputStream;


public class CameraActivity extends AppCompatActivity implements View.OnClickListener, ControlView.Callback {

    private CameraView camera;
    private ViewGroup controlPanel;

    private boolean mCapturingPicture;
    private boolean mCapturingVideo;

    // To show stuff in the callback
    private Size mCaptureNativeSize;
    private long mCaptureTime;
    private long maxVideoDuration = 120000; //2 minutes

    //variables used to set media path and name
    private String path;
    private String imageName;
    //variable used to find action type {IMAGE_CAPTURE,VIDEO_RECORD}
    private boolean hasToRecordVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.cam_activity_camera);
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE);


        path = getIntent().getStringExtra("path");
        imageName = getIntent().getStringExtra("name");
        hasToRecordVideo = getIntent().getBooleanExtra("record_video", false);

        camera = findViewById(R.id.camera);
        camera.setSessionType(SessionType.VIDEO);
        camera.addCameraListener(new CameraListener() {
            public void onCameraOpened(CameraOptions options) {
                onOpened();
            }

            public void onPictureTaken(byte[] jpeg) {
                onPicture(jpeg);
            }

            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
                onVideo(video);
            }
        });

        findViewById(R.id.edit).setOnClickListener(this);
        findViewById(R.id.capturePhoto).setOnClickListener(this);
        findViewById(R.id.captureVideo).setOnClickListener(this);
        findViewById(R.id.toggleCamera).setOnClickListener(this);
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

    private void onPicture(byte[] jpeg) {
        mCapturingPicture = false;
        long callbackTime = System.currentTimeMillis();
        if (mCapturingVideo) {
            message("Captured while taking video. Size=" + mCaptureNativeSize, false);
            return;
        }

        // This can happen if picture was taken with a gesture.
        if (mCaptureTime == 0) mCaptureTime = callbackTime - 300;
        if (mCaptureNativeSize == null) mCaptureNativeSize = camera.getPictureSize();

        Constant.path = saveImageToCard(jpeg);
        CustomLog.e("onPhotoTaken", "" + Constant.path);
        setResult(-1);
        onBackPressed();
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
        } catch (java.io.IOException e) {
            CustomLog.e("PictureDemo", "Exception in photoCallback", e);
        }

        return path;

    }

    private File getFilePath() {

        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SeSolutions/";
        String imageName = Constant.IMAGE_NAME + Util.getCurrentdate(Constant.TIMESTAMP);

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
        updateRecordingIcon();
        Intent intent = new Intent(CameraActivity.this, VideoPreviewActivity.class);
        intent.putExtra("video", Uri.fromFile(video));
        Constant.path = video.getPath();
        startActivity(intent);
        finish();
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
            case R.id.captureVideo:
                if (mCapturingVideo) {
                    camera.stopCapturingVideo();
                } else {
                    captureVideo();
                }
                break;
            case R.id.toggleCamera:
                toggleCamera();
                break;
        }
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

    private void captureVideo() {
        if (camera.getSessionType() != SessionType.VIDEO) {
            message("Can't record video while session type is 'picture'.", false);
            return;
        }
        if (mCapturingPicture || mCapturingVideo) return;
        mCapturingVideo = true;
        //  message("Recording for 8 seconds...", true);
        camera.startCapturingVideo(getFilePath(), maxVideoDuration);
        updateRecordingIcon();
    }

    //SES CHANGE changing button icon
    private void updateRecordingIcon() {
        ((AppCompatImageView) findViewById(R.id.captureVideo)).setImageDrawable(ContextCompat.getDrawable(this, mCapturingVideo ? R.drawable.cam_stop : R.drawable.cam_ic_video));
    }

    private void toggleCamera() {
        if (mCapturingPicture) return;
        switch (camera.toggleFacing()) {
            case BACK:
                message("Switched to back camera!", false);
                break;

            case FRONT:
                message("Switched to front camera!", false);
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
        message("Changed " + control.getName() + " to " + name, false);
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;
        for (int grantResult : grantResults) {
            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (valid && !camera.isStarted()) {
            camera.start();
        }
    }

    //endregion
}

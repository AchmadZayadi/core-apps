package com.sesolutions.ui.clickclick;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sesolutions.R;
import com.sesolutions.camerahelper.MessageView;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

import static com.sesolutions.utils.Constant.path;


public class ClickClickPreview extends AppCompatActivity {
    private VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tiktok_preview);
        videoView = findViewById(R.id.video);
        final MessageView actualResolution = findViewById(R.id.actualResolution);

        Uri videoUri = getIntent().getParcelableExtra("video");
        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);
        controller.setMediaPlayer(videoView);
        videoView.setMediaController(controller);
        videoView.setVideoURI(videoUri);
        Constant.videoUri = videoUri;
        K4LVideoTrimmer videoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeline));
        if (videoTrimmer != null) {
            videoTrimmer.setMaxDuration(60);
            videoTrimmer.setVideoURI(Uri.parse(path));
            videoTrimmer.setOnTrimVideoListener(new OnTrimVideoListener() {
                @Override
                public void getResult(Uri uri) {
                    path = uri.getPath();
                    CustomLog.e("uri", "" + uri.toString());
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

                @Override
                public void cancelAction() {
                    Constant.path = null;
                    onBackPressed();
                }
            });
        }

    }

    private void setFailure() {
        path = null;
    }


    @Override
    public void onBackPressed() {
        setFailure();
        super.onBackPressed();

    }

    void playVideo() {
        if (videoView.isPlaying()) return;
        videoView.start();
    }
}

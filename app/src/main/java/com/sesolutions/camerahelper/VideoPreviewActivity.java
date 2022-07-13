package com.sesolutions.camerahelper;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.sesolutions.R;
import com.sesolutions.utils.Constant;


public class VideoPreviewActivity extends AppCompatActivity {

    private VideoView videoView;
    private Uri videoUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cam_activity_video_preview);
        videoView = findViewById(R.id.video);
        findViewById(R.id.ivSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPreviewActivity.super.onBackPressed();
            }
        });
        findViewById(R.id.ivCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPreviewActivity.this.onBackPressed();
            }
        });
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideo();
            }
        });
        final MessageView actualResolution = findViewById(R.id.actualResolution);

        videoUri = getIntent().getParcelableExtra("video");
        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);
        controller.setMediaPlayer(videoView);
        videoView.setMediaController(controller);
        videoView.setVideoURI(videoUri);
        Constant.videoUri = videoUri;

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                actualResolution.setTitle("Actual resolution");
                actualResolution.setMessage(mp.getVideoWidth() + " x " + mp.getVideoHeight());
                ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                float videoWidth = mp.getVideoWidth();
                float videoHeight = mp.getVideoHeight();
                float viewWidth = videoView.getWidth();
                lp.height = (int) (viewWidth * (videoHeight / videoWidth));
                videoView.setLayoutParams(lp);
                playVideo();
            }
        });
    }

    private void setFailure() {
        Constant.path = null;
    }

   /* private void setSuccess() {
        try {
            //set URI so it can be used find thumbnail on PostVideoFragment
            Constant.videoUri = videoUri;
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/

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

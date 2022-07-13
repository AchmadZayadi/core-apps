package com.sesolutions.imageeditengine;

import android.animation.Animator;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.droidninja.imageeditengine.BaseFrag;
import com.sesolutions.R;
import com.sesolutions.ui.customviews.AnimationAdapter;

import java.io.File;

public class VideoEditorFragment extends BaseFrag implements View.OnClickListener {

    private String videoPath;

    public VideoEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jz_video, container, false);
    }

    public static VideoEditorFragment newInstance(String path) {
        VideoEditorFragment cropFragment = new VideoEditorFragment();
        cropFragment.videoPath = path;
        return cropFragment;
    }

    private VideoView videoView;
    private ImageView ivPlayPause;
    // private HttpProxyCacheServer proxy;

    @Override
    protected void initView(View view) {
        videoView = view.findViewById(R.id.storyVideo);
        ivPlayPause = view.findViewById(R.id.ivPlayPause);
        ivPlayPause.setOnClickListener(this);
        view.findViewById(R.id.v1).setOnClickListener(this);
        try {
            // Copy file to temporary file in order to view it.
            //temporaryFile = generateTemporaryFile(file.getName());
            //FileUtils.copyFile(file, temporaryFile);
            previewVideo(new File(videoPath), videoView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* protected File generateTemporaryFile(String filename) throws IOException {
        String tempFileName = "20130318_010530_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File tempFile = File.createTempFile(
                tempFileName,       *//* prefix     "20130318_010530" *//*
                filename,           *//* filename   "video.3gp" *//*
                storageDir          *//* directory  "/data/sdcard/..." *//*
        );

        return tempFile;
    }*/

    protected void previewVideo(File file, VideoView videoView) {
        videoView.setVideoPath(file.getAbsolutePath());

        // MediaController mediaController = new MediaController(getContext());
        // VideoControllerView mediaController = new VideoControllerView(getContext());
        // videoView.setMediaController(mediaController);

        // mediaController.setMediaPlayer(videoView);
        videoView.seekTo(1);
        // videoView.pause();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ivPlayPause.setAlpha(1f);
                ivPlayPause.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.jz_play_normal));
                ivPlayPause.setVisibility(View.VISIBLE);
            }
        });
        //videoView.setVisibility(View.VISIBLE);
    }

    boolean isVideoPaused = false;
    private int stopPosition;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.v1:
                if (ivPlayPause.getVisibility() == View.VISIBLE) return;
                ivPlayPause.post(() -> {
                    ivPlayPause.animate().alpha(1).setDuration(500).setListener(new AnimationAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            ivPlayPause.setAlpha(0f);
                            ivPlayPause.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.jz_play_normal));
                            ivPlayPause.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }
                    });
                });
                break;
            case R.id.ivPlayPause:
                if (videoView.isPlaying()) {
                    isVideoPaused = true;
                    stopPosition = videoView.getCurrentPosition();
                    videoView.pause();
                    ivPlayPause.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.jz_play_normal));
                } else {
                    if (isVideoPaused) {
                        videoView.seekTo(stopPosition);
                        videoView.start();
                        ivPlayPause.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.jz_pause_normal));
                    } else {
                        ivPlayPause.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.jz_pause_normal));
                        videoView.start();
                    }
                    isVideoPaused = false;
                    ivPlayPause.postDelayed(() -> {
                        ivPlayPause.animate().alpha(0).setDuration(500).setListener(new AnimationAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ivPlayPause.setVisibility(View.GONE);
                            }
                        });
                    }, 3000);
                }
                break;
        }
    }
}

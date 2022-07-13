package com.sesolutions.ui.clickclick;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gowtham.library.ui.ActVideoTrimmer;
import com.gowtham.library.utils.CompressOption;
import com.gowtham.library.utils.LogMessage;
import com.gowtham.library.utils.TrimVideo;
import com.gowtham.library.utils.TrimVideoOptions;
import com.sesolutions.R;
import com.sesolutions.utils.Constant;

import java.io.File;

import static com.gowtham.library.utils.TrimVideo.TRIM_VIDEO_OPTION;
import static com.gowtham.library.utils.TrimVideo.TRIM_VIDEO_URI;
import static com.gowtham.library.utils.TrimVideo.VIDEO_TRIMMER_REQ_CODE;

public class ClickClickPreview2 extends AppCompatActivity {

    private static final int REQUEST_TAKE_VIDEO = 552;

    private VideoView videoView;

    private MediaController mediaController;

    private EditText edtFixedGap, edtMinGap, edtMinFrom, edtMAxTo;

    private int trimType;

    private static final String TAG = "MainActivity";
    private TrimVideoOptions options;
    TextView trimid;

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tiktok_preview2);
        videoView = findViewById(R.id.video_view);
        trimid = findViewById(R.id.trimid);
        mediaController = new MediaController(this);
        Log.e("path",""+getIntent().getStringExtra("videouri"));
        Uri videoUri = getIntent().getParcelableExtra("video");
        mediaController.setAnchorView(videoView);
        mediaController.setMediaPlayer(videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();


        videoView.setOnPreparedListener(mediaPlayer -> {
            mediaController.setAnchorView(videoView);
        });

        Constant.videoUri = videoUri;
        options = new TrimVideoOptions();
        options.destination = "/storage/emulated/0/DCIM/TESTFOLDER";

        Intent intent = new Intent(this, ActVideoTrimmer.class);
        intent.putExtra(TRIM_VIDEO_URI, videoUri.toString());
        intent.putExtra(TRIM_VIDEO_OPTION, options);
        startActivityForResult(intent, VIDEO_TRIMMER_REQ_CODE);

      /*  TrimVideo.activity(getIntent().getStringExtra("videouri"))
               // .setCompressOption(new CompressOption()) //pass empty constructor for default compress option
                .setDestination("/storage/emulated/0/DCIM/TESTFOLDER")
                .start(this);*/

        trimid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result","OK");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result","CANSEL");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == VIDEO_TRIMMER_REQ_CODE && data != null) {
                Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(data));
                Log.d(TAG, "Trimmed path:: " + uri);
                videoView.setMediaController(mediaController);
                videoView.setVideoURI(uri);
                videoView.requestFocus();
                videoView.start();

                videoView.setOnPreparedListener(mediaPlayer -> {
                    mediaController.setAnchorView(videoView);
                });

                String filepath = String.valueOf(uri);
                File file = new File(filepath);
                long length = file.length();
                Constant.path=file.getPath();
                Log.d(TAG, "Video size:: " + (length / 1024));
            } else if (requestCode == REQUEST_TAKE_VIDEO && resultCode == RESULT_OK) {
            /*    //check video duration if needed
                if (TrimmerUtils.getVideoDuration(this,data.getData())<=30){
                    Toast.makeText(this,"Video should be larger than 30 sec",Toast.LENGTH_SHORT).show();
                    return;
                }*/
                if (data.getData() != null) {
                    LogMessage.v("Video path:: " + data.getData());
                    openTrimActivity(String.valueOf(data.getData()));
                } else {
                    Toast.makeText(this, "video uri is null", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openTrimActivity(String data) {
        if (trimType == 0) {
            TrimVideo.activity(data)
                    .setCompressOption(new CompressOption()) //pass empty constructor for default compress option
                    .setDestination("/storage/emulated/0/DCIM/TESTFOLDER")
                    .start(this);
        }
    }





}
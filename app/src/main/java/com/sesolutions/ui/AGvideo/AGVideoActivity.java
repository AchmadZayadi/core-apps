package com.sesolutions.ui.AGvideo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.sesolutions.R;
import com.sesolutions.ui.AGvideo.mediaplayer.MediaExo;
import com.sesolutions.ui.AGvideo.popup.VideoEpisodePopup;
import com.sesolutions.ui.AGvideo.popup.VideoSpeedPopup;


import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class AGVideoActivity extends AppCompatActivity  {
    private AGVideo mPlayer;
    private JZDataSource mJzDataSource;
    private List<AGEpsodeEntity> episodeList;
    private TabLayout episodes;
    private int playingNum = 0;
    //倍数弹窗
    private VideoSpeedPopup videoSpeedPopup;
    private VideoEpisodePopup videoEpisodePopup;


    String videourl="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_agvideo);

        videourl=getIntent().getStringExtra("VIDEOURL");
       // initVideoData();

        episodeList = new ArrayList<>();
        episodeList.add(new AGEpsodeEntity(videourl,"My video"));
       /* for (int i = 0; i < UrlsKt.getLdjVideos().length; i++) {
            episodeList.add(new AGEpsodeEntity(UrlsKt.getLdjVideos()[i], "鹿鼎记 第" + (i + 1) + "集"));
        }*/

        initView();
    //    ScreenRotateUtils.getInstance(this.getApplicationContext()).setOrientationChangeListener(this);
    }

    private void initView() {
        episodes = findViewById(R.id.video_episodes);
        mPlayer = findViewById(R.id.ag_player);
        mJzDataSource = new JZDataSource(episodeList.get(0).getVideoUrl(), episodeList.get(0).getVideoName());
        mPlayer.setUp(mJzDataSource
                , JzvdStd.SCREEN_NORMAL, MediaExo.class);
        mPlayer.startVideo();
    }


}

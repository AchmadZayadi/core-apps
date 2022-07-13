package com.sesolutions.ui.clickclick.music;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.downloader.PRDownloader;
import com.sesolutions.R;
import com.sesolutions.animate.Techniques;
import com.sesolutions.animate.YoYo;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.common.MainApplication;
import com.sesolutions.ui.common.SplashAnimatedActivity;
import com.sesolutions.ui.customviews.AnimationAdapter;
import com.sesolutions.ui.musicplayer.MusicService;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.ArrayList;

public class AddMusicActivity extends BaseActivity implements View.OnClickListener, MediaController.MediaPlayerControl, OnUserClickedListener<Integer, Object> {

    public CardView cvMusicMain;
    private ProgressDialog progressDialog;
    public AppCompatTextView etStoreSearch;
    private FragmentManager fragmentManager;
    //service
    public AppCompatTextView tvHashtag;
    public AppCompatImageView ivHash;
    public AppCompatTextView tvActivity;
    public AppCompatImageView ivActivity;
    private RelativeLayout rlSearchFilter;
    private RelativeLayout rltop;
    private MusicService musicSrv;
    // private Intent playIntent;
    //binding
    private LinearLayout llFavorite;
    private LinearLayout llDiscover;
    private boolean musicBound = true;//false;
    //activity and playback pause flags
    private boolean paused = false, playbackPaused = false;
    private ProgressBar seekBar;
    private TextView tvSongTitle;
    private ImageView fabPlay;
    private ImageView ivSongImage;
    private Drawable dPause;
    private Drawable dPlay;
    private AppCompatImageView mButton;
    private Albums pendingSong;
    private ProgressBar pbLoad;
    private boolean isFav = false;
    //connect to the service
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
            musicSrv.setProgressListener(Constant.Listener.COMMON, AddMusicActivity.this);
            ((MainApplication) getApplication()).setMusicService(musicSrv);
            if (null != pendingSong) {
                playSong(pendingSong);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    int menuTitleActiveColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.getInt("my_pid", -1) == android.os.Process.myPid()) {
                // app was not killed
                CustomLog.e("app_state_form_common_activity", "app process was not killed");

            } else {
                // app was killed
                CustomLog.e("app_state_from_common_activity", "app process was killed");
                Intent intent = new Intent(this, SplashAnimatedActivity.class);
                intent.putExtra(Constant.KEY_COOKIE, Constant.SESSION_ID);
                finish();
                startActivity(intent);
            }
        }
        setContentView(R.layout.activity_music);
        PRDownloader.initialize(getApplicationContext());
        findViewById(R.id.main).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
        fragmentManager = getSupportFragmentManager();
        dPause = ContextCompat.getDrawable(this, R.drawable.pause_rounded_bluew);
        dPlay = ContextCompat.getDrawable(this, R.drawable.play_rounded_blue);
        init();
        openaddmusicfragment();
        menuTitleActiveColor = Color.parseColor(Constant.menuButtonActiveTitleColor);

        ivActivity.setColorFilter(menuTitleActiveColor);
        ivHash.setColorFilter(menuTitleActiveColor);
        tvActivity.setTextColor(menuTitleActiveColor);

    }


    private void openaddmusicfragment() {
        fragmentManager.beginTransaction().replace(R.id.container, new AddMusicFragment()).addToBackStack(null).commit();

    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        try {
            switch (object1) {
                case Constant.Events.MUSIC_PROGRESS:
                    seekBar.setProgress(postion);
                    break;
                case Constant.Events.MUSIC_PREPARED:
                    pbLoad.setVisibility(View.GONE);
                    fabPlay.setEnabled(true);
                    break;
                case Constant.Events.MUSIC_CHANGED:
                    pbLoad.setVisibility(View.VISIBLE);
                    fabPlay.setEnabled(false);
                    showSongDetail(musicSrv.getCurrentSong());
                    break;
                case Constant.Events.PLAY:
                    fabPlay.setImageDrawable(dPause);
                    break;
                case Constant.Events.PAUSE:
                    fabPlay.setImageDrawable(dPlay);
                    break;
                case Constant.Events.STOP:
                    musicSrv.removeListener(Constant.Listener.COMMON);
                    hideMusicLayout();
                    stopMusicPlayer();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }


    public void showMusicLayout() {

        try {
            Techniques technique = Techniques.values()[Techniques.SLIDE_IN_UP];
            YoYo.with(technique)
                    .duration(200)
                    //  .repeat(1)
                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .withListener(new AnimationAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            cvMusicMain.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            cvMusicMain.setVisibility(View.VISIBLE);
                            // mLayout.addPanelSlideListener(PostFeedFragment.this);
                        }
                    })
                    .playOn(cvMusicMain);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.etStoreSearch:
//                goToSearchFragment()

                fragmentManager.beginTransaction().replace(R.id.container,
                        new SearchAddMusicFragment())
                        .addToBackStack(null)
                        .commit();
                hideTop();
                break;
            case R.id.tvHashtag:
                if (!isFav) {

                    tvHashtag.setTextColor(menuTitleActiveColor);
                    tvHashtag.setTypeface(null, Typeface.BOLD);
                    tvActivity.setTextColor(Color.parseColor("#000000"));
                    tvActivity.setTypeface(null, Typeface.NORMAL);
                    ivActivity.setVisibility(View.GONE);
                    ivHash.setVisibility(View.VISIBLE);
//                    if (isPlaying()) {
//                        stopMusicPlayer();
//                    }
                    fragmentManager.beginTransaction().replace(R.id.container, new FavouriteMusicFragment()).commit();
                    isFav = true;
                }
                break;
            case R.id.tvActivity:
                if (rlSearchFilter.getVisibility() == View.GONE) {
                    rlSearchFilter.setVisibility(View.VISIBLE);
                    rltop.setVisibility(View.VISIBLE);
                }
                ivActivity.setVisibility(View.VISIBLE);
                ivHash.setVisibility(View.GONE);
                tvActivity.setTextColor(menuTitleActiveColor);
                tvActivity.setTypeface(null, Typeface.BOLD);
                tvHashtag.setTextColor(Color.parseColor("#000000"));
                tvHashtag.setTypeface(null, Typeface.NORMAL);
                if (isFav) {
                    isFav = false;
//                    if (isPlaying()) {
//                        stopMusicPlayer();
//                    }
                    fragmentManager.beginTransaction().replace(R.id.container, new AddMusicFragment()).commit();
                }
                break;

            case R.id.fabPlay:
                CustomLog.e("duration1", "" + musicSrv.getDur());
                //  CustomLog.e("duration2", "" + getDuration());
                if (musicSrv.isPng()) {
                    fabPlay.setImageDrawable(dPlay);
                    pause();
                    // musicSrv.pausePlayer();
                } else {
                    fabPlay.setImageDrawable(dPause);
                    musicSrv.go();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        if (isPlaying()) {
//            stopMusicPlayer();
//        }
        super.onBackPressed();
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
    public int getBufferPercentage() {
        return 0;
    }

    @Override
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

    @Override
    public void pause() {
        playbackPaused = true;
        musicSrv.pausePlayer();
    }


    @Override
    public void seekTo(int pos) {
        musicSrv.seek(Util.progressToTimer(pos, getDuration()));
    }

    @Override
    public void start() {
        musicSrv.go();
    }

    //user song select
    public void songPicked(Albums song) {
        if (null != musicSrv) {
            pendingSong = null;
            playSong(song);
        } else {
            pendingSong = song;
            initService();
        }
        showSongDetail(song);
    }



    public void playSong(Albums song) {
        int position = musicSrv.updateSongList(song);
        musicSrv.setSong(position - 1);
        musicSrv.playSong();
        if (playbackPaused) {
            //    setController();
            playbackPaused = false;
        }
    }

    public boolean isPaused() {
        return playbackPaused;
    }

    public void showTop() {
        rlSearchFilter.setVisibility(View.VISIBLE);
        rltop.setVisibility(View.VISIBLE);
    }

    public void hideTop() {
        rlSearchFilter.setVisibility(View.GONE);
        rltop.setVisibility(View.GONE);
    }

    //start and bind the service when the activity starts
    @Override
    protected void onStart() {
        super.onStart();
        try {
            initService();
        } catch (Exception e) {
            CustomLog.e(e);
        }
       /* if (playIntent == null) {
            playIntent = new Intent(getApplicationContext(), MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }*/
    }

    public void playNext() {
        musicSrv.playNext();
        if (playbackPaused) {
            //  setController();
            playbackPaused = false;
        }
        // controller.show(0);
    }

    public void playPrev() {
        musicSrv.playPrev();
        if (playbackPaused) {
            // setController();
            playbackPaused = false;
        }
        // controller.show(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (musicSrv != null) {
            musicSrv.pausePlayer();
            musicSrv.removeListener(Constant.Listener.COMMON);
            //musicSrv.removeAllListeners();
        }
    }

    @Override
    protected void onDestroy() {
        if (musicSrv != null) {
            musicSrv.removeListener(Constant.Listener.COMMON);
            //musicSrv.removeAllListeners();
        }
        super.onDestroy();
    }

    public void stopMusicPlayer() {
        musicSrv.callListeners(Constant.Events.STOP, "", 0);
        musicSrv = null;
        ((MainApplication) getApplication()).stopMusic();
    }

    private void init() {
        llFavorite = findViewById(R.id.llFavorite);
        llDiscover = findViewById(R.id.llDiscoverr);
        tvActivity = findViewById(R.id.tvActivity);
        tvHashtag = findViewById(R.id.tvHashtag);
        ivActivity = findViewById(R.id.ivActivity);
        ivHash = findViewById(R.id.ivHash);
        etStoreSearch = findViewById(R.id.etStoreSearch);
        rlSearchFilter = findViewById(R.id.rlSearchFilter);
        rltop = findViewById(R.id.rltop);
        etStoreSearch.setOnClickListener(this);
        mButton = findViewById(R.id.ivBack);
        mButton.setOnClickListener(this);
        cvMusicMain = findViewById(R.id.cvMusicMain);
        seekBar = findViewById(R.id.seekbar);
        tvSongTitle = findViewById(R.id.tvSongTitle);
        fabPlay = findViewById(R.id.fabPlay);
        pbLoad = findViewById(R.id.pbLoad);
        ivSongImage = findViewById(R.id.ivSongImage);
        fabPlay.setOnClickListener(this);
        cvMusicMain.setOnClickListener(this);
        tvHashtag.setOnClickListener(this);
        tvActivity.setOnClickListener(this);
        llDiscover.setOnClickListener(this);
        llFavorite.setOnClickListener(this);
    }

    @Override
    protected void onHomePressed() {
        onBackPressed();
    }


    @Override
    public void hideBaseLoader() {
        try {
            if (!isFinishing() && progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void initService() {
        try {
            musicSrv = ((MainApplication) getApplication()).onStart(musicConnection);
            if (musicSrv != null && (musicSrv.isPng() || musicSrv.isBuffering())) {
                //  musicSrv.removeAllListeners();
                musicSrv.setProgressListener(Constant.Listener.COMMON, this);
                showSongDetail(musicSrv.getCurrentSong());
            } else {
                hideMusicLayout();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showSongDetail(Albums currentSong) {
        tvSongTitle.setText(currentSong.getTitle());
        Glide.with(this).load(currentSong.getImageUrl()).into(ivSongImage);
    }

    public void removeSong(int currentItem) {
        musicSrv.removeSongAtPosition(currentItem);
    }
}


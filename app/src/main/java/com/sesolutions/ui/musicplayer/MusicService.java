package com.sesolutions.ui.musicplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.sesolutions.BuildConfig;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.music.Albums;
import com.sesolutions.ui.common.CommonActivity;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//import com.bumptech.glide.request.animation.GlideAnimation;

/*
 * This is demo code to accompany the Mobiletuts+ series:
 * Android SDK: Creating a Music Player
 *
 * Sue Smith - February 2014
 */

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private static final long PROGRESS_BAR_UPDATE_TIME = 1200;
    //media player
    private MediaPlayer player;
    //song list
    private List<Albums> songs;
    //current position
    private int songPosn;
    //binder
    private final IBinder musicBind = new MusicBinder();
    //title of current song
    //private String songTitle = "";

    //shuffle flag and random
    private boolean shuffle = false;
    private Random rand;
    private Map<Integer, OnUserClickedListener<Integer, Object>> listeners;
    private Handler mHandler;
    private boolean repeat;
    private SesNotificationBroadcast broadcastReceiver;

    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosn = 0;
        //random
        rand = new Random();
        //create player
        player = new MediaPlayer();
        //initialize
        initMusicPlayer();
        final IntentFilter intentFilter = new IntentFilter();
        //adding some filters
        intentFilter.addAction(NOTIFY_DELETE);
        intentFilter.addAction(NOTIFY_PLAY);
        intentFilter.addAction(NOTIFY_PAUSE);
        intentFilter.addAction(NOTIFY_PREVIOUS);
        intentFilter.addAction(NOTIFY_NEXT);
        intentFilter.addAction(NOTIFY_STOP);

        broadcastReceiver = new SesNotificationBroadcast();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    public void setProgressListener(Integer id, OnUserClickedListener<Integer, Object> listener) {
        if (null == this.listeners) {
            listeners = new HashMap<>();
        }
        this.listeners.put(id, listener);
    }

    public void removeListener(Integer id) {
        try {
            if (null != listeners && listeners.containsKey(id)) {
                this.listeners.remove(id);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public Map<Integer, OnUserClickedListener<Integer, Object>> getProgressListener() {
        return listeners;
    }


    public void removeAllListeners() {
        this.listeners = null;


    }


    public void initMusicPlayer() {
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    /*
    This method removes selected song from list and play next
    don't call this method if there is only one song in list*/

    public void removeSongAtPosition(int position) {

        try {
            if (songPosn == position) {
                boolean isRepeat = repeat;
                repeat = false;
                playNext();
                repeat = isRepeat;
                if (songPosn > 0) {
                    songPosn--;
                }
            } else if (position < songPosn) {
                songPosn--;
            }
            songs.remove(position);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    //pass song list
    public void setList(List<Albums> theSongs) {
        songs = theSongs;
    }

    //pass song list
    public List<Albums> getSongList() {
        return songs;
    }

    //pass song list
    public int updateSongList(Albums song) {
        songs.add(song);
        return songs.size();
    }



    public boolean isShuffeled() {
        return shuffle;
    }

    public boolean isRepeat() {
        return repeat;
    }

    //binder
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    //play a song
    public void playSong() {
        try {
            removeTimer();
            //play
            player.reset();
            //TODO REMOVE THIS LINE IF MI ISSUE NOT SOLVED
            initMusicPlayer();
            //get song
            Albums playSong = songs.get(songPosn);
            //get title
            // songTitle = playSong.getTitle();
            try {
                //   player.setDataSource(getApplicationContext(), trackUri);
                if(playSong.getSongUrl()!=null){
                    player.setDataSource(playSong.getSongUrl());
                }else {
                    player.setDataSource(playSong.getSongselfurl());
                }
                isBuffering = true;
                callListeners(Constant.Events.MUSIC_CHANGED, "", songPosn);
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }
            player.prepareAsync();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }    //play a song

    public void playLoadedSong() {
        try {
            removeTimer();
            //play
            player.reset();
            //TODO REMOVE THIS LINE IF MI ISSUE NOT SOLVED
            initMusicPlayer();
            //get song
            Albums playSong = songs.get(songPosn);
            //get title
            // songTitle = playSong.getTitle();

            try {
                //   player.setDataSource(getApplicationContext(), trackUri);
                player.setDataSource(Constant.songPath);
                isBuffering = true;
                callListeners(Constant.Events.MUSIC_CHANGED, "", songPosn);
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }
            player.prepareAsync();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    //set the song
    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    public Albums getCurrentSong() {
        return songs.get(songPosn);
    }

    public int getCurrentSongId() {
        return songs.get(songPosn).getSongId();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track
        try {
            callListeners(Constant.Events.MUSIC_COMPLETED, "", songPosn);
            removeTimer();
            if (player.getCurrentPosition() > 0) {
                mp.reset();
                if (!repeat) {
                    pausePlayer();
                }else {
                    playNext();
                }
           }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("MUSIC PLAYER", "Playback Error");
        mp.reset();
        removeTimer();
        callListeners(Constant.Events.ERROR, "", 0);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        CustomLog.e("MUSIC PLAYER", "PREPARED");
        try {
            isBuffering = false;
            callListeners(Constant.Events.MUSIC_PREPARED, "", -1);
            //start playback
            removeTimer();


            //  mp.start();
            //  player = mp;
            player.start();
            updateProgressBar();
            //notification
            notification();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    //playback methods
    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    private boolean isBuffering;

    public boolean isBuffering() {
        return isBuffering;
    }

    public void pausePlayer() {
        callListeners(Constant.Events.PAUSE, "", 0);
        player.pause();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        callListeners(Constant.Events.PLAY, "", 0);
        player.start();
    }

    //skip to previous track
    public void playPrev() {
        songPosn--;
        if (songPosn < 0) songPosn = songs.size() - 1;
        callListeners(Constant.Events.PREVIOUS, "", 0);
        playSong();
    }

    //skip to next
    public void playNext() {
        try {
            if (!repeat) {
                if (shuffle) {
                    int newSong = songPosn;
                    while (newSong == songPosn) {
                        newSong = rand.nextInt(songs.size());
                    }
                    songPosn = newSong;
                } else {
                    songPosn++;
                    if (songPosn >= songs.size()) songPosn = 0;
                }
            }
            callListeners(Constant.Events.NEXT, "", songPosn);
            playSong();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public int getCurrentSongPosition() {
        return songPosn;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    //toggle shuffle
    public void setShuffle() {
        shuffle = !shuffle;
    }

    public void setRepeat() {
        repeat = !repeat;
    }

    public void updateProgressBar() {
        mHandler = new Handler();
        mHandler.postDelayed(mUpdateTimeTask, PROGRESS_BAR_UPDATE_TIME);
    }

    public void removeTimer() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler = null;
        }
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {
                if (listeners != null) {
                    long totalDuration = player.getDuration();
                    long currentDuration = player.getCurrentPosition();

                    // Displaying Total Duration time
                    // songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
                    // Displaying time completed playing
                    // songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

                    // Updating progress bar
                    int progress = Util.getProgressPercentage(currentDuration, totalDuration);
                    CustomLog.d("Progress", "" + progress);
                    //   songProgressBar.setProgress(progress);

                    callListeners(Constant.Events.MUSIC_PROGRESS
                            , Util.milliSecondsToTimer(totalDuration)
                                    + "@"
                                    + Util.milliSecondsToTimer(currentDuration)
                            , progress);
                } // Running this thread after 100 milliseconds
                if (null != mHandler)
                    mHandler.postDelayed(this, PROGRESS_BAR_UPDATE_TIME);
            } catch (Exception e) {
                CustomLog.e(e);
            }

        }
    };
    private static int NOTIFICATION_ID = 7868;
    public static final String NOTIFY_PREVIOUS = "com.sesolutions.previous";
    public static final String NOTIFY_DELETE = "com.sesolutions.delete";
    public static final String NOTIFY_PAUSE = "com.sesolutions.pause";
    public static final String NOTIFY_PLAY = "com.sesolutions.play";
    public static final String NOTIFY_NEXT = "com.sesolutions.next";
    public static final String NOTIFY_STOP = "com.sesolutions.stop";

    boolean currentVersionSupportBigNotification = currentVersionSupportBigNotification();

    public static boolean currentVersionSupportBigNotification() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        return sdkVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN;
    }

    public void callListeners(Integer a1, String a2, int a3) {


        try {
            if (null != listeners) {
                Collection<OnUserClickedListener<Integer, Object>> list = listeners.values();
                for (OnUserClickedListener<Integer, Object> lst : list) {
                    lst.onItemClicked(a1, a2, a3);
                }
            }
            if (a1 != Constant.Events.MUSIC_PROGRESS && a1 != Constant.Events.STOP) {
                notification(true);
            }
            //endregion
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void notification() {
        notification(false);
    }

    public Notification getNotification(String songName) {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }
        // NotificationCompat.Builder mBuilder
        Notification notification = new NotificationCompat.Builder(this, channel)
                .setContentTitle(songName)
                //  .setContentTitle("snap map fake location");
                //  Notification notification = mBuilder
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.icon_notification_se)
                .setContentTitle(songName)
                .build();


        return notification;
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelName = BuildConfig.APP_NAME.replace(" ", "");
        String channelId = BuildConfig.APP_NAME.replace(" ", "");
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return channelId;
    }


    @SuppressLint("NewApi")
    public void notification(boolean fromListener) {
        String songName = getCurrentSong().getTitle();
        String albumName = getCurrentSong().getName();
        RemoteViews simpleContentView = setListeners(new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification1));
       // RemoteViews expandedView = setListeners(new RemoteViews(getApplicationContext().getPackageName(), R.layout.big_notification));
       /* final Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(songName).build();*/
        final Notification notification = getNotification(songName);
        //setListeners(simpleContentView);
        //setListeners(expandedView);
        notification.contentView = simpleContentView;
       /* if (currentVersionSupportBigNotification) {
            notification.bigContentView = expandedView;
        }*/
        try {
            // long albumId = getCurrentSong().getAlbumId();
            NotificationTarget notificationTarget = new NotificationTarget(
                    this,
                    R.id.imageViewAlbumArt,
                    simpleContentView,
                    notification,
                    NOTIFICATION_ID);
          /*  NotificationTarget notificationTarget2 = new NotificationTarget(
                    this,
                    R.id.imageViewAlbumArt,
                    expandedView,
                    notification,
                    NOTIFICATION_ID);*/
            //and then use that target in usual glide way
            // Uri uri = ContentUris.withAppendedId(albumId);
            Glide.with(getApplicationContext()).asBitmap()
                    .load(getCurrentSong().getImageUrl())
                    .into(notificationTarget);
           /* Glide.with(getApplicationContext()).asBitmap()
                    .load(getCurrentSong().getImageUrl())
                    .into(notificationTarget2);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isPlaying = fromListener == isPng();
        if (isPlaying) {
            notification.contentView.setViewVisibility(R.id.btnPause, View.GONE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);

            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.GONE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
            }
        }
        else {
            notification.contentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
            notification.contentView.setViewVisibility(R.id.btnPlay, View.GONE);

            if (currentVersionSupportBigNotification) {
                notification.bigContentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
                notification.bigContentView.setViewVisibility(R.id.btnPlay, View.GONE);
            }
        }
        notification.contentView.setTextViewText(R.id.textSongName, songName);
        notification.contentView.setTextViewText(R.id.textAlbumName, albumName);
        if (currentVersionSupportBigNotification) {
            notification.bigContentView.setTextViewText(R.id.textSongName, songName);
            notification.bigContentView.setTextViewText(R.id.textAlbumName, albumName);
        }
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        //lockScreenControls();
        startForeground(NOTIFICATION_ID, notification);

    }

    public RemoteViews setListeners(RemoteViews view) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);

        PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnDelete, pDelete);

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPause, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnNext, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPlay, pPlay);
        return view;
    }

    private void lockScreenControls() {
       /* try {
            ComponentName mEventReceiver = new ComponentName(getPackageName(), NotificationBroadcast.class.getName());
            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.registerMediaButtonEventReceiver(mEventReceiver);
            // build the PendingIntent for the remote control client
            Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            mediaButtonIntent.setComponent(mEventReceiver);
            PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0);
            // create and register the remote control client
            RemoteControlClient myRemoteControlClient = new RemoteControlClient(mediaPendingIntent);
            mAudioManager.registerRemoteControlClient(myRemoteControlClient);
        } catch (Exception e) {
            CustomLog.e(e);
        }*/
        // Use the media button APIs (if available) to register ourselves for media button
        // events

        /*MediaButtonHelper.registerMediaButtonEventReceiverCompat(mAudioManager, mMediaButtonReceiverComponent);
        // Use the remote control APIs (if available) to set the playback state
        if (mRemoteControlClientCompat == null) {
            Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
            intent.setComponent(mEventReceiver);
            mRemoteControlClientCompat = new RemoteControlClientCompat(PendingIntent.getBroadcast(this *//*context*//*,0 *//*requestCode, ignored*//*, intent *//*intent*//*, 0 *//*flags*//*));
            RemoteControlHelper.registerRemoteControlClient(mAudioManager,mRemoteControlClientCompat);
        }
        mRemoteControlClientCompat.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
        mRemoteControlClientCompat.setTransportControlFlags(
                RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                        RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                        RemoteControlClient.FLAG_KEY_MEDIA_NEXT |
                        RemoteControlClient.FLAG_KEY_MEDIA_STOP);

        //update remote controls
        mRemoteControlClientCompat.editMetadata(true)
                .putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, "NombreArtista")
                .putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, "Titulo Album")
                .putString(MediaMetadataRetriever.METADATA_KEY_TITLE, nombreCancion)
                //.putLong(MediaMetadataRetriever.METADATA_KEY_DURATION,playingItem.getDuration())
                // TODO: fetch real item artwork
                .putBitmap(RemoteControlClientCompat.MetadataEditorCompat.METADATA_KEY_ARTWORK, getAlbumArt())
                .apply();
    }*/
    }

}

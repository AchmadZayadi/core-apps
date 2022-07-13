package com.sesolutions.ui.common;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import androidx.multidex.MultiDex;

import com.balsikandar.crashreporter.CrashReporter;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.manimaran.crash_reporter.CrashReporterConfiguration;
import com.sesolutions.BuildConfig;
import com.sesolutions.ui.musicplayer.MusicService;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.CustomLog;

import java.net.Socket;
import java.net.URISyntaxException;

import io.socket.client.IO;

/**
 * Created by root on 6/11/17.
 */

public class MainApplication extends Application {
    private static final String TAG = "MainApplication";
    private Intent playIntent;
    private MusicService musicSrv;
    private boolean musicBound = false;
    private ServiceConnection musicConnection;

    private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseCrashlytics crashlytics;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private io.socket.client.Socket mSocket;
    public io.socket.client.Socket getSocket() {
        return mSocket;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // if (!BuildConfig.BUILD_TYPE.equals("debug")) {
       // Fabric.with(this, new Crashlytics());
        //}

        crashlytics = FirebaseCrashlytics.getInstance();

        crashlytics.log("Start logging!");

        crashlytics.setUserId("785");
        crashlytics.setCustomKey("DisplayName", "TestQQQ");
        crashlytics.setCustomKey("Email", "koushalrathor@gmail.com");

        crashlytics.setCustomKey("koushal", "jhdjhdjhdj");
        crashlytics.setCustomKey(TAG, true);
        crashlytics.setCustomKey("integer", 1234);
        crashlytics.setCustomKey("float", 567.89f);
        crashlytics.setCustomKey("timestamp", System.currentTimeMillis());
        CrashReporterConfiguration crashReporterConfiguration;
        if (BuildConfig.DEBUG) {
            //initialise reporter with external path
           CrashReporter.initialize(this);
        }

        try {
             mSocket = IO.socket(AppConfiguration.LINUX_BASE_URL);
          } catch (URISyntaxException e) {
              throw new RuntimeException(e);
         }
    }


    synchronized public FirebaseAnalytics getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        }

        return mFirebaseAnalytics;
    }

    public MusicService onStart(ServiceConnection musicConnection) {
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
            this.musicConnection = musicConnection;
        }
        return musicSrv;
    }

    public void setMusicService(MusicService musicSrv) {
        this.musicSrv = musicSrv;

    }

    public MusicService getMusicService() {
        return musicSrv;
    }


    public void stopMusic() {
        //musicSrv.stopService()
        /*musicSrv.stopService(playIntent);
        musicSrv = null;
        stopService(playIntent);*/
        //  unbindService(musicSrv.);
        // musicSrv.stopForeground(true);
        try {
            if (musicSrv != null) {
                musicSrv.removeTimer();
                musicSrv.removeAllListeners();
                unbindService(musicConnection);
                stopService(playIntent);
                musicSrv = null;
                playIntent = null;
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onTerminate() {
        stopMusic();
        super.onTerminate();
    }

    private HttpProxyCacheServer proxy;

    public HttpProxyCacheServer getProxy(Context context) {
      //  MainApplication app = (MainApplication) context.getApplicationContext();
        return proxy == null ? (proxy = newProxy()) : proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }

    public Intent getIntent() {
        return playIntent;
    }
}

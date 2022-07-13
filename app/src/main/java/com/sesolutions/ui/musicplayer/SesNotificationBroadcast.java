package com.sesolutions.ui.musicplayer;

/**
 * Created by WarFly on 1/14/2018.
 */

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.MainApplication;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.Map;

public class SesNotificationBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CustomLog.e("TAG_action_1", "" + intent.getAction());
        try {
                MusicService service = ((MainApplication) context.getApplicationContext()).getMusicService();
                if (service == null) {
                    return;
                }
                Map<Integer, OnUserClickedListener<Integer, Object>> listener = service.getProgressListener();
                if (intent.getAction().equals(MusicService.NOTIFY_PLAY)) {
                    service.go();
                   // service.updateNotification();
                } else if (intent.getAction().equals(MusicService.NOTIFY_PAUSE)) {
                    service.pausePlayer();
                   // service.updateNotification();
                } else if (intent.getAction().equals(MusicService.NOTIFY_NEXT)) {
                    service.playNext();
                    //service.notification();
                } else if (intent.getAction().equals(MusicService.NOTIFY_DELETE)) {
                    ((MainApplication) context.getApplicationContext()).stopMusic();
                    removenotification(context);
                    if (listener != null) {
                        callListeners(listener, Constant.Events.STOP, "", 0);
                    } /*else {
                        ((MainApplication) context.getApplicationContext()).stopMusic();
                    }*/

                    // ((MainApplication) context.getApplicationContext()).stopMusic();

                    /* Intent in = new Intent(context, MainActivity.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(in);*/
                } else if (intent.getAction().equals(MusicService.NOTIFY_PREVIOUS)) {
                    service.playPrev();

                }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void removenotification(Context context){
        NotificationManager notifManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
    }

    public void callListeners(Map<Integer, OnUserClickedListener<Integer, Object>> listeners, Integer a1, String a2, int a3) {
        if (null != listeners) {
            for (OnUserClickedListener<Integer, Object> lst : listeners.values()) {
                lst.onItemClicked(a1, a2, a3);
            }
        }

    }

    public String ComponentName() {
        return this.getClass().getName();
    }
}


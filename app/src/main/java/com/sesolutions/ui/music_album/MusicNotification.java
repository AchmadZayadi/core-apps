package com.sesolutions.ui.music_album;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import android.widget.RemoteViews;

import com.sesolutions.R;

/**
 * Created by root on 1/12/17.
 */


public class MusicNotification {

    private Context parent;
    private NotificationManager nManager;
    private NotificationCompat.Builder nBuilder;
    private RemoteViews remoteView;

    public MusicNotification(Context parent) {
        // TODO Auto-generated constructor stub
        this.parent = parent;
        nBuilder = new NotificationCompat.Builder(parent)
                .setContentTitle("Parking Meter")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true);

        remoteView = new RemoteViews(parent.getPackageName(), R.layout.notification_view);

        //set the button listeners
        setListeners(remoteView);
        nBuilder.setContent(remoteView);

        nManager = (NotificationManager) parent.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(2, nBuilder.build());
    }

    public void setListeners(RemoteViews view){
        //listener 1
        Intent volume = new Intent(parent,MusicNotification.class);
        volume.putExtra("DO", "volume");
        PendingIntent btn1 = PendingIntent.getActivity(parent, 0, volume, 0);
        view.setOnClickPendingIntent(R.id.btn1, btn1);

        //listener 2
        Intent stop = new Intent(parent, MusicNotification.class);
        stop.putExtra("DO", "stop");
        PendingIntent btn2 = PendingIntent.getActivity(parent, 1, stop, 0);
        view.setOnClickPendingIntent(R.id.btn2, btn2);
    }

    public void notificationCancel() {
        nManager.cancel(2);
    }
}
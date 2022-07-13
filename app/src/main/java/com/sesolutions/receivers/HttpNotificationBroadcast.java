package com.sesolutions.receivers;

/**
 * Created by WarFly on 1/14/2018.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.Map;

public class HttpNotificationBroadcast extends BroadcastReceiver {
    public static final String NOTIFY_PROGRESS = "com.sesolutions.progress";
    public static final String NOTIFY_FINISHED = "com.sesolutions.finished";
    private final OnUserClickedListener<Integer, Object> listener;

    public HttpNotificationBroadcast(OnUserClickedListener<Integer, Object> listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // CustomLog.e("TAG_action_1", "" + intent.getAction());
        try {
            if (NOTIFY_PROGRESS.equals(intent.getAction())) {
                listener.onItemClicked(Constant.Events.PROGRESS_UPDATE,
                        intent.getBooleanExtra(Constant.KEY_SUCCESS, false),
                        intent.getIntExtra(Constant.KEY_DATA, 0));
            } else if (NOTIFY_FINISHED.equals(intent.getAction())) {
                listener.onItemClicked(Constant.Events.SUCCESS,
                        100,
                        -1);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void callListeners(Map<Integer, OnUserClickedListener<Integer, Object>> listeners, Integer a1, String a2, int a3) {
        if (null != listeners) {
            for (OnUserClickedListener<Integer, Object> lst : listeners.values()) {
                lst.onItemClicked(a1, a2, a3);
            }
        }

    }
}


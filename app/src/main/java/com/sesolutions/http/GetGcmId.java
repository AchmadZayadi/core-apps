package com.sesolutions.http;

import android.content.Context;
import android.os.AsyncTask;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

/**
 * Created by root on 11/1/18.
 */

public class GetGcmId extends AsyncTask<Context, Void, Void> {
    private final OnUserClickedListener<Integer, Object> listener;
    //private GoogleCloudMessaging gcm;

    public GetGcmId(OnUserClickedListener<Integer, Object> listener) {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Context... params) {
        try {
            Constant.GCM_DEVICE_ID = FirebaseInstanceId.getInstance().getToken();
           /* if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(params[0]);
            }*/
            //  Constant.GCM_DEVICE_ID = GoogleCloudMessaging.getInstance(params[0]).register(params[0].getString(R.string.FIREBASE_ID));
        } catch (Exception ex) {
            CustomLog.e(ex);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        CustomLog.e("GCM_ID", "" + Constant.GCM_DEVICE_ID);
        if (listener != null) {
            listener.onItemClicked(Constant.Events.GCM_FETCHED, Constant.EMPTY, 0);
        }
    }
}

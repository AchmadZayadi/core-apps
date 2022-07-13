package com.sesolutions.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.sesolutions.BuildConfig;
import com.sesolutions.R;
import com.sesolutions.receivers.HttpNotificationBroadcast;
import com.sesolutions.responses.SesResponse;
import com.sesolutions.ui.customviews.CircularProgressBar;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

public class HttpImageNotificationRequest extends AsyncTask<HttpRequestVO, Integer, String> {
    private static final int NOTIFICATION_ID = 234;
    private static final int PROGRESS_MAX = 100;
    private final boolean showProgress;
    protected Context context;
    protected Handler handler;
    protected int requestCode;
    private ProgressDialog pDialog;
    // protected MyMultiPartEntity.ProgressListener listener;
    // private ProgressDialog pDialog;

    private MyMultiPartEntity.ProgressListener progressListener =
            new MyMultiPartEntity.ProgressListener() {
                @Override
                public void transferred(final float progress) {
                    CustomLog.d("progress__", "" + progress);
                    ((Activity) context).runOnUiThread(() -> {
                        try {
                            if (null != pDialog) {
                                ((TextView) pDialog.findViewById(R.id.tvText)).setText(((int) progress) + " %");
                                ((CircularProgressBar) pDialog.findViewById(R.id.cpb)).setProgressWithAnimation(progress, 1500);
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                    });
                }
            };

    public HttpImageNotificationRequest(Activity activity, Handler handler) {
        this(activity, handler, -1, false);
    }

    public HttpImageNotificationRequest(Activity activity, Handler handler, MyMultiPartEntity.ProgressListener progressListener) {
        this(activity, handler, -1, false);
        this.progressListener = progressListener;
    }

    public HttpImageNotificationRequest(Activity activity, Handler handler, boolean showProgress) {
        this(activity, handler, -1, showProgress);
    }

    public HttpImageNotificationRequest(Context activity, Handler handler, int requestCode, boolean showProgress) {
        this.context = activity;
        this.handler = handler;
        this.requestCode = requestCode;
        this.showProgress = showProgress;

    }

    public void run(HttpRequestVO... params) {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private void updateNotification(int progress) {
        mBuilder.setProgress(100, progress, false);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    private void sendBroadcast(int progress, String action) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction(action);
        intent.putExtra(Constant.KEY_DATA, progress);
        // intent.putExtra(Constant.KEY_SUCCESS, isFinished);
        context.sendBroadcast(intent);
    }

    private NotificationCompat.Builder mBuilder;
    private NotificationManagerCompat notificationManager;

    private void showNotification() {
        notificationManager = NotificationManagerCompat.from(context);
        String channelName = BuildConfig.APP_NAME.replace(" ", "");
        String channelId = BuildConfig.APP_NAME.replace(" ", "");
        mBuilder = new NotificationCompat.Builder(context, channelId);
        mBuilder.setContentTitle("Updating Status")
                .setContentText("Upload in progress")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        // Issue the initial notification with zero progress
        mBuilder.setProgress(PROGRESS_MAX, 0, false);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            if (showProgress) {
               /* pDialog = new ProgressDialog(activity);
                pDialog.setMessage("Uploading please wait...");
                pDialog.setCancelable(false);
                pDialog.setIndeterminate(false);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setMax(100);
                pDialog.setProgress(0);
                pDialog.show();*/
                // new ThemeManager().applyTheme((ViewGroup) pDialog,activity);
                pDialog = ProgressDialog.show(context, "", "", true);
                pDialog.setCancelable(false);
                pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                pDialog.setContentView(R.layout.dialog_progress_text);
                //  pDialog.findViewById(R.id.rlDialogMain).setBackgroundColor(Color.parseColor(Constant.foregroundColor));
                ((TextView) pDialog.findViewById(R.id.tvText)).setTextColor(Color.WHITE);
                CircularProgressBar circularProgressBar = pDialog.findViewById(R.id.cpb);
                circularProgressBar.setColor(Color.parseColor(Constant.colorPrimary));
                circularProgressBar.setBackgroundColor(Color.parseColor(Constant.menuButtonActiveTitleColor.replace("#", "#67")));
                //  circularProgressBar.setProgressBarWidth(getResources().getDimension(R.dimen.progressBarWidth));
                // circularProgressBar.setBackgroundProgressBarWidth(getResources().getDimension(R.dimen.backgroundProgressBarWidth));
                circularProgressBar.setProgressWithAnimation(0, 0); // Default duration = 1500ms
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    protected String doInBackground(HttpRequestVO... request) {

        String response = null;
        if (request != null && request.length > 0)
            try {
//                response = new HttpImageRequestHelper().executeHttpRequest(HttpRequestHelper.INSTANCE.getCertFromFile(context), request[0], progressListener);
                response = new HttpImageRequestHelper().executeHttpRequest(HttpRequestHelper.INSTANCE.getCertFromString(), request[0], progressListener);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        return response;
    }

    @Override
    protected void onPostExecute(String response) {

        try {
            if (showProgress) {
                pDialog.dismiss();
            }
            if (null != response) {
                CustomLog.e("response_createPost", "" + response);
                SesResponse resp = new Gson().fromJson(response, SesResponse.class);
                if (TextUtils.isEmpty(resp.getError())) {
                    sendBroadcast(100, HttpNotificationBroadcast.NOTIFY_FINISHED);
                } else {
                    Util.showToast(context, resp.getErrorMessage());
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

        Message msg = handler.obtainMessage();
        msg.obj = response;
        handler.sendMessage(msg);
    }
}

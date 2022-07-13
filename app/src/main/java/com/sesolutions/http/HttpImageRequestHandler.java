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
import androidx.annotation.NonNull;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sesolutions.R;
import com.sesolutions.firebase.AppVersion;
import com.sesolutions.firebase.FirebaseHelper;
import com.sesolutions.ui.common.MaintenanceActivity;
import com.sesolutions.ui.customviews.CircularProgressBar;
import com.sesolutions.ui.welcome.WelcomeActivity;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

public class HttpImageRequestHandler extends AsyncTask<HttpRequestVO, Integer, String> {
    private final boolean showProgress;
    private Context activity;
    protected Handler handler;
    protected int requestCode;
    // protected MyMultiPartEntity.ProgressListener listener;
    private ProgressDialog pDialog;

    private MyMultiPartEntity.ProgressListener progressListener =
            new MyMultiPartEntity.ProgressListener() {
                @Override
                public void transferred(final float progress) {
                    CustomLog.d("progress__", "" + progress);
                    ((Activity) activity).runOnUiThread(() -> {
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

    public HttpImageRequestHandler(Activity activity, Handler handler) {
        this(activity, handler, -1, false);
    }

    public HttpImageRequestHandler(Activity activity, Handler handler, MyMultiPartEntity.ProgressListener progressListener) {
        this(activity, handler, -1, false);
        this.progressListener = progressListener;
    }

    public HttpImageRequestHandler(Activity activity, Handler handler, boolean showProgress) {
        this(activity, handler, -1, showProgress);
    }

    public HttpImageRequestHandler(Context activity, Handler handler, int requestCode, boolean showProgress) {
        this.activity = activity;
        this.handler = handler;
        this.requestCode = requestCode;
        this.showProgress = showProgress;

    }

    public void run(HttpRequestVO... params) {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        //check if sslCertificate is fetched or not
       /* if (null != HttpRequestHelper.sslCertificate) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
            return;
        }
        //if not fetched then first fetch certificate from firebase database then call API
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(FirebaseHelper.TABLE_APP_VERSION);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    AppVersion user = dataSnapshot.getValue(AppVersion.class);
                    HttpRequestHelper.sslCertificate = user.getSslCertificate();
                    executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });*/
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
                pDialog = ProgressDialog.show(activity, "", "", true);
                pDialog.setCancelable(false);
                pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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

    /*private X509Certificate getCertFromFile(Context context, String path) throws Exception {
        AssetManager assetManager = context.getResources().getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream caInput = new BufferedInputStream(inputStream);
        X509Certificate cert = null;
        CertificateFactory cf = CertificateFactory.getInstance("X509");
        cert = (X509Certificate) cf.generateCertificate(caInput);
        cert.getSerialNumber();
        return cert;
    }*/

    @Override
    protected String doInBackground(HttpRequestVO... request) {

        String response = null;
        if (request != null && request.length > 0)
            try {
//                response = new HttpImageRequestHelper().executeHttpRequest(HttpRequestHelper.INSTANCE.getCertFromFile(activity), request[0], progressListener);
                response = new HttpImageRequestHelper().executeHttpRequest(HttpRequestHelper.INSTANCE.getCertFromString(), request[0], progressListener);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        if (showProgress) {
            pDialog.dismiss();
        }

        if (null != result && result.contains("\"error_message\":\"account_deleted\"")) {
            Intent intent = new Intent(activity, WelcomeActivity.class);
            intent.putExtra(Constant.KEY_TYPE, Constant.Events.ACCOUNT_DELETED);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
        } else if (null != result && result.contains("\"error_message\":\"maintenance_code_enable\"")) {
            Intent intent = new Intent(activity, MaintenanceActivity.class);
            intent.putExtra(Constant.KEY_TYPE, Constant.Events.ACCOUNT_DELETED);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
        } else {
            Message msg = handler.obtainMessage();
            msg.obj = result;
            handler.sendMessage(msg);
        }
    }
}

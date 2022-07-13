package com.sesolutions.http;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.sesolutions.firebase.AppVersion;
import com.sesolutions.firebase.FirebaseHelper;
import com.sesolutions.ui.common.MaintenanceActivity;
import com.sesolutions.ui.welcome.NameValue;
import com.sesolutions.ui.welcome.WelcomeActivity;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;

public class HttpRequestHandler extends AsyncTask<HttpRequestVO, Integer, String> {

    private final Context activity;
    protected Handler handler;
    protected int requestCode;

    public HttpRequestHandler(Context context, Handler handler) {
        this(context, handler, -1);
    }

    public HttpRequestHandler(Context activity, Handler handler, int requestCode) {
        this.activity = activity;
        this.handler = handler;
        this.requestCode = requestCode;

    }

    public void run(HttpRequestVO... params) {
       executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
     /*   if (null != HttpRequestHelper.sslCertificate) {
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
            return;
        }
        //if not fetched then first fetch certificate from firebase database then call API
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child(FirebaseHelper.TABLE_APP_VERSION);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                try {
                    AppVersion user = dataSnapshot.getValue(AppVersion.class);
                    HttpRequestHelper.sslCertificate = user.getSslCertificate();
                    executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {
            }
        });*/
    }


    @Override
    protected String doInBackground(HttpRequestVO... request) {
        String response = null;
        if (request != null)
            try {
                response = executeHttpRequest(request[0]);
            } catch (HttpHostConnectException e) {
                e.printStackTrace();
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (NoHttpResponseException e) {
                e.printStackTrace();
            }
        return response;
    }



    private String executeHttpRequest(/*Context ctx,*/ HttpRequestVO request) throws NoHttpResponseException, ConnectTimeoutException,
            SocketTimeoutException, HttpHostConnectException {
        String response = null;


        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 120000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 120000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);


       /* ClientConnectionManager ccm = null;
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SchemeRegistry registry = new SchemeRegistry();
//            SSLSocketFactory sf = new MySslSocketFactolry(HttpRequestHelper.INSTANCE.getCertFromFile(activity), trustStore);
            SSLSocketFactory sf = new MySslSocketFactolry(HttpRequestHelper.INSTANCE.getCertFromString(), trustStore);

            HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            sf.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);

            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ccm = new ThreadSafeClientConnManager(httpParameters, registry);
        } catch (KeyStoreException e) {
            CustomLog.e(e);
        } catch (IOException e) {
            CustomLog.e(e);
        } catch (NoSuchAlgorithmException e) {
            CustomLog.e(e);
        } catch (CertificateException e) {
            CustomLog.e(e);
        } catch (KeyManagementException e) {
            CustomLog.e(e);
        } catch (UnrecoverableKeyException e) {
            CustomLog.e(e);
        } catch (Exception e) {
            CustomLog.e(e);
        }*/

        DefaultHttpClient httpClient = new DefaultHttpClient( httpParameters);

        //DefaultHttpClient httpClient = getNewHttpClient();

        httpClient.setParams(httpParameters);
        UrlEncodedFormEntity entity;


        List<NameValue> list = new ArrayList<>();
        try {
            for (String key : request.params.keySet()) {
                list.add(new NameValue(key, String.valueOf(request.params.get(key))));
            }



            entity = new UrlEncodedFormEntity(list, "UTF-8");

            if (HttpPost.METHOD_NAME.equals(request.requestMethod)) {
                HttpPost httpPost = new HttpPost(request.url);

                CustomLog.e("URL", "" + request.url);
                CustomLog.e("REQUEST", "" + new Gson().toJson(request.params));

                for (String key : request.headres.keySet()) {
                    httpPost.setHeader(key, request.headres.get(key));
                }
                CustomLog.e("HEADERS", "" + new Gson().toJson(request.headres));

                httpPost.setEntity(entity);
                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity resEntityGet = httpResponse.getEntity();

                if (resEntityGet != null) {
                    response = EntityUtils.toString(resEntityGet, "UTF-8");
                } else {
                    return null;
                }

            } else {
                HttpGet httpPost = new HttpGet(request.url);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity resEntityGet = httpResponse.getEntity();
                if (resEntityGet != null) {
                    response = EntityUtils.toString(resEntityGet, "UTF-8");
                }
            }
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            CustomLog.e(e);
           // Toast.makeText(activity,"Time out error!",Toast.LENGTH_SHORT).show();
        }

        return response;
    }


    @Override
    protected void onPostExecute(String result) {
        /*if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
			dialog = null;
		}*/
        if (null != result && result.contains("\"error_message\":\"account_deleted\"")) {
            CustomLog.e("response", "" + result);
            Intent intent = new Intent(activity, WelcomeActivity.class);
            intent.putExtra(Constant.KEY_TYPE, Constant.Events.ACCOUNT_DELETED);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
        } else if (null != result && result.contains("\"error_message\":\"maintenance_code_enable\"")) {
            CustomLog.e("response", "" + result);
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

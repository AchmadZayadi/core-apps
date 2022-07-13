package com.sesolutions.http;

import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import static org.apache.http.entity.mime.HttpMultipartMode.BROWSER_COMPATIBLE;


public class HttpImageRequestHelper {

    private String getMediaType(String key) {
        if (key.contains("sescontest_audio_file")
                || key.contains(Constant.KEY_MUSIC_SONG)) {
            return "audio/*";
        } else if (key.contains(Constant.KEY_VIDEO)) {
            return "video/*";
        } else if (key.contains("attachmentVideo")) {
            return "video/*";
        } else {
            return "image/jpeg";
        }
    }

    private FileBody createFileBody(String key, File file) {
        //CustomLog.e("pathhh", "" + file.getAbsolutePath());
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        if (null == type) {
            type = getMediaType(key);
        }
        return new FileBody(file, type);
    }

    public String executeHttpRequest(X509Certificate cert, HttpRequestVO request, MyMultiPartEntity.ProgressListener listener) {
        String responce = null;
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 120000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 120000;

        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
      /*  ClientConnectionManager ccm = null;
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new MySslSocketFactolry(cert, trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            //  params = new BasicHttpParams();
            // HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            //  HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            SchemeRegistry registry = new SchemeRegistry();
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
        httpClient.setParams(httpParameters);
        try {

            CustomLog.e("URL", "" + request.url);
            CustomLog.e("REQUEST", "" + new Gson().toJson(request.params));

            MultipartEntity entity;
            if (null != listener) {
                entity = new MyMultiPartEntity(listener);
            } else {
                entity = new MultipartEntity(BROWSER_COMPATIBLE);
            }

            if (HttpPost.METHOD_NAME.equals(request.requestMethod)) {
                HttpPost httpPost = new HttpPost(request.url);

                for (String key : request.params.keySet()) {
                    if (key.startsWith(Constant.FILE_TYPE) || (key.equalsIgnoreCase("video")) || (key.equalsIgnoreCase("image"))
                    ) {
                        String value = String.valueOf(request.params.get(key));
                        CustomLog.e("image_video", "value__" + value);
                        if (/*null != value &&*/ !value.equals("")) {
                            if (value.startsWith("http")) {
                                entity.addPart(key.replace(Constant.FILE_TYPE, ""), new StringBody(String.valueOf(request.params.get(key)), Charset.forName("UTF-8")));
                            } else {
                                //File file = new File(String.valueOf(request.params.get(key)));
                                entity.addPart(key.replace(Constant.FILE_TYPE, ""), createFileBody(key, new File(String.valueOf(request.params.get(key)))));
                                // CustomLog.e("mimetype", "" + getMimeType(file.getAbsolutePath()));
                            }
                        }
                    } else {
                        entity.addPart(key, new StringBody(String.valueOf(request.params.get(key)), Charset.forName("UTF-8")));
                    }
                }
                for (String key : request.headres.keySet()) {
                    httpPost.setHeader(key, request.headres.get(key));
                }
                CustomLog.e("HEADERS", "" + new Gson().toJson(request.headres));


                httpPost.setEntity(entity);
                HttpResponse httpResponse = httpClient.execute(httpPost);

                //   final int statusCode = httpResponse.getStatusLine().getStatusCode();
                HttpEntity resEntityGet = httpResponse.getEntity();

                if (resEntityGet != null) {
                    responce = EntityUtils.toString(resEntityGet/*, Consts.UTF_8*/);
                } else {
                    return null;
                }
            }
            return responce;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(Constant.TAG, Log.getStackTraceString(e));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Constant.TAG, Log.getStackTraceString(e));
        }
        return responce;
    }
}

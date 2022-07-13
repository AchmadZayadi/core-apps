package com.sesolutions.ui.video;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.http.VideoDownloadController;
import com.sesolutions.ui.common.CommentLikeHelper;
import com.sesolutions.ui.customviews.InsideWebViewClient;
import com.sesolutions.ui.customviews.VideoEnabledWebChromeClient;
import com.sesolutions.ui.customviews.VideoEnabledWebView;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FontManager;
import com.sesolutions.utils.Util;

import java.util.ArrayList;

public class IFrameVideoFragment extends CommentLikeHelper implements View.OnClickListener {

    private static final String TAG = "IFrameVideoFragment";

    private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;
    private String iFrameData;
    private String url;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_iframe_video, container, false);
        applyTheme(v);
        init();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            activity.setStatusBarColor(Color.BLACK);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onStop() {
        activity.setStatusBarColor(Util.manipulateColor(Color.parseColor(Constant.colorPrimary)));
        super.onStop();
    }

    private void init() {
        try {
            iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME);
            webView = v.findViewById(R.id.wbVideo);
            setupWebView();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onBackPressed() {
        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
        if (!webChromeClient.onBackPressed()) {
            /*if (webView.canGoBack())
            {
                webView.goBack();
            }
            else
            {*/
            // Standard back button implementation (for example this could close the app)
            super.onBackPressed();
            // }
        }
    }

    private void setupWebView() {
        try {
            // Save the web view
            webView = v.findViewById(R.id.wbVideo);
            webView.setDownloadListener(new VideoDownloadController(context));
            // Initialize the VideoEnabledWebChromeClient and set event handlers
            View nonVideoLayout = v.findViewById(R.id.rlNonVideo); // Your own view, read class comments
            ViewGroup videoLayout = v.findViewById(R.id.videoLayout); // Your own view, read class comments
            //noinspection all
            View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null); // Your own view, read class comments
            webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
            {
                // Subscribe to standard events, such as onProgressChanged()...
                @Override
                public void onProgressChanged(WebView view, int progress) {
                    // Your code...
                }
            };
            webChromeClient.setOnToggledFullscreen(fullscreen -> {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    activity.getWindow().setAttributes(attrs);
                    //noinspection all
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                } else {
                    WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    activity.getWindow().setAttributes(attrs);
                    //noinspection all
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            });
            webView.setWebChromeClient(webChromeClient);
            webView.setBackgroundColor(Color.BLACK);
            // Call private class InsideWebViewClient
            webView.setWebViewClient(new InsideWebViewClient());
            //  webView.loadData(iFrameData, null, null);
            playVideo();

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void playVideo() {
        // url = "<div><div style=\"left: 0; width: 100%; height: 0; position: relative; padding-bottom: 56.2493%;\"><iframe src=\"//cdn.iframe.ly/api/iframe?url=https%3A%2F%2Fwww.youtube.com%2Fwatch%3Fv%3DTTmgctk9zLE&amp;key=bc356e2b5ced72fb0ae9a5d7d3fe80fd\" style=\"border: 0; top: 0; left: 0; width: 100%; height: 100%; position: absolute;\" allowfullscreen scrolling=\"no\"></iframe></div></div>";
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        } else {
            String iString = iFrameData;//.replace("\r\n", "").replace("\t", "");
            CustomLog.e("iString", iString);
            // iString = "<iframe    title=\"YouTube video player\"    id=\"videoFrame67\"    class=\"youtube_iframe_ses youtube_iframe_big\"src=\"https://www.youtube.com/embed/TJXn_sYtoYg?enablejsapi=1&wmode=opaque\"    frameborder=\"0\"    allowfullscreen=\"\"    scrolling=\"no\">    </iframe>    <script type=\"text/javascript\">        en4.core.runonce.add(function() {        var doResize = function() {            var aspect = 16 / 9;            var el = document.id(\"videoFrame67\");if(typeof el == \"undefined\" || !el)return;            var parent = el.getParent();            var parentSize = parent.getSize();            el.set(\"width\", parentSize.x);            el.set(\"height\", parentSize.x / aspect);        }        window.addEvent(\"resize\", doResize);        doResize();        });    </script>";
            webView.loadData(iString, null, null);
            // webView.loadUrl(iString);
        }
    }

    @Override
    public void onPause() {
        try {
            Log.e(TAG, "onPause: ");
            if (webView != null) {
                webView.onPause();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (webView != null) {
                webView.onResume();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    public void askForPermission() {
        try {
            new TedPermission(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(R.string.MSG_PERMISSION_DENIED)
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // Toast.makeText(How_It_Works_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            try {

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };


    public static IFrameVideoFragment newInstance(String url, String data) {
        IFrameVideoFragment frag = new IFrameVideoFragment();
        frag.iFrameData = data;
        frag.url = url;
        return frag;
    }

}

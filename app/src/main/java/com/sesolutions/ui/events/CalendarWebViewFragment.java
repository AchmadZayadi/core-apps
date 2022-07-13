package com.sesolutions.ui.events;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

public class CalendarWebViewFragment extends BaseFragment implements OnUserClickedListener<Integer, Object> {
    // @Bind(R.id.webView)
    public WebView webView;//faq web view
    //  @Bind(R.id.progressBarForAboutBeckett)
    protected ProgressBar progress;//faq progress bar
    public View v;
    private String webUrl;
    private String title;
    public TextView tvTitle;

    public static CalendarWebViewFragment newInstance(String url, String title) {
        CalendarWebViewFragment fragment = new CalendarWebViewFragment();
        fragment.webUrl = url;
        fragment.title = title;
        return fragment;
    }

    @Override
    public void onRefresh() {
        webView.loadUrl(webUrl);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            v = inflater.inflate(R.layout.fragment_web_view, container, false);
            applyTheme(v);
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void setupWebView() {
        try {
            try {
                CookieSyncManager.createInstance(context);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setAcceptCookie(true);
                cookieManager.removeSessionCookie();
                cookieManager.setCookie(webUrl, getCookie());
                CookieSyncManager.getInstance().sync();

                String cookie = cookieManager.getCookie(webUrl);

                CustomLog.d("cookie", "cookie ------>" + cookie);
            } catch (Exception e) {
                CustomLog.e(e);
            }
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(false);
            webView.getSettings().setSupportZoom(false);
            webView.setWebViewClient(new browser(progress));
            // webView.setInitialScale(50);
            webView.loadUrl(webUrl);
            // new ApiController(webUrl, null, context, this, -1).execute();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void initScreenData() {
        init();
        if (isNetworkAvailable(context))
            setupWebView();
        else {
            progress.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notInternetMsg(v);
                }
            }, 200);
        }
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        // hideBaseLoader();
       /* if (null != object2) {
            webView.loadDataWithBaseURL(Constant.BASE_URL, ("" + object2).replaceFirst("<style>", "<style>.layout_sesevent_browse_menu{display:none;}</style><style>"),
                    null, "UTF-8", webUrl);
        }*/
        return false;
    }

    //browser class
    private class browser extends WebViewClient {
        private ProgressBar progressBar;

        public browser(ProgressBar bar) {
            progressBar = bar;
            progressBar.setVisibility(View.VISIBLE);
        }

       /* @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            CustomLog.e("shouldOverrideUrlLoading", view.getUrl());
            return super.shouldOverrideUrlLoading(view, request);
        }*/

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            try {
                CustomLog.e("onPageStarted", url);
                if (url.startsWith(Constant.BASE_URL + "event/")) {
                    //this means user click to view event
                    CustomLog.e("view event", url);
                    onRefresh();
                    int eventId = Integer.parseInt(url.replace(Constant.BASE_URL + "event/", ""));
                    fragmentManager.beginTransaction().replace(R.id.container, ViewEventFragment.newInstance(eventId)).addToBackStack(null).commit();

                } else if (url.startsWith(Constant.BASE_URL + "events/create")) {
                    CustomLog.e("create event", url);
                    CustomLog.d("create event", url);
                    onRefresh();
                    fragmentManager.beginTransaction().replace(R.id.container,
                            CreateEditEventFragment.newinstance(Constant.FormType.CREATE_EVENT, Constant.URL_CREATE_EVENT, null))
                            .addToBackStack(null).commit();

                } else {
                    CustomLog.d("onPageStarted", url);
                    super.onPageStarted(view, url, favicon);
                }
            } catch (Exception e) {
                CustomLog.e(e);
                onRefresh();
            }

        }


        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);
            //  injectCSS(view);
            progressBar.setVisibility(View.GONE);
            String urlString = webView.getUrl();

            CustomLog.d("getUrl", webView.getUrl());
            CustomLog.d("getOriginalUrl", webView.getOriginalUrl());
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void init() {
        webView = v.findViewById(R.id.webView);
        progress = v.findViewById(R.id.progressBar);
        // v.findViewById(R.id.ivBack).setOnClickListener(this);
    }
}

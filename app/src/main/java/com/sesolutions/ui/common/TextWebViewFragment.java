package com.sesolutions.ui.common;

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

import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SesResponse;
import com.sesolutions.responses.credit.CreditResult;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MenuTab;
import com.sesolutions.utils.URL;

public class TextWebViewFragment extends BaseFragment implements OnUserClickedListener<Integer, Object> {
    private static final int REQ_TERMS = 103;
    private static final int REQ_GUIDELINE = 104;
    public WebView webView;

    protected ProgressBar progress;
    public View v;
    private String selectedScreen;
    private OnUserClickedListener<Integer, Object> listener;
    private String url;

    public static TextWebViewFragment newInstance(String selectedScreen, String url, OnUserClickedListener<Integer, Object> listener) {
        TextWebViewFragment fragment = new TextWebViewFragment();
        fragment.selectedScreen = selectedScreen;
        fragment.listener = listener;
        fragment.url = url;
        return fragment;
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
                if (null != url) {
                    CookieSyncManager.createInstance(context);
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.setAcceptCookie(true);
                    cookieManager.removeSessionCookie();
                    cookieManager.setCookie(url, getCookie());
                    CookieSyncManager.getInstance().sync();

                    String cookie = cookieManager.getCookie(url);

                    CustomLog.d("cookie", "cookie ------>" + cookie);
                }
            } catch (Exception e) {
                CustomLog.e(e);
            }
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(false);
            webView.getSettings().setSupportZoom(false);
            webView.setWebViewClient(new browser(progress));
            // webView.setInitialScale(50);
            //webView.loadData(webUrl);
            // new ApiController(webUrl, null, context, this, -1).execute();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void initScreenData() {
        init();
        if (isNetworkAvailable(context)) {
            setupWebView();
            fetchHtmlData();
        } else {
            progress.setVisibility(View.GONE);
            new Handler().postDelayed(() -> notInternetMsg(v), 200);
        }
    }

    private void fetchHtmlData() {
        if (MenuTab.Credit.TERMS.equals(selectedScreen))
            new ApiController(URL.CREDIT_TERMS, null, context, this, REQ_TERMS).execute();
        else if (MenuTab.Credit.POINT_EARN_HOW.equals(selectedScreen))
            new ApiController(URL.CREDIT_EARN_HOW, null, context, this, REQ_GUIDELINE).execute();
        else if (MenuTab.Credit.HELP.equals(selectedScreen)) {
            webView.loadUrl(url);
            if (null != listener) {
                listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 1);
            }
        }
    }

    private CreditResult parseCreditResult(String data) {
        if (data != null) {
            SesResponse resp = new Gson().fromJson(data, SesResponse.class);
            return resp.getResult(CreditResult.class);
        }
        return null;
    }

    @Override
    public boolean onItemClicked(Integer object1, Object data, int postion) {

        switch (object1) {
            case REQ_TERMS:
                CreditResult result = parseCreditResult("" + data);
                if (null != result) {
                    webView.loadData(result.getTerms(), "text/html", null);
                    if (null != listener) {
                        listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 1);
                    }
                }
                break;

            case REQ_GUIDELINE:
                result = parseCreditResult("" + data);
                if (null != result && null != result.getGuideline()) {
                    webView.loadData(result.getGuideline().getGuideline(), "text/html", null);
                    if (null != listener) {
                        listener.onItemClicked(Constant.Events.SET_LOADED, selectedScreen, 1);
                    }
                }

                break;
        }
        return false;
    }

    //browser class
    private class browser extends WebViewClient {
        private ProgressBar progressBar;

        public browser(ProgressBar bar) {
            progressBar = bar;
            progressBar.setVisibility(View.VISIBLE);
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

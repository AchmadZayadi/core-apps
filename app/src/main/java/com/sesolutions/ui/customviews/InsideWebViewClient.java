package com.sesolutions.ui.customviews;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by root on 21/12/17.
 */

public class InsideWebViewClient extends WebViewClient {
    @Override
    // Force links to be opened inside WebView and not in Default Browser
    // Thanks http://stackoverflow.com/a/33681975/1815624
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}
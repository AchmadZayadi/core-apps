package com.sesolutions.ui

import android.app.AlertDialog
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sesolutions.R
import com.sesolutions.ui.common.BaseActivity
import kotlinx.android.synthetic.main.content_web_view.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class WebViewActivity : BaseActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        val url:String = intent.getStringExtra("web").toString()
        val title:String = intent.getStringExtra("title").toString()

        initView(url)
        tvToolbarTitle.text = title


    }

    private fun initView(url :String) {
        swipeRefreshLayout.isRefreshing = true

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
        }

        displayContent(url)

        btnToolbarBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun displayContent(url: String) {

        val settings = webView.getSettings()
        settings.setJavaScriptEnabled(true)
        settings.setSupportZoom(true)
        settings.setBuiltInZoomControls(true)
        settings.setDisplayZoomControls(false)
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY)
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.loadUrl(url)
        viewAnimator.displayedChild = 1

        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                swipeRefreshLayout.isRefreshing = false
                swipeRefreshLayout.isEnabled = false
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                val builder = AlertDialog.Builder(this@WebViewActivity)
                var message = "SSL Certificate error."
                when (error.primaryError) {
                    SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                    SslError.SSL_EXPIRED -> message = "The certificate has expired."
                    SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                    SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
                }
                message += "Do you want to continue anyway?"

                builder.setTitle("SSL Certificate Error")
                builder.setMessage(message)
                builder.setPositiveButton("Continue") { dialog, which ->
                    handler.proceed()
                }
                builder.setNegativeButton("Cancel") { dialog, which ->
                    handler.cancel()
                    finish()
                }
                val dialog = builder.create()
                dialog.show()
            }
        })
    }

}
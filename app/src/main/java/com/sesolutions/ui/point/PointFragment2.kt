package com.sesolutions.ui.point

import android.app.AlertDialog
import android.graphics.Color
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sesolutions.R
import com.sesolutions.ui.common.BaseFragment
import com.sesolutions.utils.Constant
import com.sesolutions.utils.SPref
import kotlinx.android.synthetic.main.content_web_view.swipeRefreshLayout
import kotlinx.android.synthetic.main.content_web_view.viewAnimator
import kotlinx.android.synthetic.main.content_web_view_point.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class PointFragment2 : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_point, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        ivBack.setOnClickListener {
            onBackPressed()
        }
        toolbar.setBackgroundColor(Color.parseColor("#084B96"))
        tvTitle.text = "Poin Anda"

        displayContent()
    }

    private fun displayContent() {

        val settings = webView.getSettings()
        settings.setJavaScriptEnabled(true)
        settings.setSupportZoom(true)
        settings.setBuiltInZoomControls(true)
        settings.setDisplayZoomControls(false)
        settings.setDomStorageEnabled(true)
       // webView_point.setScrollBarStyle(webView_point.SCROLLBARS_OUTSIDE_OVERLAY)
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        val postData = "auth_token=" + SPref.getInstance().getToken(context)

        webView.postUrl(Constant.URL_POINT_MENU, postData.toByteArray())

       // webView_point.loadUrl(url)
       viewAnimator.displayedChild = 1

        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                swipeRefreshLayout.isRefreshing = false
                swipeRefreshLayout.isEnabled = false
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                val builder = AlertDialog.Builder(context)
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

                }
                val dialog = builder.create()
                dialog.show()
            }
        })
    }


}
package com.sesolutions.ui.common;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.sesolutions.R;
import com.sesolutions.ui.credit.PurchaseFormagment;
import com.sesolutions.ui.signup.SignInFragment;
import com.sesolutions.ui.signup.SignInFragment2;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.ModuleUtil;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.URL;
import com.sesolutions.utils.Util;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class WebViewFragment extends BaseFragment implements View.OnClickListener {
    public WebView webView;
    protected ProgressBar progress;
    public View v;
    private String webUrl;
    private String title;
    public TextView tvTitle;


    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            v = inflater.inflate(R.layout.fragment_web_view, container, false);
            init();
            applyTheme(v);
            if (isNetworkAvailable(context))
                setCookie();
                //  setupWebView();
            else {
                progress.setVisibility(View.GONE);
                new Handler().postDelayed(() -> notInternetMsg(v), 200);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void init() {
        if (TextUtils.isEmpty(title)) {
            v.findViewById(R.id.appBar).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.appBar).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
        }
        webView = v.findViewById(R.id.webView);
        tvTitle = v.findViewById(R.id.tvTitle);
        tvTitle.setSelected(true);
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
        tvTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        tvTitle.setSingleLine(true);
        tvTitle.setSelected(true);
        progress = v.findViewById(R.id.progressBar);
        v.findViewById(R.id.ivBack).setOnClickListener(this);
    }

    public PermissionListener permissionlistener = new PermissionListener() {


        @Override
        public void onPermissionGranted() {
            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    onShowCustomFileChooser();
                } else {
                    openCustomFileChooser();
                }

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

    private void setCookie() {
        try {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.setCookie(Constant.BASE_URL, getCookie(), value -> {
                    String cookie = cookieManager.getCookie(Constant.BASE_URL);
                    CookieManager.getInstance().flush();
                    CustomLog.d("cookie", "cookie ------>" + cookie);
                    setupWebView();
                });
            } else {
                cookieManager.setCookie(webUrl, getCookie());
                new Handler().postDelayed(this::setupWebView, 700);
                CookieSyncManager.getInstance().sync();
            }

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void setupWebView() {
        try {

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setBuiltInZoomControls(false);
            webView.getSettings().setSupportZoom(false);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setDomStorageEnabled(true);
            // for supporting strip payment checkout
            webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4.4; One Build/KTU84L.H4) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/28.0.0.20.16;]");

            webView.setWebViewClient(new browser(progress));
            webView.setWebChromeClient(new ChromeClient());
            if (Build.VERSION.SDK_INT >= 19) {
                webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else if (Build.VERSION.SDK_INT >= 16 /*&& Build.VERSION.SDK_INT < 19*/) {
                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
            String web_url_final="";
            if(webUrl.contains("page-directories/dashboard/edit/")){
                web_url_final=webUrl+"?removeSiteHeaderFooter=true&sesapi_platform=2&auth_token="+ SPref.getInstance().getToken(context);
            }else {
                web_url_final=webUrl;
            }
            Log.e("final url",""+web_url_final);
            webView.loadUrl(web_url_final);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static WebViewFragment newInstance(String url, String title) {
        WebViewFragment fragment = new WebViewFragment();
        fragment.webUrl = url;
        fragment.title = title;
        return fragment;
    }

    @Override
    public void onClick(View v) {
        super.onBackPressed();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    public class ChromeClient extends WebChromeClient {
        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;
            askForPermission(permissionlistener, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return true;
        }

        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            askForPermission(permissionlistener, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {
            openFileChooser(uploadMsg, acceptType);
        }
    }

    // For Android 5.0
    public void onShowCustomFileChooser() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
            } catch (IOException ex) {
                // Error occurred while creating the File
                CustomLog.e("onShowFileChooser", "Unable to create Image File", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");
        Intent[] intentArray;
        if (takePictureIntent != null) {
            intentArray = new Intent[]{takePictureIntent};
        } else {
            intentArray = new Intent[0];
        }
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
    }

    // openFileChooser for Android 3.0+
    public void openCustomFileChooser() {
        // Create AndroidExampleFolder at sdcard
        // Create AndroidExampleFolder at sdcard
        File imageStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES)
                , "AndroidExampleFolder");
        if (!imageStorageDir.exists()) {
            // Create AndroidExampleFolder at sdcard
            imageStorageDir.mkdirs();
        }
        // Create camera captured image file path and name
        File file = new File(
                imageStorageDir + File.separator + "IMG_"
                        + String.valueOf(System.currentTimeMillis())
                        + ".jpg");
        mCapturedImageURI = Uri.fromFile(file);
        // Camera capture image intent
        final Intent captureIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        // Create file chooser intent
        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
        // Set camera intent to file chooser
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                , new Parcelable[]{captureIntent});
        // On select image call onActivityResult method of activity
        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        webView.destroy();
        webView = null;
        super.onDestroy();
    }


    //browser class
    public class browser extends WebViewClient {
        private ProgressBar progressBar;

        public browser(ProgressBar bar) {
            progressBar = bar;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            //   cookieManager.removeSessionCookie();
            cookieManager.setCookie(url, getCookie());
            CookieSyncManager.getInstance().sync();
            return super.shouldInterceptRequest(view, url);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.endsWith("bookings")) {
                activity.taskPerformed = Constant.FormType.BECOME_NORMAL;
                closeWebview();
                return true;
            }
            return false;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            tvTitle.setText(view.getTitle());
            progressBar.setVisibility(View.GONE);
            String urlString = webView.getUrl();
            if (urlString.contains("finish/state/failure")) {
                Util.showSnackbar(view, getStrings(R.string.MSG_SUBSCRIPTION_FAILED));
                if (urlString.contains("contest_id")) {
                    closeWebview();
                } else {
                    goToWelcomeFragment(0);
                }

            } else if (urlString.contains("finish/state/active")) {
                /*now the url will be this :
                    http://pagestd.socialenginesolutions.com/contestpayment/finish/state/active/contest_id/45*
                    It means Payment of package is successful : so handle accordingly*/
                if (urlString.contains("contest_id")) {
                    handleContestPackagePayment(urlString, Constant.ResourceType.CONTEST);
                } else if (urlString.contains("business_id")) {
                    handleContestPackagePayment(urlString, Constant.ResourceType.BUSINESS);
                } else if (urlString.contains("group_id")) {
                    handleContestPackagePayment(urlString, Constant.ResourceType.GROUP);
                } else if (urlString.contains("page_id")) {
                    handleContestPackagePayment(urlString, Constant.ResourceType.PAGE);

                } else if (urlString.contains("sescredit")) {
                    WebViewFragment.super.onBackPressed();

                } else if (urlString.contains("crowdfunding")) {
                    WebViewFragment.super.onBackPressed();

                } else {
                    Util.showSnackbar(view, getStrings(R.string.MSG_SUBSCRIPTION_SUCCESS));
                    goToWelcomeFragment(4);
                }
            }
            CustomLog.d("getUrl", webView.getUrl());
            CustomLog.d("getOriginalUrl", webView.getOriginalUrl());
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void closeWebview() {
        super.onBackPressed();
    }

    private void handleContestPackagePayment(String urlString, String rcType) {
        try {
            int MODULE = ModuleUtil.getInstance().fetchDestination(rcType);
            String[] str = urlString.split("/");
            int id = Integer.parseInt(str[str.length - 1]);
            goTo(MODULE, Constant.KEY_ID, id);
            activity.finish();
        } catch (Exception e) {
            CustomLog.e(e);
            somethingWrongMsg(v);
            //if anything goes wrong restart app and show subscription dialog
            goToWelcomeFragment(4);
        }
    }

    private void goToWelcomeFragment(int type) {
       // fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment()).commit();
        fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment2())
                .commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }
}


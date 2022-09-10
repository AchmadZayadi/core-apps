package com.sesolutions.ui.common;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.preference.PreferenceManager;

import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.perf.internal.SessionManager;
import com.google.gson.Gson;
import com.sesolutions.BuildConfig;
import com.sesolutions.R;
import com.sesolutions.http.GetGcmId;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.http.ResourceToConstantTask;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.SignInResponse;
import com.sesolutions.responses.SlideShowImage;
import com.sesolutions.responses.videos.Result;
import com.sesolutions.responses.videos.VideoBrowse;
import com.sesolutions.responses.videos.Videos;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.dashboard.MainActivity;
import com.sesolutions.ui.intro.IntroActivity;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.ui.welcome.WelcomeActivity;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class SplashAnimatedActivity extends BaseActivity {

    private AppCompatImageView ivImage;
    private boolean isUserLoggedIn = false;
    private boolean isBackPressed = false;
    public List<Videos> videoList;
    public int videoListSize;
    public Result result;
    private HttpProxyCacheServer proxy;
    FrameLayout flMain;
    SharedPreferences mPrefs;
    final String welcomeScreenShownPref = "welcomeScreenShown";
    AppCompatImageView ivSplash;


    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.animated_splash);
            ProviderInstaller.installIfNeeded(getApplicationContext());
            flMain = findViewById(R.id.flMain);
            //check if user share something in our app
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            if (action != null && type != null) {
                //this means user share something
                //so go to OutSideShareActivity instead of MainActivity
                Constant.isSharingFromOutside = true;
            }

            // SPref.getInstance().updateSharePreferences(SplashAnimatedActivity.this, Constant.KEY_LOGGED_IN, isUserLoggedIn);

            isUserLoggedIn = SPref.getInstance().getBoolean(this, Constant.KEY_LOGGED_IN);
            mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            // second argument is the default to use if the preference can't be found

            /*if(isUserLoggedIn){
                //callDefaultDataApi();
                SignInResponse response=SPref.getInstance().getUserInfo(this,Constant.KEY_USERINFO_JSON);

                Saveuserdata(response);

                DefaultDataVo responsede=SPref.getInstance().getDefaultInfo(this,Constant.KEY_APPDEFAULT_DATA);
                if(responsede!=null){
                    callDefaultDataSet(responsede,1000);
                }else {
                    callDefaultDataApi();
                }
            }else {
                callDefaultDataApi();
            }*/
            callCheckLogin();


            new ResourceToConstantTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new GetGcmId(null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);

            ivImage = findViewById(R.id.ivImage);
            ivSplash = findViewById(R.id.iv_splash);
            //   setImage();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Constant.language = Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
            } else {
                //noinspection deprecation
                Constant.language = Resources.getSystem().getConfiguration().locale.getLanguage();
            }
            Constant.VALUE_ANDROID_ID = SPref.getInstance().getString(this, Constant.KEY_ANDROID_ID);
            if (TextUtils.isEmpty(Constant.VALUE_ANDROID_ID)) {
                Constant.VALUE_ANDROID_ID = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                SPref.getInstance().updateSharePreferences(this, Constant.KEY_ANDROID_ID, Constant.VALUE_ANDROID_ID);
            }
            // CustomLog.e("androidId", "" + Constant.VALUE_ANDROID_ID);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onBackPressed() {
        isBackPressed = true;
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        isBackPressed = true;
        super.onStop();
    }

    void setImage() {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.iv_splash_screen)
                .error(R.drawable.iv_splash_screen);

        Glide.with(this).load("").apply(options).into(ivSplash);
    }


    @Override
    protected void onResume() {
        super.onResume();


//       setLanguage();
    }

    private void callHandler(int timer) {
        new Handler().postDelayed(() -> {
            try {
                if (!isBackPressed) {
                    findViewById(R.id.lavLoader).setVisibility(View.INVISIBLE);
                    if (isUserLoggedIn) {
                        // openMainActivity();
                        startActivity(new Intent(SplashAnimatedActivity.this, MainActivity.class));

                        // finish();
                    } else {
                        List<SlideShowImage> introImageList = SPref.getInstance().getIntroImages(this);
                        if (!Constant.isSharingFromOutside && introImageList != null && introImageList.size() > 0) {
                            Intent intent = new Intent(SplashAnimatedActivity.this, IntroActivity.class);
                            startActivity(intent);
                            // finish();
                        } else {
                            goToLogin();
                        }
                    }
                }
            } catch (Exception e) {
                CustomLog.e(e);
                goToLogin();
            }
        }, timer);// SPLASH_TIME_OUT);


        handlerww.postDelayed(r, 100);


    }


    Handler handlerww = new Handler();

    final Runnable r = new Runnable() {
        public void run() {
            callDefaultDataApilast();
        }
    };


    private void goToLogin() {
        Boolean welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref, false);

        if (!welcomeScreenShown) {

            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putBoolean(welcomeScreenShownPref, true);
            editor.commit(); // Very important to save the preference
            Intent intent = new Intent(SplashAnimatedActivity.this, OnBoardingActivity.class);
            startActivity(intent);

        } else {
            Intent loginIntent = new Intent(SplashAnimatedActivity.this, WelcomeActivity.class);
            if (Constant.isSharingFromOutside) {
                Intent intent = getIntent();
                loginIntent.putExtras(intent.getExtras());
                loginIntent.setAction(intent.getAction());
                loginIntent.setType(intent.getType());
            }
            startActivity(loginIntent);
        }


        // finish();

    }

    public boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public UserMaster getUserMasterDetail() {
        String json = getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).getString(Constant.KEY_USER_MASTER, "{}");
        return new Gson().fromJson(json, UserMaster.class);
    }

    public void callMusicAlbumApi(final int req) {

        try {
            if (isNetworkAvailable(getApplicationContext())) {
                try {
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_TICK_BROWSE);
                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.params.put(Constant.KEY_PAGE, 1);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(getApplicationContext()));
                    request.requestMethod = HttpPost.METHOD_NAME;
                    Handler.Callback callback = new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            hideBaseLoader();
                            try {
                                String response = (String) msg.obj;

                                CustomLog.e("repsonse1", "" + response);
                                if (response != null) {
                                    ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                    if (TextUtils.isEmpty(err.getError())) {
                                        VideoBrowse resp = new Gson().fromJson(response, VideoBrowse.class);
                                        result = resp.getResult().getForyou().getResult();
                                        if (result.getVideos() != null) {
                                            videoList.addAll(result.getVideos());
                                        }
                                        for (int j = 0; j < resp.getResult().getCreators().size(); j++) {
                                           /* if (resp.getResult().getCreators().get(j).getVideo() != null) {
                                                videoList.add(resp.getResult().getCreators().get(j).getVideo());
                                            }*/
                                        }
                                        videoListSize = videoList.size();
                                        new AsyncCaller().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getApplicationContext());
                                    }
                                }

                            } catch (Exception e) {
                                hideBaseLoader();

                                CustomLog.e(e);
                            }

                            // dialog.dismiss();
                            return true;
                        }
                    };
                    new HttpRequestHandler(getApplicationContext(), new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();

                }

            }
        } catch (Exception e) {
            hideBaseLoader();

        }
    }

    private class AsyncCaller extends AsyncTask<Context, Void, Void> {

        @Override
        protected Void doInBackground(Context... params) {
            loadvideos();
            return null;
        }

        public void loadvideos() {
            for (int i = 0; i < videoListSize; i++) {
                URL url = null;
                try {
                    proxy = ((MainApplication) getApplicationContext()).getProxy(getApplicationContext());
                    if (videoList.get(i).getIframeURL() != null) {
                        url = new URL(proxy.getProxyUrl(videoList.get(i).getIframeURL(), true));

                        InputStream inputStream = url.openStream();
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];
                        int length = 0;
                        while ((length = inputStream.read(buffer)) != -1) {
                            //nothing to do
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

    private void callDefaultDataApi() {
        if (isNetworkAvailable(this)) {
            try {
                HttpRequestVO request = new HttpRequestVO(Constant.URL_DEFAULT_DATA);
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getUserMasterDetail(this).getAuthToken());
                request.requestMethod = HttpPost.METHOD_NAME;
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                Handler.Callback callback = msg -> {

                    try {
                        String response = (String) msg.obj;

                        CustomLog.e("repsonse_default_data", "" + response);

                        if (response != null) {
                            DefaultDataVo datVo = new Gson().fromJson(response, DefaultDataVo.class);
                            SPref.getInstance().saveDefaultInfo(this, Constant.KEY_APPDEFAULT_DATA, datVo);
                            callDefaultDataSet(datVo, 100);
                        }

                    } catch (Exception e) {
                        CustomLog.e(e);
                    }

                    return true;
                };
                new HttpRequestHandler(this, new Handler(callback)).run(request);

            } catch (Exception ignored) {
            }
        } else {
            Util.showSnackbar(flMain, Constant.MSG_NO_INTERNET);
        }
    }

    private void callDefaultDataApilast() {
        try {
            HttpRequestVO request = new HttpRequestVO(Constant.URL_DEFAULT_DATA);
            request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getUserMasterDetail(this).getAuthToken());
            request.requestMethod = HttpPost.METHOD_NAME;
            request.headres.put(Constant.KEY_COOKIE, getCookie());
            Handler.Callback callback = msg -> {

                try {
                    String response = (String) msg.obj;
                    if (response != null) {
                        DefaultDataVo datVo = new Gson().fromJson(response, DefaultDataVo.class);
                        SPref.getInstance().saveDefaultInfo(this, Constant.KEY_APPDEFAULT_DATA, datVo);
                    }
                } catch (Exception e) {
                    CustomLog.e(e);
                }
                return true;
            };
            new HttpRequestHandler(this, new Handler(callback)).run(request);
        } catch (Exception ignored) {
        }
    }

    private void callDefaultDataSet(DefaultDataVo datVo, int timer) {
        datVo.getResult().setAppConfiguration();
        if (!isUserLoggedIn) {
            Util.cacheImageWithGlide(datVo.getResult().getLoginBackgroundImage(), this);
            Util.cacheImageWithGlide(datVo.getResult().getForgotPasswordBackgroundImage(), this);
            Util.cacheImageWithGlide(datVo.getResult().getRateusBackgroundImage(), this);
            Util.cacheImageWithGlide(datVo.getResult().getReaction(), this);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                AppConfiguration.hasWelcomeVideo = datVo.getResult().isVideoSlideshow();
                AppConfiguration.isWelcomeScreenEnabled = datVo.getResult().isWelcomeScreenEnabled();
                List<SlideShowImage> list = datVo.getResult().getSlideshow();
                if (list != null && list.size() > 0) {
                    AppConfiguration.isSlideImagesAvailable = true;
                    SPref.getInstance().updateSharePreferences(SplashAnimatedActivity.this, list, Constant.KEY_SLIDE_SHOW);
                } else {
                    AppConfiguration.isSlideImagesAvailable = false;
                }

                SPref.getInstance().updateSharePreferences(SplashAnimatedActivity.this, SPref.IMAGE_LOGIN_BG, datVo.getResult().getLoginBackgroundImage());
                SPref.getInstance().updateSharePreferences(SplashAnimatedActivity.this, SPref.IMAGE_FORGOT_PASSWORD_BG, datVo.getResult().getForgotPasswordBackgroundImage());
                SPref.getInstance().updateSharePreferences(SplashAnimatedActivity.this, datVo.getResult().getGraphics(), SPref.KEY_GRAPICS);
                SPref.getInstance().updateSharePreferences(SplashAnimatedActivity.this, SPref.KEY_WELCOME_VIDEO, datVo.getResult().getVideoUrl());
                SPref.getInstance().saveSocialLogin(SplashAnimatedActivity.this, datVo.getResult().getSocialLogin());
                SPref.getInstance().updateSharePreferences(SplashAnimatedActivity.this, datVo.getResult().getDemoUser());

                // SPref.getInstance().updateSharePreferences(SplashAnimatedActivity.this, Constant.KEY_COOKIE, "PHPSESSID=" + datVo.getSessionId() + ";");
                SPref.getInstance().updateSharePreferences(SplashAnimatedActivity.this, Constant.KEY_ENABLE_SKIP, datVo.getResult().isEnableSkipLogin());
                SPref.getInstance().updateSharePreferences(SplashAnimatedActivity.this, SPref.IMAGE_RATE_US, datVo.getResult().getRateusBackgroundImage());
                if (null != datVo.getResult().getThemeStyling()) {
                    SPref.getInstance().saveThemeColors(SplashAnimatedActivity.this, datVo.getResult().getThemeStyling());
                    CustomLog.e("colorsList", datVo.getResult().getThemeStyling().toString());
//                                        DbHelper.saveThemeColors(SplashAnimatedActivity.this, datVo.getResult().getThemeStyling());
                }
            }
        }).run();

        //  int versionApp = Integer.parseInt(BuildConfig.VERSION_NAME);
        //  int versionAppApi = Integer.parseInt(datVo.getResult().getVersionApp());


        // CustomLog.d("hasilnyaa22",String.valueOf(versionAppApi));
        //  CustomLog.d("hasilnyaa",String.valueOf(versionApp));

        if (datVo.getResult().getVersionCode() >= BuildConfig.VERSION_CODE) {
            showDialog(datVo);
        } else {
            callHandler(timer);
        }


    }

    private void showDialog(DefaultDataVo da) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }


            progressDialog = ProgressDialog.show(this, "", "", true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), this);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);

            tvMsg.setText(da.getResult().getVersionUpdate());
            tvMsg.setMovementMethod(LinkMovementMethod.getInstance());

            AppCompatButton bOk = progressDialog.findViewById(R.id.bCamera);
            bOk.setText(R.string.TXT_OK);
            AppCompatButton bCancel = progressDialog.findViewById(R.id.bGallary);
            bCancel.setText(R.string.TXT_CANCEL);
            bCancel.setVisibility(View.GONE);
            bOk.setText("Update Aplikasi");
            bOk.setOnClickListener(v -> {
                gotoPlayStore();
                progressDialog.dismiss();
            });


        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void callCheckLogin() {
        if (isNetworkAvailable(this)) {
            try {
                final HttpRequestVO request = new HttpRequestVO(Constant.URL_CHECK_LOGIN);
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(this));
                request.requestMethod = HttpPost.METHOD_NAME;
                try {
                    //handle if user coming after inputing cirrect maintenance code
                    if (null != getIntent().getExtras()) {
                        String cookie = getIntent().getExtras().getString(Constant.KEY_COOKIE, "");
                        if (!TextUtils.isEmpty(cookie)) {
                            request.headres.put(Constant.KEY_COOKIE, cookie);
                            request.params.remove(Constant.KEY_AUTH_TOKEN);
                        }
                    }
                } catch (Exception ignored) {

                }
                Handler.Callback callback = msg -> {
                    try {
                        String response = (String) msg.obj;
                        CustomLog.e("response", "" + response);

                        if (response != null) {
                            JSONObject json = new JSONObject(response);
                            isUserLoggedIn = !(json.get(Constant.KEY_RESULT) instanceof String);
                            SPref.getInstance().updateSharePreferences(SplashAnimatedActivity.this, Constant.KEY_LOGGED_IN, isUserLoggedIn);
                            if (isUserLoggedIn) {
                                SignInResponse resp = new Gson().fromJson(response, SignInResponse.class);
                                SPref.getInstance().saveUserInfo(SplashAnimatedActivity.this, Constant.KEY_USERINFO_JSON, resp);
                                Saveuserdata(resp);

//                                callMusicAlbumApi(1);
                            } else {
                                //it means user logout
                                Constant.SESSION_ID = "PHPSESSID=" + json.get("session_id") + ";";
                                SPref.getInstance().updateSharePreferences(SplashAnimatedActivity.this, Constant.KEY_COOKIE, Constant.SESSION_ID);
                            }

                            callDefaultDataApi();
                            // callHandler(800);
                        } else {
                            Util.showSnackbar(flMain, getString(R.string.MSG_NO_INTERNET));
                        }

                    } catch (Exception e) {
                        CustomLog.e(e);
                        Util.showSnackbar(flMain, getString(R.string.msg_something_wrong));
                    }
                    return true;
                };
                new HttpRequestHandler(this, new Handler(callback)).run(request);

            } catch (Exception e) {
                CustomLog.e(e);
            }
        } else {
            Util.showSnackbar(flMain, getString(R.string.MSG_NO_INTERNET));
        }
    }

    private void Saveuserdata(SignInResponse resp) {
        try {
            Constant.SESSION_ID = "PHPSESSID=" + resp.getSessionId() + ";";
            SPref.getInstance().updateSharePreferences(SplashAnimatedActivity.this, Constant.KEY_COOKIE, "PHPSESSID=" + resp.getSessionId() + ";");
            UserMaster vo = resp.getResult();
            SPref.getInstance().updateSharePreferences(SplashAnimatedActivity.this, Constant.KEY_LOGGED_IN_ID, vo.getLoggedinUserId());
            vo.setAuthToken(vo.getAuthToken());
            SPref.getInstance().saveUserMaster(SplashAnimatedActivity.this, vo, resp.getSessionId());
        } catch (Exception ex) {
            ex.printStackTrace();
            callDefaultDataApi();
        }
    }

    public String getCookie() {
        return TextUtils.isEmpty(Constant.SESSION_ID) ? SPref.getInstance().getString(this, Constant.KEY_COOKIE) : Constant.SESSION_ID;
    }

  /*  private void saveDefaultReaction() {
        try {
            String response = assetJSONFile("reactions.json", this);
            CommentLike resp = new Gson().fromJson(response, CommentLike.class);
            SPref.getInstance().saveReactionPluginType(this, resp.getResult().getStats().getReactionPlugin());
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }*/

    private String assetJSONFile(String filename, Context context) {
        try {
            AssetManager manager = context.getAssets();
            InputStream file = manager.open(filename);
            byte[] formArray = new byte[file.available()];
            file.read(formArray);
            file.close();

            return new String(formArray);
        } catch (Exception e) {
            CustomLog.e(e);
            return "{}";
        }
    }

    public void gotoPlayStore() {
        final String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://appgallery.huawei.com/#/app/C103366529")));

        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }
}


package com.sesolutions.ui.common;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.sesolutions.R;
import com.sesolutions.responses.Emotion;
import com.sesolutions.responses.Feeling;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.feed.Activity;
import com.sesolutions.ui.signup.UserMaster;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import java.util.Map;


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    public Activity activity;
    public BaseFragment currentFragment;
    public int isBackFrom = 0;
    public int taskPerformed;
    public static int backcoverchange=0;
    public static int taskPerformed2=0;
    public static int commentcount=0;
    public Map<String, Object> filteredMap;

    //variable used for move image url from one fragment to other
    public String stringValue;
    public int taskId;
    protected Toolbar toolbar;
    public ProgressDialog progressDialog;
    private Emotion emotion;
    public static Emotion emotion2;
    public static String gifimageurl="";
    private Feeling feelings;
    private Friends freinds;
    public InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfiguration.theme == 2)
            setLightStatusBar(Color.parseColor(Constant.white));
        else
            setStatusBarColor(Util.manipulateColor(Color.parseColor(Constant.colorPrimary)));
    }

    public void setTaskPerformed(int taskPerformed) {
        this.taskPerformed = taskPerformed;
        taskPerformed2 = taskPerformed;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    public static Emotion getEmotion2() {
        return emotion2;
    }

    public static void setEmotion2(Emotion emotion2) {
        BaseActivity.emotion2 = emotion2;
    }

    public static void setGifurl(String gifimageurl) {
        BaseActivity.gifimageurl = gifimageurl;
    }

    public String getStickerIcon() {
        String icon = null;
        if (null != feelings) {
            icon = feelings.getIcon();
            feelings = null;
        } else if (null != emotion) {
            icon = emotion.getIcon();
            emotion = null;
        }
        return icon;
    }

    public Feeling getFeelings() {
        return feelings;
    }

    public void setFeelings(Feeling feelings) {
        this.feelings = feelings;
    }

    public Friends getFreinds() {
        return freinds;
    }

    public void setFreinds(Friends freinds) {
        this.freinds = freinds;
    }


    public void setFullScreenWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams attrs = this.getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        this.getWindow().setAttributes(attrs);
    }


    public void showBaseLoader(boolean isCancelable) {
        try {
            progressDialog = ProgressDialog.show(this, "", "", true);
            progressDialog.setCancelable(isCancelable);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_progress);
            // new showBaseLoaderAsync(context).execute();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void hideBaseLoader() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showProgressBar(String message) {
        try {

            progressDialog = ProgressDialog.show(this, "", message, true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(message);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_progress);

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public UserMaster getUserMasterDetail() {
        String json = getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).getString(Constant.KEY_USER_MASTER, "{}");
        return new Gson().fromJson(json, UserMaster.class);
    }

    public boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }


    public String getCookie() {
        return TextUtils.isEmpty(Constant.SESSION_ID) ? getSharedPreferences(Constant.PREFRENCE_NAME, MODE_PRIVATE).getString(Constant.KEY_COOKIE, Constant.EMPTY) : Constant.SESSION_ID;
    }

    public void closeKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            View v = getCurrentFocus();
            if (v == null) {
                return;
            }
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (null != currentFragment) {
                currentFragment.onBackPressed();
            } else {
                supportFinishAfterTransition();
            }
        } catch (Exception e) {
            CustomLog.e(e);
            supportFinishAfterTransition();
        }
    }

    public void initToolBar(String title) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(title);
       /* getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getHomeIcon());
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setStatusBarColor(getResourceColor(R.color.colorPrimaryDark));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));*/
        //Util.setToolbarFont(this, toolbar);

    }

 /*   protected int getHomeIcon() {
        return 0;
    }*/

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(0);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    public void setLightStatusBar(int color) {
        if (Build.VERSION.SDK_INT >= 23) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    public void setStatusBarTranslucent() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            // window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onHomePressed();
                return true;
        }
        return false;
    }

    protected void onHomePressed() {
        finish();
    }


    protected void showVisibility(int... viewIds) {
        for (int i : viewIds) {
            findViewById(i).setVisibility(View.VISIBLE);
        }
    }

    protected void showVisibility(View convertView, int... viewIds) {
        for (int i : viewIds) {
            convertView.findViewById(i).setVisibility(View.VISIBLE);
        }
    }

    protected int getResourceColor(int colorId) {
        return ContextCompat.getColor(this, colorId);
    }


   /* protected void showInternetDialog(final int taskCode, final Object[] params) {
        if (dialogInternet == null) {
            dialogInternet = new Dialog(this);
            dialogInternet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogInternet.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogInternet.setContentView(R.layout.dialog_interner_error);
        }
        if (!dialogInternet.isShowing()) {
            dialogInternet.findViewById(R.id.btn_retry).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogInternet.dismiss();
                    executeTask(taskCode, params);
                }
            });
            dialogInternet.show();
        }
    }*/
}

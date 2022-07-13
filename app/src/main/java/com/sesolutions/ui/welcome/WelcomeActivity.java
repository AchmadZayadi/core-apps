package com.sesolutions.ui.welcome;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SlideShowImage;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.common.MainApplication;
import com.sesolutions.ui.signup.SignInFragment;
import com.sesolutions.ui.signup.SignInFragment2;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class
WelcomeActivity extends BaseActivity implements OnUserClickedListener<Integer, Object> {

    public boolean useAlternativeTheme = false;
    private VideoView videoView;
    private ImageView ivImage;
    private List<SlideShowImage> imageList;
    private int listSize;
    private int currentImageIndex = 0;
    private Handler handler;
    private ImageView ivHidden;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setFullScreenWindow();
        setStatusBarColor(Util.manipulateColor(Color.parseColor(AppConfiguration.hasWelcomeVideo ? "#000000" : Constant.colorPrimary)));//"#A6118010")));
        //setContentView(AppConfiguration.isWelcomeScreenEnabled ? R.layout.activity_welcome_image_slide : R.layout.activity_welcome_2);
        setContentView(R.layout.activity_welcome_image_slide);

        ivImage = findViewById(R.id.ivImage);
        ivHidden = findViewById(R.id.ivHidden);
        videoView = findViewById(R.id.videoView);
        setStatusBarColor(Color.parseColor("#157EC2"));

        if (/*AppConfiguration.isSlideImagesAvailable || */AppConfiguration.isWelcomeScreenEnabled) {

            //if is to show image then wait untill first image is downloaded then go to siginfragment
            imageList = SPref.getInstance().getSlideImages(this);

            //download first image and open sigin screen
            findViewById(R.id.pbLoad).setVisibility(View.VISIBLE);
            // isloaderVisible = true;
            String imageUrl;
            if (null != imageList && imageList.size() > 0) {
                listSize = imageList.size();
                imageUrl = imageList.get(0).getImage();
            } else {
                listSize = 1;
                imageUrl = SPref.getInstance().getString(this, SPref.IMAGE_LOGIN_BG);
                imageList = new ArrayList<>();
                imageList.add(new SlideShowImage(imageUrl));
            }
            findViewById(R.id.pbLoad).setVisibility(View.GONE);
            openWelcomeFragment2();
            Glide.with(this)
                    .setDefaultRequestOptions(new RequestOptions().dontAnimate().dontTransform().placeholder(R.drawable.gradient_transparent))
                    .load(imageUrl)
                    /* .listener(new RequestListener<Drawable>() {

                         @Override
                         public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                             // openWelcomeFragment2();
                             // findViewById(R.id.pbLoad).setVisibility(View.GONE);
                             return false;
                         }

                         @Override
                         public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                             // findViewById(R.id.pbLoad).setVisibility(View.GONE);
                             // openWelcomeFragment2();
                             // startHandler();
                             return false;
                         }
                     })*/.into(ivImage);
        } else {
            openSigninFragment();
        }

        if (AppConfiguration.hasWelcomeVideo) {
            askForPermission();
        }
    }

    public void askForPermission() {
        try {
            new TedPermission(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(getResources().getString(R.string.MSG_PERMISSION_DENIED))
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
                initVideo();
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

    public void openWelcomeFragment2() {
        int SCREEN_TYPE = getIntent().getIntExtra(Constant.KEY_TYPE, 0);

        //if wecome screen is disabled then go to signinfragment :: SCREEN_TYPE : 1
        if (!AppConfiguration.isWelcomeScreenEnabled) {
            SCREEN_TYPE = 1;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, WelcomeFragment2.newInstance(this, SCREEN_TYPE))
                .addToBackStack(null)
                .commit();
    }

    private void initHandler() {
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // CustomLog.e("runnable", "" + currentImageIndex);
            try {
                loadNextImage(currentImageIndex % listSize);
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }
    };

    public void updateText(int index) {
        try {
            if (currentFragment instanceof WelcomeFragment2) {
                ((WelcomeFragment2) currentFragment).updateText(imageList.get(index));
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void loadNextImage(int position) {
        updateText(position);
        currentImageIndex++;
        Glide.with(this)
                .load(imageList.get(position % listSize).getImage())
                .listener(new RequestListener<Drawable>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        ivImage.setImageDrawable(resource);
                        handler.postDelayed(runnable, AppConfiguration.SLIDE_TIME);
                        return false;
                    }
                })
                .into(ivHidden);

    }

    public void openSigninFragment() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
        ft.replace(R.id.container, new SignInFragment2(), "detailFragment");
        ft.addToBackStack(null);
        ft.commit();


    }

    @Override
    public void onStart() {
        super.onStart();

        if (AppConfiguration.isSlideImagesAvailable) {
            initHandler();
        }

        if (AppConfiguration.hasWelcomeVideo) {
            if (videoView != null) {
                videoView.start();
            }
        }
    }

    private void initVideo() {
        try {
            // videoView = (VideoView) findViewById(R.id.videoView);
            String url = SPref.getInstance().getString(this, SPref.KEY_WELCOME_VIDEO);
            if (null != url) {
                String proxyUrl = ((MainApplication) getApplicationContext()).getProxy(this).getProxyUrl(url);
                videoView.setVideoPath(proxyUrl);
                //  videoView.setVideoURI(Uri.parse(list.get(0).getVideoUrl()));//Uri.parse("http://clips.vorwaerts-gmbh.de/VfE_html5.mp4"));
                videoView.requestFocus();

                videoView.setOnPreparedListener(mp -> {
                    mp.setVolume(0f, 0f);
                    mp.setLooping(true);
                });
                videoView.start();
            } else {
                AppConfiguration.hasWelcomeVideo = false;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    //private HttpProxyCacheServer proxy;

    /*public static HttpProxyCacheServer getProxy(Context context) {
        MainApplication app = (MainApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }*/


    @Override
    public void onStop() {

        if (AppConfiguration.isSlideImagesAvailable) {
            if (handler != null) {
                handler.removeCallbacks(runnable);
            }
        }
        if (AppConfiguration.hasWelcomeVideo) {
            if (videoView != null) {
                videoView.stopPlayback();
            }
        }

        super.onStop();
    }

    /*@Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        if (useAlternativeTheme) {
            theme.applyStyle(R.style.SESAppThemeActionBar, true);
        }
        // you could also use a switch if you have many themes that could apply
        return theme;
    }*/

   /* public void openScreen() {
        int type = 0;
        if (getIntent().hasExtra(Constant.KEY_TYPE)) {
            type = getIntent().getIntExtra(Constant.KEY_TYPE, 0);
        }
        switch (type) {
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, WelcomeFragment2.newInstance(false))
                        .addToBackStack(null)
                        .commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignInFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignUpFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case 3:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, WelcomeFragment2.newInstance(true))
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }
*/


    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        //show-hide video / slide show as per setting
        if (eventType == Constant.Events.SET_LOADED) {
            //1 means
            if (position == 1) {
                showSlideImagesLayout();
            } else {
                showVideoLayout();
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.currentFragment.onActivityResult(requestCode, resultCode, data);
        Log.e("22","33");
    }


    private void showSlideImagesLayout() {
        ivImage.setVisibility(View.VISIBLE);
        ivHidden.setVisibility(View.VISIBLE);
        if (null != videoView)
            videoView.setVisibility(View.GONE);
    }

    private void showVideoLayout() {
        ivImage.setVisibility(View.GONE);
        ivHidden.setVisibility(View.GONE);
        if (null != videoView)
            videoView.setVisibility(View.VISIBLE);
    }
}

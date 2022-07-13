package com.sesolutions.ui.clickclick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sesolutions.R;
import com.sesolutions.http.HttpImageNotificationRequest;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.http.ParserCallbackInterface;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.Video;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.clickclick.discover.DiscoverActivityFragment;
import com.sesolutions.ui.clickclick.me.MeFragment;
import com.sesolutions.ui.clickclick.notification.VideoNotificationFragment;
import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.page.CreateEditPageFragment;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.jzvd.Jzvd;

import static com.sesolutions.ui.dashboard.ApiHelper.REQ_CODE_VIDEO;


public class ActivityClickClick extends BaseActivity implements View.OnClickListener, OnUserClickedListener<Integer, Object>,
        BottomNavigationView.OnNavigationItemSelectedListener, ParserCallbackInterface {
    public Video videoDetail;
    public BaseActivity activity;
    private String imageFilePath;
    private static final int CAMERA_VIDEO_REQUEST = 7080;
    private RecyclerView rvTiktok;
    private AdapterClickClickRecyclerView mAdapter;
    private LinearLayout linearLayout;
    private ViewPagerLayoutManager mViewPagerLayoutManager;
    private int mCurrentPosition = -1;
    private boolean foryou = false;
    private TextView tvforyou;
    private boolean isMe;
    private TextView tvFollowing;
    private FragmentManager fragmentManager;
    private ProgressDialog progressDialog;
    private AppCompatImageView mButton;
    private boolean onFollowing;
    private final ThemeManager themeManager;

    public ActivityClickClick() {
        themeManager = new ThemeManager();;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setting full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_tiktok);
        fragmentManager = getSupportFragmentManager();

        init();

       // throw new RuntimeException("This is a crash");

          //   menuTitleActiveColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
     //   navigation.setBackgroundColor(Color.parseColor(Constant.foregroundColor));
    //    navigation.setSelectedTabIndicatorColor(Color.parseColor(Constant.menuButtonActiveTitleColor));
        //hiding tabindicator
      //

    }
    private int menuTitleActiveColor;


    private void init() {
        isMe = false;
        linearLayout = (LinearLayout) findViewById(R.id.lltop);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        menuTitleActiveColor = Color.parseColor(Constant.menuButtonActiveTitleColor);
        // FOR NAVIGATION VIEW ITEM TEXT COLOR
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},  // unchecked
                new int[]{android.R.attr.state_checked},   // checked
                new int[]{}                                // default
        };

// Fill in color corresponding to state defined in state
        int[] colors = new int[]{
                Color.parseColor("#ffffff"),
                menuTitleActiveColor,
                menuTitleActiveColor,
        };

        // apply to text color
        ColorStateList navigationViewColorStateList = new ColorStateList(states, colors);
        navigation.setItemTextColor(navigationViewColorStateList);
        navigation.setItemIconTintList(navigationViewColorStateList);


        mButton = findViewById(R.id.ivBack);
        tvforyou = findViewById(R.id.tvforyou);
        tvFollowing = findViewById(R.id.tvFollowing);
        mButton.setOnClickListener(this);
        tvforyou.setOnClickListener(this);
        tvFollowing.setOnClickListener(this);
        startTiktTok();
    }

    private void startTiktTok() {
        try {
            if (!onFollowing) {
                foryou = false;
                showTop(true);
                fragmentManager.beginTransaction().replace(R.id.container, new ClickClickFragment()).commit();
            } else {
                fragmentManager.beginTransaction().replace(R.id.container, new FollowingFragment()).commit();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void showTop(Boolean show) {
        if (show) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.tvforyou:
                if (!foryou) {
                    onFollowing = false;
                    tvforyou.setAlpha(1f);
                    tvFollowing.setAlpha(0.65f);
                    showTop(true);
                    startTiktTok();
                }
                break;
            case R.id.tvFollowing:
                onFollowing = true;
                foryou = false;
                showTop(true);
                tvforyou.setAlpha(0.65f);
                tvFollowing.setAlpha(1f);
                fragmentManager.beginTransaction().replace(R.id.container, new FollowingFragment()).commit();
                break;
        }
    }





    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        BaseFragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                showTop(true);
                if (onFollowing) {
                    fragment = new FollowingFragment();
                } else {
                    foryou = false;
                    fragment = new ClickClickFragment();
                }
                break;

            case R.id.navigation_dashboard:
                foryou = false;
                showTop(false);
                fragment = new DiscoverActivityFragment();
                break;


            case R.id.navigation_notifications:
                foryou = false;
                showTop(false);
                fragment = new VideoNotificationFragment();
                break;

            case R.id.navigation_profile:
                foryou = false;
                showTop(false);
                isMe = true;
                fragment = new MeFragment();
                break;
            case R.id.navigation_create:
                //  navigation.setSelectedItemId(R.id.navigation_create);

                foryou = false;
                showTop(false);
                takeVideoFromCamera();
                break;
        }


        if(item.getItemId()!=R.id.navigation_create){
            return loadFragment(fragment);
        }else {
            if (onFollowing) {
                fragment = new FollowingFragment();
            } else {
                foryou = false;
                fragment = new ClickClickFragment();
            }

            return loadFragment(fragment);
        }


    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            Log.e("","");
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CustomLog.e("onActivityResult", "requestCode : " + requestCode + " resultCode : " + resultCode);
        try {
            switch (requestCode) {
                case CAMERA_VIDEO_REQUEST:
                    if(resultCode == Activity.RESULT_OK) {
                        String result = data.getStringExtra("result");
                        if (result.equalsIgnoreCase("OK")) {
                            String selectedImagePath = null;
                            //ImagePath will use to upload video
                            if (Constant.path != null) {
                                selectedImagePath = Constant.path;
                                CustomLog.e("VIDEO_REQUEST", "" + selectedImagePath);
                            }

                            if (selectedImagePath != null) {
                                List<String> photoPaths = new ArrayList<>();
                                photoPaths.add(selectedImagePath);
//                        Constant.videoUri = selectedImageUri;
                                imageFilePath = selectedImagePath;
                                onResponseSuccess(REQ_CODE_VIDEO, photoPaths);
                                //  videoView.setVideoPath(selectedImagePath);
                            }
                        } else {
                            Intent intent = new Intent(ActivityClickClick.this, ActivityClickClick.class);
                            startActivity(intent);
                            finish();
                        }
                    }

//                    Util.showToast(getApplicationContext(), "Video Recorded successfully.");
                    break;
              /*  default:

                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
                    fragment.onActivityResult(requestCode, resultCode, data);
                    break;*/
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (!isMe) {
            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
                return true;
            }
            return false;
        } else {
            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment)
                        //.addToBackStack(null)
                        .commit();
                return true;
            }
            return false;
        }

    }


    private void takeVideoFromCamera() {
        // fimg = new File(image_path_source_temp + imageName);
        // Uri uri = Uri.fromFile(fimg);
        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SeSolutions/";
        String imageName = Constant.IMAGE_NAME + Util.getCurrentdate(Constant.TIMESTAMP);

       /* File dir = new File(imagePath);
        try {
            if (dir.mkdir()) {
            } else {
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }*/
        Constant.videoUri = null;
        Intent cameraIntent = new Intent(getApplicationContext(), CreateClickClick.class);
        cameraIntent.putExtra("path", imagePath);
        cameraIntent.putExtra("name", imageName);
        cameraIntent.putExtra("record_video", true);
        startActivityForResult(cameraIntent, CAMERA_VIDEO_REQUEST);
    }

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        return false;
    }

    private void autoPlayVideo(int postion) {
        if (rvTiktok == null || rvTiktok.getChildAt(0) == null) {
            return;
        }
        JzvdStdClickClick player = rvTiktok.getChildAt(0).findViewById(R.id.videoplayer);
        if (player != null) {
            player.startVideoAfterPreloading();
        }
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void submit() {
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.FILE_TYPE + "videoupload", videoDetail.getSrc());
        callPostSubmitApi(params);
    }

    private void callPostSubmitApi(Map<String, Object> params) {
        if (isNetworkAvailable(getApplicationContext())) {
            try {
                //boolean showProgressLoader = isImageSelected ||
                //   isVideoSelected ||
                //  isBuySellSelected ||
                //  isMusicSelected;
                //if (!showProgressLoader) {
                //  showBaseLoader(false);
                //}
                //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                //     dialog.setCancelable(true);
                HttpRequestVO request = new HttpRequestVO(Constant.URL_CREATE_TIKTOK);
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.putAll(params);
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(getApplicationContext()));
                request.requestMethod = HttpPost.METHOD_NAME;
                    /*Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);

                            if (null != response) {
                                SesResponse resp = new Gson().fromJson(response, SesResponse.class);
                                if (TextUtils.isEmpty(resp.getError())) {

                                    if (videoDetail != null && videoDetail.isFromDevice()) {
                                        Constant.TASK_POST = false;
                                        Util.showToast(context, resp.getStringResult());
                                    } else {
                                        Constant.TASK_POST = true;
                                    }
                                    onBackPressed();
                                } else {
                                    Util.showSnackbar(v, resp.getErrorMessage());
                                }

                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return true;
                    };*/
                new HttpImageNotificationRequest(activity, null /*new Handler(callback)*/, true).run(request);
                if (videoDetail != null && videoDetail.isFromDevice()) {
                    Constant.TASK_POST = false;
                    Util.showToast(getApplicationContext(), getString(R.string.TXT_UPLOADING));
                } else {
                    Constant.TASK_POST = true;
                }

                super.onBackPressed();
            } catch (Exception e) {
                hideBaseLoader();
            }
        } else {
            Util.showToast(getApplicationContext(), "NO INTERNET");
        }

    }

    @Override
    public void onResponseSuccess(int reqCode, Object result) {
        try {
            switch (reqCode) {
                case REQ_CODE_VIDEO:
                    if (result != null) {
                        String filePath = ((List<String>) result).get(0);
                        videoDetail = new Video();
                        videoDetail.setFromDevice(true);
                        videoDetail.setSrc(filePath);
                        CustomLog.e("video src", "" + videoDetail.getSrc());
                        Map<String, Object> map = new HashMap<>();
                        map.put(Constant.KEY_SONG_ID, Constant.musicid);
                        map.put(Constant.KEY_VIDEO, videoDetail.getSrc());
                        map.put("moduleName", "sesvideo");
                        map.put("not_merge_song", "1");
                        Constant.path = null;
                        Constant.musicid = 0;
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, CreateEditPageFragment.newInstance(Constant.FormType.CREATE_TICK, map, Constant.URL_TICK_CREATE, null, false)).addToBackStack(null).commitAllowingStateLoss();
//                        submit();
                        break;
                    }
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onConnectionTimeout(int reqCode, String result) {
    }

}

package com.sesolutions.imageeditengine;

import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droidninja.imageeditengine.BaseFrag;
import com.droidninja.imageeditengine.Constants;
import com.droidninja.imageeditengine.utils.FragmentUtil;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.http.HttpImageRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.comment.CommentAttachImageAdapter;
import com.sesolutions.ui.customviews.AnimationAdapter;
import com.sesolutions.ui.customviews.CustomSwipableViewPager;
import com.sesolutions.ui.customviews.fab.FloatingActionButton;
import com.sesolutions.ui.dashboard.StaticShare;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;


public class MultipleImageEditActivity extends BaseImageEditActivity
        implements PhotoEditorFragment.OnFragmentInteractionListener, OnUserClickedListener<Integer, Object>,
        CropFragment.OnFragmentInteractionListener, View.OnClickListener {
    private final int REQ_CREATE = 705;
    private Rect cropRect;
    private List<String> imagePaths;
    private FloatingActionButton fabSubmit;
    private View rlCaption;
    private TextView tvCaption;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_image_edit);
        rlCaption = findViewById(R.id.rlCaption);
        fabSubmit = findViewById(R.id.fabSubmit);
        tvCaption = findViewById(R.id.tvCaption);
        fabSubmit.setOnClickListener(this);
        tvCaption.setOnClickListener(this);
        /*imagePaths = getIntent().getStringArrayListExtra(ImageEditor.EXTRA_IMAGE_PATH_LIST);
        if (imagePaths != null) {
            setImageRecyclerView();
            setUpViewpager();
        }*/

        askForPermission(new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                FilePickerBuilder.getInstance()
                        .setMaxCount(10)
                        .enableImagePicker(true)
                        .enableVideoPicker(true)
                        // .setSelectedFiles(filePaths)
                        .setActivityTheme(R.style.FilePickerTheme)
                        .pickPhoto(MultipleImageEditActivity.this);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                onBackPressed();
            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void askForPermission(PermissionListener permissionlistener, String... permission) {
        try {
            new TedPermission(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(getString(R.string.MSG_PERMISSION_DENIED))
                    .setPermissions(permission)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private CustomSwipableViewPager viewPager;
    private FilePagerAdapter filePagerAdapter;

    private void setUpViewpager() {
        viewPager = findViewById(R.id.viewPager);
        viewPager.setPagingEnabled(false);

        //create a list with empty strings
        commentArray = new ArrayList<>(Collections.nCopies(imagePaths.size(), ""));

        filePagerAdapter = new FilePagerAdapter(getSupportFragmentManager());
        for (String path : imagePaths) {
            if (path.endsWith(".mp4")) {
                filePagerAdapter.addFragment(VideoEditorFragment.newInstance(path));
            } else {
                filePagerAdapter.addFragment(PhotoEditorFragment.newInstance(path));
            }
        }
        viewPager.setOffscreenPageLimit(imagePaths.size());
        viewPager.setAdapter(filePagerAdapter);

    }

    @Override
    public void onCropClicked(Bitmap bitmap) {
        FragmentUtil.addFragment(this, R.id.container,
                CropFragment.newInstance(bitmap, cropRect));
    }

    @Override
    public void onDoneClicked(String imagePath) {

        Intent intent = new Intent();
        intent.putExtra(ImageEditor.EXTRA_EDITED_PATH, imagePath);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private CommentAttachImageAdapter<String> adapterImage;

    private void setImageRecyclerView() {
        try {
            RecyclerView rvImageAttach = findViewById(R.id.rvImageAttach);
            rvImageAttach.setHasFixedSize(true);
            // LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            //rvImageAttach.setLayoutManager(layoutManager);
            adapterImage = new CommentAttachImageAdapter<String>(imagePaths, this, this);
            rvImageAttach.setAdapter(adapterImage);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onImageCropped(Bitmap bitmap, Rect cropRect) {
        this.cropRect = cropRect;
        PhotoEditorFragment photoEditorFragment =
                (PhotoEditorFragment) FragmentUtil.getFragmentByTag(this,
                        PhotoEditorFragment.class.getSimpleName());
        if (photoEditorFragment != null) {
            photoEditorFragment.setImageWithRect(cropRect);
            photoEditorFragment.reset();
            FragmentUtil.removeFragment(this,
                    (BaseFrag) FragmentUtil.getFragmentByTag(this, CropFragment.class.getSimpleName()));
        }
    }

    @Override
    public void onCancelCrop() {
        FragmentUtil.removeFragment(this,
                (BaseFrag) FragmentUtil.getFragmentByTag(this, CropFragment.class.getSimpleName()));
    }

    @Override
    public void onBackPressed() {
        try {
            if (null != currentFragment) {
                currentFragment.onBackPressed();
            } else if (null != imagePaths){
                showDialog(getString(R.string.msg_quote_go_back), R.string.YES, R.string.NO);
            } else{
                supportFinishAfterTransition();
            }
        } catch (Exception e) {
            CustomLog.e(e);
            supportFinishAfterTransition();
        }
    }

    public void showDialog(String msg, int positiveText, int negetiveText) {
        try {

            progressDialog = ProgressDialog.show(this, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme((ViewGroup) progressDialog.findViewById(R.id.rlDialogMain), this);
            TextView tvMsg = (TextView) progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);

            AppCompatButton bCamera = progressDialog.findViewById(R.id.bCamera);
            bCamera.setText(positiveText);
            AppCompatButton bGallary = progressDialog.findViewById(R.id.bGallary);
            bGallary.setText(negetiveText);

            bCamera.setOnClickListener(v -> {
                progressDialog.dismiss();
                supportFinishAfterTransition();
            });

            bGallary.setOnClickListener(v -> {
                progressDialog.dismiss();
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        try {
            switch (eventType) {
                case REQ_CREATE:
                    hideBaseLoader();
                    break;
                case Constant.Events.FEED_ATTACH_IMAGE_CANCEL:
                    if (imagePaths.size() > 1) {
                        imagePaths.remove(position);
                        adapterImage.notifyItemRemoved(position);
                        //adapterImage.notifyItemRangeChanged(position,);
                        filePagerAdapter.deleteItem(position);
                    } else {
                        onBackPressed();
                    }
                    break;
                //called when user switches viewpager content
                case Constant.Events.IMAGE_5:
                    viewPager.setCurrentItem(position, true);
                    updateCaptionTextView(position);
                    break;
                case Constants.Events.DONE:
                    finalImageArray.add("position" + position);
                    finalImageMap.put(Constant.FILE_TYPE + "attachmentImage[" + position + "]", data);
                    finalImageMap.put("comment[" + position + "]", commentArray.get(position));
                    if (finalImageArray.size() == imagePaths.size()) {
                        //if sizes are same it means all images fetched , so upload this to server
                        callUploadApi();
                    }
                    break;
                case Constants.Events.TASK:
                    switch (position) {
                        case Constants.TASK_CAPTION:
                            commentArray.set(viewPager.getCurrentItem(), (String) data);
                            updateCaptionTextView(viewPager.getCurrentItem());
                            break;
                        case Constants.TASK_CROP:
                            onCropClicked((Bitmap) data);
                            break;
                        case Constants.TASK_HIDE_CAPTION:
                            if (rlCaption.getVisibility() != View.VISIBLE) return false;
                            fabSubmit.setVisibility(View.GONE);
                            rlCaption.animate().setDuration(300).translationY(rlCaption.getHeight()).setListener(new AnimationAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    rlCaption.setVisibility(View.GONE);
                                }
                            });
                            break;
                        case Constants.TASK_SHOW_CAPTION:
                            if (Integer.parseInt("" + data) != 0) return false;
                            rlCaption.animate().setDuration(300).translationY(0).setListener(new AnimationAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    rlCaption.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    rlCaption.setVisibility(View.VISIBLE);
                                    fabSubmit.setVisibility(View.VISIBLE);
                                }
                            });
                            break;
                        default:
                            PhotoEditorFragment photoEditorFragment =
                                    (PhotoEditorFragment) FragmentUtil.getFragmentByTag(this,
                                            PhotoEditorFragment.class.getSimpleName());
                            if (photoEditorFragment != null) {
                                if (Constants.TASK_WALLPAPER == position) {
                                    photoEditorFragment.changeWallpaper("" + data);
                                } else if (Constants.TASK_STICKER == position) {
                                    photoEditorFragment.setStickerBitMap("" + data);
                                } else if (Constants.TASK_FONT == position) {
                                    photoEditorFragment.fetchSelectedFont("" + data);
                                }
                            }
                            break;
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return false;
    }

    private void updateCaptionTextView(int index) {
        tvCaption.setText(commentArray.get(index));
    }

    private void callUploadApi() {

        if (isNetworkAvailable(this)) {
            try {

                HttpRequestVO request = new HttpRequestVO(Constant.URL_STORY_CREATE);
                request.params.put(Constant.KEY_USER_ID, SPref.getInstance().getLoggedInUserId(this));
                request.params.putAll(finalImageMap);
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(this));

                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("response_story_create", "" + response);
                            if (response != null) {
                                ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                                if (TextUtils.isEmpty(err.getError())) {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    StaticShare.TASK_PERFORMED = Constant.Events.STORY_CREATE;
                                    supportFinishAfterTransition();
                                } else {
                                    Util.showSnackbar(fabSubmit, err.getErrorMessage());
                                }
                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        return false;
                    }
                };
                new HttpImageRequestHandler(this, new Handler(callback), true).run(request);

            } catch (Exception e) {
                CustomLog.e(e);

            }

        } else {
            Util.showSnackbar(fabSubmit, getString(R.string.no_network_message));
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CustomLog.e("onActivityResult", "requestCode : " + requestCode + " resultCode : " + resultCode);
        try {

            switch (requestCode) {
                case FilePickerConst.REQUEST_CODE_PHOTO:
                    if (resultCode == -1 && data != null) {
                        imagePaths = new ArrayList<>(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS));
                        setImageRecyclerView();
                        setUpViewpager();
                    } else {
                        onBackPressed();
                    }
                    break;
                /*case CAMERA_PIC_REQUEST:
                    if (resultCode == -1) {
                        setImage(Constant.path);
                        CustomLog.d("CAMERA_PIC_REQUEST", Constant.path);
                    }
                    break;*/
            }

             /*  if (requestCode == CAMERA_PIC_REQUEST && resultCode == -1) {
                CustomLog.e("on", "requestCode : " + requestCode + " resultCode : " + resultCode);
             if (requestCode == Constant.SELECT_PICTURE) {
                    CustomLog.e("inner", "requestCode : " + requestCode + " resultCode : " + resultCode);
                    // pic image from gallery
                    Uri selectedImageUri = intentdata.getData();
                    Util.FCopy(image_path_source_temp + imageName, getPath(selectedImageUri));
                }
                // CheckOrient();
                // takeImage.setImageBitmap(Image_BMP);
                //   takeImage.setScaleType(ImageView.ScaleType.FIT_XY);/*CENTER_CROP
        }*/

        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabSubmit:
                fetchAllEditedImages();
                break;
            case R.id.tvCaption:
                CaptionDialogFragment.newInstance(this, commentArray.get(viewPager.getCurrentItem())).show(getSupportFragmentManager(), "caption");
                break;
        }
    }

    Map<String, Object> finalImageMap;
    List<String> finalImageArray;
    List<String> commentArray;

    private void fetchAllEditedImages() {
        finalImageArray = new ArrayList<>();
        boolean hasImages = false;
        finalImageMap = new HashMap<>();
        for (int i = 0; i < imagePaths.size(); i++) {
            if (imagePaths.get(i).endsWith(".mp4")) {
                finalImageMap.put("comment[" + i + "]", commentArray.get(i));
                finalImageMap.put(Constant.FILE_TYPE + "attachmentVideo[" + i + "]", imagePaths.get(i));

                finalImageArray.add("position" + i);
            } else {
                hasImages = true;
                //this method returns processed image on "onItemClicked:case DONE"
                ((PhotoEditorFragment) filePagerAdapter.getItem(i)).processFinalImage(i);
            }
        }

        //if story doesn't have images then dont wait for image processing , simply update all videos
        if (!hasImages) {
            callUploadApi();
        }

    }
}

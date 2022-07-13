package com.sesolutions.ui.music_album;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.sesolutions.R;
import com.sesolutions.camerahelper.CameraActivity;
import com.sesolutions.http.HttpImageRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.http.MyMultiPartEntity;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.customviews.CircularProgressBar;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.soundcloud.android.crop.Crop;
import com.yalantis.ucrop.UCrop;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class AlbumImageFragment2 extends BaseFragment implements View.OnClickListener, MyMultiPartEntity.ProgressListener {

    private static final int CAMERA_PIC_REQUEST = 7079;
    private View v;

    TextView bChoose25;
    AppCompatButton bSave;
    CircleImageView ivProfileImage;
    ImageView dummyimage;
    RelativeLayout realtivedammy;
    private String imagePath;
    private boolean isCameraOptionSelected = false;
    private String url;
    private String imageUrl;
    private Map<String, Object> map;
    private String title;
    private String imageKey;
    private int TASK_TYPE;
    Boolean flag_data=false;
    ImageView ivCamera2;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_profile_image, container, false);
        try {
            applyTheme(v);
            init();
            imageKey = Constant.KEY_IMAGE;
            if (map != null && map.get(Constant.KEY_IMAGE) != null) {
                imageKey = (String) map.get(Constant.KEY_IMAGE);
                TASK_TYPE = (int) map.get(Constant.KEY_TYPE);
                map.remove(Constant.KEY_IMAGE);
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void onDestroyView() {
        try {
            if (getView() != null) {
                ViewGroup parent = (ViewGroup) getView().getParent();
                parent.removeAllViews();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        super.onDestroyView();
    }

    private CircularProgressBar circularProgressBar;
    private TextView tvProgress;

    private void init() {
        ivProfileImage = v.findViewById(R.id.ivProfileImage);
        ivCamera2 = v.findViewById(R.id.ivCamera2);
        dummyimage = v.findViewById(R.id.dummyimage);
        realtivedammy = v.findViewById(R.id.realtivedammy);
        ivProfileImage.setBorderColor(Color.parseColor(Constant.colorPrimary));

        circularProgressBar = v.findViewById(R.id.cpb);
        tvProgress = v.findViewById(R.id.tvProgress);
        circularProgressBar.setColor(Color.parseColor(Constant.colorPrimary));
        // circularProgressBar.setBackgroundColor(Color.parseColor(Constant.menuButtonActiveTitleColor.replace("#", "#67")));
        circularProgressBar.setProgressWithAnimation(0, 0); // Default duration = 1500ms
        changeLayoutParams(true);

        bSave = v.findViewById(R.id.bSave);
        bChoose25 = v.findViewById(R.id.bChoose25);
        bChoose25.setText("Upload Photo");
        bSave.setVisibility(View.GONE);

        bChoose25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForPermission(permissionlistener, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            }
        });
        bChoose25.setVisibility(View.GONE);
        ivCamera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForPermission(permissionlistener, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            }
        });

        realtivedammy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askForPermission(permissionlistener, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            }
        });

        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
        bSave.setOnClickListener(this);
        v.findViewById(R.id.ivBack).setOnClickListener(this);

        Log.e("22222","122");

        Log.e("imageurl",""+imageUrl.length());
        Log.e("imageurl",""+imageUrl);


        if(imageUrl!=null && imageUrl.length()>0){
            Util.showImageWithGlide(ivProfileImage, imageUrl, context, R.drawable.placeholder_square);
            bSave.setText(R.string.TXT_SAVE_PHOTO);
            dummyimage.setVisibility(View.GONE);
            realtivedammy.setVisibility(View.GONE);
            ivProfileImage.setVisibility(View.VISIBLE);
            bChoose25.setText("Change Photo");
            bSave.setVisibility(View.VISIBLE);
        }else {
            Util.showImageWithGlide(ivProfileImage, "https://www.cornwallbusinessawards.co.uk/wp-content/uploads/2017/11/dummy450x450.jpg", context, R.drawable.placeholder_square);

            dummyimage.setVisibility(View.VISIBLE);
            realtivedammy.setVisibility(View.VISIBLE);
            ivProfileImage.setVisibility(View.GONE);
            bSave.setVisibility(View.GONE);
        }

    }

    private void changeLayoutParams(boolean isSmall) {
        if (isSmall) {
            v.findViewById(R.id.vScrim).setVisibility(View.GONE);
            //    new Handler().postDelayed(() -> {
            circularProgressBar.setVisibility(View.GONE);
            tvProgress.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParams = ivProfileImage.getLayoutParams();
            float size = context.getResources().getDimension(R.dimen.size_image_upload_small);
            layoutParams.width = (int) size;
            layoutParams.height = (int) size;
            ivProfileImage.setLayoutParams(layoutParams);
            //     }, 1000);
        } else {
            v.findViewById(R.id.vScrim).setVisibility(View.VISIBLE);
            //  new Handler().postDelayed(() -> {
            circularProgressBar.setVisibility(View.VISIBLE);
            tvProgress.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = ivProfileImage.getLayoutParams();
            float size = context.getResources().getDimension(R.dimen.size_image_upload_normal);
            layoutParams.width = (int) size;
            layoutParams.height = (int) size;
            ivProfileImage.setLayoutParams(layoutParams);
            //  }, 1000);
        }
    }

    private void showImageChooser() {
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setActivityTheme(R.style.FilePickerTheme)
                .showFolderView(true)
                .enableImagePicker(true)
                .enableVideoPicker(false)
                .pickPhoto(this);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.bChoose25:
//                    showDialog(Constant.MSG_SELECT_IMAGE_SOURCE);
                    break;

                case R.id.bSave:
                    // showImageChooser();
                    if (!TextUtils.isEmpty(imagePath)) {
                        ivCamera2.setVisibility(View.GONE);
                        callUploadImageApi(imagePath);
                    } else {
                        Util.showSnackbar(v, getStrings(R.string.MSG_SELECT_IMAGE));
                    }
                    break;
                case R.id.ivBack:
                    onBackPressed();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    /**
     * camera activity call back
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CustomLog.e("onActivityResult", "requestCode : " + requestCode + " resultCode : " + resultCode);
        try {

            switch (requestCode) {
                case FilePickerConst.REQUEST_CODE_PHOTO:
                    if (resultCode == -1 && data != null) {
                        List<String> photoPaths = new ArrayList<>(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS));
                        setImage(photoPaths.get(0));
                        CustomLog.d("REQUEST_CODE_PHOTO", photoPaths.get(0));
                    }
                    break;
                case CAMERA_PIC_REQUEST:
                    if (resultCode == -1) {
                        setImage(Constant.path);
                        CustomLog.d("CAMERA_PIC_REQUEST", Constant.path);
                    }
                    break;
                case Crop.REQUEST_CROP:

                    //   Uri resultUri = UCrop.getOutput(data);
                    Uri resultUri22 = Crop.getOutput(data);
                    ivProfileImage.setImageURI(resultUri22);
                    ivProfileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);/*CENTER_CROP*/
                    bSave.setText(R.string.TXT_SAVE_PHOTO);
                    dummyimage.setVisibility(View.GONE);
                    realtivedammy.setVisibility(View.GONE);
                    ivProfileImage.setVisibility(View.VISIBLE);
                    bChoose25.setText("Change Photo");
                    bSave.setVisibility(View.VISIBLE);

                    break;

                case UCrop.REQUEST_CROP:
                    Uri resultUri = UCrop.getOutput(data);
                    Log.e("imagePath",""+imagePath);
                    ivProfileImage.setImageDrawable(Drawable.createFromPath(imagePath));
                    ivProfileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);/*CENTER_CROP*/
                    //  ivProfileImage.setImageDrawable(Drawable.createFromPath(imagePath));
                    bSave.setText(R.string.TXT_SAVE_PHOTO);
                    dummyimage.setVisibility(View.GONE);
                    realtivedammy.setVisibility(View.GONE);
                    ivProfileImage.setVisibility(View.VISIBLE);
                    bChoose25.setText("Change Photo");
                    bSave.setVisibility(View.VISIBLE);

                    break;

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


    public void setImage(String path) {
        /*   Util.FCopy(image_path_source_temp + imageName, path);*/
        imagePath = path;
        File file=new File(path);
      /*  UCrop uCrop = UCrop.of(Uri.fromFile(file), Uri.fromFile(file));
        uCrop = uCrop.useSourceImageAspectRatio();
        uCrop.start(getActivity(), AlbumImageFragment2.this);*/
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(Uri.fromFile(file), destination).asSquare().start(getActivity(), AlbumImageFragment2.this);
     }
    private void takeImageFromCamera() {
        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getStrings(R.string.app_name).replace(" ", "") + "/";
        String imageName = Constant.IMAGE_NAME + Util.getCurrentdate(Constant.TIMESTAMP);

        File dir = new File(imagePath);
        try {
            if (dir.mkdir()) {
            } else {
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        Intent cameraIntent = new Intent(context, CameraActivity.class);
        cameraIntent.putExtra("path", imagePath);
        cameraIntent.putExtra("name", imageName);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }

    private void callUploadImageApi(String filePath) {
        if (isNetworkAvailable(context)) {
            changeLayoutParams(false);
            bSave.setText(R.string.TXT_UPLOADING);
            bSave.setEnabled(false);
            bChoose25.setEnabled(false);
            try {

                HttpRequestVO request = new HttpRequestVO(url);
                request.params.put(Constant.FILE_TYPE + imageKey, filePath);
                request.params.putAll(map);
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, Constant.EMPTY);
                request.requestMethod = HttpPost.METHOD_NAME;
                Handler.Callback callback = msg -> {
                    bSave.setEnabled(true);
                    bChoose25.setEnabled(true);
                    try {
                        String response = (String) msg.obj;
                        CustomLog.e("repsonse", "" + response);
                        if (response != null) {

                            ErrorResponse err = new Gson().fromJson(response, ErrorResponse.class);
                            if (TextUtils.isEmpty(err.getError())) {
                                JSONObject json = new JSONObject(response);

                                if (json.get("result") instanceof JSONObject) {
                                    if (json.getJSONObject("result").has("images")) {
                                        activity.stringValue = json.getJSONObject("result").getJSONObject("images").getString("main");
                                        activity.taskId = TASK_TYPE;
                                    } else {
                                        Util.showSnackbar(v, json.getJSONObject("result").optString("success_message"));
                                    }
                                } else {
                                    Util.showSnackbar(v, json.getString("result"));
                                }
                                activity.backcoverchange = Constant.TASK_IMAGE_UPLOAD;
                                activity.taskPerformed = Constant.TASK_IMAGE_UPLOAD;

                                if(url.equalsIgnoreCase(Constant.URL_UPLOAD_PAGE_COVER) || url.equalsIgnoreCase(Constant.URL_UPLOAD_GROUP_COVER)
                                        || url.equalsIgnoreCase(Constant.URL_UPLOAD_GROUP_PHOTO)  || url.equalsIgnoreCase(Constant.URL_MUSIC_UPLOAD_COVER) ){
                                    activity.taskId = Constant.TASK_COVER_UPLOAD;
                                }else {
                                    activity.taskId = Constant.TASK_PHOTO_UPLOAD;
                                }



                                onBackPressed();
                                /*if(!flag_data){
                                    goTo(Constant.GoTo.VIEW_PROFILE, Constant.KEY_ID, SPref.getInstance().getLoggedInUserId(context));
                                }
                                 getActivity().finish();*/

                            } else {
                                changeLayoutParams(true);
                                bSave.setText(R.string.TXT_SAVE_PHOTO);
                                Util.showSnackbar(v, err.getErrorMessage());
                            }

                        } else {
                            changeLayoutParams(true);
                            bSave.setText(R.string.TXT_SAVE_PHOTO);
                            notInternetMsg(v);
                        }
                    } catch (Exception e) {
                        CustomLog.e(e);
                        somethingWrongMsg(v);
                        bSave.setText(R.string.TXT_SAVE_PHOTO);
                        changeLayoutParams(true);
                    }

                    return true;
                };
                new HttpImageRequestHandler(activity, new Handler(callback), this).run(request);

            } catch (Exception e) {
                bSave.setEnabled(true);
                bChoose25.setEnabled(true);
                changeLayoutParams(true);
                CustomLog.e(e);
            }
        } else {
            changeLayoutParams(true);
            notInternetMsg(v);
        }
    }


    public void showDialog(String msg) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                isCameraOptionSelected = true;
                askForPermission(permissionlistener, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            });

            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> {
                progressDialog.dismiss();
                isCameraOptionSelected = false;
                askForPermission(permissionlistener, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                //showImageChooser();
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private final PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // Toast.makeText(How_It_Works_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            try {
                if (isCameraOptionSelected) {
                    takeImageFromCamera();
                } else {
                    showImageChooser();
                }
            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

    public static AlbumImageFragment2 newInstance(String title, String url, String imageUrl, Map<String, Object> map) {
        AlbumImageFragment2 frag = new AlbumImageFragment2();
        frag.title = title;
        frag.url = url;
        frag.imageUrl = imageUrl;
        frag.map = map;
        return frag;
    }

    public static AlbumImageFragment2 newInstance(String title, String url, String imageUrl, Map<String, Object> map, boolean flagdata ) {
        AlbumImageFragment2 frag = new AlbumImageFragment2();
        frag.title = title;
        frag.url = url;
        frag.imageUrl = imageUrl;
        frag.map = map;
        frag.flag_data = flagdata;
        return frag;
    }


    @Override
    public void transferred(float progress) {
        CustomLog.d("progress__", "" + progress);
        try {
            activity.runOnUiThread(() -> {
                //   if (null != pDialog) {
                tvProgress.setText(((int) progress) + " %");
                circularProgressBar.setProgressWithAnimation(progress, 1500);
                //  }
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

   /* private void clearImage() {
        flagPictureTaken = false;
        File imagef = new File(image_path_source_temp + imageName);
        imagef.delete();
        Image_BMP = null;
        takeImage.setScaleType(ImageView.ScaleType.FIT_XY);*//*CENTER_INSIDE*//*
        takeImage.setImageResource(R.drawable.image_placeholder);
    }*/
}

package com.sesolutions.ui.signup;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
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
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.camerahelper.CameraActivity;
import com.sesolutions.http.GetGcmId;
import com.sesolutions.http.HttpImageRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.http.MyMultiPartEntity;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.SignInResponse;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.WebViewFragment;
import com.sesolutions.ui.customviews.CircularProgressBar;
import com.sesolutions.ui.music_album.AlbumImageFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;
import com.soundcloud.android.crop.Crop;
import com.yalantis.ucrop.UCrop;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class ProfileImageFragment extends BaseFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object>, MyMultiPartEntity.ProgressListener {

    private static final int CAMERA_PIC_REQUEST = 7079;
    private static final int CAMERA_Crop_REQUEST = 7081;
    private View v;
    String SubScritionId="";

    TextView bChoose25;
    AppCompatButton bSave;
    CircleImageView ivProfileImage;
    ImageView dummyimage;
    private String imagePath;
    private boolean isCameraOptionSelected = false;
    RelativeLayout realtivedammy;
    TextView ivSkip;
    ImageView ivCamera2;





    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.fragment_profile_image, container, false);
        try {
            applyTheme(v);
            init();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private CircularProgressBar circularProgressBar;
    private TextView tvProgress;

    private void init() {
        ivProfileImage = v.findViewById(R.id.ivProfileImage);
        dummyimage = v.findViewById(R.id.dummyimage);
        ivCamera2 = v.findViewById(R.id.ivCamera2);
        realtivedammy = v.findViewById(R.id.realtivedammy);
        ivSkip = v.findViewById(R.id.ivSkip);
        circularProgressBar = (CircularProgressBar) v.findViewById(R.id.cpb);
        tvProgress = v.findViewById(R.id.tvProgress);
        circularProgressBar.setColor(Color.parseColor(Constant.colorPrimary));
        // circularProgressBar.setBackgroundColor(Color.parseColor(Constant.menuButtonActiveTitleColor.replace("#", "#67")));
        circularProgressBar.setProgressWithAnimation(0, 0); // Default duration = 1500ms
        changeLayoutParams(true);
        bSave = v.findViewById(R.id.bSave);
        v.findViewById(R.id.rlMain).setBackgroundColor(Color.parseColor(Constant.backgroundColor));
        bChoose25 = v.findViewById(R.id.bChoose25);
        View bSkip = v.findViewById(R.id.bSkip);
        ivSkip.setVisibility(View.VISIBLE);
        dummyimage.setVisibility(View.VISIBLE);
        realtivedammy.setVisibility(View.VISIBLE);
        ivProfileImage.setVisibility(View.GONE);
        bSave.setOnClickListener(this);
        bSkip.setVisibility(View.GONE);
        bSkip.setOnClickListener(this);
        ivSkip.setOnClickListener(this);
        v.findViewById(R.id.ivBack).setOnClickListener(this);

        if(!SPref.getInstance().getString(context,"FACEBOOK_URI").equals("")){
            Util.showImageWithGlide(ivProfileImage, SPref.getInstance().getString(context,"FACEBOOK_URI"), context, R.drawable.image_placeholder);
        }

        bChoose25.setText("Upload Photo");
        bSave.setVisibility(View.GONE);

        bChoose25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SPref.getInstance().getString(context,"FACEBOOK_URI").equals("")){
                    askForPermission(Manifest.permission.CAMERA);
                }

            }
        });

        ivCamera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SPref.getInstance().getString(context,"FACEBOOK_URI").equals("")){
                    askForPermission(permissionlistener, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }


            }
        });

        realtivedammy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SPref.getInstance().getString(context,"FACEBOOK_URI").equals("")){
                    askForPermission(permissionlistener, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        });

    }

    private void changeLayoutParams(boolean isSmall) {
        if (isSmall) {
          //123  v.findViewById(R.id.ll1).setVisibility(View.VISIBLE);
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
                    if(SPref.getInstance().getString(context,"FACEBOOK_URI").equals("")){
                        askForPermission(Manifest.permission.CAMERA);
                    }
                    break;
                case R.id.bSave:
                    // showImageChooser();
                    ivCamera2.setVisibility(View.GONE);
                    callUploadImageApi(imagePath);
                    break;
                case R.id.ivSkip:
                case R.id.bSkip:
                    // showImageChooser();
                    callUploadImageApi("");
                    break;
                case R.id.ivBack:
                    onBackPressed();
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void goToScreenAsPerResult(String result) {
        switch (result) {
            case Constant.RESULT_FORM_OTP:
            case Constant.RESULT_FORM_OTP_LOGIN:
                openOtpFragment(OTPFragment.FROM_SIGNUP, "", null);
                break;
            case Constant.RESULT_FORM_3:
                openWebView(Constant.URL_SUBSCRIPTION+"&user_subscription_id="+SubScritionId, Constant.TITLE_SUBSCRIPTION);
                break;
            case Constant.RESULT_FORM_4:
                fragmentManager.beginTransaction().replace(R.id.container, new JoinFragment()).commit();
                break;
            case Constant.RESULT_FORM_INTEREST2:
                goToSignUpFragment(Constant.VALUE_GET_FORM_INTEREST);
                break;
            default:
             //   fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment()).commit();
                fragmentManager.beginTransaction().replace(R.id.container, new SignInFragment2())
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    private void openSubscriptionWebview(String urlTerms, String titleTerms) {
        fragmentManager.beginTransaction().replace(R.id.container, WebViewFragment.newInstance(urlTerms, titleTerms)).addToBackStack(null).commit();
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

                case CAMERA_PIC_REQUEST:
                    if (resultCode == -1) {
                        setImage(Constant.path);
                        CustomLog.d("CAMERA_PIC_REQUEST", Constant.path);
                        bChoose25.setText("Change Photo");
                        bSave.setVisibility(View.VISIBLE);
                    }
                    break;
                case UCrop.REQUEST_CROP:
                //    handleCropResult(data);
                     Uri resultUri = UCrop.getOutput(data);
              //       imagePath = resultUri.getPath();
                    Log.e("imagePath",""+imagePath);
                 //   ivProfileImage.setImageURI(null);
               //     ivProfileImage.setImageURI(resultUri);

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
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    public String convertBitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        CustomLog.e("encodeToString", temp);
        return temp;
    }

    public Bitmap convertStringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }



    public void setImage(String path) {
        /*   Util.FCopy(image_path_source_temp + imageName, path);*/
        imagePath = path;
        File file=new File(path);
       /* UCrop uCrop = UCrop.of(Uri.fromFile(file), Uri.fromFile(file));
        uCrop = uCrop.useSourceImageAspectRatio();
        uCrop.start(getActivity(), ProfileImageFragment.this);*/

        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(Uri.fromFile(file), destination).asSquare().start(getActivity(), ProfileImageFragment.this);
     //   Crop.of(Uri.fromFile(file), destination).asSquare().start(getActivity());




    }

    private void takeImageFromCamera() {
        // fimg = new File(image_path_source_temp + imageName);
        // Uri uri = Uri.fromFile(fimg);
        String imagePath = Util.getAppFolderPath();
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

    private void callUploadImageApi(final String filePath) {
        if (TextUtils.isEmpty(Constant.GCM_DEVICE_ID)) {
            this.imagePath = filePath;
            new GetGcmId(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, context);
            return;
        }


        if (isNetworkAvailable(context)) {
            bSave.setText(TextUtils.isEmpty(filePath) ? R.string.TXT_SAVE_PHOTO : R.string.TXT_UPLOADING);
            try {
                HttpRequestVO request = new HttpRequestVO(Constant.URL_SIGNUP);
                if (!TextUtils.isEmpty(filePath)) {
                    //showBaseLoader(false);
                    changeLayoutParams(false);
                    request.params.put(Constant.KEY_IMAGE, filePath);
                }
                request.params.put(Constant.KEY_VALIDATE_PHOTO_FORM, 1);
                request.headres.put(Constant.KEY_COOKIE, getCookie());
                request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                request.params.put(Constant.KEY_DEVICE_UID, Constant.GCM_DEVICE_ID);
                request.requestMethod = HttpPost.METHOD_NAME;

                Handler.Callback callback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (response != null) {
                                JSONObject json = new JSONObject(response);
                                if (json.get(Constant.KEY_RESULT) instanceof String) {
                                    String result = json.getString(Constant.KEY_RESULT);
                                    try {
                                        SubScritionId = json.getString("user_subscription_id");
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                        SubScritionId="";
                                    }

                                    goToScreenAsPerResult(result);
                                } else {

                                    SignInResponse vo = new Gson().fromJson(response, SignInResponse.class);
                                    if (TextUtils.isEmpty(vo.getError())) {
                                        UserMaster um = vo.getResult();
                                        um.setAuthToken(vo.getAouthToken());
                                        um.setLoggedinUserId(um.getUserId());
                                        SPref.getInstance().saveUserMaster(context, um, vo.getSessionId());
                                        SPref.getInstance().updateSharePreferences(context, Constant.KEY_AUTH_TOKEN, vo.getAouthToken());
                                        SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN, true);
                                        SPref.getInstance().updateSharePreferences(context, Constant.KEY_LOGGED_IN_ID, um.getUserId());

                                        goToDashboard();
                                    } else {
                                        changeLayoutParams(true);
                                        bSave.setText(R.string.TXT_SAVE_PHOTO);
                                        Util.showSnackbar(v, vo.getErrorMessage());
                                    }
                                }
                            } else {
                                //  bSave.setText(TextUtils.isEmpty(filePath) ? Constant.TXT_SKIP : Constant.TXT_SAVE_PHOTO);
                                somethingWrongMsg(v);
                                bSave.setText(R.string.TXT_SAVE_PHOTO);
                                changeLayoutParams(true);
                            }
                        } catch (Exception e) {
                            somethingWrongMsg(v);
                            bSave.setText(R.string.TXT_SAVE_PHOTO);
                            changeLayoutParams(true);
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
                    }
                };
                new HttpImageRequestHandler(activity, new Handler(callback), this).run(request);

            } catch (Exception e) {
                somethingWrongMsg(v);
                changeLayoutParams(true);
                bSave.setText(R.string.TXT_SAVE_PHOTO);
            }
        } else {
            notInternetMsg(v);
            changeLayoutParams(true);
        }
    }

    private void askForPermission(String permission) {
        try {
            new TedPermission(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(Constant.MSG_PERMISSION_DENIED)
                    .setPermissions(permission, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
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

    @Override
    public boolean onItemClicked(Integer object1, Object object2, int postion) {
        callUploadImageApi(imagePath);
        return false;
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
        takeImage.setImageResource(R.drawable.imageplaceh_older);
    }*/
}

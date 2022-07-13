package com.sesolutions.ui.dashboard;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.sesolutions.ui.common.CreateProfileVideoForm;
import com.sesolutions.utils.MediaUtils;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.FileProvider;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.camerahelper.CameraActivity;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.http.ParserCallbackInterface;
import com.sesolutions.imageeditengine.ImageEditor;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.Links;
import com.sesolutions.responses.Video;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.common.BaseFragment;
import com.sesolutions.ui.common.FormHelper;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.FileUtil;
import com.sesolutions.utils.PathUtil;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import me.riddhimanadib.formmaster.model.FormElementMusicFile;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getCacheDir;
import static com.sesolutions.ui.common.FormHelper.locationTag;

/**
 * Created by root on 21/11/17.
 */

public abstract class ApiHelper extends BaseFragment implements ParserCallbackInterface {
    static final int REQ_CODE_LINK = 1;
    public static final int REQ_CODE_VIDEO = 2;
    public static final int REQ_CODE_IMAGE = 3;
    public static final int REQ_CODE_MUSIC = 4;
    public static final int REQ_CODE_VIDEO_LINK = 5;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 9099;
    public AppCompatEditText etBody;
    public AppCompatEditText etComment;
    private static final int CAMERA_PIC_REQUEST = 7078;
    private static final int CAMERA_VIDEO_REQUEST = 7080;
    private static final int MUSIC_REQUEST = 7081;

    private boolean isCameraOptionSelected;
    public boolean canShowThumbnail;
    private String imageFilePath;
    public Video videoDetail;
    Links linkDetail;
    public String links;
    public int MAX_COUNT = 10;
    ArrayList<String> selectedImageList;
    String tempLink = "/";

    void callPreviewLinkApi(final String link) {
        tempLink = link;
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {
                try {
                    showBaseLoader(false);
                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_LINK_PREVIEW);
                    String link2 = link.replace("https://youtu.be/", "https://www.youtube.com/watch?v=");
                    if (!link.startsWith("http")) {
                        link2 = "http://" + link;
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_URI, link2);
                    request.params.put(Constant.KEY_C_TYPE, Constant.VALUE_C_TYPE);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;


                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                           /* links = link;
                            onResponseSuccess(REQ_CODE_LINK, response);*/

                            if (null != response) {
                                CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                if (TextUtils.isEmpty(resp.getError())) {
                                    //  attachedFileType = TYPE_LINK;
                                    linkDetail = resp.getResult().getLink();
                                    linkDetail.setUri(link);
                                    onResponseSuccess(REQ_CODE_LINK, link);
                                    //    updateMenuItem(linkDetail.getUri());
                                } else {
                                    Util.showSnackbar(etBody, resp.getErrorMessage());
                                    showLinkDialog(link);
                                }

                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
                    };

                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();
                }
            }

        } catch (Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }
    }

    void showLinkDialog(String value) {
        final EditText taskEditText = new EditText(context);
        taskEditText.setMaxLines(1);
        taskEditText.setText(value);
        // taskEditText.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_holo_border_grey_light));
        //taskEditText.setMar(10, 0, 10, 0);
        View view = getLayoutInflater().inflate(R.layout.titlebar, null);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setCustomTitle(view)
                .setTitle(Constant.MSG_ENTER_LINK)
                .setView(taskEditText)
                .setPositiveButton(Constant.TXT_ADD, (dialog1, which) -> {
                    String link = String.valueOf(taskEditText.getText());
                    CustomLog.e("value", link);
                    callPreviewLinkApi(link);
                })
                .create();
        dialog.show();
    }

    public void showVideoSourceDialog(String msg) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);
            ((AppCompatButton) progressDialog.findViewById(R.id.bCamera)).setText(R.string.TXT_YOU_TUBE);
            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                showEdittextDialog(Constant.EMPTY);
                // takeImageFromCamera();
            });
            ((AppCompatButton) progressDialog.findViewById(R.id.bGallary)).setText(R.string.TXT_VIMEO);
            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> {
                progressDialog.dismiss();
                showEdittextDialog(Constant.EMPTY);
                //showImageChooser();
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    void showVideoSourceDialog(String msg, final boolean alsoFromMyDevice,String resorcetype,int resId) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            Log.e("resorcetype",""+resorcetype);
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_ten);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);
            ((TextView) progressDialog.findViewById(R.id.yic)).setText(R.string.TXT_YOU_TUBE);

            ((TextView) progressDialog.findViewById(R.id.yic)).setTextColor(Color.parseColor(Constant.text_color_1));
            ((TextView) progressDialog.findViewById(R.id.mydevicetext)).setTextColor(Color.parseColor(Constant.text_color_1));
            ((TextView) progressDialog.findViewById(R.id.vimeotext)).setTextColor(Color.parseColor(Constant.text_color_1));

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                showEdittextDialog(Constant.EMPTY,resorcetype,resId);
                // takeImageFromCamera();
            });
            ((TextView) progressDialog.findViewById(R.id.vimeotext)).setText(R.string.TXT_VIMEO);
            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> {
                progressDialog.dismiss();
                showEdittextDialog(Constant.EMPTY,resorcetype,resId);
                //showImageChooser();
            });

            ((TextView) progressDialog.findViewById(R.id.mydevicetext)).setText(R.string.my_device);
            progressDialog.findViewById(R.id.bLink).setOnClickListener(v -> {
                progressDialog.dismiss();
                isVideoSelected = alsoFromMyDevice;
//                showImageDialog(Constant.MSG_SELECT_VIDEO_SOURCE);

                if( resorcetype!=null && resorcetype.equalsIgnoreCase("sesgroup_group")){
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_PARENT_ID, resId);
                    fragmentManager.beginTransaction().replace(R.id.container, CreateProfileVideoForm.newInstance(Constant.FormType.CREATE_PAGE_VIDEO, map, Constant.URL_GROUP_VIDEO_CREATE)).addToBackStack(null).commit();
                 }else  if(resorcetype!=null && resorcetype.equalsIgnoreCase("businesses")){
                    Map<String, Object> map = new HashMap<>();
                    map.put(Constant.KEY_PARENT_ID, resId);
                    fragmentManager.beginTransaction().replace(R.id.container, CreateProfileVideoForm.newInstance(Constant.FormType.CREATE_PAGE_VIDEO, map, Constant.URL_BUSINESS_VIDEO_CREATE)).addToBackStack(null).commit();
               }
                else {
                    openVideoPicker(true);
                }

                //  showEdittextDialog(Constant.EMPTY);
                //showImageChooser();
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }


    }


    private void showEdittextDialog(String value,String rctype,int resId) {
        final EditText taskEditText = new EditText(context);
        taskEditText.setMaxLines(1);
        taskEditText.setText(value);
        View view = getLayoutInflater().inflate(R.layout.titlebar_video, null);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setCustomTitle(view)
                .setTitle(Constant.MSG_ENTER_VIDEO_URL)
                // .setMessage("What do you want to do next?")
                .setView(taskEditText)
                .setPositiveButton(Constant.TXT_ADD, (dialog1, which) -> {
                    String videoUrl = String.valueOf(taskEditText.getText());
                    CustomLog.e("value", videoUrl);
                 //   callAttachVideoApi(videoUrl.replace("https://youtu.be/", "https://www.youtube.com/watch?v="),rctype);
                    callAttachVideoApi(videoUrl,rctype,resId);
                })
                //  .setNegativeButton("Can, null)
                .create();
        dialog.show();
    }

    void showVideoSourceDialog(String msg, final boolean alsoFromMyDevice) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_ten);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);
            ((TextView) progressDialog.findViewById(R.id.yic)).setText(R.string.TXT_YOU_TUBE);
            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                showEdittextDialog(Constant.EMPTY);
                // takeImageFromCamera();
            });
            ((TextView) progressDialog.findViewById(R.id.vimeotext)).setText(R.string.TXT_VIMEO);
            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> {
                progressDialog.dismiss();
                showEdittextDialog(Constant.EMPTY);
                //showImageChooser();
            });

            ((TextView) progressDialog.findViewById(R.id.mydevicetext)).setText(R.string.my_device);
            progressDialog.findViewById(R.id.bLink).setOnClickListener(v -> {
                progressDialog.dismiss();
                isVideoSelected = alsoFromMyDevice;
//                showImageDialog(Constant.MSG_SELECT_VIDEO_SOURCE);
                openVideoPicker(true);
                //  showEdittextDialog(Constant.EMPTY);
                //showImageChooser();
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }


    }


    private void showEdittextDialog(String value) {
        final EditText taskEditText = new EditText(context);
        taskEditText.setMaxLines(1);
        taskEditText.setText(value);
        View view = getLayoutInflater().inflate(R.layout.titlebar_video, null);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setCustomTitle(view)
                .setTitle(Constant.MSG_ENTER_VIDEO_URL)
                // .setMessage("What do you want to do next?")
                .setView(taskEditText)
                .setPositiveButton(Constant.TXT_ADD, (dialog1, which) -> {
                    String videoUrl = String.valueOf(taskEditText.getText());
                    CustomLog.e("value", videoUrl);
                    callAttachVideoApi(videoUrl.replace("https://youtu.be/", "https://www.youtube.com/watch?v="),"",0);
                })
                //  .setNegativeButton("Can, null)
                .create();
        dialog.show();
    }

    public void showImageDialog(String msg, boolean isCameraEnabled, boolean isGalleryEnabled) {
        if (isCameraEnabled && isGalleryEnabled) {
            showImageDialog(msg);
        } else if (isCameraEnabled) {
            isCameraOptionSelected = true;
            askForPermission(Manifest.permission.CAMERA);
        } else if (isGalleryEnabled) {
            isCameraOptionSelected = false;
            askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    public static final int CAMERA_IMAGE_REQ_CODE = 185;
    public static final int GALLERY_IMAGE_REQ_CODE = 188;
    public void showImageDialog(String msg) {
        try {
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = ProgressDialog.show(context, "", "", true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);
            Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_message_two);
            new ThemeManager().applyTheme(progressDialog.findViewById(R.id.rlDialogMain), context);
            TextView tvMsg = progressDialog.findViewById(R.id.tvDialogText);
            tvMsg.setText(msg);

            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                isCameraOptionSelected = true;
              //  askForPermission(Manifest.permission.CAMERA);
                // takeImageFromCamera();

                ImagePicker.with(this)
                        // User can only capture image from Camera
                        .cameraOnly()
                        // Image size will be less than 1024 KB
                        // .compress(1024)
                        //  Path: /storage/sdcard0/Android/data/package/files
                        .saveDir(getContext().getExternalFilesDir(null))
                        //  Path: /storage/sdcard0/Android/data/package/files/DCIM
                        .saveDir(getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM))
                        //  Path: /storage/sdcard0/Android/data/package/files/Download
                        .saveDir(getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS))
                        //  Path: /storage/sdcard0/Android/data/package/files/Pictures
                        .saveDir(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                        //  Path: /storage/sdcard0/Android/data/package/files/Pictures/ImagePicker
                        .saveDir(new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ImagePicker"))
                        //  Path: /storage/sdcard0/Android/data/package/files/ImagePicker
                        .saveDir(getContext().getExternalFilesDir("ImagePicker"))
                        //  Path: /storage/sdcard0/Android/data/package/cache/ImagePicker
                        .saveDir(new File(getContext().getExternalCacheDir(), "ImagePicker"))
                        //  Path: /data/data/package/cache/ImagePicker
                        .saveDir(new File(getCacheDir(), "ImagePicker"))
                        //  Path: /data/data/package/files/ImagePicker
                        .saveDir(new File(getContext().getFilesDir(), "ImagePicker"))
                        // Below saveDir path will not work, So do not use it
                        //  Path: /storage/sdcard0/DCIM
                        //  .saveDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
                        //  Path: /storage/sdcard0/Pictures
                        //  .saveDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
                        //  Path: /storage/sdcard0/ImagePicker
                        //  .saveDir(File(Environment.getExternalStorageDirectory(), "ImagePicker"))
                        .start(CAMERA_IMAGE_REQ_CODE);

            });
            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> {
                progressDialog.dismiss();
                isCameraOptionSelected = false;
                //askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                ImagePicker.with(this)
                        // Crop Image(User can choose Aspect Ratio)
                        .crop()
                        // User can only select image from Gallery
                        .galleryOnly()

                        .galleryMimeTypes(new String[]{"image/png", "image/jpg","image/jpeg"})
                        // Image resolution will be less than 1080 x 1920
                        .maxResultSize(1080, 1920)
                        // .saveDir(getExternalFilesDir(null)!!)
                        .start(GALLERY_IMAGE_REQ_CODE);
                //showImageChooser();
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void openImagePicker() {
        isVideoSelected = false;


            askForPermission(Manifest.permission.CAMERA);

       /* } else {
            FilePickerBuilder.getInstance()
                    .setMaxCount(MAX_COUNT)
                    .setSelectedFiles(selectedImageList != null ? selectedImageList : new ArrayList<>())
                    .setActivityTheme(R.style.FilePickerTheme)
                    .showFolderView(true)
                    .enableImagePicker(true)
                    .enableVideoPicker(false)
                    .pickPhoto(this);
        *//*    new TedPermission(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(Constant.MSG_PERMISSION_DENIED)
                    .setPermissions(android.Manifest.permission.ACCESS_MEDIA_LOCATION)
                    .check();*//*
        }*/


    }



    protected void openVideoPicker(boolean videoSelected) {
        isVideoSelected = videoSelected;
        askForPermission(Manifest.permission.CAMERA);
    }

    private void showImageChooser() {
        FilePickerBuilder.getInstance()
                .setMaxCount(MAX_COUNT)
                .setSelectedFiles(selectedImageList != null ? selectedImageList : new ArrayList<>())
                .setActivityTheme(R.style.FilePickerTheme)
                .showFolderView(true)
                .enableImagePicker(true)
                .enableVideoPicker(false)
                .pickPhoto(this);
    }

    private void showVideoChooser() {
//        Constant.videoUri = null;
//        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, REQUEST_TAKE_GALLERY_VIDEO);

        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .setSelectedFiles(selectedImageList != null ? selectedImageList : new ArrayList<>())
                .setActivityTheme(R.style.FilePickerTheme)
                .showFolderView(false)
                .enableVideoPicker(true)
                .enableImagePicker(false)
                .pickPhoto(this);


    }

    private void takeImageFromCamera() {

        String imagePath = Environment.DIRECTORY_DOWNLOADS + "/SeSolutions/";
        String imageName = Constant.IMAGE_NAME + Util.getCurrentdate(Constant.TIMESTAMP);

        File dir = new File(imagePath);
        try {
            if (dir.mkdir()) {
                CustomLog.d("j", "j");
            } else {
                CustomLog.d("i", "i");
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
        Intent cameraIntent = new Intent(activity, CameraActivity.class);
        cameraIntent.putExtra("path", imagePath);
        cameraIntent.putExtra("name", imageName);
        cameraIntent.putExtra("record_video", false);
        getActivity().startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }

    private void takeVideoFromCamera() {

        String imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SeSolutions/";
        String imageName = Constant.IMAGE_NAME + Util.getCurrentdate(Constant.TIMESTAMP);

        Constant.videoUri = null;
        Intent cameraIntent = new Intent(activity, CameraActivity.class);
        cameraIntent.putExtra("path", imagePath);
        cameraIntent.putExtra("name", imageName);
        cameraIntent.putExtra("record_video", true);
        startActivityForResult(cameraIntent, CAMERA_VIDEO_REQUEST);
    }


    public void askForPermission(String permission) {
        try {
            new TedPermission(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(Constant.MSG_PERMISSION_DENIED)
                    .setPermissions(permission, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void askForPermission2(String permission) {
        try {
            new TedPermission(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(Constant.MSG_PERMISSION_DENIED)
                    .setPermissions(permission, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public static Intent getFileIntent(String[] type) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, type);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        return intent;
    }
    public static final String[] EXTRA_MIME_DOC=new String[]{"text/plane","text/html","application/pdf","application/msword","application/vnd.ms.excel", "application/mspowerpoint","application/zip"};


    public boolean isVideoSelected = false;
    public static final int FILE = 25;

    private final PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // Toast.makeText(How_It_Works_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            try {
                if(iWORdDOCUMENT){
                    startActivityForResult(getFileIntent(EXTRA_MIME_DOC), FILE);
                }
                else {
                    canShowThumbnail = false;
                    if (isCameraOptionSelected) {
                        if (isVideoSelected) {
                            takeVideoFromCamera();
                        } else {
                            takeImageFromCamera();
                        }
                    } else if (isVideoSelected) {
                        //showing thumbnail only if video selected from gallery
                        canShowThumbnail = true;
                        showVideoChooser();
//                    fetchVideo();
                    } else
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



    public void showAudioChooser(boolean canSelectMultiple) {
        try {
            Intent chooseFile;
            Intent intent;
            chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("audio/*");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, canSelectMultiple);
            }
            intent = Intent.createChooser(chooseFile, getStrings(R.string.choose_songs));
            startActivityForResult(intent, MUSIC_REQUEST);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private void fetchVideo() {
        Constant.videoUri = null;
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);
    }

    /**
     * camera activity call back
     */

    public void openDOCPicker() {
        isVideoSelected = false;
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
    }


    public static final int REQ_EDITOR=190;
    private final int LOCATION_AUTOCOMPLETE_REQUEST_CODE = 8976;
    public boolean iWORdDOCUMENT = false;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CustomLog.e("onActivityResult", "requestCode : " + requestCode + " resultCode : " + resultCode);
        try {
            switch (requestCode) {
                case 25:
                    File file= MediaUtils.getRealPath(getActivity(), data.getData());
                    List<String> photoPaths22 = new ArrayList<>();
                    photoPaths22.add(file.getPath());
                    imageFilePath = file.getPath();
                    Log.e("document45454",""+file.getPath());
                    onResponseSuccess(REQ_CODE_IMAGE, photoPaths22);
                    break;
                case REQ_EDITOR:
                    if (resultCode == -1) {
                        if (data != null) {
                            CustomLog.e("desc", "not null");
                            String desc = data.getStringExtra(Constant.TEXT);
                            int tag = data.getIntExtra(Constant.TAG, -1);
                            CustomLog.e("desc", desc);
                            FormHelper.mFormBuilder.getAdapter().setValueAtTag(tag, desc);
                        } else {
                            CustomLog.e("desc", "null");
                        }
                    }
                    break;
                case LOCATION_AUTOCOMPLETE_REQUEST_CODE:
                    switch (resultCode) {
                        case RESULT_OK:
                            Place place = Autocomplete.getPlaceFromIntent(data);
                            CharSequence address = place.getAddress();
                            //to get latitude using places api
                            Double lat = place.getLatLng().latitude;
                            //to get longitude using places api
                            Double lang = place.getLatLng().longitude;
                            //Using Geocoder to get all the other fields of that place.
                            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                            //getting exact location using geocoder.
                            List<Address> addresses = geocoder.getFromLocation(lat, lang, 1);

                            CustomLog.e("lat:", "" + lat);
                            CustomLog.e("lang", "" + lang);
                            CustomLog.e("country", "" + addresses.get(0).getCountryName());
                            CustomLog.e("city", "" + addresses.get(0).getLocality());
                            CustomLog.e("ZIP", "" + addresses.get(0).getPostalCode());
                            CustomLog.e("state", "" + addresses.get(0).getAdminArea());
                            CustomLog.e("subLoc", "" + addresses.get(0).getSubLocality());
                            CustomLog.e("locale", "" + addresses.get(0).getLocale());

                            //To send all the location fields with location in form
                            FormHelper.mapHiddenFields.put("ses_city", addresses.get(0).getLocality());
                            FormHelper.mapHiddenFields.put("ses_zip", addresses.get(0).getPostalCode());
                            FormHelper.mapHiddenFields.put("ses_country", addresses.get(0).getCountryName());
                            FormHelper.mapHiddenFields.put("ses_state", addresses.get(0).getAdminArea());
                            FormHelper.mapHiddenFields.put("ses_lat", lat);
                            FormHelper.mapHiddenFields.put("lat", lat);
                            FormHelper.mapHiddenFields.put("lng", lang);
                            FormHelper.mapHiddenFields.put("ses_lng", lang);

                            //setting value of location in the locationTag.
                            if (null != address) {
                                FormHelper.mFormBuilder.getAdapter().setValueAtTag(locationTag, address.toString());
                            }
                            break;
                    }
                    break;

                case FilePickerConst.REQUEST_CODE_PHOTO:
                    if (resultCode == -1 && data != null) {
                        List<String> photoPaths = new ArrayList<>(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_PHOTOS));
//                        onResponseSuccess(REQ_CODE_IMAGE, photoPaths);
                        Log.e("tostring",""+photoPaths.toString());
                        if(photoPaths.size() > 0) {
                            if (photoPaths.get(0).endsWith(".mp4")) {
                                Constant.videoUri = Uri.fromFile(new File(photoPaths.get(0)));
                                onResponseSuccess(REQ_CODE_VIDEO, photoPaths);
                            } else
                                onResponseSuccess(REQ_CODE_IMAGE, photoPaths);
                        }
                    }
                    break;
                case CAMERA_PIC_REQUEST:
                    if (resultCode == -1) {
                        //setImage(Constant.path);
                        List<String> photoPaths = new ArrayList<>();
                        photoPaths.add(data.getData().getPath());
                        CustomLog.d("CAMERA_PIC_REQUEST", Constant.path);
                        imageFilePath = data.getData().getPath();
                        onResponseSuccess(REQ_CODE_IMAGE, photoPaths);
                    }
                    break;

                case ImageEditor.RC_IMAGE_EDITOR:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        List<String> photoPaths = new ArrayList<>();
                        photoPaths.add(data.getStringExtra(ImageEditor.EXTRA_EDITED_PATH));
                        onResponseSuccess(ImageEditor.RC_IMAGE_EDITOR, photoPaths);
                        //   edited_image.setImageBitmap(BitmapFactory.decodeFile(imagePath))
                    }

                    break;
                case    MUSIC_REQUEST:
                    if (null != data) { // checking empty selection
                        Uri uri;
                        List<String> photoPaths = new ArrayList<>();
                        if (null != data.getClipData()) { // checking multiple selection or not
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                uri = data.getClipData().getItemAt(i).getUri();
                                String selectedImagePath = FileUtil.getPath(context, uri);
                                if (selectedImagePath != null) {
                                    photoPaths.add(selectedImagePath);
                                    //Constant.videoUri = selectedImageUri;
                                    // imageFilePath = selectedImagePath;
                                    //  videoView.setVideoPath(selectedImagePath);
                                }
                            }
                        } else {
                            uri = data.getData();
                            String selectedImagePath = FileUtil.getPath(context, uri);
                            if (selectedImagePath != null) {
                                photoPaths.add(selectedImagePath);
                            }
                        }


                        //   CustomLog.e("VIDEO_REQUEST", "" + selectedImagePath);
                        if (photoPaths.size() > 0) {
                            onResponseSuccess(REQ_CODE_MUSIC, photoPaths);


                        } else {
                            Util.showToast(context, "Error fetching music");
                        }
                    }
                    break;
                case REQUEST_TAKE_GALLERY_VIDEO:
                    Uri selectedImageUri = data.getData();

                    // MEDIA GALLERY
                    //ImagePath will use to upload video
                    String selectedImagePath = getPath(selectedImageUri);
                    CustomLog.e("VIDEO_REQUEST", "" + selectedImagePath);

                    if (selectedImagePath != null) {
                        List<String> photoPaths = new ArrayList<>();
                        photoPaths.add(selectedImagePath);
                        Constant.videoUri = selectedImageUri;
                        imageFilePath = selectedImagePath;
                        onResponseSuccess(REQ_CODE_VIDEO, photoPaths);
                        //  videoView.setVideoPath(selectedImagePath);
                    } else {
                        Util.showToast(context, "Error fetching video");
                    }
                    break;

                case CAMERA_VIDEO_REQUEST:
                    //ImagePath will use to upload video
                    selectedImagePath = Constant.path;
                    CustomLog.e("VIDEO_REQUEST", "" + selectedImagePath);
                    if (selectedImagePath != null) {
                        List<String> photoPaths = new ArrayList<>();
                        photoPaths.add(selectedImagePath);
//                        Constant.videoUri = selectedImageUri;
                        imageFilePath = selectedImagePath;
                        onResponseSuccess(REQ_CODE_VIDEO, photoPaths);
                    } else {
                        Util.showToast(context, "Error fetching video");
                    }
                    break;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    private void callAttachVideoApi(final String videoUrl,String rctype,int resId) {
        try {
            if (isNetworkAvailable(context)) {

                try {
                    showBaseLoader(false);
                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request=null;
                    if(rctype!= null &&rctype.equalsIgnoreCase("sesgroup_group")){
                        request = new HttpRequestVO(Constant.URL_CREATE_VIDEO2);
                        request.params.put(Constant.KEY_TYPE, 1);
                        request.params.put("parent_id", resId);
                    }else if(rctype!= null && rctype.equalsIgnoreCase("businesses")){
                        request = new HttpRequestVO(Constant.URL_CREATE_VIDEO3);
                        request.params.put(Constant.KEY_TYPE, 1);
                        request.params.put("parent_id", resId);
                    }else {
                        request = new HttpRequestVO(Constant.URL_CREATE_VIDEO);
                        request.params.put(Constant.KEY_C_TYPE, Constant.VALUE_C_TYPE);
                    }
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_URI, videoUrl);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));
                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (null != response) {

                                if(rctype!= null && (rctype.equalsIgnoreCase("sesgroup_group") ||
                                        rctype.equalsIgnoreCase("businesses"))){
                                    videoDetail = new Gson().fromJson(response, Video.class);
                                    onResponseSuccess(REQ_CODE_VIDEO_LINK, response);
                                }else {
                                    CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                    if (TextUtils.isEmpty(resp.getError())) {
                                        //  attachedFileType = TYPE_VIDEO;
                                        videoDetail = resp.getResult().getVideo();
                                        //   updateMenuItem(videoDetail.getVideoId());
                                        onResponseSuccess(REQ_CODE_VIDEO_LINK, response);
                                    } else {
                                        Util.showSnackbar(etBody, "This field can't be empty");
                                        showEdittextDialog(videoUrl);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            CustomLog.e(e);
                        }

                        // dialog.dismiss();
                        return true;
                    };
                    // requestHandler = new HttpRequestHandler(this, new Handler(callback));
                    //requestHandler.execute(request);
                    new HttpRequestHandler(activity, new Handler(callback)).run(request);

                } catch (Exception e) {
                    hideBaseLoader();
                }
            }
        } catch (Exception e) {
            hideBaseLoader();
            CustomLog.e(e);
        }
    }

    private static final String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA};
    private static final String[] mediaColumns = {MediaStore.Video.Media._ID};

    public String getThumbnailPathForLocalFile(Activity context, Uri fileUri) {
        try {
            // Uri fileUri = getImageContentUri(context, new File(filePath));// Uri.parse(new File(filePath).toString());
            long fileId = getFileId(activity, fileUri);

            MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),
                    fileId, MediaStore.Video.Thumbnails.MICRO_KIND, null);

            Cursor thumbCursor;


            thumbCursor = context.managedQuery(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + " = "
                            + fileId, null, null);

            if (thumbCursor.moveToFirst()) {
                String thumbPath = thumbCursor.getString(thumbCursor
                        .getColumnIndex(MediaStore.Video.Thumbnails.DATA));

                return thumbPath;
            }

        } catch (Exception ignored) {
        }

        return null;
    }

    private static long getFileId(Activity context, Uri fileUri) {

        try {
            Cursor cursor = context.getContentResolver().query(fileUri, mediaColumns, null, null,
                    null);

            if (Objects.requireNonNull(cursor).moveToFirst()) {
                int columnIndex = cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                int id = cursor.getInt(columnIndex);

                return id;
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }

        return 0;
    }

}

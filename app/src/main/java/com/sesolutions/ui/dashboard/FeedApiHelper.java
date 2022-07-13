package com.sesolutions.ui.dashboard;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.camerahelper.CameraActivity;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.http.ParserCallbackInterface;
import com.sesolutions.responses.CommonResponse;
import com.sesolutions.responses.Links;
import com.sesolutions.responses.Video;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

/**
 * Created by root on 21/11/17.
 */

public abstract class FeedApiHelper extends HomeFragment implements ParserCallbackInterface {
    private static final int REQ_CODE_LINK = 1;
    static final int REQ_CODE_VIDEO = 2;
    static final int REQ_CODE_IMAGE = 3;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 9099;
    AppCompatEditText etBody;
    private static final int CAMERA_PIC_REQUEST = 7078;

    // --Commented out by Inspection (23-08-2018 20:55):public boolean isFileAttached;
    private boolean isCameraOptionSelected;
    private String imageFilePath;
    // --Commented out by Inspection (23-08-2018 20:55):public int attachedFileType;
    Video videoDetail;
    private Links linkDetail;
    // --Commented out by Inspection (23-08-2018 20:55):public String links;
    private final int MAX_COUNT = 10;
    private ArrayList<String> selectedImageList;



    void showVideoSourceDialog(String msg) {
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
            ((AppCompatButton) progressDialog.findViewById(R.id.bCamera)).setText(Constant.TXT_YOU_TUBE);
            progressDialog.findViewById(R.id.bCamera).setOnClickListener(v -> {
                progressDialog.dismiss();
                showEdittextDialog(Constant.EMPTY);
                // takeImageFromCamera();
            });
            ((AppCompatButton) progressDialog.findViewById(R.id.bGallary)).setText(Constant.TXT_VIMEO);
            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> {
                progressDialog.dismiss();
                showEdittextDialog(Constant.EMPTY);
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
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(Constant.MSG_ENTER_VIDEO_URL)
                // .setMessage("What do you want to do next?")
                .setView(taskEditText)
                .setPositiveButton(Constant.TXT_ADD, (dialog1, which) -> {
                    String videoUrl = String.valueOf(taskEditText.getText());
                    CustomLog.e("value", videoUrl);
                    callAttachVideoApi(videoUrl);
                })
                //  .setNegativeButton("Can, null)
                .create();
        dialog.show();
    }


    void showImageDialog(String msg) {
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
                askForPermission(Manifest.permission.CAMERA);
                // takeImageFromCamera();
            });
            progressDialog.findViewById(R.id.bGallary).setOnClickListener(v -> {
                progressDialog.dismiss();
                isCameraOptionSelected = false;
                askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                //showImageChooser();
            });
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    protected void showImageChooser() {
        FilePickerBuilder.getInstance()
                .setMaxCount(MAX_COUNT)
                .setSelectedFiles(selectedImageList != null ? selectedImageList : new ArrayList<>())
                .setActivityTheme(R.style.FilePickerTheme)
                .showFolderView(true)
                .enableImagePicker(true)
                .enableVideoPicker(false)
                .pickPhoto(this);
    }

    private void takeImageFromCamera() {
        String imagePath = Util.getAppFolderPath();
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
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    }


    private void askForPermission(String permission) {
        try {
            new TedPermission(context)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(Constant.MSG_PERMISSION_DENIED)
                    .setPermissions(permission, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    private final boolean isVideoSelected = false;
    private final PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // Toast.makeText(How_It_Works_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            try {
                if (isCameraOptionSelected) {
                    takeImageFromCamera();
                } else if (isVideoSelected) {
                    fetchVideo();
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

    private void fetchVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);
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
                        onResponseSuccess(REQ_CODE_IMAGE, photoPaths);


                    }
                    break;
                case CAMERA_PIC_REQUEST:
                    if (resultCode == -1) {
                        //setImage(Constant.path);
                        List<String> photoPaths = new ArrayList<>();
                        photoPaths.add(Constant.path);
                        CustomLog.d("CAMERA_PIC_REQUEST", Constant.path);
                        imageFilePath = Constant.path;
                        onResponseSuccess(REQ_CODE_IMAGE, photoPaths);

                    }
                    break;
                case REQUEST_TAKE_GALLERY_VIDEO:
                    Uri selectedImageUri = data.getData();
                    // MEDIA GALLERY
                    //ImagePath will use to upload video
                    String selectedImagePath = getPath(selectedImageUri);
                    CustomLog.e("VIDEO_REQUEST", "" + selectedImagePath);
                    if (selectedImagePath != null) {
                        //From here can pass captured video data to other Activity/Fragmrnt

                        //or

                        //upload video to server - with this ImagePath
                        List<String> photoPaths = new ArrayList<>();
                        photoPaths.add(selectedImagePath);

                        imageFilePath = selectedImagePath;
                        onResponseSuccess(REQ_CODE_VIDEO, photoPaths);
                        //  videoView.setVideoPath(selectedImagePath);
                    } else {
                        Util.showToast(context, "Error fetching video");
                    }


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

    private void callAttachVideoApi(final String videoUrl) {
        try {
            //  new AsyncRequest(context, this, Constant.POST_REQUEST, Constant.URL_LOGIN, header, request, CODE_LOGIN, false, true, Constant.EMPTY).execute();
            if (isNetworkAvailable(context)) {

                try {
                    showBaseLoader(false);
                    //    dialog = ProgressDialog.show(ctx, Constant.PLEASE_WAIT, Constant.LOADING_ISSUES, true);
                    //     dialog.setCancelable(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_CREATE_VIDEO);

                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_URI, videoUrl);
                    request.params.put(Constant.KEY_C_TYPE, Constant.VALUE_C_TYPE);
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getToken(context));

                    request.requestMethod = HttpPost.METHOD_NAME;



                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse", "" + response);
                            if (null != response) {
                                CommonResponse resp = new Gson().fromJson(response, CommonResponse.class);
                                if (TextUtils.isEmpty(resp.getError())) {
                                    //  attachedFileType = TYPE_VIDEO;
                                    videoDetail = resp.getResult().getVideo();
                                    //   updateMenuItem(videoDetail.getVideoId());
                                    onResponseSuccess(REQ_CODE_VIDEO, response);
                                } else {
                                    Util.showSnackbar(etBody, resp.getErrorMessage());
                                    showEdittextDialog(videoUrl);
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



    private static long getFileId(Activity context, Uri fileUri) {

        Cursor cursor = context.managedQuery(fileUri, mediaColumns, null, null,
                null);

        if (cursor.moveToFirst()) {
            int columnIndex = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int id = cursor.getInt(columnIndex);

            return id;
        }

        return 0;
    }
}

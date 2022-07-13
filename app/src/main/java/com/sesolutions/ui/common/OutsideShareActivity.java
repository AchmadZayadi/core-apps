package com.sesolutions.ui.common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.loader.content.CursorLoader;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sesolutions.R;
import com.sesolutions.http.HttpRequestHandler;
import com.sesolutions.http.HttpRequestVO;
import com.sesolutions.ui.dashboard.PostFeedFragment;
import com.sesolutions.ui.dashboard.composervo.ComposerOption;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.MediaUtils;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.http.client.methods.HttpPost;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

/**
 * Created by root on 12/12/17.
 */

public class OutsideShareActivity extends BaseActivity {
    public static final int SHARING_URL = 16;
    public static final int SHARING_TEXT = 17;
    public static final int SHARING_IMAGE = 18;
    public static final int SHARING_IMAGE_MULTIPLE = 19;

    public static int SHARING;
    private ComposerOption composerOption;
    public String sharingText;
    public List<String> mSelectPath;


    public void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_welcome);

        mSelectPath = new ArrayList<>();
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        //   Toast.makeText(this,"act: "+action,Toast.LENGTH_SHORT).show();
        //   Toast.makeText(this,"type: "+type,Toast.LENGTH_SHORT).show();

        /**
         * Check if user is logged-out in case of External share
         * Redirect to login first and send the intent data to Login Activity
         * After successful login load the intent data here
         */
        if (!SPref.getInstance().isLoggedIn(this)) {
            Intent loginIntent = new Intent(this, SplashAnimatedActivity.class);
            loginIntent.putExtras(intent.getExtras());
            loginIntent.setAction(action);
            loginIntent.setType(type);
            finish();
            startActivity(loginIntent);
        } else if (action != null && type != null) {
            switch (action) {
                case Intent.ACTION_SEND:
                    if (type.equals("text/plain")) {
                        handleSendText(intent); // Handle text being sent
                        callComposerOptionApi();
                    } else if (type.startsWith("image/")) {
                        isSingleImage = true;
                        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                        //    Toast.makeText(this,"Begining..",Toast.LENGTH_SHORT).show();
                        /*if (!mAppConst.checkManifestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            mAppConst.requestForManifestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    ConstantVariables.READ_EXTERNAL_STORAGE);
                        } else {
                            handleSendImage(intent); // Handle single image being sent
                        }*/
                    }
                    break;
                case Intent.ACTION_SEND_MULTIPLE:
                    isSingleImage = false;
                    if (type.startsWith("image/") || type.startsWith("*/*")) {
                        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
                        //Toast.makeText(this,"Begining..",Toast.LENGTH_SHORT).show();
                       /*if (!mAppConst.checkManifestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            mAppConst.requestForManifestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    ConstantVariables.READ_EXTERNAL_STORAGE);
                         } else {
                            handleSendMultipleImages(intent); // Handle multiple images being sent
                        }*/
                    }
                    break;
            }
        }

        //   callComposerOptionApi();


    }

    public void askForPermission(String permission) {
        try {
            new TedPermission(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage(Constant.MSG_PERMISSION_DENIED)
                    .setPermissions(permission, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

  /*  @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        CustomLog.e("onSaveInstanceState", "onSaveInstanceState");
        super.onSaveInstanceState(outState, outPersistentState);
    }*/

    private boolean isSingleImage;
    public PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            // Toast.makeText(How_It_Works_Activity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            try {
                if (isSingleImage) {
                    handleSendImage(); // Handle single image being sent
                } else {
                    handleSendMultipleImages(); // Handle multiple images being sent
                }

            } catch (Exception e) {
                CustomLog.e(e);
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (sharedText != null) {
            // Update UI to reflect text being shared
            Matcher m = Patterns.WEB_URL.matcher(sharedText);
            if (m.find()) {
                SHARING = SHARING_URL;
                sharingText = m.group();
                // attachLink(sharingText);
            } else {
                SHARING = SHARING_TEXT;
                sharingText = sharedText;
            }
        }
    }


    public void callComposerOptionApi() {

        try {
            if (isNetworkAvailable(this)) {
                try {
                    showBaseLoader(true);
                    HttpRequestVO request = new HttpRequestVO(Constant.URL_COMPOSE_OPTION);

                    request.params.put(Constant.KEY_LIMIT, Constant.RECYCLE_ITEM_THRESHOLD);
                    request.headres.put(Constant.KEY_COOKIE, getCookie());
                    request.params.put(Constant.KEY_AUTH_TOKEN, SPref.getInstance().getUserMasterDetail(this).getAuthToken());

                    request.requestMethod = HttpPost.METHOD_NAME;

                    Handler.Callback callback = msg -> {
                        hideBaseLoader();
                        try {
                            String response = (String) msg.obj;
                            CustomLog.e("repsonse23", "" + response);
                            if (response != null) {
                                composerOption = new Gson().fromJson(response, ComposerOption.class);
                                SPref.getInstance().saveTextColorString(OutsideShareActivity.this, composerOption.getResult().getTextStringColor());
                                openPostFeed();
                            } else {
                                finish();
                            }

                        } catch (Exception e) {
                            CustomLog.e(e);
                        }
                        // dialog.dismiss();
                        return true;
                    };
                    new HttpRequestHandler(this, new Handler(callback)).run(request);

                } catch (Exception e) {
                    //hideBaseLoader();

                }

            } else {
                Util.showToast(this, Constant.MSG_NO_INTERNET);
            }

        } catch (Exception e) {
            CustomLog.e(e);
            //hideBaseLoader();
        }
    }

    public void handleSendImage() {
        Intent intent = getIntent();
        SHARING = SHARING_IMAGE;
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            InputStream inputStream = null;
            try {
                //   inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = getCorrectlyOrientedImage(this,imageUri);
                //    int width=bitmap.getWidth();
                //     int height=bitmap.getHeight();
                final int REQUIRED_SIZE = 900; // Is this kilobites? 306
                Bitmap bMapScaled=null;
                try {
                    int sizelent=      byteSizeOf(bitmap);
                    int mbfile=sizelent/10000000;
                    if(mbfile<10){
                        bMapScaled=bitmap;
                        Log.e("BELOW","BELOW");
                    }else {
                        bMapScaled = Bitmap.createScaledBitmap(bitmap, 350, 350, true);
                        Log.e("LARGE","LARGE");
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                    bMapScaled = Bitmap.createScaledBitmap(bitmap, 350, 350, true);
                    Log.e("LARGE","LARGE");
                }
                // Method to get the uri from bitmap
                Uri tempUri = getImageUri(this, bMapScaled);

                // get real path from uri and add it to mSelectPath
                if (tempUri != null) {
                    //  String s = getRealPathFromURI(tempUri);
                    File file = MediaUtils.getRealPath(this, tempUri);
                    // Toast.makeText(this,"File path "+file.getPath(),Toast.LENGTH_SHORT).show();
                    mSelectPath.add(file.getAbsolutePath());
                }

            } catch (Exception e) {
                CustomLog.e(e);
                //   Toast.makeText(this,"Error 1 "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }

        callComposerOptionApi();
    }
    public void handleSendMultipleImages() {
        Intent intent = getIntent();
        SHARING = SHARING_IMAGE_MULTIPLE;
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            for (int i = 0; i < imageUris.size(); i++) {
                InputStream inputStream = null;
                try {
                    //  inputStream = getContentResolver().openInputStream(imageUris.get(i));
                    Bitmap bitmap = getCorrectlyOrientedImage(this,imageUris.get(i));
                    final int REQUIRED_SIZE = 900; // Is this kilobites? 306

                    // Find the correct scale value. It should be the power of 2.
                    int width_tmp = bitmap.getWidth(), height_tmp =bitmap.getHeight();
                    Bitmap bMapScaled=null;
                    int mbfile=0;
                    try {
                        int sizelent=      byteSizeOf(bitmap);
                        mbfile=sizelent/10000000;
                        Log.e("HEIFILESIZE",""+mbfile);
                        if(mbfile<10){
                            bMapScaled=bitmap;
                            // bMapScaled = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth()*0.75), (int) (bitmap.getHeight()*0.75), true);
                        }else {
                            bMapScaled = Bitmap.createScaledBitmap(bitmap, 350, 350, true);
                            Log.e("LARGE","LARGE");
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                        bMapScaled = Bitmap.createScaledBitmap(bitmap, 350, 350, true);
                        Log.e("LARGE","LARGE");
                    }
                    // Method to get the uri from bitmap
                    Uri tempUri = getImageUri(this, bMapScaled);
//                    Bitmap newbit=getCorrectlyOrientedImage(this,tempUri2);
                    //                  Uri tempUri = getImageUri(this, newbit);


                    // get real path from uri and add it to mSelectPath
                    if (tempUri != null) {
                        File file = MediaUtils.getRealPath(this, tempUri);
                        //  Toast.makeText(this,"File path "+file.getPath(),Toast.LENGTH_SHORT).show();

                        mSelectPath.add(file.getPath());
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    //    Toast.makeText(this,"Error 2"+e.getMessage(),Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    CustomLog.e(e);
                    //     Toast.makeText(this,"Error 3"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }
        callComposerOptionApi();
    }

    public  int getOrientation(Context context, Uri photoUri) {
        /* it's on the external media. */
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if (cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    int MAX_IMAGE_DIMENSION=2545;

    public  Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(photoUri);
        BitmapFactory.Options dbo = new BitmapFactory.Options();
        dbo.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, dbo);
        is.close();

        int rotatedWidth, rotatedHeight;
        int orientation = getOrientation(context, photoUri);

        Log.e("ORIENTION",""+orientation);
        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight;
            rotatedHeight = dbo.outWidth;
        } else {
            rotatedWidth = dbo.outWidth;
            rotatedHeight = dbo.outHeight;
        }

        Bitmap srcBitmap;
        is = context.getContentResolver().openInputStream(photoUri);
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
            float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
            float maxRatio = Math.max(widthRatio, heightRatio);

            // Create the bitmap from file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = (int) maxRatio;
            srcBitmap = BitmapFactory.decodeStream(is, null, options);
        } else {
            srcBitmap = BitmapFactory.decodeStream(is);
        }
        is.close();

        /*
         * if the orientation is not 0 (or -1, which means we don't know), we
         * have to do a rotation.
         */
        if (orientation > 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);

            srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }

        return srcBitmap;
    }

    public static Bitmap rotateImage11(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }


    // Get image from bitmap for real path
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (inImage != null) {
            inImage.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
            String path = MediaStore.Images.Media.insertImage(
                    inContext.getContentResolver(), inImage, "IMG_"+ Calendar.getInstance().getTime(), null);
            return Uri.parse(path);
        } else {
            return null;
        }
    }

   /* public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    //    String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, **"IMG_" + Calendar.getInstance().getTime(),** null);
        String path = MediaStore.Images.Media.insertImage(
                inContext.getContentResolver(), inImage, "IMG_"+ Calendar.getInstance().getTime(), null);

        return Uri.parse(path);
    }*/


    /**
     * Method to retrieve the path of an image URI
     *
     * @param contentUri uri of image.
     * @return returns the real path of image.
     */
    private String getRealPathFromURI(Uri contentUri) {
        String result;
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        if (cursor == null) {
            result = contentUri.getPath();
        } else {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
            cursor.close();
        }
        return result;
    }


    private void openPostFeed() {
        //-2 means sharing from OUTSIDE
      /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {*/
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, PostFeedFragment.newInstance(composerOption, -2)).addToBackStack(null).commit();
           /* }
        }, 300);*/

    }

}

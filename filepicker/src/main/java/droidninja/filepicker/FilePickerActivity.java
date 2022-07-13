package droidninja.filepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import droidninja.filepicker.fragments.DocFragment;
import droidninja.filepicker.fragments.DocPickerFragment;
import droidninja.filepicker.fragments.MediaPickerFragment;
import droidninja.filepicker.fragments.PhotoPickerFragmentListener;
import droidninja.filepicker.utils.FragmentUtil;
import droidninja.filepicker.utils.ImageCaptureManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FilePickerActivity extends BaseFilePickerActivity
        implements PhotoPickerFragmentListener, DocFragment.DocFragmentListener,
        DocPickerFragment.DocPickerFragmentListener, MediaPickerFragment.MediaPickerFragmentListener {

    private static final String TAG = FilePickerActivity.class.getSimpleName();
    public static final int REQUEST_TAKE_VIDEO = 999;
    private int type;
    private ImageCaptureManager imageCaptureManager;
    public static boolean isVideoCamera = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_file_picker);
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<String> selectedPaths =
                    intent.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
            type = intent.getIntExtra(FilePickerConst.EXTRA_PICKER_TYPE, FilePickerConst.MEDIA_PICKER);

            if (selectedPaths != null) {

                if (PickerManager.getInstance().getMaxCount() == 1) {
                    selectedPaths.clear();
                }

                PickerManager.getInstance().clearSelections();
                if (type == FilePickerConst.MEDIA_PICKER) {
                    PickerManager.getInstance().add(selectedPaths, FilePickerConst.FILE_TYPE_MEDIA);
                } else {
                    PickerManager.getInstance().add(selectedPaths, FilePickerConst.FILE_TYPE_DOCUMENT);
                }
            } else {
                selectedPaths = new ArrayList<>();
            }

            setToolbarTitle(PickerManager.getInstance().getCurrentCount());
            openSpecificFragment(type, selectedPaths);
            imageCaptureManager = new ImageCaptureManager(this);
        }
    }

    private void setToolbarTitle(int count) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            int maxCount = PickerManager.getInstance().getMaxCount();
            if (maxCount == -1 && count > 0) {
                actionBar.setTitle(String.format(getString(R.string.attachments_num), count));
            } else if (maxCount > 0 && count > 0) {
                actionBar.setTitle(
                        String.format(getString(R.string.attachments_title_text), count, maxCount));
            } else if (!TextUtils.isEmpty(PickerManager.getInstance().getTitle())) {
                actionBar.setTitle(PickerManager.getInstance().getTitle());
            } else {
                if (type == FilePickerConst.MEDIA_PICKER) {
                    actionBar.setTitle(R.string.select_photo_text);
                } else {
                    actionBar.setTitle(R.string.select_doc_text);
                }
            }
        }
    }

    private void openSpecificFragment(int type, @Nullable ArrayList<String> selectedPaths) {
        if (type == FilePickerConst.MEDIA_PICKER) {
            MediaPickerFragment photoFragment = MediaPickerFragment.newInstance();
            FragmentUtil.addFragment(this, R.id.container, photoFragment);
        } else {
            if (PickerManager.getInstance().isDocSupport())
                PickerManager.getInstance().addDocTypes();

            DocPickerFragment photoFragment = DocPickerFragment.newInstance();
            FragmentUtil.addFragment(this, R.id.container, photoFragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picker_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_done);
        if (menuItem != null) {
            if (PickerManager.getInstance().getMaxCount() == 1) {
                menuItem.setVisible(false);
            } else {
                menuItem.setVisible(true);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_done) {
            if (type == FilePickerConst.MEDIA_PICKER) {
                returnData(PickerManager.getInstance().getSelectedPhotos());
            } else {
                returnData(PickerManager.getInstance().getSelectedFiles());
            }

            return true;
        } else if (i == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (i == R.id.action_camera)
            askForPermission();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PickerManager.getInstance().reset();
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FilePickerConst.REQUEST_CODE_MEDIA_DETAIL:
                if (resultCode == Activity.RESULT_OK) {
                    if (type == FilePickerConst.MEDIA_PICKER) {
                        returnData(PickerManager.getInstance().getSelectedPhotos());
                    } else {
                        returnData(PickerManager.getInstance().getSelectedFiles());
                    }
                } else {
                    setToolbarTitle(PickerManager.getInstance().getCurrentCount());
                }
                break;
            case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    String imagePath = imageCaptureManager.notifyMediaStoreDatabase();
                    ArrayList<String> path = new ArrayList<>();
                    path.add(imagePath);
                    setResult(RESULT_OK, new Intent().putStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA, path));
                    finish();
                }
                break;
            case REQUEST_TAKE_VIDEO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri contentURI = data.getData();
                    String recordedVideoPath = getPath(contentURI);
                    Log.e("recordedVideoPath", recordedVideoPath);
                    saveVideoToInternalStorage(recordedVideoPath);
                    ArrayList<String> path = new ArrayList<>();
                    path.add(recordedVideoPath);
                    setResult(RESULT_OK, new Intent().putStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA, path));
                    finish();
                }
                break;
        }
    }

    private void saveVideoToInternalStorage(String filePath) {

        File newfile;

        try {

            File currentFile = new File(filePath);
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + "SNS");
            newfile = new File(wallpaperDirectory, "MP4_" + System.currentTimeMillis() + ".mp4");

            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }

            if (currentFile.exists()) {

                InputStream in = new FileInputStream(currentFile);
                OutputStream out = new FileOutputStream(newfile);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.e("vii", "Video file saved successfully.");
            } else {
                Log.e("vii", "Video saving failed. Source file missing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
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

    private void returnData(ArrayList<String> paths) {
        Intent intent = new Intent();
        if (type == FilePickerConst.MEDIA_PICKER) {
            intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA, paths);
        } else {
            intent.putStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS, paths);
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemSelected() {
        int currentCount = PickerManager.getInstance().getCurrentCount();
        setToolbarTitle(currentCount);

        if (PickerManager.getInstance().getMaxCount() == 1 && currentCount == 1) {
            returnData(
                    type == FilePickerConst.MEDIA_PICKER ? PickerManager.getInstance().getSelectedPhotos()
                            : PickerManager.getInstance().getSelectedFiles());
        }
    }

    private void askForPermission() {
        new TedPermission(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        try {
                            if (isVideoCamera) {
                                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                if (takeVideoIntent.resolveActivity(getPackageManager()) != null)
                                    startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
                            } else {
                                Intent intent = imageCaptureManager.dispatchTakePictureIntent(getApplicationContext());
                                if (intent != null) {
                                    startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.no_camera_exists, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage(getString(R.string.permission_filepicker_camera))
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }
}

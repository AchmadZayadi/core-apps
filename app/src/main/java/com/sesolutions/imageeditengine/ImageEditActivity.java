package com.sesolutions.imageeditengine;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import android.view.ViewGroup;
import android.widget.TextView;

import com.droidninja.imageeditengine.BaseFrag;
import com.droidninja.imageeditengine.Constants;
import com.droidninja.imageeditengine.utils.FragmentUtil;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.CustomLog;


public class ImageEditActivity extends BaseImageEditActivity
        implements PhotoEditorFragment.OnFragmentInteractionListener, OnUserClickedListener<Integer, Object>,
        CropFragment.OnFragmentInteractionListener {
    private Rect cropRect;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.droidninja.imageeditengine.R.layout.activity_image_edit);

        String imagePath = getIntent().getStringExtra(ImageEditor.EXTRA_IMAGE_PATH);
        if (imagePath != null) {
            FragmentUtil.replaceFragment(this, R.id.container,
                    PhotoEditorFragment.newInstance(imagePath));
        }
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
            } else {
                showDialog(getString(R.string.msg_quote_go_back), R.string.YES, R.string.NO);
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
        switch (eventType) {
            case Constants.Events.DONE:
                onDoneClicked((String) data);
                break;
            case Constants.Events.TASK:
                if (Constants.TASK_CROP == position) {
                    onCropClicked((Bitmap) data);
                } else {
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
                }
                break;
        }
        return false;
    }
}

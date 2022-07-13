package com.sesolutions.imageeditengine;

import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sesolutions.ui.common.BaseActivity;
import com.sesolutions.utils.SesColorUtils;

public abstract class BaseImageEditActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static int[] getBitmapOffset(ImageView img, Boolean includeLayout) {
        int[] offset = new int[2];
        float[] values = new float[9];

        Matrix m = img.getImageMatrix();
        m.getValues(values);

        offset[0] = (int) values[5];
        offset[1] = (int) values[2];

        if (includeLayout) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) img.getLayoutParams();
            int paddingTop = (int) (img.getPaddingTop());
            int paddingLeft = (int) (img.getPaddingLeft());

            offset[0] += paddingTop + lp.topMargin;
            offset[1] += paddingLeft + lp.leftMargin;
        }
        return offset;
    }

    @Override
    protected void onStart() {
        super.onStart();
        setStatusBarColor(Color.BLACK);
    }

    @Override
    protected void onStop() {
        setStatusBarColor(SesColorUtils.getPrimaryDarkColor(this));
        super.onStop();
    }
}

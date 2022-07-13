package com.sesolutions.utils;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

public class SesColorUtils {

    public static int getAppBgColor(Context context) {
        return Color.parseColor(Constant.backgroundColor);
    }

    public static int getText2Color(Context context) {
        return Color.parseColor(Constant.text_color_2);
    }

    public static int getText1Color(Context context) {
        return Color.parseColor(Constant.text_color_1);
    }

    public static int getForegroundColor(Context context) {
        return Color.parseColor(Constant.foregroundColor);
    }

    public static int getNavigationTitleColor(Context context) {
        return Color.parseColor(Constant.navigationTitleColor);
    }

    public static int getPrimaryColor(Context context) {
        return Color.parseColor(Constant.colorPrimary);
    }

    public static int getPrimaryDarkColor(Context context) {
        return Util.manipulateColor(getPrimaryColor(context));//Color.parseColor(Constant.colorPrimaryDark);
    }

    public static int getColor(Context context, @ColorRes final int color) {
        return ContextCompat.getColor(context, color);
    }

    public static int getReviewTextColor(Context context, boolean b) {
        return Color.parseColor(b ? "#ffffff" : Constant.text_color_2);
    }

    public static int getCrowdTextColor(Context context, boolean expired) {
        return Color.parseColor(expired ? Constant.red : Constant.colorPrimary);
    }
}

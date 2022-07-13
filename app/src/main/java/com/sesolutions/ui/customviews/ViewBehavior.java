package com.sesolutions.ui.customviews;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sesolutions.R;

/**
 * Created by root on 2/1/18.
 */
public class ViewBehavior extends CoordinatorLayout.Behavior<HeaderView> {

    private static final float MAX_SCALE = 0.5f;

    private Context mContext;

    private int mStartMarginLeftTitle;
    private int mStartMarginLeftSubTitle;
    private int mEndMargintLeft;
    private int mMarginRight;
    private int mStartMarginBottom;
    private boolean isHide;

    public ViewBehavior(Context context, AttributeSet attrs) {
        mContext = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, HeaderView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, HeaderView child, View dependency) {
        shouldInitProperties(child, dependency);

        int maxScroll = ((AppBarLayout) dependency).getTotalScrollRange();
        float percentage = Math.abs(dependency.getY()) / (float) maxScroll;

        // Set scale for the title
        float size = ((1 - percentage) * MAX_SCALE) + 1;
        child.setScaleXTitle(size);
        child.setScaleYTitle(size);

        // Set position for the header view
        float childPosition = dependency.getHeight()
                + dependency.getY()
                - child.getHeight()
                - (getToolbarHeight() - child.getHeight()) * percentage / 2;

        childPosition = childPosition - mStartMarginBottom * (1f - percentage);
        child.setY(childPosition);

        // Set Margin for title
        RelativeLayout.LayoutParams lpTitle = (RelativeLayout.LayoutParams) child.getTitle().getLayoutParams();
        lpTitle.leftMargin = (int) ((mStartMarginLeftTitle) - (percentage * (mStartMarginLeftTitle - mEndMargintLeft)));

        if (lpTitle.leftMargin < 20) {
            lpTitle.leftMargin = 20;
        }
        lpTitle.rightMargin = mMarginRight;
        child.getTitle().setLayoutParams(lpTitle);

        // Set Margin for subtitle
        RelativeLayout.LayoutParams lpSubTitle = (RelativeLayout.LayoutParams) child.getSubTitle().getLayoutParams();
        lpSubTitle.leftMargin = (int) ((mStartMarginLeftSubTitle) - (percentage * (mStartMarginLeftSubTitle - mEndMargintLeft)));

        if (lpSubTitle.leftMargin < 20) {
            lpSubTitle.leftMargin = 20;
        }
        lpSubTitle.rightMargin = mMarginRight;
        child.getSubTitle().setLayoutParams(lpSubTitle);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (isHide && percentage < 1) {
                child.setVisibility(View.VISIBLE);
                isHide = false;
            } else if (!isHide && percentage == 1) {
                child.setVisibility(View.GONE);
                isHide = true;
            }
        }
        return true;
    }

    private void shouldInitProperties(HeaderView child, View dependency) {

        if (mStartMarginLeftTitle == 0)
            mStartMarginLeftTitle = getStartMarginLeftTitle(child);

        if (mStartMarginLeftSubTitle == 0)
            mStartMarginLeftSubTitle = getStartMarginLeftSubTitle(child);

        if (mEndMargintLeft == 0)
            mEndMargintLeft = mContext.getResources().getDimensionPixelOffset(R.dimen.height_toolbar);

        if (mStartMarginBottom == 0)
            mStartMarginBottom = mContext.getResources().getDimensionPixelOffset(R.dimen.margin_full);

        if (mMarginRight == 0)
            mMarginRight = mContext.getResources().getDimensionPixelOffset(R.dimen.margin_full);
    }

    public int getStartMarginLeftTitle(HeaderView headerView) {
        TextView title = headerView.getTitle();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        int stringWidth = getStingWidth(title);

        int marginLeft = (int) ((width / 2) - ((stringWidth + (stringWidth * MAX_SCALE)) / 2));
        return marginLeft;
    }

    public int getStartMarginLeftSubTitle(HeaderView headerView) {
        TextView subTitle = headerView.getSubTitle();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        int stringWidth = getStingWidth(subTitle);

        int marginLeft = ((width / 2) - (stringWidth / 2));
        return marginLeft;
    }

    public int getStingWidth(TextView textView) {
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(textView.getText().toString(), 0, textView.getText().toString().length(), bounds);
        return bounds.width();
    }

    public int getToolbarHeight() {
        int result = 0;
        TypedValue tv = new TypedValue();
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            result = TypedValue.complexToDimensionPixelSize(tv.data, mContext.getResources().getDisplayMetrics());
        }
        return result;
    }
}
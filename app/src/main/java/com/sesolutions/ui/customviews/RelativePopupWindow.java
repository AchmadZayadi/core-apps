package com.sesolutions.ui.customviews;

/**
 * Created by WarFly on 12/31/2017.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.core.widget.PopupWindowCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author kakajika
 * @since 2016/07/01
 */
public class RelativePopupWindow extends PopupWindow {

    @IntDef({
            RelativePopupWindow.VerticalPosition.CENTER,
            RelativePopupWindow.VerticalPosition.ABOVE,
            RelativePopupWindow.VerticalPosition.BELOW,
            RelativePopupWindow.VerticalPosition.ALIGN_TOP,
            RelativePopupWindow.VerticalPosition.ALIGN_BOTTOM,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface VerticalPosition {
        int CENTER = 0;
        int ABOVE = 1;
        int BELOW = 2;
        int ALIGN_TOP = 3;
        int ALIGN_BOTTOM = 4;
    }

    @IntDef({
            RelativePopupWindow.HorizontalPosition.CENTER,
            RelativePopupWindow.HorizontalPosition.LEFT,
            RelativePopupWindow.HorizontalPosition.RIGHT,
            RelativePopupWindow.HorizontalPosition.ALIGN_LEFT,
            RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface HorizontalPosition {
        int CENTER = 0;
        int LEFT = 1;
        int RIGHT = 2;
        int ALIGN_LEFT = 3;
        int ALIGN_RIGHT = 4;
    }

    public RelativePopupWindow(Context context) {
        super(context);
    }

    public RelativePopupWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativePopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RelativePopupWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public RelativePopupWindow() {
        super();
    }

    public RelativePopupWindow(View contentView) {
        super(contentView);
    }

    public RelativePopupWindow(int width, int height) {
        super(width, height);
    }

    public RelativePopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public RelativePopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    /**
     * Show at relative position to anchor View.
     * @param anchor Anchor View
     * @param vertPos Vertical Position Flag
     * @param horizPos Horizontal Position Flag
     */
    public void showOnAnchor(@NonNull View anchor, @RelativePopupWindow.VerticalPosition int vertPos, @RelativePopupWindow.HorizontalPosition int horizPos) {
        showOnAnchor(anchor, vertPos, horizPos, 0, 0);
    }

    /**
     * Show at relative position to anchor View.
     * @param anchor Anchor View
     * @param vertPos Vertical Position Flag
     * @param horizPos Horizontal Position Flag
     * @param fitInScreen Automatically fit in screen or not
     */
    public void showOnAnchor(@NonNull View anchor, @RelativePopupWindow.VerticalPosition int vertPos, @RelativePopupWindow.HorizontalPosition int horizPos, boolean fitInScreen) {
        showOnAnchor(anchor, vertPos, horizPos, 0, 0, fitInScreen);
    }

    /**
     * Show at relative position to anchor View with translation.
     * @param anchor Anchor View
     * @param vertPos Vertical Position Flag
     * @param horizPos Horizontal Position Flag
     * @param x Translation X
     * @param y Translation Y
     */
    public void showOnAnchor(@NonNull View anchor, @RelativePopupWindow.VerticalPosition int vertPos, @RelativePopupWindow.HorizontalPosition int horizPos, int x, int y) {
        showOnAnchor(anchor, vertPos, horizPos, x, y, true);
    }

    /**
     * Show at relative position to anchor View with translation.
     * @param anchor Anchor View
     * @param vertPos Vertical Position Flag
     * @param horizPos Horizontal Position Flag
     * @param x Translation X
     * @param y Translation Y
     * @param fitInScreen Automatically fit in screen or not
     */
    public void showOnAnchor(@NonNull View anchor, @RelativePopupWindow.VerticalPosition int vertPos, @RelativePopupWindow.HorizontalPosition int horizPos, int x, int y, boolean fitInScreen) {
        setClippingEnabled(fitInScreen);
        View contentView = getContentView();
        contentView.measure(makeDropDownMeasureSpec(getWidth()), makeDropDownMeasureSpec(getHeight()));
        final int measuredW = contentView.getMeasuredWidth();
        final int measuredH = contentView.getMeasuredHeight();
        if (!fitInScreen) {
            final int[] anchorLocation = new int[2];
            anchor.getLocationInWindow(anchorLocation);
            x += anchorLocation[0];
            y += anchorLocation[1] + anchor.getHeight();
        }
        switch (vertPos) {
            case RelativePopupWindow.VerticalPosition.ABOVE:
                y -= measuredH + anchor.getHeight();
                break;
            case RelativePopupWindow.VerticalPosition.ALIGN_BOTTOM:
                y -= measuredH;
                break;
            case RelativePopupWindow.VerticalPosition.CENTER:
                y -= anchor.getHeight()/2 + measuredH/2;
                break;
            case RelativePopupWindow.VerticalPosition.ALIGN_TOP:
                y -= anchor.getHeight();
                break;
            case RelativePopupWindow.VerticalPosition.BELOW:
                // Default position.
                break;
        }
        switch (horizPos) {
            case RelativePopupWindow.HorizontalPosition.LEFT:
                x -= measuredW;
                break;
            case RelativePopupWindow.HorizontalPosition.ALIGN_RIGHT:
                x -= measuredW - anchor.getWidth();
                break;
            case RelativePopupWindow.HorizontalPosition.CENTER:
                x += anchor.getWidth()/2 - measuredW/2;
                break;
            case RelativePopupWindow.HorizontalPosition.ALIGN_LEFT:
                // Default position.
                break;
            case RelativePopupWindow.HorizontalPosition.RIGHT:
                x += anchor.getWidth();
                break;
        }
        if (fitInScreen) {
            PopupWindowCompat.showAsDropDown(this, anchor, x, y, Gravity.NO_GRAVITY);
        } else {
            showAtLocation(anchor, Gravity.NO_GRAVITY, x, y);
        }
    }

    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), getDropDownMeasureSpecMode(measureSpec));
    }

    private static int getDropDownMeasureSpecMode(int measureSpec) {
        switch (measureSpec) {
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                return View.MeasureSpec.UNSPECIFIED;
            default:
                return View.MeasureSpec.EXACTLY;
        }
    }

}


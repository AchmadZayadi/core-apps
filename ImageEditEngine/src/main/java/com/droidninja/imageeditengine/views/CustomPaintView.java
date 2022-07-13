package com.droidninja.imageeditengine.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.droidninja.imageeditengine.Constants;
import com.droidninja.imageeditengine.OnUserClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by panyi on 17/2/11.
 */

public class CustomPaintView extends View implements View.OnTouchListener {
    private Paint mPaint;
    private Bitmap mDrawBit;
    private Paint mEraserPaint;

    private Canvas mPaintCanvas = null;

    private int mColor;
    private RectF bounds;

    private Path mPath;
    private List<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();

    private OnUserClick listener;

    public CustomPaintView(Context context) {
        super(context);
        init(context);
    }

    public CustomPaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomPaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomPaintView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mDrawBit == null) {
            generatorBit();
        }
    }

    private void generatorBit() {
        mDrawBit =
                Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mPaintCanvas = new Canvas(mDrawBit);
    }

    private void init(Context context) {

        mColor = Color.RED;
        bounds = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
        this.setOnTouchListener(this);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(15f);

        mEraserPaint = new Paint();
        mEraserPaint.setAlpha(0);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setDither(true);
        mEraserPaint.setStyle(Paint.Style.STROKE);
        mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
        mEraserPaint.setStrokeCap(Paint.Cap.ROUND);
        mEraserPaint.setStrokeWidth(40);

        mPath = new Path();


    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
        this.mPaint.setColor(mColor);
    }

    public void setWidth(float width) {
        this.mPaint.setStrokeWidth(width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {

            for (Path p : paths) {
                canvas.drawPath(p, mPaint);
            }
            canvas.drawPath(mPath, mPaint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        undonePaths.clear();
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mPaintCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        paths.add(mPath);
        mPath = new Path();

        listener.onItemClicked(Constants.Events.VIEW_EDITED, null, Constants.TYPE_PAINT);

    }

    public void onClickUndo() {
        if (paths.size() > 0) {
            undonePaths.add(paths.remove(paths.size() - 1));
            invalidate();
        } else {

        }
        //toast the user
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        Log.e("onTouch", x + "___" + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }




        public void onUndoPressed() {
            onClickUndo();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        if (mDrawBit != null && !mDrawBit.isRecycled()) {
            // mDrawBit.recycle();
        }
    }

   /* public void setEraser(boolean eraser) {
        this.eraser = eraser;
        mPaint.setColor(eraser ? Color.TRANSPARENT : mColor);
    }*/

    public Bitmap getPaintBit() {
        return mDrawBit;
    }

    public void reset() {
        if (mDrawBit != null && !mDrawBit.isRecycled()) {
            //mDrawBit.recycle();
        }

        generatorBit();
    }

    public void setBounds(RectF bitmapRect) {
        this.bounds = bitmapRect;
    }

    public OnUserClick getListener() {
        return listener;
    }

    public void setListener(OnUserClick listener) {
        this.listener = listener;
    }

}//end class

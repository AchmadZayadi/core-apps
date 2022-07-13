package com.sesolutions.utils;

import androidx.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.sesolutions.listeners.OnUserClickedListener;

/**
 * Created by root on 16/11/17.
 */

public class CustomClickableSpan extends ClickableSpan {
    private final int event;
    private final Object data;
    private final int position;
    private final OnUserClickedListener<Integer, Object> listener;

    public CustomClickableSpan() {
        this.event = -1;
        this.data = null;
        this.position = -1;
        this.listener = null;
    }

    public CustomClickableSpan(OnUserClickedListener<Integer, Object> listener, int event, Object data, int position) {
        this.event = event;
        this.data = data;
        this.position = position;
        this.listener = listener;
    }

    @Override
    public void onClick(@NonNull View textView) {
        if (null != listener)
            listener.onItemClicked(event, data, position);
    }

    @Override
    public void updateDrawState(TextPaint ds) {// override updateDrawState
        ds.setUnderlineText(false); // set to false to remove underline
    }
}

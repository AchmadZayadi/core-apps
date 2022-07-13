package com.sesolutions.imageeditengine;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.droidninja.imageeditengine.Constants;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.ui.common.BaseDialogFragment;
import com.sesolutions.ui.customviews.fab.FloatingActionButton;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SesColorUtils;

public class CaptionDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    private View v;

    private AppCompatEditText etCaption;

    private OnUserClickedListener<Integer, Object> listener;
    private String text;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_edittext, container, false);
        applyTheme(v);
        try {
            etCaption = v.findViewById(R.id.etCaption);
            FloatingActionButton fab = v.findViewById(R.id.fabHide);
            fab.setFabColor(SesColorUtils.getPrimaryColor(getContext()));
            fab.setFabIconColor(SesColorUtils.getNavigationTitleColor(getContext()));
            fab.setOnClickListener(this);
            etCaption.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    text = etCaption.getText().toString();
                    onDismiss();
                    return true;
                }
                return false;
            });
            if (TextUtils.isEmpty(text)) {
                text = "";
            }
            etCaption.setText(text);
            new Handler().postDelayed(() -> {
                openKeyboard();
                etCaption.requestFocus();
                etCaption.setSelection(text.length());
            }, 300);

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!TextUtils.isEmpty(text)) {
            listener.onItemClicked(Constants.Events.TASK, text, Constants.TASK_CAPTION);
            text = null;
        }
    }


    public void onDismiss() {
        closeKeyboard();
        onDismiss(getDialog());
    }

    public static CaptionDialogFragment newInstance(OnUserClickedListener<Integer, Object> listener, String text) {
        CaptionDialogFragment frag = new CaptionDialogFragment();
        frag.listener = listener;
        frag.text = text;
        return frag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabHide:
                text = etCaption.getText().toString();
                onDismiss();
                break;
        }
    }
}

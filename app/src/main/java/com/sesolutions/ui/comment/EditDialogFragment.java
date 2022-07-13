package com.sesolutions.ui.comment;


import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.comment.CommentData;
import com.sesolutions.ui.common.BaseDialogFragment;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

public class EditDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    private View v;

    private AppCompatEditText etCaption;

    private OnUserClickedListener<Integer, Object> listener;
    private String text;
    private CommentData data;
    private int listPosition;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_comment_edit, container, false);
        applyTheme(v);
        try {
            etCaption = v.findViewById(R.id.etBody);
            TextView tvUpdate = v.findViewById(R.id.tvUpdate);
            TextView tvCancel = v.findViewById(R.id.tvCancel);

            GradientDrawable gdr1 = (GradientDrawable) tvUpdate.getBackground();
            gdr1.setColor(SesColorUtils.getPrimaryColor(getContext()));
            tvUpdate.setBackground(gdr1);

           /* GradientDrawable gdr2 = (GradientDrawable) tvUpdate.getBackground();
            gdr2.setStroke(gdr2.strSesColorUtils.getPrimaryColor(getContext()));
            tvCancel.setBackground(gdr2);*/
            Util.showImageWithGlide(v.findViewById(R.id.ivProfileImage), data.getUserImage());
            tvCancel.setOnClickListener(this);
            tvUpdate.setOnClickListener(this);
           /* etCaption.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    text = etCaption.getText().toString();
                    onDismiss();
                    return true;
                }
                return false;
            });*/
            text = unecodeStr(data.getBody());
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
        closeKeyboard();
        super.onDismiss(dialog);
        if (!TextUtils.isEmpty(text)) {
            listener.onItemClicked(Constant.Events.CONTENT_EDIT, text, listPosition);
            text = null;
        }
    }


    public void onDismiss() {
        onDismiss(getDialog());
    }

    public static EditDialogFragment newInstance(OnUserClickedListener<Integer, Object> listener, int position, CommentData data) {
        EditDialogFragment frag = new EditDialogFragment();
        frag.listener = listener;
        frag.listPosition = position;
        frag.data = data;
        return frag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvUpdate:
                text = etCaption.getText().toString();
                onDismiss();
                break;
            case R.id.tvCancel:
                text = null;
                onDismiss();
                break;
        }
        // onDismiss();
    }
}

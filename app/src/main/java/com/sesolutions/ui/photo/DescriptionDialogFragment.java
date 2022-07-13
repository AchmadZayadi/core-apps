package com.sesolutions.ui.photo;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.droidninja.imageeditengine.utils.Utility;
import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

public class DescriptionDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    private View v;

    private OnUserClickedListener<Integer, Object> listener;
    private EditText etBody;
    private int isSavePressed;
    private String desc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        // activity.setTitle(title);
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_description_gallary, container, false);
        isSavePressed = 0;
        try {
            init();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(desc)) {
                        etBody.setText(desc);
                        etBody.setSelection(desc.length());
                    }
                    openKeyboard();
                    etBody.requestFocus();

                }
            }, 400);
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    public void openKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }


    private void init() {

        etBody = v.findViewById(R.id.etBody);
        v.findViewById(R.id.tvSave).setOnClickListener(this);
        v.findViewById(R.id.tvCancel).setOnClickListener(this);
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        listener.onItemClicked(Constant.Events.CONTENT_EDIT, etBody.getText().toString(), isSavePressed);
    }

    public void onDismiss() {
        onDismiss(getDialog());
    }

    public static DescriptionDialogFragment newInstance(OnUserClickedListener<Integer, Object> listener, String desc) {
        DescriptionDialogFragment frag = new DescriptionDialogFragment();
        frag.listener = listener;
        frag.desc = desc;
        return frag;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onClick(View v) {
        hideKeyboard(getActivity());
        switch (v.getId()) {
            case R.id.tvCancel:
                 onDismiss();
                 Utility.hideSoftKeyboard(getActivity());
                break;
            case R.id.tvSave:
                submitIfValid();
                break;
        }
    }

    private void submitIfValid() {
        String body = etBody.getText().toString();
        if (TextUtils.isEmpty(body)) {
            etBody.setError(null);
        } else {
            isSavePressed = 1;
            onDismiss();
        }
    }
}

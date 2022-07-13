package com.sesolutions.ui.common;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.sesolutions.R;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.Util;

import org.apache.commons.lang3.StringEscapeUtils;

public class BaseDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {


    private ProgressDialog progressDialog;

   /* @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }*/

    public void applyTheme(View v) {
        if (v != null) {
            new ThemeManager().applyTheme((ViewGroup) v, getContext());
        }
    }

    public String unecodeStr(String escapedString) {
        try {
            return StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeJava(escapedString));
        } catch (Exception e) {
            CustomLog.d("warnning", "emoji parsing error at " + escapedString);
        }

        return escapedString;
    }


    public boolean isNetworkAvailable(Context context) {
        boolean result = false;
        try {
            result = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return result;
    }

    public void showBaseLoader(boolean isCancelable) {
        try {
            progressDialog = ProgressDialog.show(getContext(), "", "", true);
            progressDialog.setCancelable(isCancelable);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_progress);
            // new showBaseLoaderAsync(context).execute();
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void notInternetMsg(View v) {
        Util.showSnackbar(v, getContext().getString(R.string.MSG_NO_INTERNET));
    }

    public String getCookie() {
        return TextUtils.isEmpty(Constant.SESSION_ID) ? SPref.getInstance().getCookie(getContext()) : Constant.SESSION_ID;
    }


    public void hideBaseLoader() {
        try {
            if (getActivity() != null && !getActivity().isFinishing() && progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void closeKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            View v = ((Activity) getContext()).getCurrentFocus();
            if (v == null) {
                return;
            }
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void closeKeyboard(View v) {
        try {
            InputMethodManager inputManager = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            // View v = ((Activity) getContext()).getCurrentFocus();
            if (v == null) {
                return;
            }
            inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void openKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    public void onDismiss() {
        onDismiss(getDialog());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
        // onDismiss();
    }
}

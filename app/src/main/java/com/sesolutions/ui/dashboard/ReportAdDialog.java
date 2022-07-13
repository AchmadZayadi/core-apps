package com.sesolutions.ui.dashboard;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.http.ApiController;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.feed.CommunityHiddenData;
import com.sesolutions.thememanager.ThemeManager;
import com.sesolutions.ui.postfeed.FeedPrivacyAdapter;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReportAdDialog extends AppCompatDialogFragment implements View.OnClickListener, OnUserClickedListener<Integer, Object> {

    private View v;
    LinearLayoutCompat llQuestions;


    // private int colorPrimary;


    private OnUserClickedListener<Integer, Object> listener;
    private int position;
    private FeedPrivacyAdapter adapter;
    private CommunityHiddenData data;
    private int mAdId;
    private String selectedOptionValue;
    private Map<String, String> optionMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_ad_hide, container, false);
        try {
            //   colorPrimary = Color.parseColor(Constant.colorPrimary);

            new ThemeManager().applyTheme((ViewGroup) v, getContext());
            ((TextView) v.findViewById(R.id.tvTitle)).setText(data.getHeading());
            ((TextView) v.findViewById(R.id.tvDialogText)).setText(data.getDescription());

            final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
            AppCompatButton bSubmit = v.findViewById(R.id.bJoin);
            bSubmit.setText(data.getSubmitButtonText());

            AppCompatRadioButton rb1 = (AppCompatRadioButton) v.findViewById(R.id.rb1);
            AppCompatRadioButton rb2 = (AppCompatRadioButton) v.findViewById(R.id.rb2);
            AppCompatRadioButton rb3 = (AppCompatRadioButton) v.findViewById(R.id.rb3);
            AppCompatRadioButton rb4 = (AppCompatRadioButton) v.findViewById(R.id.rb4);
            AppCompatRadioButton rb5 = (AppCompatRadioButton) v.findViewById(R.id.rb5);

            AppCompatEditText etOther = v.findViewById(R.id.etOther);
            etOther.setHint(data.getOtherText());


            optionMap = data.getOptions();

            rb1.setText(optionMap.get("Offensive"));
            rb2.setText(optionMap.get("Misleading"));
            rb3.setText(optionMap.get("Inappropriate"));
            rb4.setText(optionMap.get("Licensed Material"));
            rb5.setText(optionMap.get("Other"));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{
                                new int[]{Color.parseColor(Constant.menuButtonTitleColor)} //enabled
                        },
                        new int[]{Color.parseColor(Constant.outsideButtonBackgroundColor)}
                );


                rb1.setButtonTintList(colorStateList);
                rb2.setButtonTintList(colorStateList);
                rb3.setButtonTintList(colorStateList);
                rb4.setButtonTintList(colorStateList);
                rb5.setButtonTintList(colorStateList);
            }

            new Handler().postDelayed(() -> {
                radioGroup.setVisibility(View.VISIBLE);
            }, 400);
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                String value = ((AppCompatRadioButton) group.findViewById(checkedId)).getText().toString();
                selectedOptionValue = Util.getKeyFromValue(optionMap, value);
                if (radioGroup.getCheckedRadioButtonId() == R.id.rb5) {
                    bSubmit.setVisibility(View.VISIBLE);
                    etOther.setVisibility(View.VISIBLE);
                } else {
                    bSubmit.setVisibility(View.GONE);
                    etOther.setVisibility(View.GONE);
                    callReportApi(null);
                }
            });

            v.findViewById(R.id.bJoin).setOnClickListener(v -> {
                String text = etOther.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    etOther.setError(data.getOtherText());
                    etOther.requestFocus();
                    return;
                }
                callReportApi(text);
            });

        } catch (Exception e) {
            CustomLog.e(e);
        }
        return v;
    }

    private void callReportApi(String text) {
        if (isNetworkAvailable(getContext())) {
            v.findViewById(R.id.llMain).setVisibility(View.GONE);
            v.findViewById(R.id.pbMain).setVisibility(View.VISIBLE);
            Map<String, Object> map = new HashMap<>();
            map.put("value", selectedOptionValue);
            if (null != text) {
                map.put("text", text);
            }
            map.put(Constant.KEY_AD_ID, mAdId);
            new ApiController(Constant.URL_REPORT_CUMMUNITY_AD, map, getContext(), this, -1).setExtraKey(position).execute();
        } else {
            Util.showSnackbar(v, getString(R.string.MSG_NO_INTERNET));
        }
    }


    private boolean isNetworkAvailable(Context context) {
        boolean result = false;
        try {
            result = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return result;
    }

    /*@Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }*/

   /* public void onDismiss() {
        onDismiss(getDialog());
    }*/

    public static ReportAdDialog newInstance(CommunityHiddenData data, OnUserClickedListener<Integer, Object> listener, int position, int adId) {
        ReportAdDialog frag = new ReportAdDialog();
        frag.listener = listener;
        frag.data = data;
        frag.mAdId = adId;
        frag.position = position;
        return frag;
    }


    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.bCancel:
                    dismiss();
                    break;

            }
        } catch (Exception e) {
            CustomLog.e(e);
        }
    }

    @Override
    public boolean onItemClicked(Integer eventType, Object data, int position) {
        // v.findViewById(R.id.llMain).setVisibility(View.GONE);
        v.findViewById(R.id.pbMain).setVisibility(View.GONE);

        try {
            if (!TextUtils.isEmpty("" + data)) {
                JSONObject json = new JSONObject("" + data);
                if (json.get("result") instanceof String) {
                    listener.onItemClicked(Constant.Events.REPORT, json.getString("result"), position);
                    dismiss();
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);
            Util.showSnackbar(v, getString(R.string.msg_something_wrong));
        }

        return false;
    }
}

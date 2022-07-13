package com.sesolutions.ui.crowdfunding;


import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sesolutions.R;
import com.sesolutions.listeners.OnUserClickedListener;
import com.sesolutions.responses.qna.FormCustomParam;
import com.sesolutions.ui.common.BaseDialogFragment;
import com.sesolutions.ui.welcome.Dummy;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SPref;
import com.sesolutions.utils.SesColorUtils;
import com.sesolutions.utils.Util;

public class DonateDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    private View v;

    private AppCompatEditText etCaption;

    private OnUserClickedListener<Integer, Object> listener;
    private String url, price;
    private Dummy.Result data;
    private int fundId;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        if (v != null) {
            return v;
        }
        v = inflater.inflate(R.layout.dialog_comment_edit, container, false);
        applyTheme(v);
        try {

            url = data.getCustomParams(FormCustomParam.class).getPaymentUrl();
            etCaption = v.findViewById(R.id.etBody);
            etCaption.setInputType(InputType.TYPE_CLASS_NUMBER);
            TextView tvUpdate = v.findViewById(R.id.tvUpdate);
            TextView tvCancel = v.findViewById(R.id.tvCancel);

            GradientDrawable gdr1 = (GradientDrawable) tvUpdate.getBackground();
            gdr1.setColor(SesColorUtils.getPrimaryColor(getContext()));
            tvUpdate.setBackground(gdr1);
            Dummy.Formfields ffPrice = data.getFormFielsByName("price");
            etCaption.setHint(ffPrice.getLabel());
            if (!TextUtils.isEmpty(ffPrice.getDescription())) {
                v.findViewById(R.id.tvDesc).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.tvDesc)).setText(ffPrice.getDescription());
            } else {
                v.findViewById(R.id.tvDesc).setVisibility(View.GONE);
            }

            tvUpdate.setText(data.getFormFielsByName("button").getLabel());

           /* GradientDrawable gdr2 = (GradientDrawable) tvUpdate.getBackground();
            gdr2.setStroke(gdr2.strSesColorUtils.getPrimaryColor(getContext()));
            tvCancel.setBackground(gdr2);*/
            Util.showImageWithGlide(v.findViewById(R.id.ivProfileImage), SPref.getInstance().getUserMasterDetail(getContext()).getPhotoUrl());
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
            new Handler().postDelayed(() -> {
                openKeyboard();
                etCaption.requestFocus();
                // etCaption.setSelection(text.length());
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
        if (!TextUtils.isEmpty(price)) {
            listener.onItemClicked(Constant.Events.ACCEPT, url + price, fundId);
            price = null;
        }
    }


    public void onDismiss() {
        onDismiss(getDialog());
    }

    public static DonateDialogFragment newInstance(OnUserClickedListener<Integer, Object> listener, int fundId, Dummy.Result data) {
        DonateDialogFragment frag = new DonateDialogFragment();
        frag.listener = listener;
        frag.fundId = fundId;
        frag.data = data;
        return frag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvUpdate:
                price = etCaption.getText().toString();
                onDismiss();
                break;
            case R.id.tvCancel:
                price = null;
                onDismiss();
                break;
        }
        // onDismiss();
    }
}

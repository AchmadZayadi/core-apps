package me.riddhimanadib.formmaster.viewholder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Handler;
import androidx.core.widget.CompoundButtonCompat;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import me.riddhimanadib.formmaster.R;
import me.riddhimanadib.formmaster.listener.ReloadListener;
import me.riddhimanadib.formmaster.model.BaseFormElement;
import me.riddhimanadib.formmaster.model.FormElementCheckbox;

/**
 * Created by Riddhi - Rudra on 30-Jul-17.
 */

public class FormElementCheckBoxViewHolder extends BaseViewHolder implements CompoundButton.OnCheckedChangeListener {

    private final LinearLayout llMainForm;
    public AppCompatTextView mTextViewTitle, mTextViewPositive, mTextViewNegative;
    public AppCompatCheckBox mSwitch;
    private ReloadListener mReloadListener;
    private BaseFormElement mFormElement;
    private FormElementCheckbox mFormElementSwitch;
    private int mPosition;

    public FormElementCheckBoxViewHolder(View v, Context context, ReloadListener reloadListener) {
        super(v);
        mTextViewTitle = (AppCompatTextView) v.findViewById(R.id.formElementTitle);
        mTextViewPositive = (AppCompatTextView) v.findViewById(R.id.formElementPositiveText);
        mTextViewNegative = (AppCompatTextView) v.findViewById(R.id.formElementNegativeText);
        llMainForm = (LinearLayout) v.findViewById(R.id.llMainForm);

        mSwitch = (AppCompatCheckBox) v.findViewById(R.id.formElementSwitch);
        mReloadListener = reloadListener;

    }

    @Override
    public void bind(final int position, BaseFormElement formElement, final Context context) {
        try {
            mFormElement = formElement;
            mPosition = position;
            mFormElementSwitch = (FormElementCheckbox) mFormElement;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = formElement.isHidden() ? 0 : params.height;
            llMainForm.setLayoutParams(params);
            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(mSwitch, ColorStateList.valueOf(mFormElementSwitch.getTintColor()));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
            } else {
                mSwitch.setButtonTintList(ColorStateList.valueOf(mFormElementSwitch.getTintColor()));
                //setButtonTintList is accessible directly on API>19
            }
            mTextViewTitle.setText(mFormElementSwitch.getTitle());
            mTextViewPositive.setText(mFormElementSwitch.getPositiveText());
            mTextViewPositive.setVisibility(null != mFormElementSwitch.getPositiveText() ? View.VISIBLE : View.GONE);
            mSwitch.setChecked(mFormElementSwitch.getValue().equals("1"));
            mTextViewPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mFormElementSwitch.getClickListener()) {
                        mFormElementSwitch.getClickListener().onTextClicked(mFormElementSwitch.getTag());
                    }
                }
            });
            mTextViewNegative.setHint(mFormElementSwitch.getNegativeText());
            mSwitch.setTag(getAdapterPosition());
            mSwitch.setOnCheckedChangeListener(this/*new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (null != mReloadListener)
                        mReloadListener.updateValue(position, b ? "1" : "0");
                }
            }*/);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != mReloadListener)
                    mReloadListener.updateValue((int) buttonView.getTag(), isChecked ? "1" : "0");
            }
        }, 100);
    }
}

package me.riddhimanadib.formmaster.model;

import android.text.Spannable;
import android.text.TextUtils;

import me.riddhimanadib.formmaster.listener.OnTextClickListener;

/**
 * Created by Riddhi - Rudra on 28-Jul-17.
 */

public class FormElementButton extends BaseFormElement {

    private OnTextClickListener listener;
    private Spannable text;

    public FormElementButton() {
    }

    public static FormElementButton createInstance() {
        FormElementButton formElementTextSingleLine = new FormElementButton();
        formElementTextSingleLine.setType(BaseFormElement.TYPE_BUTTON);
        return formElementTextSingleLine;
    }

    public FormElementButton setTag(int mTag) {
        return (FormElementButton) super.setTag(mTag);
    }

    public FormElementButton setType(int mType) {
        return (FormElementButton) super.setType(mType);
    }

    public FormElementButton setTitle(String mTitle) {
        return (FormElementButton) super.setTitle(mTitle);
    }

    public FormElementButton setValue(String mValue) {
        return (FormElementButton) super.setValue(mValue);
    }

    public FormElementButton setHint(String mHint) {
        return (FormElementButton) super.setHint(mHint);
    }

    public FormElementButton setRequired(boolean required) {
        return (FormElementButton) super.setRequired(required);
    }

    public FormElementButton setClickListener(OnTextClickListener listener) {
        this.listener = listener;
        return this;
    }

    public OnTextClickListener getClickListener() {
        return this.listener;
    }

    public boolean isTextEmpty() {
        return TextUtils.isEmpty(text);
    }

    public Spannable getButtonText() {
        return text;
    }

    public void setButtonText(Spannable text) {
        this.text = text;
    }
}

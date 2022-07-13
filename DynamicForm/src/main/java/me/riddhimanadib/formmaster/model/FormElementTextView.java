package me.riddhimanadib.formmaster.model;

import me.riddhimanadib.formmaster.listener.OnTextClickListener;

/**
 * Created by Riddhi - Rudra on 28-Jul-17.
 */

public class FormElementTextView extends BaseFormElement {
    private OnTextClickListener listener;

    public FormElementTextView() {
    }

    public FormElementTextView setClickListener(OnTextClickListener listener) {
        this.listener = listener;
        return this;
    }

    public OnTextClickListener getClickListener() {
        return this.listener;
    }

   /* public static FormElementTextView createInstance(String value) {
        FormElementTextView formElementTextSingleLine = new FormElementTextView();
        FormElementTextView.setType(BaseFormElement.TYPE_EDITTEXT_TEXT_SINGLELINE);
        FormElementTextView.setValue(value);
        return formElementTextSingleLine;
    }*/

    /*public static FormElementTextView createInstance() {
        FormElementTextView formElementTextSingleLine = new FormElementTextView();
        FormElementTextView.setType(BaseFormElement.TYPE_EDITTEXT_TEXT_SINGLELINE);
       // formElementTextSingleLine.setValue(value);
        return formElementTextSingleLine;
    }*/

    public FormElementTextView setTag(int mTag) {
        return (FormElementTextView) super.setTag(mTag);
    }

    public FormElementTextView setType(int mType) {
        return (FormElementTextView) super.setType(mType);
    }

    public FormElementTextView setTitle(String mTitle) {
        return (FormElementTextView) super.setTitle(mTitle);
    }

    public FormElementTextView setValue(String mValue) {
        return (FormElementTextView) super.setValue(mValue);
    }

    public FormElementTextView setHint(String mHint) {
        return (FormElementTextView) super.setHint(mHint);
    }

    public FormElementTextView setRequired(boolean required) {
        return (FormElementTextView) super.setRequired(required);
    }
}

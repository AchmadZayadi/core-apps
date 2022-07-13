package me.riddhimanadib.formmaster.model;

import me.riddhimanadib.formmaster.listener.OnTextClickListener;


public class FormElementDateTimePicker extends BaseFormElement {
    private OnTextClickListener listener;
    public FormElementDateTimePicker() {
    }

    public FormElementDateTimePicker setClickListener(OnTextClickListener listener) {
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

    public FormElementDateTimePicker setTag(int mTag) {
        return (FormElementDateTimePicker) super.setTag(mTag);
    }

    public FormElementDateTimePicker setType(int mType) {
        return (FormElementDateTimePicker) super.setType(mType);
    }

    public FormElementDateTimePicker setTitle(String mTitle) {
        return (FormElementDateTimePicker) super.setTitle(mTitle);
    }

    public FormElementDateTimePicker setValue(String mValue) {
        return (FormElementDateTimePicker) super.setValue(mValue);
    }

    public FormElementDateTimePicker setHint(String mHint) {
        return (FormElementDateTimePicker) super.setHint(mHint);
    }

    public FormElementDateTimePicker setRequired(boolean required) {
        return (FormElementDateTimePicker) super.setRequired(required);
    }

}

package me.riddhimanadib.formmaster.model;

import me.riddhimanadib.formmaster.listener.OnTextClickListener;

/**
 * Created by Riddhi - Rudra on 28-Jul-17.
 */

public class FormElementTitle extends BaseFormElement {
    private OnTextClickListener listener;
    private boolean boldText;

    public FormElementTitle() {
    }


    public FormElementTitle setTag(int mTag) {
        return (FormElementTitle) super.setTag(mTag);
    }

    public FormElementTitle setType(int mType) {
        return (FormElementTitle) super.setType(mType);
    }

    public FormElementTitle setTitle(String mTitle) {
        return (FormElementTitle) super.setTitle(mTitle);
    }

    public FormElementTitle setValue(String mValue) {
        return (FormElementTitle) super.setValue(mValue);
    }

    public FormElementTitle setHint(String mHint) {
        return (FormElementTitle) super.setHint(mHint);
    }

    public FormElementTitle setRequired(boolean required) {
        return (FormElementTitle) super.setRequired(required);
    }

    public void setBoldText(boolean boldText) {
        this.boldText = boldText;
    }

    public boolean isBoldText() {
        return boldText;
    }
}

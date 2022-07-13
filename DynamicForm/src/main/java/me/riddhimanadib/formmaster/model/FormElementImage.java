package me.riddhimanadib.formmaster.model;

import me.riddhimanadib.formmaster.listener.OnTextClickListener;

/**
 * Created by Riddhi - Rudra on 28-Jul-17.
 */

public class FormElementImage extends BaseFormElement {

    private OnTextClickListener clickListener;
    private String thumbnail = "";

    public FormElementImage() {
    }

    public static FormElementImage createInstance() {
        FormElementImage FormElementSwitch = new FormElementImage();
        FormElementSwitch.setType(BaseFormElement.TYPE_IMAGE_TYPE);
        return FormElementSwitch;
    }

    public FormElementImage setTag(int mTag) {
        return (FormElementImage) super.setTag(mTag);
    }

    public FormElementImage setType(int mType) {
        return (FormElementImage) super.setType(mType);
    }

    public FormElementImage setTitle(String mTitle) {
        return (FormElementImage) super.setTitle(mTitle);
    }

    public FormElementImage setValue(String mValue) {
        return (FormElementImage) super.setValue(mValue);
    }

    public FormElementImage setThumbnail(String mValue) {
        thumbnail = mValue;
        return this;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public FormElementImage setHint(String mHint) {
        return (FormElementImage) super.setHint(mHint);
    }

    public FormElementImage setRequired(boolean required) {
        return (FormElementImage) super.setRequired(required);
    }


    public FormElementImage setClickListener(OnTextClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    // custom getters

    public OnTextClickListener getClickListener() {
        return this.clickListener;
    }


}

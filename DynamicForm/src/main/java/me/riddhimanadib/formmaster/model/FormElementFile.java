package me.riddhimanadib.formmaster.model;

import me.riddhimanadib.formmaster.listener.OnTextClickListener;

/**
 * Created by Riddhi - Rudra on 28-Jul-17.
 */

public class FormElementFile extends BaseFormElement {

    private OnTextClickListener clickListener;
    private String thumbnail = "";

    public FormElementFile() {
    }

    public static FormElementFile createInstance() {
        FormElementFile FormElementSwitch = new FormElementFile();
        FormElementSwitch.setType(BaseFormElement.TYPE_FILE);
        return FormElementSwitch;
    }

    public FormElementFile setTag(int mTag) {
        return (FormElementFile) super.setTag(mTag);
    }

    public FormElementFile setType(int mType) {
        return (FormElementFile) super.setType(mType);
    }

    public FormElementFile setTitle(String mTitle) {
        return (FormElementFile) super.setTitle(mTitle);
    }

    public FormElementFile setValue(String mValue) {
        return (FormElementFile) super.setValue(mValue);
    }

    public FormElementFile setThumbnail(String mValue) {
        thumbnail = mValue;
        return this;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public FormElementFile setHint(String mHint) {
        return (FormElementFile) super.setHint(mHint);
    }

    public FormElementFile setRequired(boolean required) {
        return (FormElementFile) super.setRequired(required);
    }


    public FormElementFile setClickListener(OnTextClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    // custom getters

    public OnTextClickListener getClickListener() {
        return this.clickListener;
    }


}

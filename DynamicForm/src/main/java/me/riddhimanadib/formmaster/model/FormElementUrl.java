package me.riddhimanadib.formmaster.model;

import me.riddhimanadib.formmaster.listener.OnTextClickListener;

/**
 * Created by Riddhi - Rudra on 28-Jul-17.
 */

public class FormElementUrl extends BaseFormElement {

    private OnTextClickListener clickListener;
    private String thumbnail = "";

    public FormElementUrl() {
    }

    public static FormElementUrl createInstance() {
        FormElementUrl formElementUrl = new FormElementUrl();
        formElementUrl.setType(BaseFormElement.TYPE_URL);
        return formElementUrl;
    }

    public FormElementUrl setTag(int mTag) {
        return (FormElementUrl) super.setTag(mTag);
    }

    public FormElementUrl setType(int mType) {
        return (FormElementUrl) super.setType(mType);
    }

    public FormElementUrl setTitle(String mTitle) {
        return (FormElementUrl) super.setTitle(mTitle);
    }

    public FormElementUrl setValue(String mValue) {
        return (FormElementUrl) super.setValue(mValue);
    }




    public FormElementUrl setHint(String mHint) {
        return (FormElementUrl) super.setHint(mHint);
    }

    public FormElementUrl setRequired(boolean required) {
        return (FormElementUrl) super.setRequired(required);
    }


    public FormElementUrl setClickListener(OnTextClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    // custom getters

    public OnTextClickListener getClickListener() {
        return this.clickListener;
    }


}

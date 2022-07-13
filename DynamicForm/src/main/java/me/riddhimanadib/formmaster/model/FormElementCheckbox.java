package me.riddhimanadib.formmaster.model;

import android.graphics.Color;

import me.riddhimanadib.formmaster.listener.OnTextClickListener;

/**
 * Created by Riddhi - Rudra on 28-Jul-17.
 */

public class FormElementCheckbox extends BaseFormElement {

    private String positiveText; // text for right side
    private String negativeText; // text for left side
    private OnTextClickListener clickListener;
    private int tintColor;

    public FormElementCheckbox() {
    }

    public static FormElementCheckbox createInstance() {
        FormElementCheckbox FormElementSwitch = new FormElementCheckbox();
        FormElementSwitch.setType(BaseFormElement.TYPE_CHECKBOX);
        return FormElementSwitch;
    }

    public FormElementCheckbox setTag(int mTag) {
        return (FormElementCheckbox) super.setTag(mTag);
    }

    public FormElementCheckbox setType(int mType) {
        return (FormElementCheckbox) super.setType(mType);
    }

    public FormElementCheckbox setTitle(String mTitle) {
        return (FormElementCheckbox) super.setTitle(mTitle);
    }

    public FormElementCheckbox setValue(String mValue) {
        return (FormElementCheckbox) super.setValue(mValue);
    }

    public FormElementCheckbox setHint(String mHint) {
        return (FormElementCheckbox) super.setHint(mHint);
    }

    public FormElementCheckbox setRequired(boolean required) {
        return (FormElementCheckbox) super.setRequired(required);
    }

    // custom setters
    public FormElementCheckbox setSwitchTexts(String positiveText, String negativeText) {
        this.positiveText = positiveText;
        this.negativeText = negativeText;
        return this;
    }

    public FormElementCheckbox setPositiveText(String positiveText) {
        this.positiveText = positiveText;
        return this;
    }

    public FormElementCheckbox setClickListener(OnTextClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    public int getTintColor() {
        if(tintColor==0){
            tintColor= Color.parseColor("#2587C8");
        }
        return tintColor;
    }

    public void setTintColor(int tintColor) {
        this.tintColor = tintColor;
    }

    // custom getters
    public String getPositiveText() {
        return this.positiveText;
    }

    public OnTextClickListener getClickListener() {
        return this.clickListener;
    }

    public String getNegativeText() {
        return this.negativeText;
    }

}

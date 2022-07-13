package me.riddhimanadib.formmaster.model;

import android.graphics.Color;
import android.text.TextUtils;

import me.riddhimanadib.formmaster.listener.OnTextClickListener;

/**
 * Created by Riddhi - Rudra on 28-Jul-17.
 */

public class FormElementRating extends BaseFormElement {

   // private OnTextClickListener clickListener;
    private int tintColor;

    public FormElementRating() {
    }

    public static FormElementRating createInstance() {
        FormElementRating FormElementSwitch = new FormElementRating();
        FormElementSwitch.setType(BaseFormElement.TYPE_RATING);
        return FormElementSwitch;
    }

    public FormElementRating setTag(int mTag) {
        return (FormElementRating) super.setTag(mTag);
    }

    public FormElementRating setType(int mType) {
        return (FormElementRating) super.setType(mType);
    }

    public FormElementRating setTitle(String mTitle) {
        return (FormElementRating) super.setTitle(mTitle);
    }

    public FormElementRating setValue(String mValue) {
        return (FormElementRating) super.setValue(mValue);
    }

    public FormElementRating setHint(String mHint) {
        return (FormElementRating) super.setHint(mHint);
    }

    public FormElementRating setRequired(boolean required) {
        return (FormElementRating) super.setRequired(required);
    }

   /* public FormElementRating setClickListener(OnTextClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }*/

    public int getTintColor() {
        if(tintColor==0){
            tintColor= Color.parseColor("#2587C8");
        }
        return tintColor;
    }

    public void setTintColor(int tintColor) {
        this.tintColor = tintColor;
    }



  /*  public OnTextClickListener getClickListener() {
        return this.clickListener;
    }*/


}

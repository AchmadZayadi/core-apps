package me.riddhimanadib.formmaster.model;

import me.riddhimanadib.formmaster.listener.OnTextClickListener;

/**
 * Created by Riddhi - Rudra on 28-Jul-17.
 */

public class FormElementGroupQuestion extends BaseFormElement {

    private OnTextClickListener clickListener;

    public FormElementGroupQuestion() {
    }

    public static FormElementGroupQuestion createInstance() {
        FormElementGroupQuestion FormElementSwitch = new FormElementGroupQuestion();
        FormElementSwitch.setType(BaseFormElement.TYPE_GROUP_QUESTION);
        return FormElementSwitch;
    }

    public FormElementGroupQuestion setTag(int mTag) {
        return (FormElementGroupQuestion) super.setTag(mTag);
    }

    public FormElementGroupQuestion setType(int mType) {
        return (FormElementGroupQuestion) super.setType(mType);
    }

    public FormElementGroupQuestion setTitle(String mTitle) {
        return (FormElementGroupQuestion) super.setTitle(mTitle);
    }

    public FormElementGroupQuestion setValue(String mValue) {
        return (FormElementGroupQuestion) super.setValue(mValue);
    }

    public FormElementGroupQuestion setHint(String mHint) {
        return (FormElementGroupQuestion) super.setHint(mHint);
    }

    public FormElementGroupQuestion setRequired(boolean required) {
        return (FormElementGroupQuestion) super.setRequired(required);
    }


    public FormElementGroupQuestion setClickListener(OnTextClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    // custom getters
    public OnTextClickListener getClickListener() {
        return this.clickListener;
    }


}

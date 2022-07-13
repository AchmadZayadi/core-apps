package me.riddhimanadib.formmaster.model;

import me.riddhimanadib.formmaster.listener.OnTextClickListener;

/**
 * Created by Riddhi - Rudra on 28-Jul-17.
 */

public class FormElementLocationSuggest extends BaseFormElement {

    private OnTextClickListener listener;

    public FormElementLocationSuggest() {
    }

    public static FormElementLocationSuggest createInstance(String value) {
        FormElementLocationSuggest formElementTextSingleLine = new FormElementLocationSuggest();
        formElementTextSingleLine.setType(BaseFormElement.TYPE_LOCATION_SUGGEST);
        formElementTextSingleLine.setValue(value);
        return formElementTextSingleLine;
    }

    public FormElementLocationSuggest setTag(int mTag) {
        return (FormElementLocationSuggest) super.setTag(mTag);
    }

    public FormElementLocationSuggest setName(String name) {
        return (FormElementLocationSuggest) super.setName(name);
    }

    public FormElementLocationSuggest setType(int mType) {
        return (FormElementLocationSuggest) super.setType(mType);
    }

    public FormElementLocationSuggest setTitle(String mTitle) {
        return (FormElementLocationSuggest) super.setTitle(mTitle);
    }

    public FormElementLocationSuggest setValue(String mValue) {
        return (FormElementLocationSuggest) super.setValue(mValue);
    }

    public FormElementLocationSuggest setHint(String mHint) {
        return (FormElementLocationSuggest) super.setHint(mHint);
    }

    public FormElementLocationSuggest setRequired(boolean required) {
        return (FormElementLocationSuggest) super.setRequired(required);
    }

    public OnTextClickListener getClickListener() {
        return listener;
    }

    public FormElementLocationSuggest setClickListener(OnTextClickListener listener) {
        this.listener = listener;
        return this;
    }
}

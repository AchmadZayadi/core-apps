package me.riddhimanadib.formmaster.model;

/**
 * Created by Riddhi - Rudra on 28-Jul-17.
 */

public class FormElementTextSingleLine extends BaseFormElement {

    public FormElementTextSingleLine() {
    }

    public static FormElementTextSingleLine createInstance(String value) {
        FormElementTextSingleLine formElementTextSingleLine = new FormElementTextSingleLine();
        formElementTextSingleLine.setType(BaseFormElement.TYPE_EDITTEXT_TEXT_SINGLELINE);
        formElementTextSingleLine.setValue(value);
        return formElementTextSingleLine;
    }

    public static FormElementTextSingleLine createInstance() {
        FormElementTextSingleLine formElementTextSingleLine = new FormElementTextSingleLine();
        formElementTextSingleLine.setType(BaseFormElement.TYPE_EDITTEXT_TEXT_SINGLELINE);
        // formElementTextSingleLine.setValue(value);
        return formElementTextSingleLine;
    }

    public FormElementTextSingleLine setTag(int mTag) {
        return (FormElementTextSingleLine) super.setTag(mTag);
    }

    public FormElementTextSingleLine setType(int mType) {
        return (FormElementTextSingleLine) super.setType(mType);
    }

    public FormElementTextSingleLine setTitle(String mTitle) {
        return (FormElementTextSingleLine) super.setTitle(mTitle);
    }

    public FormElementTextSingleLine setValue(String mValue) {
        return (FormElementTextSingleLine) super.setValue(mValue);
    }

    public FormElementTextSingleLine setName(String name) {
        return (FormElementTextSingleLine) super.setName(name);
    }

    public FormElementTextSingleLine setHint(String mHint) {
        return (FormElementTextSingleLine) super.setHint(mHint);
    }

    public FormElementTextSingleLine setRequired(boolean required) {
        return (FormElementTextSingleLine) super.setRequired(required);
    }

}

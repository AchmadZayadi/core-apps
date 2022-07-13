package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 3/1/18.
 */

public class ValidateFieldError {
    @SerializedName("type")
    private String type;
    @SerializedName("name")
    private String name;
    @SerializedName("label")
    private String label;
    @SerializedName("errorMessage")
    private String errormessage;
    @SerializedName("isRequired")
    private boolean isrequired;
    @SerializedName("value")
    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getErrormessage() {
        return errormessage;
    }

    public void setErrormessage(String errormessage) {
        this.errormessage = errormessage;
    }

    public boolean getIsrequired() {
        return isrequired;
    }

    public void setIsrequired(boolean isrequired) {
        this.isrequired = isrequired;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}


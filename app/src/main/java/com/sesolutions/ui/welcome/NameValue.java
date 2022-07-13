package com.sesolutions.ui.welcome;

import com.google.gson.annotations.SerializedName;

import org.apache.http.NameValuePair;

import java.io.Serializable;

/**
 * Created by root on 2/11/17.
 */

public class NameValue implements NameValuePair, Serializable {
    @SerializedName("key")
    private String name;
    private String value;


    public NameValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

}

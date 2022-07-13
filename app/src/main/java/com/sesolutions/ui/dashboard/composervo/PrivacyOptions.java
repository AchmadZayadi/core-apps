package com.sesolutions.ui.dashboard.composervo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PrivacyOptions   {
    @SerializedName("name")
    private String name;
    @SerializedName("value")
    private String value;

    public String getName() {
        return name;
    }



    public String getValue() {
        return value;
    }

}

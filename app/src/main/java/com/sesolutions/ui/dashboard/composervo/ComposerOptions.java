package com.sesolutions.ui.dashboard.composervo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ComposerOptions  {
    @SerializedName("value")
    private String value;
    @SerializedName("name")
    private String name;
    private String imageCode;
    private String colorCode;

    public String getValue() {
        return value;
    }



    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getName() {
        return name;
    }



    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }
}

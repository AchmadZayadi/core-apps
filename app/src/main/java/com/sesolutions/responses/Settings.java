package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 28/11/17.
 */

public class Settings {
    @SerializedName("class")
    private String clazz;
    @SerializedName("label")
    private String label;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

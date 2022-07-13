package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 12/12/17.
 */

public class Membership {

    @SerializedName("label")
    private String label;
    @SerializedName("text")
    private String text;
    @SerializedName("action")
    private String action;
    @SerializedName("icon")
    private String icon;
    @SerializedName("type")
    private String type;
    @SerializedName("name")
    private String name;


    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}

package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 22/11/17.
 */

public class Feeling {

    @SerializedName("feelingicon_id")
    private int feelingiconId;
    @SerializedName("icon")
    private String icon;
    @SerializedName("resource_type")
    private String resourceType;
    @SerializedName("title")
    private String title;
    private int feeling_type;
    private int feeling_id;

    public int getFeeling_type() {
        return feeling_type;
    }

    public void setFeeling_type(int feeling_type) {
        this.feeling_type = feeling_type;
    }

    public int getFeeling_id() {
        return feeling_id;
    }

    public void setFeeling_id(int feeling_id) {
        this.feeling_id = feeling_id;
    }

    public int getFeelingiconId() {
        return feelingiconId;
    }

    public void setFeelingiconId(int feelingiconId) {
        this.feelingiconId = feelingiconId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

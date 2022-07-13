package com.sesolutions.responses.page;

import com.google.gson.annotations.SerializedName;

public class Shortcuts {

    @SerializedName("title")
    private String title;

    @SerializedName("resource_type")
    private String resource_type;

    @SerializedName("resource_id")
    private int resource_id;

    @SerializedName("shortcut_id")
    private int shortcut_id;

    @SerializedName("is_saved")
    private boolean is_saved;

    {
        is_saved=false;
    }

    public int getShortcut_id() {
        return shortcut_id;
    }

    public void setShortcut_id(int shortcut_id) {
        this.shortcut_id = shortcut_id;
    }

    public int getResource_id() {
        return resource_id;
    }

    public String getResource_type() {
        return resource_type;
    }

    public String getTitle() {
        return title;
    }

    public boolean isIs_saved() {
        return is_saved;
    }

    public void setIs_saved(boolean is_saved) {
        this.is_saved = is_saved;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setResource_id(int resource_id) {
        this.resource_id = resource_id;
    }
}

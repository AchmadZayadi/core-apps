package com.sesolutions.ui.dashboard.composervo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ActivityStikersMenu  {
    @SerializedName("label")
    private String label;
    @SerializedName("name")
    private String name;
    @SerializedName("title")
    private String title;

    public ActivityStikersMenu(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


}

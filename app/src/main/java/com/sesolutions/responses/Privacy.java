package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AheadSoft on 09-03-2018.
 */

public class Privacy {

    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

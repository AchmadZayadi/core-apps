package com.sesolutions.responses.videos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 7/12/17.
 */

public class Tabs {

    @SerializedName("label")
    private String label;
    @SerializedName("name")
    private String name;
    private int totalCount;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

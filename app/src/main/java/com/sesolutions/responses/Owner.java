package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 7/12/17.
 */

public class Owner {
    @SerializedName("title")
    private String title;
    @SerializedName("id")
    private int id;
    @SerializedName("href")
    private String href;
    @SerializedName("images")
    private String images;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getImages() {
        return images;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}

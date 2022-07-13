package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by root on 22/11/17.
 */

public class Emotion implements Serializable {

    @SerializedName("gallery_id")
    private int galleryId;
    @SerializedName("icon")
    private String icon;
    @SerializedName("title")
    private String title;
    @SerializedName("color")
    private String color;
    @SerializedName("files_id")
    private int fileId;




    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getGalleryId() {
        return galleryId;
    }

    public void setGalleryId(int galleryId) {
        this.galleryId = galleryId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

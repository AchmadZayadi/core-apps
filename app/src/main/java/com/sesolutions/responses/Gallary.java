package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by root on 24/11/17.
 */

public class Gallary {

    @SerializedName("title")
    private String title;
    @SerializedName("gallery_id")
    private int galleryId;
    @SerializedName("is_selected")
    private boolean isSelected;
    @SerializedName("category")
    private String category;
    @SerializedName("icon")
    private String icon;
    @SerializedName("images")
    private List<Images> images;


    /*mannuly adding */
    private int filesId;
    private String imageIcon;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(String imageIcon) {
        this.imageIcon = imageIcon;
    }


    public int getFilesId() {
        return filesId;
    }

    public void setFilesId(int filesId) {
        this.filesId = filesId;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getGalleryId() {
        return galleryId;
    }

    public void setGalleryId(int galleryId) {
        this.galleryId = galleryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    private static class Images {
        @SerializedName("files_id")
        private int filesId;
        @SerializedName("icon")
        private String icon;

        public int getFilesId() {
            return filesId;
        }

        public void setFilesId(int filesId) {
            this.filesId = filesId;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}

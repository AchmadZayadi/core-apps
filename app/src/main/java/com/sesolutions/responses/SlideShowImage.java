package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.utils.SpanUtil;

/**
 * Created by root on 19/12/17.
 */

public class SlideShowImage {

    @SerializedName("image")
    private String image;
    @SerializedName("videourl")
    private String videoUrl;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("title_color")
    private String titleColor;
    @SerializedName("description_color")
    private String descriptionColor;
    @SerializedName("background_color")
    private String backgroundColor;

    public SlideShowImage(String imageUrl) {
        image = imageUrl;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return SpanUtil.getHtmlString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public String getDescriptionColor() {
        return descriptionColor;
    }

    public void setDescriptionColor(String descriptionColor) {
        this.descriptionColor = descriptionColor;
    }
}

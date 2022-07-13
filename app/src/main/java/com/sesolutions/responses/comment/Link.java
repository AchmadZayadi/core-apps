package com.sesolutions.responses.comment;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.utils.SpanUtil;

/**
 * Created by root on 28/11/17.
 */

public class Link {

    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("href")
    private String href;
    @SerializedName("medium")
    private String medium;
    @SerializedName("images")
    private Images images;


    //manually creating this field
    private String uri;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }
}


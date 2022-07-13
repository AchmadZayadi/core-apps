package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.utils.SpanUtil;

/**
 * Created by root on 10/11/17.
 */

public class Links {

    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("thumb")
    private String thumb;
    @SerializedName("medium")
    private String medium;
    @SerializedName("images")
    private String images;


    //manually creating this field
    private String uri;

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

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public boolean isYouTubeUrl() {
        if (null != uri) {
            return (uri.startsWith("https://www.youtu")
                    || uri.startsWith("https://youtu")
                    || uri.startsWith("http://www.youtu")
                    || uri.startsWith("http://youtu")
            );


        }
        return false;
    }
}

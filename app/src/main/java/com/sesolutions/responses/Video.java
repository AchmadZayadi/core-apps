package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.utils.SpanUtil;

/**
 * Created by root on 10/11/17.
 */

public class Video {

    @SerializedName("status")
    private boolean status;
    @SerializedName("video_id")
    private String videoId;
    @SerializedName("photo_id")
    private int photoId;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("src")
    private String src;
    @SerializedName("message")
    private String message;

    //custom
    private boolean isFromDevice;

    public boolean isFromDevice() {
        return isFromDevice;
    }

    public void setFromDevice(boolean fromDevice) {
        isFromDevice = fromDevice;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
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

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

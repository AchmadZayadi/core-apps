package com.sesolutions.responses.comment;

import com.google.gson.annotations.SerializedName;

public class Enable {
    @SerializedName("album")
    private int album;
    @SerializedName("video")
    private int video;

    @SerializedName("is_gif")
    private String is_gif;

    public String getIs_gif() {
        return is_gif;
    }

    public void setIs_gif(String is_gif) {
        this.is_gif = is_gif;
    }

    public int getAlbum() {
        return album;
    }

    public void setAlbum(int album) {
        this.album = album;
    }

    public int getVideo() {
        return video;
    }

    public void setVideo(int video) {
        this.video = video;
    }
}

package com.sesolutions.responses.videos;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Options;

import java.util.List;

/**
 * Created by root on 6/12/17.
 */

public class ResultView {
    private ViewVideo video;
    private ViewVideo lecture;
    private List<Options> menus;
    private ViewVideo channel;
    private ViewVideo playlist;
    @SerializedName("similar_videos")
    private List<Videos> similarVideos;
    @SerializedName("related_lectures")
    private List<Videos> related_lectures;
    @SerializedName("videos")
    private List<Videos> videos;
    @SerializedName("loggedin_user_id")
    private int loggedinUserId;

    public List<Options> getMenus() {
        return menus;
    }

    public void setMenus(List<Options> menus) {
        this.menus = menus;
    }

    public int getLoggedinUserId() {
        return loggedinUserId;
    }

    public void setLoggedinUserId(int loggedinUserId) {
        this.loggedinUserId = loggedinUserId;
    }

    public ViewVideo getChannel() {
        return channel;
    }

    public void setChannel(ViewVideo channel) {
        this.channel = channel;
    }

    public ViewVideo getPlaylist() {
        return playlist;
    }

    public void setPlaylist(ViewVideo playlist) {
        this.playlist = playlist;
    }

    public List<Videos> getVideos() {
        return videos;
    }

    public void setVideos(List<Videos> videos) {
        this.videos = videos;
    }

    public ViewVideo getVideo() {
        return video;
    }
    public ViewVideo getLecture() {
        return lecture;
    }

    public void setVideo(ViewVideo videos) {
        this.video = videos;
    }

    public List<Videos> getSimilarVideos() {
        return similarVideos;
    }

    public List<Videos> getRelatedLectures() {
        return related_lectures;
    }

    public void setSimilarVideos(List<Videos> similarVideos) {
        this.similarVideos = similarVideos;
    }
}

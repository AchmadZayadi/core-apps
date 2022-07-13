package com.sesolutions.responses.videos;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.music.Permission;

import java.util.List;

/**
 * Created by root on 5/12/17.
 */

public class Result2 {

    @SerializedName("result")
    private Result2 result;
    @SerializedName("creator")
    private List<Videos> creators;
    @SerializedName("forYou")
    private Result2 foryou;
    @SerializedName("following")
    private Result2 following;
    @SerializedName("videos")
    private List<Videos> videos;
    @SerializedName("songs")
    private List<Videos> songs;
    @SerializedName("artists")
    private List<Videos> artists;
    @SerializedName("playlists")
    private List<Videos> playlists;
    @SerializedName("category")
    private List<Category> category;
    @SerializedName("channels")
    private List<Videos> channels;
    @SerializedName("notification")
    private List<Videos> notification;
    @SerializedName("permission")
    private Permission permission;
    @SerializedName("loggedin_user_id")
    private int loggedinUserId;
    @SerializedName("total_page")
    private int totalPage;
    @SerializedName("current_page")
    private int currentPage;
    @SerializedName("action")
    private String action;
    @SerializedName("next_page")
    private int nextPage;
    @SerializedName("total")
    private int total;

    public Result2 getFollowing() {
        return following;
    }

    public Result2 getForyou() {
        return foryou;
    }

    public Result2 getResult() {
        return result;
    }

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public List<Videos> getChannels() {
        return channels;
    }

    public List<Videos> getNotifications() {
        return notification;
    }

    public void setChannels(List<Videos> channels) {
        this.channels = channels;
    }

    public List<Videos> getSongs() {
        return songs;
    }

    public void setSongs(List<Videos> songs) {
        this.songs = songs;
    }

    public List<Videos> getArtists() {
        return artists;
    }

    public List<Videos> getCreators() {
        return creators;
    }

    public void setArtists(List<Videos> artists) {
        this.artists = artists;
    }

    public List<Videos> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Videos> playlists) {
        this.playlists = playlists;
    }

    public List<Videos> getVideos() {
        return videos;
    }

    public void setVideos(List<Videos> videos) {
        this.videos = videos;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public int getLoggedinUserId() {
        return loggedinUserId;
    }

    public void setLoggedinUserId(int loggedinUserId) {
        this.loggedinUserId = loggedinUserId;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

package com.sesolutions.responses.music;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by root on 2/12/17.
 */

public class ResultSongView {
    // @SerializedName("albums")
    // private AlbumView albums;
    @SerializedName("songs")
    private AlbumView songs;
    @SerializedName("artists")
    private List<Albums> artists;
    @SerializedName("playlists")
    private List<Albums> playlists;
    @SerializedName("permission")
    private Permission permission;
    @SerializedName("loggedin_user_id")
    private int loggedinUserId;
    @SerializedName("total_page")
    private int totalPage;
    @SerializedName("current_page")
    private int currentPage;
    @SerializedName("next_page")
    private int nextPage;
    @SerializedName("total")
    private int total;

    @SerializedName("related_songs")
    private List<Albums> relatedsongs;

    public List<Albums> getRelatedsongs() {
        return relatedsongs;
    }

    public void setRelatedsongs(List<Albums> relatedsongs) {
        this.relatedsongs = relatedsongs;
    }


    public AlbumView getSongs() {
        return songs;
    }

    public void setSongs(AlbumView songs) {
        this.songs = songs;
    }

    public List<Albums> getArtists() {
        return artists;
    }

    public void setArtists(List<Albums> artists) {
        this.artists = artists;
    }

    public List<Albums> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Albums> playlists) {
        this.playlists = playlists;
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
}

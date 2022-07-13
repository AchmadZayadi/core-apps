package com.sesolutions.responses.music;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.SearchVo;

import java.util.ArrayList;
import java.util.List;

public class ResultView {
    @SerializedName("albums")
    private AlbumView albums;
    @SerializedName("playlist")
    private AlbumView playlist;
    @SerializedName("songs")
    private List<Albums> songs;
    @SerializedName("musics")
    private List<Albums> musics;
    @SerializedName("categories")
    private List<Albums> categories;
    @SerializedName("artists")
    private AlbumView artists;
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

    public AlbumView getPlaylist() {
        return playlist;
    }

    public void setPlaylist(AlbumView playlist) {
        this.playlist = playlist;
    }

    public List<Albums> getSongs() {
        return songs;
    }

    public List<Albums> getMusics() {
        return musics;
    }

    public List<Albums> getCategories() {
        return categories;
    }

    public List<SearchVo> convertSongToSearchVo() {
        List<SearchVo> list = new ArrayList<>();
        for (Albums vo : songs) {
            list.add(new SearchVo(vo));
        }
        return list;
    }

    public void setSongs(List<Albums> songs) {
        this.songs = songs;
    }

    public AlbumView getArtists() {
        return artists;
    }

    public void setArtists(AlbumView artists) {
        this.artists = artists;
    }

    public List<Albums> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Albums> playlists) {
        this.playlists = playlists;
    }

    public AlbumView getAlbums() {
        return albums;
    }

    public void setAlbums(AlbumView albums) {
        this.albums = albums;
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

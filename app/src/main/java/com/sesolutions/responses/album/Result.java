package com.sesolutions.responses.album;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Options;

import java.util.ArrayList;
import java.util.List;

public class Result {
    private List<Options> menus;
    @SerializedName("albums")
    private List<Albums> albums;
    private Albums album;
    @SerializedName("headerPhotos")
    private List<Albums> headerPhotos;
    private List<Albums> photos;
    private Options options;
    @SerializedName("can_create")
    private boolean canCreate;
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

    public Options getOptions() {
        return options;
    }

    public Albums getAlbum() {
        return album;
    }

    public void setAlbum(Albums album) {
        this.album = album;
    }

    public List<Albums> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Albums> photos) {
        this.photos = photos;
    }

    public List<Options> getMenus() {
        return menus;
    }

    public void setMenus(List<Options> menus) {
        this.menus = menus;
    }

    public List<Albums> getHeaderPhotos() {
        return headerPhotos;
    }

    public void setHeaderPhotos(List<Albums> headerPhotos) {
        this.headerPhotos = headerPhotos;
    }

    public List<Albums> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Albums> albums) {
        this.albums = albums;
    }

    public boolean getCanCreate() {
        return canCreate;
    }

    public void setCanCreate(boolean canCreate) {
        this.canCreate = canCreate;
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

    public List<StaggeredAlbums> getStaggeredAlbum() {
        if (photos != null && photos.size() > 0) {
            List<StaggeredAlbums> list = new ArrayList<>();
            //StaggeredAlbums vo = new StaggeredAlbums();
            for (int j = 0; j < photos.size(); j = j + 4) {
                StaggeredAlbums vo = new StaggeredAlbums();
                int limit = ((j + 4) < photos.size()) ? 4 : (photos.size() - j);
                for (int i = 0; i < limit; i++) {
                    switch (i % 4) {
                        case 0:
                            vo.setFirstAlbum(photos.get(i + j));
                            break;
                        case 1:
                            vo.setSecondAlbum(photos.get(i + j));
                            break;
                        case 2:
                            vo.setThirdAlbum(photos.get(i + j));
                            break;
                        case 3:
                            vo.setFourthAlbum(photos.get(i + j));
                            break;
                    }
                }
                list.add(vo);
            }
            return list;
        } else {
            return null;
        }
    }
}

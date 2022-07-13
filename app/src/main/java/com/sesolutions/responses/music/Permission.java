package com.sesolutions.responses.music;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class Permission {
    @SerializedName("canCreateAlbums")
    private int cancreatealbums;
    @SerializedName("canAlbumAddPlaylist")
    private int canalbumaddplaylist;
    @SerializedName("canAlbumAddFavourite")
    private int canalbumaddfavourite;
    @SerializedName("canAlbumShowRating")
    private int canalbumshowrating;
    private JsonElement canEdit;
    private int canDelete;

    private int canCreateVideo;
    private int watchLater;
    private int canCreatePlaylist;
    private int canCreateChannel;
    private int canChannelEnable;
    private boolean can_delete;
    private boolean canCreate;
    private boolean canComment;

    public boolean isCan_delete() {
        return can_delete;
    }

    public void setCan_delete(boolean can_delete) {
        this.can_delete = can_delete;
    }

    public boolean isCanCreate() {
        return canCreate;
    }

    public void setCanCreate(boolean canCreate) {
        this.canCreate = canCreate;
    }

    public boolean isCanComment() {
        return canComment;
    }

    public void setCanComment(boolean canComment) {
        this.canComment = canComment;
    }

    public int getCanCreateVideo() {
        return canCreateVideo;
    }

    public void setCanCreateVideo(int canCreateVideo) {
        this.canCreateVideo = canCreateVideo;
    }

    public int getWatchLater() {
        return watchLater;
    }

    public void setWatchLater(int watchLater) {
        this.watchLater = watchLater;
    }

    public int getCanCreatePlaylist() {
        return canCreatePlaylist;
    }

    public void setCanCreatePlaylist(int canCreatePlaylist) {
        this.canCreatePlaylist = canCreatePlaylist;
    }

    public int getCanCreateChannel() {
        return canCreateChannel;
    }

    public void setCanCreateChannel(int canCreateChannel) {
        this.canCreateChannel = canCreateChannel;
    }

    public int getCanChannelEnable() {
        return canChannelEnable;
    }

    public void setCanChannelEnable(int canChannelEnable) {
        this.canChannelEnable = canChannelEnable;
    }

    public int getCanEdit() {
        if (null != canEdit)
            return canEdit.getAsInt();
        return 0;
    }

    public void setCanEdit(JsonElement canEdit) {
        this.canEdit = canEdit;
    }

    public boolean canEdit() {
        return canEdit.getAsBoolean();
    }

    public int getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(int canDelete) {
        this.canDelete = canDelete;
    }

    public int getCancreatealbums() {
        return cancreatealbums;
    }

    public void setCancreatealbums(int cancreatealbums) {
        this.cancreatealbums = cancreatealbums;
    }

    public int getCanalbumaddplaylist() {
        return canalbumaddplaylist;
    }

    public void setCanalbumaddplaylist(int canalbumaddplaylist) {
        this.canalbumaddplaylist = canalbumaddplaylist;
    }

    public int getCanalbumaddfavourite() {
        return canalbumaddfavourite;
    }

    public void setCanalbumaddfavourite(int canalbumaddfavourite) {
        this.canalbumaddfavourite = canalbumaddfavourite;
    }

    public int getCanalbumshowrating() {
        return canalbumshowrating;
    }

    public void setCanalbumshowrating(int canalbumshowrating) {
        this.canalbumshowrating = canalbumshowrating;
    }
}

package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.utils.SpanUtil;

/**
 * Created by root on 7/12/17.
 */

public class ChannelPhoto {

    @SerializedName("chanelphoto_id")
    private int chanelphotoId;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("chanel_id")
    private int chanelId;
    @SerializedName("order")
    private int order;
    @SerializedName("file_id")
    private int fileId;
    @SerializedName("owner_id")
    private int ownerId;
    @SerializedName("creation_date")
    private String creationDate;
    @SerializedName("modified_date")
    private String modifiedDate;
    @SerializedName("view_count")
    private int viewCount;
    @SerializedName("comment_count")
    private int commentCount;
    @SerializedName("like_count")
    private int likeCount;
    @SerializedName("rating")
    private int rating;
    @SerializedName("favourite_count")
    private int favouriteCount;
    @SerializedName("download_count")
    private int downloadCount;
    @SerializedName("ip_address")
    private String ipAddress;
    @SerializedName("photo_id")
    private int photoId;
    @SerializedName("album_id")
    private int albumId;
    @SerializedName("user_title")
    private String userTitle;
    @SerializedName("is_content_like")
    private boolean isContentLike;
    @SerializedName("content_like_count")
    private int contentLikeCount;
    @SerializedName("shareData")
    private Share sharedata;
    @SerializedName("owner")
    private Owner owner;
    @SerializedName("can_comment")
    private boolean canComment;
    @SerializedName("resource_type")
    private String resourceType;
    @SerializedName("is_like")
    private boolean isLike;
    private Images images;

    public ChannelPhoto(String title) {
        this.title = title;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public int getChanelphotoId() {
        return chanelphotoId;
    }

    public void setChanelphotoId(int chanelphotoId) {
        this.chanelphotoId = chanelphotoId;
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

    public int getChanelId() {
        return chanelId;
    }

    public void setChanelId(int chanelId) {
        this.chanelId = chanelId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getFileId() {
        return fileId;
    }

    public String getImageUrl() {
        if (null != images) {
            if (null != images.getNormal()) {
                return images.getNormal();
            } else {
                return images.getMain();
            }
        }
        return null;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteCount(int favouriteCount) {
        this.favouriteCount = favouriteCount;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public boolean isContentLike() {
        return isContentLike;
    }

    public void setContentLike(boolean contentLike) {
        isContentLike = contentLike;
    }

    public int getContentLikeCount() {
        return contentLikeCount;
    }

    public void setContentLikeCount(int contentLikeCount) {
        this.contentLikeCount = contentLikeCount;
    }

    public Share getSharedata() {
        return sharedata;
    }

    public void setSharedata(Share sharedata) {
        this.sharedata = sharedata;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public boolean isCanComment() {
        return canComment;
    }

    public void setCanComment(boolean canComment) {
        this.canComment = canComment;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }
}

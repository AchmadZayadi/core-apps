package com.sesolutions.responses.videos;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.Owner;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.music.Images;
import com.sesolutions.responses.music.Ratings;
import com.sesolutions.utils.SpanUtil;

import java.util.List;

/**
 * Created by root on 6/12/17.
 */

public class ViewVideo {

    @SerializedName("video_id")
    private int videoId;
    @SerializedName("lecture_id")
    private int lectureId;
    @SerializedName("chanel_id")
    private int channelId;
    @SerializedName("adult")
    private int adult;
    @SerializedName("approve")
    private int approve;
    @SerializedName("title")
    private String title;
    private String overview;
    @SerializedName("description")
    private String description;
    @SerializedName("search")
    private int search;
    @SerializedName("owner_type")
    private String ownerType;
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
    @SerializedName("type")
    private String type;
    @SerializedName("code")
    private String code;
    @SerializedName("photo_id")
    private int photoId;
    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("status")
    private int status;
    @SerializedName("status_message")
    private String status_message;
    @SerializedName("file_id")
    private int fileId;
    @SerializedName("duration")
    private int duration;
    @SerializedName("rotation")
    private int rotation;
    private Owner owner;

    /*   @SerializedName("artists")
       private List<Artist> artists;*/
    @SerializedName("offtheday")
    private int offtheday;
    @SerializedName("favourite_count")
    private int favouriteCount;
    @SerializedName("is_locked")
    private int isLocked;
    @SerializedName("is_featured")
    private int isFeatured;
    @SerializedName("is_sponsored")
    private int isSponsored;
    @SerializedName("is_hot")
    private int isHot;
    @SerializedName("like_count")
    private int likeCount;
    @SerializedName("subcat_id")
    private int subcatId;
    @SerializedName("subsubcat_id")
    private int subsubcatId;
    @SerializedName("importthumbnail")
    private int importthumbnail;

    @SerializedName("tags")
    private List<Tags> tags;
    @SerializedName("share")
    private Share share;
    @SerializedName("iframeURL")
    private String iframeurl;
    private String embedded;
    @SerializedName("rating")
    private JsonElement rating;
    @SerializedName("user_image")
    private String userImage;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("user_title")
    private String userTitle;
    @SerializedName("resource_type")
    private String resourceType;
    @SerializedName("images")
    private Images images;
    private JsonElement cover;
    @SerializedName("profile_image_options")
    private List<Options> profileImageOptions;
    @SerializedName("cover_image_options")
    private List<Options> coverImageOptions;
    @SerializedName("is_content_favourite")
    private boolean contentFavourite;

    @SerializedName("related_lectures")
    private List<Videos> related_lectures;

    @SerializedName("total_videos")
    private int totalVideos;
    @SerializedName("follow_videos")
    private int followVideos;
    @SerializedName("video_count")
    private int videoCount;
    private int photos;
    @SerializedName("follow_count")
    private int followCount;
    @SerializedName("hasWatchlater")
    private boolean hasWatchlater;

    @SerializedName("is_rated")
    private boolean israted;
    private List<Tabs> tabs;

    private List<Options> menus;

    public boolean isContentFavourite() {
        return contentFavourite;
    }

    public String getEmbedded() {
        return embedded;
    }

    public String getType() {
        return type;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public boolean isIsrated() {
        return israted;
    }

    public void setIsrated(boolean israted) {
        this.israted = israted;
    }

    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int getPhotos() {
        return photos;
    }

    public void setPhotos(int photos) {
        this.photos = photos;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public boolean getHasWatchlater() {
        return hasWatchlater;
    }

    public void setHasWatchlater(boolean hasWatchlater) {
        this.hasWatchlater = hasWatchlater;
    }

    public int getTotalVideos() {
        return totalVideos;
    }

    public void setTotalVideos(int totalVideos) {
        this.totalVideos = totalVideos;
    }

    public int getFollowVideos() {
        return followVideos;
    }

    public void setFollowVideos(int followVideos) {
        this.followVideos = followVideos;
    }

    public List<Options> getMenus() {
        return menus;
    }

    public void setMenus(List<Options> menus) {
        this.menus = menus;
    }

    public List<Tabs> getTabs() {
        return tabs;
    }

    public void setTabs(List<Tabs> tabs) {
        this.tabs = tabs;
    }

    public Images getImages() {
        return images;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public Images getCover() {
        Images images = null;
        if (null != cover && cover.isJsonObject()) {
            images = new Gson().fromJson(cover.toString(), Images.class);
        }
        return images;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setCover(JsonElement cover) {
        this.cover = cover;
    }

    public List<Options> getProfileImageOptions() {
        return profileImageOptions;
    }

    public List<Options> getCoverImageOptions() {
        return coverImageOptions;
    }

    public int getVideoId() {
        return videoId;
    }

    public int getLectureId() {
        return lectureId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
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

    public int getSearch() {
        return search;
    }

    public void setSearch(int search) {
        this.search = search;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
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

   /* public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }*/

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return status_message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getOfftheday() {
        return offtheday;
    }

    public void setOfftheday(int offtheday) {
        this.offtheday = offtheday;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteCount(int favouriteCount) {
        this.favouriteCount = favouriteCount;
    }

    public int getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(int isLocked) {
        this.isLocked = isLocked;
    }

    public int getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(int isFeatured) {
        this.isFeatured = isFeatured;
    }

    public int getIsSponsored() {
        return isSponsored;
    }

    public void setIsSponsored(int isSponsored) {
        this.isSponsored = isSponsored;
    }

    public int getIsHot() {
        return isHot;
    }

    public void setIsHot(int isHot) {
        this.isHot = isHot;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getSubcatId() {
        return subcatId;
    }

    public void setSubcatId(int subcatId) {
        this.subcatId = subcatId;
    }

    public int getSubsubcatId() {
        return subsubcatId;
    }

    public void setSubsubcatId(int subsubcatId) {
        this.subsubcatId = subsubcatId;
    }

    public int getImportthumbnail() {
        return importthumbnail;
    }

    public void setImportthumbnail(int importthumbnail) {
        this.importthumbnail = importthumbnail;
    }

    public List<Tags> getTags() {
        return tags;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    public String getIframeurl() {
        return iframeurl;
    }

    public void setIframeurl(String iframeurl) {
        this.iframeurl = iframeurl;
    }

    public List<Videos> getRelatedLectures() {
        return related_lectures;
    }

    public Ratings getRating() {
        if (null != rating && rating.isJsonObject()) return getRatings();
        else return new Ratings(0, " ", getIntRating());
    }

    public void setRating(JsonElement rating) {
        this.rating = rating;
    }

    private Ratings getRatings() {
        Ratings ratings = new Ratings(0, " ", 0);
        if (null != rating && rating.isJsonObject()) {
            ratings = new Gson().fromJson(rating.toString(), Ratings.class);
        }
        return ratings;
    }

    public boolean canShowRating() {
        return null != rating;
    }

    private float getIntRating() {
        if (null != rating)
            return rating.getAsFloat();
        return 0;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public void toggleWatchLater() {
        hasWatchlater = !hasWatchlater;
    }
}

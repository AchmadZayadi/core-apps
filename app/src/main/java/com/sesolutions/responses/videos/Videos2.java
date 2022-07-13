package com.sesolutions.responses.videos;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.music.Ratings;
import com.sesolutions.responses.page.Shortcuts;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SpanUtil;

import java.util.List;


public class Videos2 {

    public boolean isFollowed;
    private boolean following;
    private List<Videos2> videos;
    private Videos2 video;
    @SerializedName("already_added")
    private boolean alreadyAdded;
    private boolean isSelected;
    @SerializedName("chanel_id")
    private int channelId;
    @SerializedName("lecture_id")
    private int lecture_id;
    @SerializedName("is_default")
    private int isdefault;
    @SerializedName("channel_id")
    private int ChannelId;
    @SerializedName("playlist_id")
    private int playlistId;


    @SerializedName("video_id")
    private int videoId;
    @SerializedName("parent_type")
    private String parentType;
    @SerializedName("name")
    private String name;
    @SerializedName("user_image")
    private String user_image;

    @SerializedName("user_username")
    private String user_username;
    @SerializedName("username")
    private String username;

    @SerializedName("artist_id")
    private int artistId;
    @SerializedName("adult")
    private int adult;
    @SerializedName("approve")
    private int approve;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;

    @SerializedName("user_follow_count")
    private int user_follow_count;

    @SerializedName("search")
    private int search;
    @SerializedName("owner_type")
    private String ownerType;
    @SerializedName("channel_image")
    private String channel_image;
    @SerializedName("owner_id")
    private int ownerId;
    @SerializedName("view_count")
    private int viewCount;
    @SerializedName("comment_count")
    private int commentCount;
    /* @SerializedName("type")
     private int type;*/
    @SerializedName("code")
    private String code;
    @SerializedName("photo_id")
    private int photoId;
    @SerializedName("rating")
    private JsonElement rating;
    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("status")
    private int status;
    @SerializedName("file_id")
    private int fileId;
    @SerializedName("duration")
    private String duration;
    @SerializedName("rotation")
    private int rotation;
    @SerializedName("is_content_like")
    private boolean isContentLike;
    /*    @SerializedName("artists")
        private String artists;*/
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
    private String location;
    @SerializedName("like_count")
    private int likeCount;
    @SerializedName("follow_count")
    private int follow_count;
    @SerializedName("subcat_id")
    private int subcatId;
    @SerializedName("subsubcat_id")
    private int subsubcatId;
    @SerializedName("importthumbnail")
    private int importthumbnail;
    @SerializedName("cover")
    private JsonElement cover;
    @SerializedName("watchlater_id")
    private int watchlaterId;
    @SerializedName("user_title")
    private String userTitle;
    /*@SerializedName("is_content_like")
    private boolean isContentLike;*/
    @SerializedName("content_like_count")
    private int contentLikeCount;
    /*@SerializedName("is_content_favourite")
    private boolean isContentFavourite;*/
    @SerializedName("content_favourite_count")
    private int contentFavouriteCount;
    @SerializedName("canWatchlater")
    private boolean canwatchlater;
    @SerializedName("isFollorActive")
    private int isFollowActive;

    @SerializedName("isChannelFollow")
    private int isChannelFollow;

    @SerializedName("is_user_follow")
    private boolean is_user_follow;


    private int isFollow;
    @SerializedName("total_videos")
    private int totalVideos;
    @SerializedName("follow_videos")
    private int followVideos;
    @SerializedName("images")
    private Images images;
    @SerializedName("song")
    private Images song;
    @SerializedName("tags")
    private Tags tags;
    private String image;
    private List<Options> menus;
    private Share share;
    @SerializedName("iframeURL")
    private String iframeURL;
    private int photos;
    @SerializedName("tag")
    private List<String> tag;
    private List<Options> options;
    @SerializedName("results")
    private Result results;
    @SerializedName("follow")
    private Result follow;
    @SerializedName("event_title")
    private String eventTitle;
    @SerializedName("page_title")
    private String pageTitle;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("current_position")
    private int current_position;
    @SerializedName("is_content_favourite")
    private String isContentFavourite;


    @SerializedName("enable_add_shortcut")
    private boolean enable_add_shortcut;

    @SerializedName("shortcut_save")
    private Shortcuts shortcut_save;


    public Shortcuts getShortcut_save() {
        return shortcut_save;
    }

    public void setShortcut_save(Shortcuts shortcut_save) {
        this.shortcut_save = shortcut_save;
    }

    public boolean isEnable_add_shortcut() {
        return enable_add_shortcut;
    }

    public void setEnable_add_shortcut(boolean enable_add_shortcut) {
        this.enable_add_shortcut = enable_add_shortcut;
    }

    public boolean isliked;
    public boolean firstTime = true;
    public boolean fromUnlike = false;
    public boolean unlikeLoop = false;

    public List<String> getTags() {
        return tag;
    }

    public boolean canFavourite() {
        return null != isContentFavourite;
    }

    public int getUserId() {
        return userId;
    }

    public int getIsdefault() {
        return isdefault;
    }

    public Tags getTag() {
        return tags;
    }

    public boolean getliked() {
        return isliked;
    }

    public void setLiked(boolean isliked) {
        this.isliked = isliked;
    }


    public List<Options> getOptions() {
        return options;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Result getResult() {
        return results;
    }

    public String getImageUrl() {
        if (null != images) {
            return images.getNormal();
        } else {
            return image;
        }
    }

    public int getCurrent_position() {
        return current_position - 1;
    }

    public boolean canLike() {
        return !isContentLike;
    }

    public boolean isContentFavourite() {
        return "true".equals(isContentFavourite);
    }

    public void setContentLike(boolean contentLike) {
        this.isContentLike = contentLike;
    }

    public boolean isContentLike() {
        return isContentLike;
    }

    public boolean getFollowed() {
        return isFollowed;
    }

    public void setFollowed(boolean isFollowed) {
        this.isFollowed = isFollowed;
    }

    public Images getImages() {
        return images;
    }

    public Videos2 getVideo() {
        return video;
    }

    public Images getSong() {
        return song;
    }

    public String getParentType() {
        return parentType;
    }

    public String getChannel_image() {
        return channel_image;
    }

    public String getEventTitle() {
        if (null != eventTitle) {
            return eventTitle;
        }
        return pageTitle;
    }

    public void toggleLike() {
        isContentLike = !isContentLike;
    }

    public void toggleFavorite() {
        isContentFavourite = "true".equals(isContentFavourite) ? "false" : "true";
    }

    /*    @Override
        public boolean toggleLike() {
            if (isContentLike) {
                likeCount = likeCount - 1;
            } else {
                likeCount = likeCount + 1;
            }
            isContentLike = !isContentLike;
            return false;
        }*/
    public void setContentFavourite(boolean contentFavourite) {
        isContentFavourite = String.valueOf(contentFavourite);
    }

    public List<Videos2> getVideos() {
        return videos;
    }


    public int getPhotos() {
        return photos;
    }

    public void setPhotos(int photos) {
        this.photos = photos;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isAlreadyAdded() {
        return alreadyAdded;
    }

    public void setAlreadyAdded(boolean alreadyAdded) {
        this.alreadyAdded = alreadyAdded;
    }

    public String getIframeURL() {
        return iframeURL;
    }

    public void setIframeURL(String iframeURL) {
        this.iframeURL = iframeURL;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    public int getFollowVideos() {
        return followVideos;
    }

    public void setFollowVideos(int followVideos) {
        this.followVideos = followVideos;
    }

    public int getTotalVideos() {
        return totalVideos;
    }

    public void setTotalVideos(int totalVideos) {
        this.totalVideos = totalVideos;
    }

    public int getIsFollowActive() {
        return isFollowActive;
    }

    public int getIsChannelFollow() {
        return isChannelFollow;
    }

    public void setIsChannelFollow(int isChannelFollow) {
        this.isChannelFollow = isChannelFollow;
    }

    public boolean getIsUserChannelFollow() {
        return is_user_follow;
    }

    public void setIsUserChannelFollow(boolean isChannelFollow) {
        this.is_user_follow = isChannelFollow;
    }

    public void setIsFollowActive(int isFollowActive) {
        this.isFollowActive = isFollowActive;
    }

    public int getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int isFollow) {
        this.isFollow = isFollow;
    }

    public int getChannelId() {
        return channelId;
    }

    public int getlecture_id() {
        return lecture_id;
    }

    public int getChannelID() {
        return ChannelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public List<Options> getMenus() {
        return menus;
    }

    public void setMenus(List<Options> menus) {
        this.menus = menus;
    }

    public String getName() {
        return name;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_username() {
        return user_username;
    }

    public void setUser_username(String user_username) {
        this.user_username = user_username;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public int getVideoId() {
        return videoId;
    }

    public void toggleWatchLaterId() {
        watchlaterId = watchlaterId == 0 ? 1 : 0;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public int getAdult() {
        return adult;
    }

    public void setAdult(int adult) {
        this.adult = adult;
    }

    public int getApprove() {
        return approve;
    }

    public void setApprove(int approve) {
        this.approve = approve;
    }

    public String getTitle() {
        return title;
    }

    public String getNameOrTitle() {
        return null != title ? title : name;
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

    public int getUser_follow_count() {
        return user_follow_count;
    }

    public void setUser_follow_count(int user_follow_count) {
        this.user_follow_count = user_follow_count;
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

    public void increaseCommentCount() {
        commentCount = commentCount + 1;
    }

    public void decreaseCommentCount() {
        commentCount = commentCount - 1;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
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

 /*   public int getType() {
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

    public JsonElement getRating() {
        return rating;
    }


    public Ratings getRatings() {
        Ratings ratings = new Ratings();
        if (rating.isJsonObject()) {
            ratings = new Gson().fromJson(rating.toString(), Ratings.class);
        }
        return ratings;
    }


    public float getIntRating() {
        if (rating != null && !rating.isJsonObject())
            return rating.getAsFloat();
        return 0;
    }

    public boolean canShowRating() {
        return null != rating;
    }


    public void setRating(JsonElement rating) {
        this.rating = rating;
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

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
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

    public int getFollowers() {
        return follow_count;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }


    public Images getCover() {
        try {
            if (null != cover && cover.isJsonObject()) {
                return new Gson().fromJson(cover.toString(), Images.class);
            }
        } catch (JsonSyntaxException e) {
            CustomLog.e(e);
        }
        return null;
    }

    public void setCover(JsonElement cover) {
        this.cover = cover;
    }

    public int getWatchlaterId() {
        return watchlaterId;
    }

    public String getUserTitle() {
        return userTitle;
    }


   /* public boolean getIsContentLike() {
        return isContentLike;
    }

    public void setIsContentLike(boolean isContentLike) {
        this.isContentLike = isContentLike;
    }


    public boolean getIsContentFavourite() {
        return isContentFavourite;
    }

    public void setIsContentFavourite(boolean isContentFavourite) {
        this.isContentFavourite = isContentFavourite;
    }*/


    public boolean hasLocation() {
        return null != location;
    }

    public String getLocation() {
        return location;
    }

    public String getStatsString(boolean isSesPlugin) {
        if (!isSesPlugin)
            return "\uf164 " + getLikeCount()
                    + "  \uf075 " + getCommentCount()
                    + "  \uf004 " + getFavouriteCount()
                    + "  \uf06e " + getViewCount();
        return "\uf06e " + getViewCount();
    }

    public Result getFollow() {
        return follow;
    }

    public void setFollow(Result follow) {
        this.follow = follow;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

package com.sesolutions.responses;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.utils.SpanUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 8/12/17.
 */

public class Group extends SesModel implements Serializable {

    @SerializedName("photo_id")
    private int photoId;
    @SerializedName("title")
    private String title;
    //  private String location;

    @SerializedName("category_name")
    private String categoryName;
    @SerializedName("owner_id")
    private int ownerId;
    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("subcat_id")
    private int subcatId;
    @SerializedName("publish_date")
    private String publishDate;

    @SerializedName("view_count")
    private JsonElement viewCount;
    @SerializedName("comment_count")
    private int commentCount;
    @SerializedName("like_count")
    private int likeCount;
    @SerializedName("favourite_count")
    private int favouriteCount;


    @SerializedName("owner_title")
    private String ownerTitle;
    @SerializedName("description")
    private String description;
    @SerializedName("member_count")
    private String memberCount;
    @SerializedName("resource_type")
    private String resourceType;

    @SerializedName("content_favourite_count")
    private int contentFavouriteCount;

    private Images images;

    private List<Options> gutterMenu;
    @SerializedName("profile_tabs")
    private List<Options> profileTabs;
    private Share share;
    private Permission permission;
    private Map<String, String> tags;

    @SerializedName("can_favorite")
    private boolean canFavorite;



    /*  @SerializedName("user_title")
      private String userTitle;*/
    @SerializedName("cover_photo")
    private String coverPhoto;
    @SerializedName("owner_photo")
    private String ownerPhoto;
    @SerializedName("created_by")
    private String createdBy;
    @SerializedName("follow_count")
    private int followCount;
    @SerializedName("photo_count")
    private int photoCount;
    @SerializedName("group_id")
    private int groupId;

    @SerializedName("menus")
    private List<Options> menus;

    public List<Options> getMenus() {
        return menus;
    }

    public void setMenus(List<Options> menus) {
        this.menus = menus;
    }

    public List<Options> getGutterMenu() {
        return gutterMenu;
    }


    public void setGutterMenu(List<Options> gutterMenu) {
        this.gutterMenu = gutterMenu;
    }

    public int getFollowCount() {
        return followCount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getOwnerPhoto() {
        return ownerPhoto;
    }

    public void setOwnerPhoto(String ownerPhoto) {
        this.ownerPhoto = ownerPhoto;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public List<Options> getProfileTabs() {
        return profileTabs;
    }

    public void setProfileTabs(List<Options> profileTabs) {
        this.profileTabs = profileTabs;
    }

    public boolean isProfileTabsValid() {
        return profileTabs != null && profileTabs.size() > 0;
    }

    public String getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(String memberCount) {
        this.memberCount = memberCount;
    }

    public String getDescription() {
        return SpanUtil.getHtmlString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }


    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }


    public boolean isCanFavorite() {
        return canFavorite;
    }

    public void setCanFavorite(boolean canFavorite) {
        this.canFavorite = canFavorite;
    }



    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }


    public Images getImages() {
        return images;
    }

    public String getImageUrl() {
        if (images != null) {
            return images.getNormal();
        }
        return "";
    }


    public void setImages(Images images) {
        this.images = images;
    }


    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSubcatId() {
        return subcatId;
    }

    public void setSubcatId(int subcatId) {
        this.subcatId = subcatId;
    }

    public JsonElement getViewCount() {
        return viewCount;
    }

    public int getViewCountInt() {
        if (viewCount != null && viewCount.isJsonPrimitive()) {
            return viewCount.getAsInt();
        }
        return 0;
    }

    public void setViewCount(JsonElement viewCount) {
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


    public int getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteCount(int favouriteCount) {
        this.favouriteCount = favouriteCount;
    }


    public String getOwnerTitle() {
        return ownerTitle;
    }

    public void setOwnerTitle(String ownerTitle) {
        this.ownerTitle = ownerTitle;
    }


    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public int getContentFavouriteCount() {
        return contentFavouriteCount;
    }

    public void setContentFavouriteCount(int contentFavouriteCount) {
        this.contentFavouriteCount = contentFavouriteCount;
    }


    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }


}

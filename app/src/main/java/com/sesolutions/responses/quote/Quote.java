package com.sesolutions.responses.quote;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.utils.SpanUtil;

import java.util.List;

/**
 * Created by prink on 10-02-2018.
 */

public class Quote {

    @SerializedName("images")
    private JsonElement images;
    @SerializedName("content_like_count")
    private int contentLikeCount;
    @SerializedName("is_content_like")
    private boolean isContentLike;
    @SerializedName("user_title")
    private String userTitle;
    @SerializedName("code")
    private String code;
    @SerializedName("mediatype")
    private int mediatype;
    @SerializedName("quotetitle")
    private String quotetitle;
    @SerializedName("offtheday")
    private int offtheday;
    @SerializedName("action_id")
    private int actionId;
    @SerializedName("like_count")
    private int likeCount;
    @SerializedName("comment_count")
    private int commentCount;
    @SerializedName("view_count")
    private int viewCount;
    @SerializedName("modified_date")
    private String modifiedDate;
    @SerializedName("creation_date")
    private String creationDate;
    @SerializedName("owner_id")
    private int ownerId;
    @SerializedName("owner_type")
    private String ownerType;
    @SerializedName("source")
    private String source;
    @SerializedName("subsubcat_id")
    private int subsubcatId;
    @SerializedName("subcat_id")
    private int subcatId;
    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("category_title")
    private String categoryTitle;
    @SerializedName("photo_id")
    private int photoId;
    @SerializedName("title")
    private String title;
    @SerializedName("user_image_url")
    private String userImageUrl;
    private Share share;
    @SerializedName("description")
    private String description;
    private String iframeUrl;
    @SerializedName("wishe_id")
    private int wishId;
    @SerializedName("quote_id")
    private int quoteId;
    @SerializedName("thought_id")
    private int thoughtId;
    @SerializedName("prayer_id")
    private int prayerId;
    @SerializedName("prayertitle")
    private String prayerTitle;
    @SerializedName("thoughttitle")
    private String thoughtTitle;
    @SerializedName("wishetitle")
    private String wishTitle;
    private List<Options> menus;

    public int getWishId() {
        return wishId;
    }

    public void setWishId(int wishId) {
        this.wishId = wishId;
    }

    public String getWishTitle() {
        return wishTitle;
    }

    public void setWishTitle(String wisheTitle) {
        this.wishTitle = wisheTitle;
    }

    public int getThoughtId() {
        return thoughtId;
    }

    public void setThoughtId(int thoughtId) {
        this.thoughtId = thoughtId;
    }

    public int getPrayerId() {
        return prayerId;
    }

    public void setPrayerId(int prayerId) {
        this.prayerId = prayerId;
    }

    public String getPrayerTitle() {
        return prayerTitle;
    }

    public void setPrayerTitle(String prayerTitle) {
        this.prayerTitle = prayerTitle;
    }

    public String getThoughtTitle() {
        return thoughtTitle;
    }

    public void setThoughtTitle(String thoughtTitle) {
        this.thoughtTitle = thoughtTitle;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public Share getShare() {
        return share;
    }

    public String getIframeUrl() {
        return iframeUrl;
    }

    public void setIframeUrl(String iframeUrl) {
        this.iframeUrl = iframeUrl;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    public List<Options> getMenus() {
        return menus;
    }

    public void setMenus(List<Options> menus) {
        this.menus = menus;
    }

    public String getDescription() {
        return SpanUtil.getHtmlString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Images getImages() {
        if (images != null && images.isJsonObject())
            return new Gson().fromJson(images, Images.class);
        else return null;
    }

    public void setImages(JsonElement images) {
        this.images = images;
    }

    public int getContentLikeCount() {
        return contentLikeCount;
    }

    public void setContentLikeCount(int contentLikeCount) {
        this.contentLikeCount = contentLikeCount;
    }

    public boolean isContentLike() {
        return isContentLike;
    }

    public void setContentLike(boolean contentLike) {
        isContentLike = contentLike;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getMediatype() {
        return mediatype;
    }

    public void setMediatype(int mediatype) {
        this.mediatype = mediatype;
    }

    public String getQuotetitle() {
        return quotetitle;
    }

    public void setQuotetitle(String quotetitle) {
        this.quotetitle = quotetitle;
    }

    public int getOfftheday() {
        return offtheday;
    }

    public void setOfftheday(int offtheday) {
        this.offtheday = offtheday;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getSubsubcatId() {
        return subsubcatId;
    }

    public void setSubsubcatId(int subsubcatId) {
        this.subsubcatId = subsubcatId;
    }

    public int getSubcatId() {
        return subcatId;
    }

    public void setSubcatId(int subcatId) {
        this.subcatId = subcatId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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

    public int getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(int quoteId) {
        this.quoteId = quoteId;
    }

    public boolean isPhoto() {
        return mediatype != 2;
    }
}

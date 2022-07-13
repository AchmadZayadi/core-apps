package com.sesolutions.responses.news;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.SesModel;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.music.Permission;
import com.sesolutions.responses.music.Ratings;
import com.sesolutions.utils.CustomLog;
import com.sesolutions.utils.SpanUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by root on 8/12/17.
 */

public class News extends SesModel {

    @SerializedName("classified_id")
    private int classifiedId;
    @SerializedName("article_id")
    private int articleId;
    @SerializedName("news_id")
    private int newsId;
    @SerializedName("recipe_id")
    private int recipeId;
    @SerializedName("custom_url")
    private String customUrl;
    @SerializedName("parent_id")
    private int parentId;
    @SerializedName("photo_id")
    private int photoId;
    @SerializedName("title")
    private String title;
    // @SerializedName("ic_location")
    private String location;
    @SerializedName("owner_type")
    private String ownerType;
    @SerializedName("owner_id")
    private int ownerId;
    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("subcat_id")
    private int subcatId;
    @SerializedName("publish_date")
    private String publishDate;
    @SerializedName("starttime")
    private String starttime;
    @SerializedName("endtime")
    private String endtime;
    @SerializedName("view_count")
    private int viewCount;
    @SerializedName("comment_count")
    private int commentCount;
    @SerializedName("like_count")
    private int likeCount;
    @SerializedName("parent_type")
    private String parentType;
    @SerializedName("seo_title")
    private String seoTitle;
    @SerializedName("seo_keywords")
    private String seoKeywords;
    @SerializedName("featured")
    private int featured;
    @SerializedName("sponsored")
    private int sponsored;
    @SerializedName("verified")
    private int verified;
    @SerializedName("is_approved")
    private int isApproved;
    @SerializedName("favourite_count")
    private int favouriteCount;
    @SerializedName("offtheday")
    private int offtheday;
    @SerializedName("style")
    private int style;
    @SerializedName("rating")
    private JsonElement rating;
    @SerializedName("search")
    private int search;
    @SerializedName("draft")
    private int draft;
    @SerializedName("is_publish")
    private int isPublish;
    @SerializedName("lat")
    private String lat;
    @SerializedName("lng")
    private String lng;
    @SerializedName("owner_title")
    private String ownerTitle;
    @SerializedName("body")
    private String body;
    @SerializedName("resource_type")
    private String resourceType;
    @SerializedName("content_like_count")
    private int contentLikeCount;
    @SerializedName("content_favourite_count")
    private int contentFavouriteCount;
    @SerializedName("news_images")
    private Images newsImages;
    @SerializedName("article_images")
    private Images articleImages;
    @SerializedName("recipe_images")
    private Images recipeImages;
    private Images images;
    @SerializedName("classified_images")
    private Images classifiedImages;
    @SerializedName("user_images")
    private String userImage;
    private List<Options> options;
    private Share share;
    private Permission permission;
    private Map<String, String> tags;

    @SerializedName("can_favorite")
    private boolean canFavorite;

    private Ratings ratings;


    //used in classified
    private String price;

    public int getRecipeId() {
        return recipeId;
    }

    public Images getRecipeImages() {
        return recipeImages;
    }

    public String getPrice() {
        return price;
    }

    public boolean isCanFavorite() {
        return canFavorite;
    }

    public void setCanFavorite(boolean canFavorite) {
        this.canFavorite = canFavorite;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Images getClassifiedImages() {
        return classifiedImages;
    }

    public void setClassifiedImages(Images classifiedImages) {
        this.classifiedImages = classifiedImages;
    }

    public int getClassifiedId() {
        return classifiedId;
    }

    public void setClassifiedId(int classifiedId) {
        this.classifiedId = classifiedId;
    }

    public Images getArticleImages() {
        return articleImages;
    }

    public void setArticleImages(Images articleImages) {
        this.articleImages = articleImages;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
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
        if (null != images) {
            if (null != images.getNormal()) {
                return images.getNormal();
            } else {
                return images.getMain();
            }
        }
        return null;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public List<Options> getOptions() {
        return options;
    }

    public void setOptions(List<Options> options) {
        this.options = options;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public void setCustomUrl(String customUrl) {
        this.customUrl = customUrl;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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


    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
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

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getSeoTitle() {
        return seoTitle;
    }

    public void setSeoTitle(String seoTitle) {
        this.seoTitle = seoTitle;
    }

    public String getSeoKeywords() {
        return seoKeywords;
    }

    public void setSeoKeywords(String seoKeywords) {
        this.seoKeywords = seoKeywords;
    }

    public int getFeatured() {
        return featured;
    }

    public void setFeatured(int featured) {
        this.featured = featured;
    }

    public int getSponsored() {
        return sponsored;
    }

    public void setSponsored(int sponsored) {
        this.sponsored = sponsored;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
        this.isApproved = isApproved;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteCount(int favouriteCount) {
        this.favouriteCount = favouriteCount;
    }

    public int getOfftheday() {
        return offtheday;
    }

    public void setOfftheday(int offtheday) {
        this.offtheday = offtheday;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public JsonElement getRating() {
        return rating;
    }

    public int getIntRating() {
        if (null == rating) return 0;
        int r = 0;
        try {
            r = rating.getAsInt();
        } catch (Exception e) {
            CustomLog.e(e);
        }
        return r;
    }

    public Ratings getRatings() {
        try {
            if (null == ratings) {
                if (rating.isJsonObject()) {
                    ratings = new Gson().fromJson(rating.toString(), Ratings.class);
                } else {
                    ratings = new Ratings();
                    ratings.setTotalRatingAverage(rating.getAsInt());
                }
            }
        } catch (JsonSyntaxException e) {
            CustomLog.e(e);
        }
        return ratings;
    }

    public void setRatings(Ratings ratings) {
        this.ratings = ratings;
    }


    public void setRating(JsonElement rating) {
        this.rating = rating;
    }

    public int getSearch() {
        return search;
    }

    public void setSearch(int search) {
        this.search = search;
    }

    public int getDraft() {
        return draft;
    }

    public void setDraft(int draft) {
        this.draft = draft;
    }

    public int getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(int isPublish) {
        this.isPublish = isPublish;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getOwnerTitle() {
        return ownerTitle;
    }

    public void setOwnerTitle(String ownerTitle) {
        this.ownerTitle = ownerTitle;
    }

    public String getBody() {
        return SpanUtil.getHtmlString(body);
    }

    public String getRawBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }


    public int getContentLikeCount() {
        return contentLikeCount;
    }

    public void setContentLikeCount(int contentLikeCount) {
        this.contentLikeCount = contentLikeCount;
    }

    public Images getNewsImages() {
        return newsImages;
    }


    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
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

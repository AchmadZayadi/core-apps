package com.sesolutions.responses.store.product;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.Courses.course.CourseContent;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.store.StoreContent;

import java.util.List;

public class WishList {

    @SerializedName("wishlist_id")
    @Expose
    private Integer wishlistId;
    @SerializedName("owner_id")
    @Expose
    private Integer ownerId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("photo_id")
    @Expose
    private Integer photoId;
    @SerializedName("cover_id")
    @Expose
    private Integer coverId;
    @SerializedName("product_count")
    @Expose
    private Integer productCount;
    @SerializedName("is_private")
    @Expose
    private Integer isPrivate;
    @SerializedName("creation_date")
    @Expose
    private String creationDate;
    @SerializedName("modified_date")
    @Expose
    private String modifiedDate;
    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @SerializedName("favourite_count")
    @Expose
    private Integer favouriteCount;
    @SerializedName("view_count")
    @Expose
    private Integer viewCount;
    @SerializedName("like_count")
    @Expose
    private Integer likeCount;
    @SerializedName("offtheday")
    @Expose
    private Integer offtheday;
    @SerializedName("courses_count")
    @Expose
    private Integer courses_count;
    @SerializedName("is_sponsored")
    @Expose
    private Integer isSponsored;
    @SerializedName("is_featured")
    @Expose
    private Integer isFeatured;
    @SerializedName("image")
    private String image;

    @SerializedName("owner_title")
    private String ownerTitle;
    @SerializedName("options")
    private List<Options> options;
    @SerializedName("products")
    private List<StoreContent> products;
    private List<CourseContent> courses;

    public String getImage() {
        return image;
    }
    public String getOwnerTitle() {
        return ownerTitle;
    }

    public List<Options> getOptions() {
        return options;
    }

    public List<StoreContent> getProducts() {
        return products;
    }public List<CourseContent> getCourses() {
        return courses;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    @SerializedName("photo")
    @Expose
    private String photo;
    private Share share;

    public Integer getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(Integer wishlistId) {
        this.wishlistId = wishlistId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public Integer getCoverId() {
        return coverId;
    }

    public void setCoverId(Integer coverId) {
        this.coverId = coverId;
    }

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
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

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteCount(Integer favouriteCount) {
        this.favouriteCount = favouriteCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getOfftheday() {
        return offtheday;
    }public Integer getCourses_count() {
        return courses_count;
    }

    public void setOfftheday(Integer offtheday) {
        this.offtheday = offtheday;
    }

    public Integer getIsSponsored() {
        return isSponsored;
    }

    public void setIsSponsored(Integer isSponsored) {
        this.isSponsored = isSponsored;
    }

    public Integer getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Integer isFeatured) {
        this.isFeatured = isFeatured;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
package com.sesolutions.responses.page;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.store.StoreContent;
import com.sesolutions.responses.videos.Tags;

import java.util.List;

public class PageInformation {

    private String title;
    private String description;
    private String owner_name;
    private List<NestedOptions> basicInformation;
    private List<NestedOptions> profileDetail;
    private List<NestedOptions> contactInformation;
    private NestedOptions meta;
    private String timezone;

    public String getOwner_name() {
        return owner_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    @SerializedName("when_and_where")
    private NestedOptions whenNwhere;
    @SerializedName("basic_information")
    private NestedOptions basicInfo;
    @SerializedName("operating_hours")
    private NestedOptions openHours;
    @SerializedName("total_page_liked_by_this_page")
    private int totalLikePages;
    @SerializedName("total_group_liked_by_this_group")
    private int totalLikeGroups;
    @SerializedName("total_business_liked_by_this_business")
    private int totalLikeBusinesses;
    @SerializedName("page_liked_by_this_page")
    private List<Albums> likePages;
    @SerializedName("group_liked_by_this_group")
    private List<Albums> likeGroups;
    @SerializedName("business_liked_by_this_business")
    private List<Albums> likeBusinesses;
    @SerializedName("total_people_who_follow_this")
    private int totalFollowedPeople;
    @SerializedName("people_who_follow_this")
    private List<Albums> followedPeople;
    @SerializedName("total_people_who_liked")
    private int totalLikePeople;
    @SerializedName("people_who_liked")
    private List<Albums> likePeople;
    @SerializedName("total_people_who_favourited")
    private int totalFavouritePeople;
    @SerializedName("people_who_favourited")
    private List<Albums> favouritePeople;
    private List<Tags> tag;

    @SerializedName("tags")
    private List<Tags> tags;

    public List<Tags> getTags() {
        return tags;
    }

    public void setTag(List<Tags> tags) {
        this.tags = tags;
    }

    @SerializedName("product_info")
    private List<StoreContent> productInfo;

    public List<StoreContent> getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(List<StoreContent> productInfo) {
        this.productInfo = productInfo;
    }

    @SerializedName("related_products")
    private List<StoreContent> relatedProducts;

    public List<StoreContent> getRelatedProducts() {
        return relatedProducts;
    }

    public void setRelatedProducts(List<StoreContent> relatedProducts) {
        this.relatedProducts = relatedProducts;
    }


    private List<StoreContent> relatedStores;

    public List<StoreContent> getRelatedStores() {
        return relatedStores;
    }

    public int getTotalLikeBusinesses() {
        return totalLikeBusinesses;
    }

    public List<Albums> getLikeBusinesses() {
        return likeBusinesses;
    }

    public List<Albums> getLikeGroups() {
        return likeGroups;
    }

    public int getTotalLikePages() {
        return totalLikePages;
    }

    public int getTotalLikeGroups() {
        return totalLikeGroups;
    }

    public int getTotalFollowedPeople() {
        return totalFollowedPeople;
    }

    public int getTotalLikePeople() {
        return totalLikePeople;
    }

    public int getTotalFavouritePeople() {
        return totalFavouritePeople;
    }

    public List<Tags> getTag() {
        return tag;
    }

    public NestedOptions getOpenHours() {
        return openHours;
    }

    public NestedOptions getWhenNwhere() {
        return whenNwhere;
    }

    public NestedOptions getBasicInfo() {
        return basicInfo;
    }

    public NestedOptions getMeta() {
        return meta;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<NestedOptions> getBasicInformation() {
        return basicInformation;
    }

    public List<NestedOptions> getProfileDetail() {
        return profileDetail;
    }

    public List<NestedOptions> getContactInformation() {
        return contactInformation;
    }

    public List<Albums> getLikePages() {
        return likePages;
    }

    public List<Albums> getFollowedPeople() {
        return followedPeople;
    }

    public List<Albums> getLikePeople() {
        return likePeople;
    }

    public List<Albums> getFavouritePeople() {
        return favouritePeople;
    }
}

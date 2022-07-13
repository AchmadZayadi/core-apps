package com.sesolutions.responses.fund;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.SesModel;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.page.Locations;
import com.sesolutions.responses.videos.Tags;
import com.sesolutions.utils.CustomLog;

import java.util.List;

public class FundContent extends SesModel {


    @SerializedName("category_title")
    private String categoryTitle;
    @SerializedName("donation_label")
    private String donationLabel;

    @SerializedName("owner_title")
    private String ownerTitle;
    @SerializedName("lng")
    private String lng;
    @SerializedName("lat")
    private String lat;

    @SerializedName("offtheday")
    private int offtheday;
    @SerializedName("rating_count")
    private int ratingCount;
    @SerializedName("is_rated")
    private boolean rated;

    private float rating;

    @SerializedName("verified")
    private int verified;

    @SerializedName("comment_count")
    private int comment_count;
    @SerializedName("like_count")
    private int like_count;
    @SerializedName("view_count")
    private int view_count;
    private String overview;
    @SerializedName("draft")
    private int draft;
    @SerializedName("search")
    private int search;
    @SerializedName("price")
    private float price;
    @SerializedName("gain_price")
    private float gainPrice;
    @SerializedName("show_start_time")
    private int showStartTime;


    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("subcat_id")
    private int subCatId;
    @SerializedName("subsubcat_id")
    private int subSubCatId;
    @SerializedName("custom_url")
    private String customUrl;
    @SerializedName("short_description")
    private String shortDescription;

    private String description;
    @SerializedName("title")
    private String title;
    @SerializedName("resource_id")
    private int resource_id;
    @SerializedName("owner_id")
    private int owner_id;
    @SerializedName("crowdfunding_id")
    private int fundId;
    @SerializedName("order_id")
    private int orderId;

    private List<Tags> tag;
    private Images images;
    @SerializedName("owner_image")
    private Images ownerImage;
    private JsonElement location;

    @SerializedName("progressbar_background_color")
    private String pbBgColor;
    @SerializedName("progressbar_fill_color")
    private String pbFillColor;
    @SerializedName("gain_amount")
    private String gainAmount;
    @SerializedName("donor_count")
    private int donorCount;

    @SerializedName("is_expired")
    private boolean expired;
    @SerializedName("campaign_expiration_label")
    private String statusLabel;
    @SerializedName("crowdfunding_contact_name")
    private String contactName;

    @SerializedName("crowdfunding_contact_email")
    private String contactEmail;
    @SerializedName("crowdfunding_contact_aboutme")
    private String aboutMe;
    @SerializedName("total_amount")
    private String totalAmount;

    private Share share;
    private List<Options> options;
    private List<Options> updateProfilePhoto;
    private List<Options> updateCoverPhoto;
    private JsonElement button;


    public int getRatingCount() {
        return ratingCount;
    }

    public boolean isRated() {
        return rated;
    }

    public boolean isExpired() {
        return expired;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public int getProgressPercent() {
        if (price > 0) {
            return (int) ((gainPrice * 100) / price);
        }
        return 100;
    }

    public List<Options> getUpdateProfilePhoto() {
        return updateProfilePhoto;
    }

    public List<Options> getUpdateCoverPhoto() {
        return updateCoverPhoto;
    }

    public String getDonationLabel() {
        return donationLabel;
    }

    public int getOrderId() {
        return orderId;
    }

    public List<Options> getOptions() {
        return options;
    }

    public Share getShare() {
        return share;
    }


    public String getLocation() {
        try {
            if (null != location) {
                if (location.isJsonObject()) {
                    return new Gson().fromJson(location, Locations.class).getFullAddress();
                } else {
                    return location.getAsString();
                }
            }
        } catch (Exception e) {
            CustomLog.e(e);

        }
        return null;
    }

    public JsonElement getLocationObject() {
        return location;
    }

    public List<Tags> getTag() {
        return tag;
    }

    public Images getImages() {
        return images;
    }

    public String getImageUrl() {
        if (images != null) {
            return images.getNormal();
        } else {
            return "";
        }
    }

    public String getMainImageUrl() {
        if (images != null) {
            return images.getMain();
        } else {
            return "";
        }
    }

    public String getOwnerImageUrl() {
        if (ownerImage != null) {
            return ownerImage.getNormal();
        } else {
            return "";
        }
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public String getOwnerTitle() {
        return ownerTitle;
    }

    public float getRating() {
        return rating;
    }

    public String getOverview() {
        return overview;
    }


    public int getShowStartTime() {
        return showStartTime;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getSubCatId() {
        return subCatId;
    }

    public int getSubSubCatId() {
        return subSubCatId;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public int getFundId() {
        return fundId;
    }

    public String getPbBgColor() {
        return pbBgColor;
    }

    public String getPbFillColor() {
        return pbFillColor;
    }

    public String getGainAmount() {
        return gainAmount;
    }

    public int getDonorCount() {
        return donorCount;
    }

    public String getDonorCountStr() {
        return "" + donorCount;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }


    public int getOfftheday() {
        return offtheday;
    }


    public int getVerified() {
        return verified;
    }


    public int getComment_count() {
        return comment_count;
    }

    public int getLike_count() {
        return like_count;
    }

    public int getView_count() {
        return view_count;
    }

    public int getDraft() {
        return draft;
    }

    public int getSearch() {
        return search;
    }


    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public int getResource_id() {
        return resource_id;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public int updateRatingCount(float rating) {
        this.rating = rating;
        rated = true;
        ratingCount = ratingCount + 1;
        return ratingCount;
    }

    public Options getButton() {
        if (null == button)
            return null;
        if (button.isJsonObject())
            return new Gson().fromJson(button, Options.class);
        if (button.isJsonArray()) {
            return new Gson().fromJson(button.getAsJsonArray().get(0), Options.class);
        } else return null;
    }


    /*public void updateButtons(int optionPosition, Options opt) {
        for (int i = 0; i < buttons.size(); i++) {
            if (i == optionPosition) {
                buttons.get(optionPosition).setLabel(opt.getLabel());
                buttons.get(optionPosition).setName(opt.getName());
                buttons.get(optionPosition).setValue(opt.getValue());
                break;
            }
        }
    }*/
}

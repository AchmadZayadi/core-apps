package com.sesolutions.responses;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.responses.feed.LocationActivity;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.videos.Category;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AheadSoft on 05-04-2018.
 */

public class CommonVO extends Group  implements Serializable {
    private int categoryLevel;

    public CommonVO() {
    }

    @SerializedName("poll_id")
    private int pollId;
    @SerializedName("event_id")
    private int eventId;
    private List<Options> options;
    @SerializedName("starttime")
    private String startTime;
    @SerializedName("endtime")
    private String endTime;
    private String timezone;
    private JsonElement location;
    private String lat;
    private String lng;

    //for event
    private int featured;
    private int sponsored;
    private int verified;
    private int hot;
    @SerializedName("can_add_to_list")
    private int canAddToList;
    @SerializedName("save_id")
    private int saveId;
    @SerializedName("event_status")
    private String eventStatus;
    @SerializedName("is_content_saved")
    private String isContentSaved;
    private List<Options> updateProfilePhoto;
    private List<Options> updateCoverPhoto;
    private List<Options> RSVP;

    //for Browse event list
    @SerializedName("owner_image")
    private Images ownerImage;
    @SerializedName("list_id")
    private int listId;
    @SerializedName("event_count")
    private int eventCount;
    @SerializedName("host_id")
    private int hostId;
    @SerializedName("host_name")
    private String hostName;

    @SerializedName("website")
    private String website;

    private String host;
    private String calanderEndTime;
    private String calanderStartTime;
    private String type;

    private String host_email;//"david@gmail.com",
    private String host_phone;//2147483647,
    private String host_description;//"",
    private String facebook_url;//"",
    private String twitter_url;//"",
    private String website_url;//"",
    private String googleplus_url;//"",


    private String image;
    @SerializedName("custom_term_condition")
    private String customTnC;
    private String overview;

    //Event Category view
    private Category category;
    private List<Category> subCategory;
    //variable used to show differnrt on recycleer view [eg. EventAdapter]
    private int itemType;
    @SerializedName("cover_images")
    private JsonElement coverImages;
    @SerializedName("category_title")
    private String categoryTitle;
    @SerializedName("subsubcategory_title")
    private String subSubCategoryTitle;
    @SerializedName("subcategory_title")
    private String subCategoryTitle;


    public void setOptions(List<Options> options) {
        this.options = options;
    }


    public String getHost() {
        return host;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public boolean canAddToList() {
        return canAddToList != 0;
    }

    public String getSubSubCategoryTitle() {
        return subSubCategoryTitle;
    }

    public String getSubCategoryTitle() {
        return subCategoryTitle;
    }

    public String getCalanderEndTime() {
        return calanderEndTime;
    }

    public String getCalanderStartTime() {
        return calanderStartTime;
    }

    public String getHost_email() {
        return host_email;
    }

    public String getHost_phone() {
        return host_phone;
    }

    public String getHost_description() {
        return host_description;
    }

    public String getFacebook_url() {
        return facebook_url;
    }

    public String getTwitter_url() {
        return twitter_url;
    }

    public String getWebsite_url() {
        return website_url;
    }

    public String getWebsite() {
        return website;
    }

    public String getGoogleplus_url() {
        return googleplus_url;
    }

    public String getEventStatus() {
        return eventStatus;
    }

    public String getType() {
        return type;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public Category getCategory() {
        return category;
    }

    public String getCustomTnC() {
        return customTnC;
    }

    public List<Category> getSubCategory() {
        return subCategory;
    }

    public int getItemType() {
        return itemType;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setSubCategory(List<Category> subCategory) {
        this.subCategory = subCategory;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getOwnerImageUrl() {
        if (ownerImage != null) {
            return ownerImage.getNormal();
        }
        return "";
    }

    public int getSaveId() {
        return saveId;
    }

    public void setSaveId(int saveId) {
        this.saveId = saveId;
    }

    public boolean isContentSaved() {
        return "true".equals(isContentSaved);
    }

    public boolean canSave() {
        return null != isContentSaved;
    }

    public void toggleSave() {
        isContentSaved = String.valueOf(!isContentSaved());
    }

    public String getOverview() {
        return overview;
    }

    public List<Options> getRSVP() {
        return RSVP;
    }

    public String getCoverImageUrl() {
        if (coverImages != null && !coverImages.isJsonObject()) {
            return coverImages.getAsString();
        } else if (coverImages != null && coverImages.isJsonObject()) {
            return new Gson().fromJson(coverImages, Images.class).getNormal();
        }
        return null;
    }

    public void setCoverImageUrl(String url) {
        // TODO: 11-06-2018 add logic to save image url
        //coverImages = new Gson().toJson(url);
    }

    public List<Options> getUpdateProfilePhoto() {
        return updateProfilePhoto;
    }

    public List<Options> getUpdateCoverPhoto() {
        return updateCoverPhoto;
    }

    public List<Options> getOptions() {
        return options;
    }

    public int getListId() {
        return listId;
    }

    public int getEventCount() {
        return eventCount;
    }

    public int getPollId() {
        return pollId;
    }

    public int getEventId() {
        return eventId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getLocationString() {
        if (null != location) {
            if (location.isJsonObject()) {
                LocationActivity vo = new Gson().fromJson(location, LocationActivity.class);
                return vo.getVenue();
            } else {
                return location.getAsString();
            }
        }
        return null;
    }

    public LocationActivity getLocationObject() {

        if (null != location && location.isJsonObject()) {
            return new Gson().fromJson(location, LocationActivity.class);
        }
        return null;
    }

    public boolean isFeatured() {
        return featured != 0;
    }

    public boolean isSponsored() {
        return sponsored != 0;
    }

    public boolean isVerified() {
        return verified != 0;
    }

    public boolean isHot() {
        return hot != 0;
    }

    public int getHostId() {
        return hostId;
    }

    public String getHostName() {
        return hostName;
    }

    public String getImage() {
        return image;
    }

    public String getCategoryString() {
        String s = categoryTitle;
        if (null != subCategoryTitle) {
            s += " -> " + subCategoryTitle;
        }
        if (null != subSubCategoryTitle) {
            s += " -> " + subSubCategoryTitle;
        }

        return s;

    }

    public void setCategoryLevel(int categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public int getCategoryLevel() {
        return categoryLevel;
    }
}

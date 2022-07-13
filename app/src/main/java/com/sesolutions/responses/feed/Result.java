package com.sesolutions.responses.feed;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.utils.Constant;

import java.util.List;

public class Result {
    @SerializedName("user_image")
    private String user_image;
    @SerializedName("user_id")
    private int user_id;
    @SerializedName("user_title")
    private String user_title;
    @SerializedName("feedOnly")
    private boolean feedOnly;
    @SerializedName("filterFeed")
    private String filterFeed;
    @SerializedName("length")
    private String length;
    @SerializedName("itemActionLimit")
    private String itemActionLimit;
    @SerializedName("activity")
    private List<Activity> activity;
    @SerializedName(Constant.ResourceType.PAGE)
    private List<Attribution> pageAttribution;
    @SerializedName(Constant.ResourceType.BUSINESS)
    private List<Attribution> businessAttribution;
    @SerializedName("activity_attribution")
    private Attribution activityAttribution;
    @SerializedName(Constant.ResourceType.GROUP)
    private List<Attribution> groupAttribution;
    @SerializedName("activityCount")
    private int activityCount;
    private int contentCounter;
    @SerializedName("nextid")
    private int nextid;
    @SerializedName("firstid")
    private int firstid;
    @SerializedName("endOfFeed")
    private boolean endOfFeed;
    @SerializedName("loggedin_user_id")
    private int loggedin_user_id;

    public List<Attribution> getModuleAttribution(String moduleName) {
        try {
            switch (moduleName) {
                case Constant.ResourceType.PAGE:
                    return pageAttribution;
               /* case Constant.ResourceType.GROUP:
                    return groupAttribution;*/
                case Constant.ResourceType.BUSINESS:
                    return businessAttribution;

            }
            return pageAttribution;
        } catch (Exception ignore) {

        }
        return null;
    }

    public Attribution getActivityAttribution() {
        return activityAttribution;
    }

    public int getContentCounter() {
        return contentCounter;
    }

    public boolean isFeedOnly() {
        return feedOnly;
    }

    public boolean isEndOfFeed() {
        return endOfFeed;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_title() {
        return user_title;
    }

    public void setUser_title(String user_title) {
        this.user_title = user_title;
    }

    public boolean getFeedOnly() {
        return feedOnly;
    }

    public void setFeedOnly(boolean feedOnly) {
        this.feedOnly = feedOnly;
    }

    public String getFilterFeed() {
        return filterFeed;
    }

    public void setFilterFeed(String filterFeed) {
        this.filterFeed = filterFeed;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getItemActionLimit() {
        return itemActionLimit;
    }

    public void setItemActionLimit(String itemActionLimit) {
        this.itemActionLimit = itemActionLimit;
    }

    public List<Activity> getActivity() {
        return activity;
    }

    public void setActivity(List<Activity> activity) {
        this.activity = activity;
    }

    public int getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(int activityCount) {
        this.activityCount = activityCount;
    }

    public int getNextid() {
        return nextid;
    }

    public void setNextid(int nextid) {
        this.nextid = nextid;
    }

    public int getFirstid() {
        return firstid;
    }

    public void setFirstid(int firstid) {
        this.firstid = firstid;
    }

    public boolean getEndOfFeed() {
        return endOfFeed;
    }

    public void setEndOfFeed(boolean endOfFeed) {
        this.endOfFeed = endOfFeed;
    }

    public int getLoggedin_user_id() {
        return loggedin_user_id;
    }

    public void setLoggedin_user_id(int loggedin_user_id) {
        this.loggedin_user_id = loggedin_user_id;
    }
}

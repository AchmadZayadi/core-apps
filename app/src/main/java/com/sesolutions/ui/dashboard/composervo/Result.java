package com.sesolutions.ui.dashboard.composervo;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ReactionPlugin;

import java.io.Serializable;
import java.util.List;

public class Result  {
    @SerializedName("reaction_plugin")
    private List<ReactionPlugin> reaction_plugin;
    @SerializedName("defaultCurrency")
    private String defaultCurrency;
    @SerializedName("sesfeelingactivity")
    private int sesfeelingactivity;
    @SerializedName("user_image")
    private String user_image;
    @SerializedName("user_id")
    private int user_id;
    @SerializedName("user_title")
    private String user_title;
    @SerializedName("userSelectedSettings")
    private List<String> userSelectedSettings;
    @SerializedName("privacySetting")
    private boolean privacySetting;
    @SerializedName("privacyOptions")
    private List<PrivacyOptions> privacyOptions;
    @SerializedName("feedSearchOptions")
    private List<FeedSearchOptions> feedSearchOptions;
    @SerializedName("enableComposer")
    private boolean enableComposer;
    @SerializedName("activityStikersMenu")
    private List<ActivityStikersMenu> activityStikersMenu;
    @SerializedName("composerOptions")
    private List<ComposerOptions> composerOptions;
    @SerializedName("textStringColor")
    private List<TextColorString> textStringColor;
    private List<FeedBg> feedBgStatusPost;
    @SerializedName("loggedin_user_id")
    private int loggedin_user_id;
    @SerializedName("intelligent_notifications")
    private IntelligentNotification intelligentNotifications;


    public String getWelcomeHtml() {
        if (null != intelligentNotifications) {
            return intelligentNotifications.getWelcomeHtml();
        }
        return null;
    }

    public String getFriendHtml() {
        if (null != intelligentNotifications) {
            return intelligentNotifications.getFriendHtml();
        }
        return null;
    }

    public String getUserBirthdayHtml() {
        if (null != intelligentNotifications) {
            return intelligentNotifications.getUserBirthdayHtml();
        }
        return null;
    }

    public String getViewerBirthdayHtml() {
        if (null != intelligentNotifications) {
            return intelligentNotifications.getFriendBirthdayHtml();
        }
        return null;
    }

    public String getDobHtml() {
        if (null != intelligentNotifications) {
            return intelligentNotifications.getDobHtml();
        }
        return null;
    }

    public List<FeedBg> getFeedBgStatusPost() {
        return feedBgStatusPost;
    }

    public List<TextColorString> getTextStringColor() {
        return textStringColor;
    }

    public List<ReactionPlugin> getReaction_plugin() {
        return reaction_plugin;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_title() {
        return user_title;
    }

    public void setUser_title(String user_title) {
        this.user_title = user_title;
    }

    public List<PrivacyOptions> getPrivacyOptions() {
        return privacyOptions;
    }

    public List<FeedSearchOptions> getFeedSearchOptions() {
        return feedSearchOptions;
    }

    public boolean getEnableComposer() {
        return enableComposer;
    }

    public List<ActivityStikersMenu> getActivityStikersMenu() {
        return activityStikersMenu;
    }

    public List<ComposerOptions> getComposerOptions() {
        return composerOptions;
    }


    


}

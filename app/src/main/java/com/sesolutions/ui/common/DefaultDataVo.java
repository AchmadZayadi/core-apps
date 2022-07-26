package com.sesolutions.ui.common;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.SearchVo;
import com.sesolutions.responses.SlideShowImage;
import com.sesolutions.ui.welcome.NameValue;
import com.sesolutions.utils.AppConfiguration;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.CustomLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 3/11/17.
 */

public class DefaultDataVo {


    @SerializedName("result")
    private Result result;
    @SerializedName("session_id")
    private String sessionId;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public static class Result {

        private String loginBackgroundImage;
        private String forgotPasswordBackgroundImage;
        private String rateusBackgroundImage;
        private List<String> reaction;
        @SerializedName("shareTextForFeed")
        private String shareTextForFeed;

        @SerializedName("enableHeaderFixedFeed")
        private boolean enableHeaderFixedFeed;

        @SerializedName("is_core_activity")
        private boolean is_core_activity;

        @SerializedName("core_modules_enabled")
        private JsonObject core_modules_enabled;

        @SerializedName("enableTabbarTitle")
        private boolean enableTabbarTitle;

        @SerializedName("descriptionTrucationLimitFeed")
        private int descriptionTrucationLimitFeed = 200;

        @SerializedName("limitForTablet")
        private String limitForTablet;

        @SerializedName("limitForPhone")
        private int limitForPhone;

        @SerializedName("enableLoggedinUserphoto")
        private boolean enableLoggedinUserphoto;
        @SerializedName("is_story_enabled")
        private boolean isStoryEnabled;
        @SerializedName("is_livestream_enabled")
        private boolean isLiveStreamingEnabled;
        @SerializedName("is_sesmultiplecurrency_enabled")
        private boolean isMulticurrencyEnabled;
        @SerializedName("linux_base_url")
        private String linuxBaseUrl;

        public String getLinuxBaseUrl() {
            return linuxBaseUrl;
        }

        public void setLinuxBaseUrl(String linuxBaseUrl) {
            this.linuxBaseUrl = linuxBaseUrl;
        }

        @SerializedName("default_currency")
        private String default_currency;

        @SerializedName("siteTitle")
        private String siteTitle;
        @SerializedName("video_url")
        private String videoUrl;

        @SerializedName("agora_app_id_live_streaming")
        private String agoraappliveid;

        public JsonObject getCore_modules_enabled() {
            return core_modules_enabled;
        }

        {
            is_core_activity = false;
        }

        public boolean isIs_core_activity() {
            return is_core_activity;
        }

        public void setIs_core_activity(boolean is_core_activity) {
            this.is_core_activity = is_core_activity;
        }

        public void setCore_modules_enabled(JsonObject core_modules_enabled) {
            this.core_modules_enabled = core_modules_enabled;
        }

        public String getAgoraappliveid() {
            return agoraappliveid;
        }

        public void setAgoraappliveid(String agoraappliveid) {
            this.agoraappliveid = agoraappliveid;
        }

        @SerializedName("isNavigationTransparent")
        private boolean isNavigationTransparent;

        @SerializedName("memberImageShapeIsRound")
        private boolean memberImageShapeIsRound;

        @SerializedName("titleHeaderType")
        private String titleHeaderType;
        @SerializedName("loggedin_user_id")
        private int loggedinUserId;
        @SerializedName("disable_welcome_screen")
        private int disable_welcome_screen;
        @SerializedName("isEnableSkipLogin")
        private boolean isEnableSkipLogin;
        private List<SlideShowImage> slideshow;
        private List<SlideShowImage> graphics;
        @SerializedName("theme_styling")
        private List<NameValue> themeStyling;
        @SerializedName("video_slideshow")
        private boolean videoSlideshow;



        @SerializedName("force_update")
        private boolean forceUpdate;
        @SerializedName("version_app")
        private String versionApp;
        @SerializedName("version_update")
        private String versionUpdate;


        private DemoUser demoUser;
        private List<SearchVo> socialLogin;
        public boolean isForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        public String getVersionApp() {
            return versionApp;
        }

        public void setVersionApp(String versionApp) {
            this.versionApp = versionApp;
        }

        public String getVersionUpdate() {
            return versionUpdate;
        }

        public void setVersionUpdate(String versionUpdate) {
            this.versionUpdate = versionUpdate;
        }

        public List<String> getReaction() {
            return reaction;
        }

        public String getLoginBackgroundImage() {
            return loginBackgroundImage;
        }

        public String getForgotPasswordBackgroundImage() {
            return forgotPasswordBackgroundImage;
        }

        public List<SearchVo> getSocialLogin() {
            return socialLogin;
        }

        public DemoUser getDemoUser() {
            return demoUser;
        }

        public String getRateusBackgroundImage() {
            return rateusBackgroundImage;
        }

        public boolean isVideoSlideshow() {
            return videoSlideshow;
        }

        public void setVideoSlideshow(boolean videoSlideshow) {
            this.videoSlideshow = videoSlideshow;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public boolean isWelcomeScreenEnabled() {
            return 0 == disable_welcome_screen;
        }

        public List<SlideShowImage> getGraphics() {
            return graphics;
        }

        public String getShareTextForFeed() {
            return shareTextForFeed;
        }

        public void setShareTextForFeed(String shareTextForFeed) {
            this.shareTextForFeed = shareTextForFeed;
        }

        public boolean isEnableHeaderFixedFeed() {
            return enableHeaderFixedFeed;
        }

        public void setEnableHeaderFixedFeed(boolean enableHeaderFixedFeed) {
            this.enableHeaderFixedFeed = enableHeaderFixedFeed;
        }

        public boolean isEnableTabbarTitle() {
            return enableTabbarTitle;
        }

        public void setEnableTabbarTitle(boolean enableTabbarTitle) {
            this.enableTabbarTitle = enableTabbarTitle;
        }

        public int getDescriptionTrucationLimitFeed() {
            return descriptionTrucationLimitFeed;
        }

        public void setDescriptionTrucationLimitFeed(int descriptionTrucationLimitFeed) {
            this.descriptionTrucationLimitFeed = descriptionTrucationLimitFeed;
        }

        public String getLimitForTablet() {
            return limitForTablet;
        }

        public void setLimitForTablet(String limitForTablet) {
            this.limitForTablet = limitForTablet;
        }

        public int getLimitForPhone() {
            return limitForPhone;
        }

        public void setLimitForPhone(int limitForPhone) {
            this.limitForPhone = limitForPhone;
        }

        public boolean isEnableLoggedinUserphoto() {
            return enableLoggedinUserphoto;
        }

        public boolean getisMulticurrencyEnabled() {
            return isMulticurrencyEnabled;
        }

        public void setEnableLoggedinUserphoto(boolean enableLoggedinUserphoto) {
            this.enableLoggedinUserphoto = enableLoggedinUserphoto;
        }

        public String getSiteTitle() {
            return siteTitle;
        }

        public void setSiteTitle(String siteTitle) {
            this.siteTitle = siteTitle;
        }

        public boolean isNavigationTransparent() {
            return isNavigationTransparent;
        }

        public void setNavigationTransparent(boolean navigationTransparent) {
            isNavigationTransparent = navigationTransparent;
        }

        public boolean isMemberImageShapeIsRound() {
            return memberImageShapeIsRound;
        }

        public void setMemberImageShapeIsRound(boolean memberImageShapeIsRound) {
            this.memberImageShapeIsRound = memberImageShapeIsRound;
        }

        public String getTitleHeaderType() {
            return titleHeaderType;
        }

        public void setTitleHeaderType(String titleHeaderType) {
            this.titleHeaderType = titleHeaderType;
        }

        public List<NameValue> getThemeStyling() {
            if (themeStyling == null)
                themeStyling = new ArrayList<>();
            return themeStyling;
        }

        public void setThemeStyling(List<NameValue> themeStyling) {
            this.themeStyling = themeStyling;
        }

        public boolean isEnableSkipLogin() {
            return isEnableSkipLogin;
        }

        public void setEnableSkipLogin(boolean enableSkipLogin) {
            isEnableSkipLogin = enableSkipLogin;
        }

        public List<SlideShowImage> getSlideshow() {
            return slideshow;
        }

        public void setSlideshow(List<SlideShowImage> slideshow) {
            this.slideshow = slideshow;
        }

        public int getLoggedinUserId() {
            return loggedinUserId;
        }

        public void setLoggedinUserId(int loggedinUserId) {
            this.loggedinUserId = loggedinUserId;
        }

        public void setAppConfiguration() {
            new Thread(() -> {
                try {
                    AppConfiguration.enableHeaderFixedFeed = enableHeaderFixedFeed;
                    AppConfiguration.enableTabbarTitle = enableTabbarTitle;
                    AppConfiguration.descriptionTrucationLimitFeed = descriptionTrucationLimitFeed;
                    AppConfiguration.limitForTablet = limitForTablet;
                    AppConfiguration.limitForPhone = limitForPhone;
                    Constant.RECYCLE_ITEM_THRESHOLD = limitForPhone;
                    AppConfiguration.enableLoggedinUserphoto = enableLoggedinUserphoto;
                    AppConfiguration.siteTitle = siteTitle;
                    AppConfiguration.isStoryEnabled = isStoryEnabled;
                    AppConfiguration.isNavigationTransparent = isNavigationTransparent;
                    AppConfiguration.memberImageShapeIsRound = memberImageShapeIsRound;

                    try {
                        AppConfiguration.titleHeaderType = Integer.parseInt(titleHeaderType);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        AppConfiguration.titleHeaderType = 0;
                    }

                    AppConfiguration.SHARE = shareTextForFeed;
                    AppConfiguration.isLiveStreamingEnabled = isLiveStreamingEnabled;
                    AppConfiguration.isMulticurrencyEnabled = isMulticurrencyEnabled;
                    AppConfiguration.LINUX_BASE_URL = linuxBaseUrl;
                    AppConfiguration.DEFAULT_CURRENCY = default_currency;
                    AppConfiguration.AGORALIVESTRIMMINGID = agoraappliveid;
                } catch (Exception e) {
                    CustomLog.e(e);
                }
            }).run();
        }

        public static class DemoUser {

            @SerializedName("innerText")
            private String innerText;
            @SerializedName("headingText")
            private String headingText;
            @SerializedName("defaultimage")
            private String defaultimage;
            @SerializedName("users")
            private List<Users> users;

            public class Users {
                @SerializedName("user_id")
                private int user_id;
                @SerializedName("image_url")
                private String image_url;

                public int getUser_id() {
                    return user_id;
                }

                public String getImage_url() {
                    return image_url;
                }
            }

            public String getInnerText() {
                return innerText;
            }

            public String getHeadingText() {
                return headingText;
            }

            public String getDefaultimage() {
                return defaultimage;
            }

            public List<Users> getUsers() {
                return users;
            }
        }
    }
}

package com.sesolutions.ui.drawer;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DrawerModel {

    @SerializedName("result")
    private Result result;

    public Result getResult() {
        return result;
    }


    public static class Menus {
        @SerializedName("type")
        private int type;
        private String module;
        @SerializedName("label")
        private String label;
        @SerializedName("icon")
        private String icon;
        @SerializedName("url")
        private String url;
        @SerializedName("class")
        private String clazz;

        public int getType() {
            return type;
        }

        public String getLabel() {
            return label;
        }

        public String getIcon() {
            return icon;
        }

        public String getUrl() {
            return url;
        }

        public String getClazz() {
            return clazz;
        }

        public String getModule() {
            return module;
        }
    }

    public static class Result {
        @SerializedName("cover_photo")
        private String coverPhoto;
        @SerializedName("title")
        private String title;
        @SerializedName("user_photo")
        private String userPhoto;
        @SerializedName("menus")
        private List<Menus> menus;
        @SerializedName("notification_count")
        private int notificationCount;
        @SerializedName("friend_req_count")
        private int friendReqCount;
        @SerializedName("message_count")
        private int messageCount;
        @SerializedName("loggedin_user_id")
        private int loggedinUserId;

        public String getCoverPhoto() {
            return coverPhoto;
        }


        public String getTitle() {
            return title;
        }


        public String getUserPhoto() {
            return userPhoto;
        }


        public List<Menus> getMenus() {
            return menus;
        }


        public int getLoggedinUserId() {
            return loggedinUserId;
        }

    }
}
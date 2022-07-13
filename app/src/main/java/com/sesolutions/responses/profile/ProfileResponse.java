package com.sesolutions.responses.profile;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.Friends;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.responses.feed.Options;

import java.util.List;

/**
 * Created by root on 13/12/17.
 */

public class ProfileResponse {

    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result {
        @SerializedName("is_self")
        private boolean isSelf;
        @SerializedName("loggedin_user_id")
        private int loggedInUserId;
        @SerializedName("profile_image_options")
        private List<Options> profileImageOption;
        @SerializedName("cover_image_options")
        private List<Options> coverImageOption;
        @SerializedName("cover_photo")
        private String coverPhoto;
        @SerializedName("profile_tabbed_menus")
        private List<Options> profileTabbedMenu;
        @SerializedName("gutterMenu")
        private List<Options> gutterMenu;
        @SerializedName("profile_tabs")
        private List<Options> profileTabs;
        @SerializedName("user_photo")
        private List<Images> userPhotos;
        @SerializedName("profile_info")
        private List<ProfileInfo> profileInfo;
        @SerializedName("profile_friends")
        private List<Friends> profileFriends;
        @SerializedName("mutual_friends")
        private List<Friends> mutualFriends;
        private Profile profile;

        public List<Friends> getMutualFriends() {
            return mutualFriends;
        }

        public void setMutualFriends(List<Friends> mutualFriends) {
            this.mutualFriends = mutualFriends;
        }

        public List<Options> getCoverImageOption() {
            return coverImageOption;
        }

        public void setCoverImageOption(List<Options> coverImageOption) {
            this.coverImageOption = coverImageOption;
        }

        public String getCoverPhoto() {
            return coverPhoto;
        }

        public void setCoverPhoto(String coverPhoto) {
            this.coverPhoto = coverPhoto;
        }

        public boolean isSelf() {
            return isSelf;
        }

        public void setSelf(boolean self) {
            isSelf = self;
        }

        public int getLoggedInUserId() {
            return loggedInUserId;
        }

        public void setLoggedInUserId(int loggedInUserId) {
            this.loggedInUserId = loggedInUserId;
        }

        public List<Options> getProfileImageOption() {
            return profileImageOption;
        }

        public void setProfileImageOption(List<Options> profileImageOption) {
            this.profileImageOption = profileImageOption;
        }

        public List<Options> getProfileTabbedMenu() {
            return profileTabbedMenu;
        }

        public List<Options> getGutterMenu() {
            return gutterMenu;
        }

        public void setGutterMenu(List<Options> gutterMenu) {
            this.gutterMenu = gutterMenu;
        }

        public List<Options> getProfileTabs() {
            return profileTabs;
        }

        public void setProfileTabs(List<Options> profileTabs) {
            this.profileTabs = profileTabs;
        }

        public List<Images> getUserPhotos() {
            return userPhotos;
        }

        public void setUserPhotos(List<Images> userPhotos) {
            this.userPhotos = userPhotos;
        }

        public List<ProfileInfo> getProfileInfo() {
            return profileInfo;
        }

        public void setProfileInfo(List<ProfileInfo> profileInfo) {
            this.profileInfo = profileInfo;
        }

        public List<Friends> getProfileFriends() {
            return profileFriends;
        }

        public void setProfileFriends(List<Friends> profileFriends) {
            this.profileFriends = profileFriends;
        }

        public Profile getProfile() {
            return profile;
        }

        public void setProfile(Profile profile) {
            this.profile = profile;
        }
    }
}

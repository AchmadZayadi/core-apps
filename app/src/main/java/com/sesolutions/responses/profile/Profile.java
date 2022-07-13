package com.sesolutions.responses.profile;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 13/12/17.
 */

public class Profile {

    @SerializedName("user_id")
    private int userId;
    @SerializedName("email")
    private String email;
    @SerializedName("username")
    private String username;
    @SerializedName("displayname")
    private String displayname;
    @SerializedName("photo_id")
    private int photoId;
    @SerializedName("status")
    private String status;
    @SerializedName("status_date")
    private String statusDate;
    @SerializedName("password")
    private String password;
    @SerializedName("salt")
    private String salt;
    @SerializedName("locale")
    private String locale;
    @SerializedName("language")
    private String language;
    @SerializedName("timezone")
    private String timezone;
    @SerializedName("search")
    private int search;
    @SerializedName("show_profileviewers")
    private int showProfileviewers;
    @SerializedName("level_id")
    private int levelId;
    @SerializedName("invites_used")
    private int invitesUsed;
    @SerializedName("extra_invites")
    private int extraInvites;
    @SerializedName("enabled")
    private int enabled;
    @SerializedName("verified")
    private int verified;
    @SerializedName("approved")
    private int approved;
    @SerializedName("creation_date")
    private String creationDate;
    @SerializedName("modified_date")
    private String modifiedDate;
    @SerializedName("lastlogin_date")
    private String lastloginDate;
    @SerializedName("member_count")
    private int memberCount;
    @SerializedName("view_count")
    private int viewCount;
    @SerializedName("comment_count")
    private int commentCount;
    @SerializedName("like_count")
    private int likeCount;
    @SerializedName("blocked_levels")
    private String blockedLevels;
    @SerializedName("blocked_networks")
    private String blockedNetworks;
    @SerializedName("infomusic_playlist")
    private int infomusicPlaylist;
    @SerializedName("cover")
    private int cover;
    @SerializedName("cover_position")
    private String coverPosition;
    @SerializedName("follow_count")
    private int followCount;
    @SerializedName("ic_location")
    private String location;
    @SerializedName("rating")
    private float rating;
    @SerializedName("user_verified")
    private int userVerified;
    @SerializedName("cool_count")
    private int coolCount;
    @SerializedName("funny_count")
    private int funnyCount;
    @SerializedName("useful_count")
    private int usefulCount;
    @SerializedName("featured")
    private int featured;
    @SerializedName("sponsored")
    private int sponsored;
    @SerializedName("vip")
    private int vip;
    @SerializedName("offtheday")
    private int offtheday;
    @SerializedName("user_photo")
    private String userPhoto;

    @SerializedName("total_mutual_friend_count")
    private int totalMutualFriendCount;
    @SerializedName("total_friend_count")
    private int totalFriendCount;

    public String getTotalFriendCount() {
        if (totalFriendCount > 0) {
            return "" + totalFriendCount;
        } else if (totalMutualFriendCount > 0) {
            return "" + totalMutualFriendCount;
        }
        return null;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public int getSearch() {
        return search;
    }

    public void setSearch(int search) {
        this.search = search;
    }

    public int getShowProfileviewers() {
        return showProfileviewers;
    }

    public void setShowProfileviewers(int showProfileviewers) {
        this.showProfileviewers = showProfileviewers;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public int getInvitesUsed() {
        return invitesUsed;
    }

    public void setInvitesUsed(int invitesUsed) {
        this.invitesUsed = invitesUsed;
    }

    public int getExtraInvites() {
        return extraInvites;
    }

    public void setExtraInvites(int extraInvites) {
        this.extraInvites = extraInvites;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
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

    public String getLastloginDate() {
        return lastloginDate;
    }

    public void setLastloginDate(String lastloginDate) {
        this.lastloginDate = lastloginDate;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
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

    public String getBlockedLevels() {
        return blockedLevels;
    }

    public void setBlockedLevels(String blockedLevels) {
        this.blockedLevels = blockedLevels;
    }

    public String getBlockedNetworks() {
        return blockedNetworks;
    }

    public void setBlockedNetworks(String blockedNetworks) {
        this.blockedNetworks = blockedNetworks;
    }

    public int getInfomusicPlaylist() {
        return infomusicPlaylist;
    }

    public void setInfomusicPlaylist(int infomusicPlaylist) {
        this.infomusicPlaylist = infomusicPlaylist;
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }

    public String getCoverPosition() {
        return coverPosition;
    }

    public void setCoverPosition(String coverPosition) {
        this.coverPosition = coverPosition;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getUserVerified() {
        return userVerified;
    }

    public void setUserVerified(int userVerified) {
        this.userVerified = userVerified;
    }

    public int getCoolCount() {
        return coolCount;
    }

    public void setCoolCount(int coolCount) {
        this.coolCount = coolCount;
    }

    public int getFunnyCount() {
        return funnyCount;
    }

    public void setFunnyCount(int funnyCount) {
        this.funnyCount = funnyCount;
    }

    public int getUsefulCount() {
        return usefulCount;
    }

    public void setUsefulCount(int usefulCount) {
        this.usefulCount = usefulCount;
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

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getOfftheday() {
        return offtheday;
    }

    public void setOfftheday(int offtheday) {
        this.offtheday = offtheday;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }
}

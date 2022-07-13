package com.sesolutions.responses.forum;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;

import java.util.List;

public class Post {

    //custom variable to show animation
    private int showAnimation;

    @Expose
    @SerializedName("menus")
    private List<Menus> menus;
    @Expose
    @SerializedName("options")
    private List<Options> options;
    @Expose
    @SerializedName("post_count")
    private int post_count;
    @Expose
    @SerializedName("reputations")
    private String reputations;
    @Expose
    @SerializedName("thanks")
    private String thanks;
    @Expose
    @SerializedName("resource_type")
    private String resource_type;
    @Expose
    @SerializedName("owner_images")
    private String owner_images;
    @Expose
    @SerializedName("owner_title")
    private String owner_title;
    @Expose
    @SerializedName("moderator_label")
    private String moderator_label;
    @Expose
    @SerializedName("thanks_count")
    private int thanks_count;
    @Expose
    @SerializedName("like_count")
    private int like_count;
    @Expose
    @SerializedName("edit_id")
    private int edit_id;
    @Expose
    @SerializedName("file_id")
    private int file_id;
    @Expose
    @SerializedName("modified_date")
    private String modified_date;
    @Expose
    @SerializedName("creation_date")
    private String creation_date;
    @Expose
    @SerializedName("body")
    private String body;
    public String getSignature() {
        return signature;
    }
    @SerializedName("signature")
    private String signature;
    @Expose
    @SerializedName("user_id")
    private int user_id;
    @Expose
    @SerializedName("forum_id")
    private int forum_id;
    @Expose
    @SerializedName("topic_id")
    private int topic_id;
    @Expose
    @SerializedName("post_id")
    private int post_id;
    public int isShowAnimation() {
        return showAnimation;
    }

    @SerializedName("share")
    public Share share;

    @SerializedName("is_content_like")
    public String isContentLike ;
    @SerializedName("isThanks")
    public boolean isContentThank ;

    @SerializedName("canEdit")
    private boolean canEdit;
    @SerializedName("canDelete")
    private boolean canDelete;
    @SerializedName("canPost")
    private boolean canPost;

    public boolean isCanEdit() {
        return canEdit;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public boolean canPost() { return canPost; }


    public boolean isContentLike() {
        return "true".equals(isContentLike);
    }
    public void setContentLike(boolean contentLike) {
        isContentLike = String.valueOf(contentLike);
    }
    public boolean canLike() {
        return null != isContentLike;
    }

    public boolean canThank() { return  isContentThank; }

    public void setContentThank(boolean contentThank) {
        isContentThank = contentThank;
    }
    public void setShowAnimation(int showAnimation) {
        this.showAnimation = showAnimation;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    public List<Menus> getMenus() {
        return menus;
    }

    public List<Options> getOptions() {
        return options;
    }

    public int getPost_count() {
        return post_count;
    }

    public String getReputations() {
        return reputations;
    }

    public String getThanks() {
        return thanks;
    }

    public String getResource_type() {
        return resource_type;
    }

    public String getOwner_images() {
        return owner_images;
    }

    public String getOwner_title() {
        return owner_title;
    }

    public String getModerator_label() {
        return moderator_label;
    }

    public int getThanks_count() {
        return thanks_count;
    }

    public int getLike_count() {
        return like_count;
    }

    public int getEdit_id() {
        return edit_id;
    }

    public int getFile_id() {
        return file_id;
    }

    public String getModified_date() {
        return modified_date;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public String getBody() {
        return body;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getForum_id() {
        return forum_id;
    }

    public int getTopic_id() {
        return topic_id;
    }

    public int getPost_id() {
        return post_id;
    }

    public static class Menus {
        @Expose
        @SerializedName("label")
        private String label;
        @Expose
        @SerializedName("name")
        private String name;

        public String getLabel() {
            return label;
        }

        public String getName() {
            return name;
        }
    }

}

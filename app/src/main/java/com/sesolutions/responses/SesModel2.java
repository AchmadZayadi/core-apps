package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.page.Shortcuts;

import java.io.Serializable;

public class SesModel2 implements Serializable {
    //custom field
    private int showAnimation;

    @SerializedName("creation_date")
    private String creationDate;
    @SerializedName("modified_date")
    private String modifiedDate;


    @SerializedName("user_id")
    private int userId;
    @SerializedName("is_content_like")
    private String isContentLike;
    @SerializedName("is_content_favourite")
    private String isContentFavourite;
    @SerializedName("is_content_follow")
    private String isContentFollow;






    public int isShowAnimation() {
        return showAnimation;
    }

    public void setShowAnimation(int showAnimation) {
        this.showAnimation = showAnimation;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isContentLike() {
        return "true".equals(isContentLike);
    }

    public boolean canLike() {
        return null != isContentLike;
    }

    public boolean canFavourite() {
        return null != isContentFavourite;
    }

    public boolean canFollow() {
        return null != isContentFollow;
    }

    public boolean isContentFavourite() {
        return "true".equals(isContentFavourite);
    }

    public boolean isContentFollow() {
        return "true".equals(isContentFollow);
    }


    public void setContentLike(boolean contentLike) {
        isContentLike = String.valueOf(contentLike);
    }

    public void setContentFavourite(boolean contentFavourite) {
        isContentFavourite = String.valueOf(contentFavourite);
    }

    public void setContentFollow(boolean contentFollow) {
        isContentFollow = String.valueOf(contentFollow);
    }

    public void toggleLike() {
        isContentLike = "true".equals(isContentLike) ? "false" : "true";
    }

    public void toggleFavorite() {
        isContentFavourite = "true".equals(isContentFavourite) ? "false" : "true";
    }
}

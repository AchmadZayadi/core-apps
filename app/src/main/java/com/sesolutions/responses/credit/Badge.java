package com.sesolutions.responses.credit;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Images;

public class Badge {
    @SerializedName("badge_id")
    private int badgeId;
    private String title;
    private String description;
    @SerializedName("photo_id")
    private int photoId;
    private int countMember;
    private Images images;
    @SerializedName("count_label")
    private String countLabel;

    public int getBadgeId() {
        return badgeId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPhotoId() {
        return photoId;
    }

    public int getCountMember() {
        return countMember;
    }

    public Images getImages() {
        return images;
    }

    public String getCountLabel() {
        return countLabel;
    }

    public String getImageUrl() {
        return null != images ? images.getNormal() : null;
    }
}

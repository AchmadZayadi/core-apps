package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Images;

public class User {
    private int resourceId;
    @SerializedName("resource_type")
    private String resourceType;
    @SerializedName("user_image")
    private Images userImage;

    public int getResourceId() {
        return resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getUserImage() {
        if (null != userImage) return userImage.getIcon();
        return null;

    }
}

package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;

public class LikeData extends CustomLike {
    private String type;
    @SerializedName("user_image")
    private String userImage;
    @SerializedName("user_id")
    private int userId;
    private int count;
    private String image;


    public String getUserImage() {
        return userImage;
    }

    public int getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public String getImage() {
        return image;
    }
}

package com.sesolutions.responses;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 14/12/17.
 */

public class ReactionPlugin extends CustomLike{
    @SerializedName("reaction_id")
    private int reactionId;
    @SerializedName("image")
    private String image;
    @SerializedName("imageUrl")
    private String imageUrl;

    public int getReactionId() {
        return reactionId;
    }

    public void setReactionId(int reactionId) {
        this.reactionId = reactionId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
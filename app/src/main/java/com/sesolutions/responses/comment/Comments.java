package com.sesolutions.responses.comment;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ReactionPlugin;

import java.util.List;

/**
 * Created by root on 20/12/17.
 */

public class Comments {
    @SerializedName("like_stats")
    private LikeStats likeStats;
    private List<ReactionPlugin> likes;

    public LikeStats getLikeStats() {
        return likeStats;
    }

    public List<ReactionPlugin> getLikes() {
        return likes;
    }

    public void setLikes(List<ReactionPlugin> likes) {
        this.likes = likes;
    }

    public void setLikeStats(LikeStats likeStats) {
        this.likeStats = likeStats;
    }
}

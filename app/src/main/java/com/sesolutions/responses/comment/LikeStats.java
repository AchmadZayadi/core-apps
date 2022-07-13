package com.sesolutions.responses.comment;

/**
 * Created by root on 20/12/17.
 */

public class LikeStats {
    private int total_likes;
    private String likes_fluent_list;

    public int getTotal_likes() {
        return total_likes;
    }

    public void setTotal_likes(int total_likes) {
        this.total_likes = total_likes;
    }

    public String getLikes_fluent_list() {
        return likes_fluent_list;
    }

    public void setLikes_fluent_list(String likes_fluent_list) {
        this.likes_fluent_list = likes_fluent_list;
    }
}

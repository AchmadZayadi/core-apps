package com.sesolutions.responses;

public class User2 {

    /**
     * user_image : http://sandbox.socialnetworking.solutions/apptesting/public/user/2e/02/31a1a88a7969509714ce7c5e14a1ea77.png
     * user_id : 9
     * user_title : tags tag
     * user_username : abajai
     * follow_count:20
     *
     */

    private String user_image;
    private int user_id;
    private String user_title;
    private String user_username;

    private String tick_video_id;
    private String follow_count;
    private String following_count;
    private String total_video_like_count;
    private boolean is_content_follow=false;

    public String getFollow_count() {
        return follow_count;
    }

    public void setFollow_count(String follow_count) {
        this.follow_count = follow_count;
    }

    public String getFollowing_count() {
        return following_count;
    }

    public void setFollowing_count(String following_count) {
        this.following_count = following_count;
    }

    public String getTick_video_id() {
        return tick_video_id;
    }

    public void setTick_video_id(String tick_video_id) {
        this.tick_video_id = tick_video_id;
    }

    public void setTotal_video_like_count(String total_video_like_count) {
        this.total_video_like_count = total_video_like_count;
    }

    public String getTotal_video_like_count() {
        return total_video_like_count;
    }

    public void setIs_content_follow(boolean is_content_follow) {
        this.is_content_follow = is_content_follow;
    }

    public boolean isIs_content_follow() {
        return is_content_follow;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_title() {
        return user_title;
    }

    public void setUser_title(String user_title) {
        this.user_title = user_title;
    }

    public String getUser_username() {
        return user_username;
    }

    public void setUser_username(String user_username) {
        this.user_username = user_username;
    }
}

package com.sesolutions.responses;

public class UserModel {

    private String user_image;
    private int user_id;
    private String user_title;
    private String user_username;

    public UserModel(String user_title, String user_username) {
        this.user_title=user_title;
        this.user_username=user_username;
    }


    public String getUser_image() {
        return user_image;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getUser_title() {
        return user_title;
    }

    public String getUser_username() {
        return user_username;
    }

}

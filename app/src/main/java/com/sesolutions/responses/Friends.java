package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.videos.Tags;

/**
 * Created by root on 9/11/17.
 */

public class Friends extends Tags{

    //FOR MEMBER
    @SerializedName("id")
    private int id;
    @SerializedName("label")
    private String label;
    @SerializedName("photo")
    private String photo;

    //FOR PROFILE INFO
   // private String title;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("user_image")
    private String userImage;
    private int startIndex;
    private int endIndex;

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void increamentIndex(int by) {
        startIndex = startIndex + by;
        endIndex = endIndex + by;
    }

    public void decreamentIndex() {
        startIndex = startIndex - 1;
        endIndex = endIndex - 1;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

/*    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }*/

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}

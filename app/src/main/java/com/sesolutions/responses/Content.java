package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;

public class Content {
    private int id;
    private String Guid;
    private String title;
    private String images;


    public int getId() {
        return id;
    }



    public String getGuid() {
        return Guid;
    }

    public String getTitle() {
        return title;
    }

    public String getImages() {
        return images;
    }
}

package com.sesolutions.responses.feed;

import com.google.gson.JsonElement;
import com.sesolutions.responses.CustomLike;
import com.sesolutions.utils.CustomLog;

public class Like extends CustomLike {
    private String name;
    private String type;
    private String image;


    public Like(String image, String title) {
        this.image = image;
        this.customTitle = title;
    }

    public Like() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getImage() {
        return image;
    }


}

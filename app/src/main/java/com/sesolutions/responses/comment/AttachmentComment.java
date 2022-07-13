package com.sesolutions.responses.comment;

import com.sesolutions.responses.feed.Images;

/**
 * Created by root on 28/11/17.
 */

public class AttachmentComment {
    private int id;
    private String type;
    private Images images;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }
}

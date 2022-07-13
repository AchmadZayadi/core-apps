package com.sesolutions.ui.dashboard.composervo;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by root on 30/1/18.
 */

public class FeedBg {
    private String photo;
    @SerializedName("background_id")
    private int bgId;

    public String getPhoto() {
        return photo;
    }


    public int getBgId() {
        return bgId;
    }

}

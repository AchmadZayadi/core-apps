package com.sesolutions.responses.music;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 4/12/17.
 */

public class Artist {
    @SerializedName("artist_id")
    private int artistId;
    private String name;
    private Images images;

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }
}

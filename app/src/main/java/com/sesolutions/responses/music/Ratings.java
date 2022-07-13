package com.sesolutions.responses.music;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 2/12/17.
 */

public class Ratings {

    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("total_rating_average")
    private float totalRatingAverage;

    public Ratings(int code, String message, float rating) {
        this.code = code;
        this.message = message;
        this.totalRatingAverage = rating;
    }

    public Ratings() {

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public float getTotalRatingAverage() {
        return totalRatingAverage;
    }

    public void setTotalRatingAverage(float totalRatingAverage) {
        this.totalRatingAverage = totalRatingAverage;
    }
}

package com.sesolutions.responses.videos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 5/12/17.
 */

public class VideoBrowse2 {
    @SerializedName("result")
    private Result2 result;
    @SerializedName("session_id")
    private String sessionId;

    public Result2 getResult() {
        return result;
    }

    public void setResult(Result2 result) {
        this.result = result;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

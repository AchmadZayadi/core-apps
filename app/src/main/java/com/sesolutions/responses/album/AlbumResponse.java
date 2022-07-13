package com.sesolutions.responses.album;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WarFly on 12/10/2017.
 */

public class AlbumResponse {

    @SerializedName("result")
    private Result result;
    @SerializedName("session_id")
    private String sessionId;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
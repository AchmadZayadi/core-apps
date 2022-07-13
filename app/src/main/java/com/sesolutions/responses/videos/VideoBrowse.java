package com.sesolutions.responses.videos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 5/12/17.
 */

public class VideoBrowse {
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

package com.sesolutions.responses.music;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 29/11/17.
 */

public class MusicBrowse {

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

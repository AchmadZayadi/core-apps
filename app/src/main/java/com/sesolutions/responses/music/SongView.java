package com.sesolutions.responses.music;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by root on 29/11/17.
 */

public class SongView {

    @SerializedName("result")
    private ResultSongView result;
    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("related_songs")
    private List<Albums> relatedsongs;

    public List<Albums> getRelatedsongs() {
        return relatedsongs;
    }

    public void setRelatedsongs(List<Albums> relatedsongs) {
        this.relatedsongs = relatedsongs;
    }

    public ResultSongView getResult() {
        return result;
    }

    public void setResult(ResultSongView result) {
        this.result = result;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

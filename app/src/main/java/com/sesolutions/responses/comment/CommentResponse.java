package com.sesolutions.responses.comment;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 27/11/17.
 */

public class CommentResponse {
    @SerializedName("session_id")
    private String sessionId;
    private String error;
    @SerializedName("error_message")
    private String errorMessage;

    @SerializedName("result")
    private Result result;

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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

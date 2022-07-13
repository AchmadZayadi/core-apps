package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.signin.Result;

/**
 * Created by root on 7/11/17.
 */

public class SignInResponse extends ErrorResponse {

    @SerializedName("result")
    private Result result;
    @SerializedName("aouth_token")
    private String aouthToken;
    @SerializedName("session_id")
    private String sessionId;


    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getAouthToken() {
        return aouthToken;
    }

    public void setAouthToken(String aouthToken) {
        this.aouthToken = aouthToken;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

}

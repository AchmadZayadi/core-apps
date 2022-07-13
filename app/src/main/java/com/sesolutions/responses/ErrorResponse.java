package com.sesolutions.responses;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    private String message;
    private String error;
    @SerializedName("error_message")
    private String errorMessage;
   // @SerializedName("aouth_token")
   // private String authToken;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return message;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return TextUtils.isEmpty(error);
    }
}

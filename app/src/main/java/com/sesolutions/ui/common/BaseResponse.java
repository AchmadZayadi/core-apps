package com.sesolutions.ui.common;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 2/11/17.
 */

public class BaseResponse<T> {
    private T result;
    private String message;
    private String error;
    @SerializedName("error_message")
    private String errorMessage;
    @SerializedName("aouth_token")
    private String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public boolean isSuccess() {
        return TextUtils.isEmpty(error);
    }
}

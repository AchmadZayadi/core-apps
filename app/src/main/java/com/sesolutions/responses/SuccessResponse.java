package com.sesolutions.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by root on 2/11/17.
 */

public class SuccessResponse extends ErrorResponse {

    private Result result;

    public Result getResult() {
        return result;
    }

    public static class Result {
        @SerializedName("loggedin_user_id")
        private int loggedin_user_id;
        @SerializedName("saved_id")
        private int saved_id;
        @SerializedName("message")
        private String message;
        @SerializedName("success_message")
        private String successMessage;

        public int getLoggedin_user_id() {
            return loggedin_user_id;
        }

        public int getSaved_id() {
            return saved_id;
        }

        public String getMessage() {
            return message;
        }

        public String getSuccessMessage() {
            return successMessage;
        }
    }
}

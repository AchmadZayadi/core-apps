package com.sesolutions.ui.welcome;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ValidateFieldError;
import com.sesolutions.utils.Constant;

import java.util.List;

/**
 * Created by root on 3/11/17.
 */

public class FormError {

    @SerializedName("result")
    private JsonElement result;
    private String error;
    private String message;

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

    public Result getResult() {
        if (null != result && result.isJsonObject())
            return new Gson().fromJson(result, Result.class);
        else return null;
    }

    public void setResult(JsonElement result) {
        this.result = result;
    }


    public static class Result {
        @SerializedName("valdateFieldsError")
        private List<ValidateFieldError> valdatefieldserror;
        @SerializedName("loggedin_user_id")
        private int loggedinUserId;


        public List<ValidateFieldError> getValdatefieldserror() {
            return valdatefieldserror;
        }

        public void setValdatefieldserror(List<ValidateFieldError> valdatefieldserror) {
            this.valdatefieldserror = valdatefieldserror;
        }

        public int getLoggedinUserId() {
            return loggedinUserId;
        }

        public void setLoggedinUserId(int loggedinUserId) {
            this.loggedinUserId = loggedinUserId;
        }

        public String fetchFirstNErrors() {
            String errors = Constant.EMPTY;
            if (null != valdatefieldserror && valdatefieldserror.size() > 0) {
                int totalCount = valdatefieldserror.size() > Constant.SHOW_TOTAL_ERROR_COUNT ?
                        Constant.SHOW_TOTAL_ERROR_COUNT : valdatefieldserror.size();
                for (int i = 0; i < totalCount; i++) {
                    errors = errors + valdatefieldserror.get(i).getErrormessage() + "\n";
                }
            }
            return errors.trim();
        }
    }
}

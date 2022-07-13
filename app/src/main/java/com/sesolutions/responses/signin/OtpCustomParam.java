package com.sesolutions.responses.signin;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.CustomParam;

public class OtpCustomParam extends CustomParam {
    @SerializedName("user_id")
    private int userId;
    @SerializedName("otpsms_duration")
    private int otpsmsDuration;
    private String action;

    public int getUserId() {
        return userId;
    }

    public int getOtpsmsDuration() {
        return otpsmsDuration;
    }

    public String getAction() {
        return action;
    }
}

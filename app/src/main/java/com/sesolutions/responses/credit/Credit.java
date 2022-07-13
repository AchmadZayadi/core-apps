package com.sesolutions.responses.credit;

import com.google.gson.annotations.SerializedName;

public class Credit {
    @SerializedName("activity_type")
    private String activityType;
    @SerializedName("first_activity")
    private String firstActivity;
    @SerializedName("next_activity")
    private String nextActivity;
    @SerializedName("max_perday")
    private String maxPerDay;
    @SerializedName("deduction")
    private String deduction;

    public String getActivityType() {
        return activityType;
    }

    public String getFirstActivity() {
        return firstActivity;
    }

    public String getNextActivity() {
        return nextActivity;
    }

    public String getMaxPerDay() {
        return maxPerDay;
    }

    public String getDeduction() {
        return deduction;
    }
}

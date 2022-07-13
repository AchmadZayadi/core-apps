package com.sesolutions.responses.fund;

import com.google.gson.annotations.SerializedName;

public class Donor {
    @SerializedName("reward_id")
    private int rewardId;
    @SerializedName("user_id")
    private int userId;
    @SerializedName(value="donor_title", alternate={"title"})
    private String title;
    //@SerializedName("donor_photo")
    @SerializedName(value="donor_photo", alternate={"reward_photo"})
    private String photo;
    //@SerializedName("reward_photo")
   // private String photoReward;
    @SerializedName("total_donated_amount")
    private String totalAmount;
    @SerializedName("minimum_donation_amount")
    private String minAmount;
    //@SerializedName("crowdfunding_creation_date")
    @SerializedName(value="crowdfunding_creation_date", alternate={"creation_date"})
    private String creationDate;
    private String body;

    public String getTitle() {
        return title;
    }

    public String getPhoto() {
        return photo;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public int getRewardId() {
        return rewardId;
    }

    public int getUserId() {
        return userId;
    }

    public String getMinAmount() {
        return minAmount;
    }

    public String getBody() {
        return body;
    }
}

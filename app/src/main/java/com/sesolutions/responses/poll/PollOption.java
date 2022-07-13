package com.sesolutions.responses.poll;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.User;

import java.util.List;

public class PollOption {

    @SerializedName("name")
    private String name;
    @SerializedName("value")
    private String value;

    @SerializedName("more_user_link")
    private boolean moreUserLink;
    private boolean userVoted;
    @SerializedName("vote_percent")
    private String votePercent;
    @SerializedName("image_type")
    private int imageType;
    @SerializedName("file_id")
    private int fileId;
    @SerializedName("has_voted")
    private boolean hasVoted;
    @SerializedName("image_id")
    private int ImageId;
    @SerializedName("votes")
    private int votes;
    @SerializedName("poll_option")
    private String pollOption;
    @SerializedName("option_image")
    private String optionImage;
    private String image;
    @SerializedName("poll_id")
    private int pollId;
    @SerializedName("poll_option_id")
    private int pollOptionId;
    @SerializedName("voted_user")
    private List<User> votedUser;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setVotePercent(String votePercent) {
        this.votePercent = votePercent;
    }

    public List<User> getVotedUser() {
        return votedUser;
    }

    public String getOptionImage() {
        return null != optionImage ? optionImage : image;
    }

    public boolean isMoreUserLink() {
        return moreUserLink;
    }

    public String getVotePercent() {
        return votePercent;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    public int getImageId() {
        return ImageId;
    }

    public int getImageType() {
        return imageType;
    }

    public int getFileId() {
        return fileId;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getPollOption() {
        return pollOption;
    }

    public int getPollId() {
        return pollId;
    }

    public int getPollOptionId() {
        return pollOptionId;
    }

    public int getProgress(int voteCount) {
        if (voteCount != 0 && votes != 0) {
            return ((votes * 100) / voteCount);
        }
        return 2;
    }

    public boolean canShowVotedUserImage(int position) {
        return (votedUser.size() > position);
    }

}

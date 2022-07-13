package com.sesolutions.responses.Courses.Test;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.User;

import java.util.List;

public class Answer {

    @SerializedName("more_user_link")
    private boolean moreUserLink;
    private boolean userVoted;
    @SerializedName("vote_percent")
    private String votePercent;
    @SerializedName("image_type")
    private int imageType;
    @SerializedName("usertest_id")
    private int usertest_id;
    @SerializedName("file_id")
    private int fileId;
    @SerializedName("has_voted")
    private boolean hasVoted;
    @SerializedName("image_id")
    private int ImageId;
    @SerializedName("votes")
    private int votes;
    @SerializedName("is_true")
    private int is_true;
    @SerializedName("is_attempt")
    private int is_attempt;
    @SerializedName("poll_option")
    private String pollOption;
    @SerializedName("answer")
    private String answer;
    @SerializedName("option_image")
    private String optionImage;
    private String image;
    @SerializedName("poll_id")
    private int pollId;
    @SerializedName("poll_option_id")
    private int pollOptionId;
    @SerializedName("voted_user")
    private List<User> votedUser;
    private Testquestion testquestion;

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

    public int getIs_true() {
        return is_true;
    }

    public int getisAttempt() {
        return is_attempt;
    }

    public Testquestion getTestquestion() {
        return testquestion;
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

    public String getAnswer() {
        return answer;
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

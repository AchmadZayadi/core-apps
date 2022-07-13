package com.sesolutions.responses.qna;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.R;
import com.sesolutions.responses.SesModel;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.videos.Tags;
import com.sesolutions.utils.Constant;
import com.sesolutions.utils.SpanUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Question extends SesModel {
    @SerializedName("question_id")
    private int questionId;
    @SerializedName("answer_id")
    private int answerId;
    private String title;
    private String description;
    @SerializedName("owner_title")
    private String ownerTitle;
    @SerializedName("owner_image")
    private Images ownerImage;

    @SerializedName("upvote_count")
    private int upvoteCount;
    @SerializedName("downvote_count")
    private int downvoteCount;
    @SerializedName("vote_count")
    private int totalVoteCount;
    @SerializedName("answer_count")
    private int answerCount;
    private int multi;
    private int mediatype;
    private String location;
    @SerializedName("best_answer")
    private int bestAnswer;
    @SerializedName("total_vote")
    private int totalVote;

    private List<Tags> tag;
    private List<Options> options;
    private Share share;
    @SerializedName("question_photo")
    private String questionPhoto;
    @SerializedName("has_voted")
    private String hasVoted;
    private String code;
    @SerializedName("open_close_label")
    private String openCloseLabel;
    @SerializedName("poll_label")
    private Options pollLabel;

    @SerializedName("category_title")
    private String categoryTitle;
    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("view_count")
    private int viewCount;
    @SerializedName("comment_count")
    private int commentCount;
    @SerializedName("like_count")
    private int likeCount;
    @SerializedName("favourite_count")
    private int favouriteCount;
    private int optionVoteCount = 3;
    @SerializedName("has_voted_id")
    private JsonElement hasVotedId;
    private int votedOptionId;
    @SerializedName("follow_count")
    private int followCount;
    @SerializedName("owner_id")
    private int ownerId;

    public int getOptionVoteCount() {
        return optionVoteCount;
    }

    public String getQuestionGuid() {
        return Constant.ResourceType.QA + "_" + questionId;
    }

    public void setVotedOptionId(int votedOptionId) {
        this.votedOptionId = votedOptionId;
    }

    public void setTotalVote(int totalVote) {
        this.totalVote = totalVote;
    }

    public int getVotedOptionId() {
        //// TODO: 16-11-2018   Add logic for multiple option selection in future
        if (votedOptionId > 0) {
            return votedOptionId;
        } else if (null != hasVotedId) {
            try {
                votedOptionId = hasVotedId.getAsJsonArray().get(0).getAsInt();
            } catch (Exception ignore) {
            }
        }
        return votedOptionId;
    }

    public boolean hasVoted(String type) {
        return type.equals(hasVoted);
    }

    public String getQuestionPhoto() {
        return questionPhoto;
    }

    public String getOpenCloseLabel() {
        return openCloseLabel;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getTotalVote() {
        return totalVote;
    }

    public int getTotalVoteCount() {
        return totalVoteCount;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerTitle() {
        return ownerTitle;
    }

    public int getUpvoteCount() {
        return upvoteCount;
    }

    public int getDownvoteCount() {
        return downvoteCount;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public int getMulti() {
        return multi;
    }

    public String getLocation() {
        return location;
    }

    public int getBestAnswer() {
        return bestAnswer;
    }

    public List<Tags> getTag() {
        return tag;
    }

    public Share getShare() {
        return share;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

    public int getFollowCount() {
        return followCount;
    }

    public List<Options> getOptions() {
        return options;
    }

    public String getOwnerText(Context context) {
        if (null != categoryTitle) {
            return SpanUtil.getHtmlString(context.getString(R.string.by_owner_in_category, ownerTitle, categoryTitle));
        } else {
            return context.getString(R.string.by_owner, ownerTitle);
        }
    }

    public JsonElement getHasVotedId() {
        return hasVotedId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public String getOwnerImage() {
        return null != ownerImage ? ownerImage.getProfile() : "";
    }

    public String getVoteCount() {
        return "" + (upvoteCount - downvoteCount);
    }

    public Map<String, Object> getGuidMap(String voteType, Map<String, Object> map) {
        updateVoteCount(voteType);
        map.put("itemguid", Constant.ResourceType.QA + "_" + questionId);
        return map;
    }

    public Map<String, Object> getAnsGuidMap(String voteType, Map<String, Object> map) {
        updateVoteCount(voteType);
        map.put("itemguid", Constant.ResourceType.ANSWER + "_" + answerId);
        return map;
    }

    public void updateVoteCount(int upVote, int downVote) {
        upvoteCount = upVote;
        downvoteCount = downVote;
        totalVoteCount = upVote + downVote;
    }

    public void updateVoteCount(String voteType) {
        if (null != hasVoted) {
            if (Constant.KEY_UP_VOTED.equals(voteType)) {
                upvoteCount = upvoteCount + 1;
                downvoteCount = downvoteCount - 1;
            } else {
                upvoteCount = upvoteCount - 1;
                downvoteCount = downvoteCount + 1;
            }
        } else {
            if (Constant.KEY_UP_VOTED.equals(voteType)) {
                upvoteCount = upvoteCount + 1;
            } else {
                downvoteCount = downvoteCount + 1;
            }
        }
        hasVoted = voteType;
        totalVoteCount = upvoteCount + downvoteCount;
    }

    public Options getPollLabel() {
        return pollLabel;
    }

    public Map<String, Object> setAsBestAns(HashMap<String, Object> map) {
        setShowAnimation(1);
        bestAnswer = 1;
        map.put(Constant.KEY_ID, answerId);
        return map;
    }

    public void setBestAnswer(int i) {
        bestAnswer = i;
    }

    public boolean canChooseBestAnswer(int loggedInId) {
        return ownerId == loggedInId;
    }

    public boolean canVotePoll() {
        return true;
    }

    public int getMediaType() {
        return mediatype;
    }

    public boolean hasUserVotedThisOption(int pollOptionId) {
        return getVotedOptionId() == pollOptionId;
    }

    public String getCode() {
        return code;
    }
}

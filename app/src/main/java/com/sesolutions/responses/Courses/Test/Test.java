package com.sesolutions.responses.Courses.Test;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.R;
import com.sesolutions.responses.SesModel;
import com.sesolutions.responses.feed.Images;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.responses.feed.UrlParams;
import com.sesolutions.responses.poll.PollOption;
import com.sesolutions.utils.SpanUtil;

import java.util.List;

public class Test extends SesModel {

    @SerializedName("poll_id")
    private int pollId;
    @SerializedName("total_marks")
    private int total_marks;

    @SerializedName("page_id")
    private int pageId;//103,
    @SerializedName("is_closed")
    private int isClosed;//0,
    @SerializedName("is_passed")
    private int is_passed;//0,
    private String title;
    private String description;
    @SerializedName("can_change_vote")
    private String changeVote;
    @SerializedName("test_question_count")
    private int test_question_count;
    //"2018-09-26 05:10:13",
    @SerializedName("view_count")
    private int viewCount;//0,
    @SerializedName("comment_count")
    private int commentCount;//0,
    @SerializedName("like_count")
    private int likeCount;//0,
    @SerializedName("vote_count")
    private int voteCount;//1,
    @SerializedName("favourite_count")
    private int favouriteCount;//0,
    private int search;//1,
    @SerializedName("closed")
    private int closed;//0,
    private String view_privacy;//"everyone",
    @SerializedName("owner_title")
    private String ownerTitle;//"Stanley R. Card",
    private Images owner_image;
    @SerializedName("content_title")
    private String contentTitle;//"hsisjsnnskd",
    private List<Options> menus;
    private List<Answer> usertest;


    //view poll
    private Share share;

    @SerializedName("resource_type")
    private String resourceType;
    @SerializedName("can_change_votes")
    private boolean canchangevotes;
    @SerializedName("can_edit")
    private boolean canEdit;
    @SerializedName("can_delete")
    private boolean canDelete;
    @SerializedName("has_voted")
    private boolean hasVoted;
    @SerializedName("has_voted_id")
    private int hasVotedId;
    @SerializedName("can_vote")
    private boolean canVote;
    private String token;

    public int getIsClosed() {
        return isClosed;
    }

    public int getIs_passed() {
        return is_passed;
    }

    public List<Options> getMenus() {
        return menus;
    }

    public boolean canChangeVote() {
        return "1".equals(changeVote);
    }

    public boolean canEdit() {
        return canEdit;
    }

    public boolean canChangevotes() {
        return canchangevotes;
    }

    public String getResourceType() {
        return resourceType;
    }

    public boolean canDelete() {
        return canDelete;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    public int isclosed() {
        return closed;
    }

    public Share getShare2() {
        return share;
    }

    public boolean canVote() {
        if (hasVoted) {
            return canChangeVote();
        }
        return canVote;
    }

    public void setMenus(List<Options> menus) {
        this.menus = menus;
    }

    public String getToken() {
        return token;
    }

    public boolean isUserVotedThisOption(int optionId) {
        return hasVoted && hasVotedId == optionId;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public void setOptions(List<Answer> options) {
        this.usertest = options;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    public void setHasVotedId(int hasVotedId) {
        this.hasVotedId = hasVotedId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPollId() {
        return pollId;
    }

    public int getTotal_marks() {
        return total_marks;
    }

    public int getPageId() {
        return pageId;
    }


    public String getTitle() {
        return title;
    }

    public int getTotal() {
        return test_question_count;
    }

    public String getDescription() {
        return description;
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

    public int getVoteCount() {
        return voteCount;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

    public int getSearch() {
        return search;
    }

    public int getClosed() {
        return closed;
    }

    public String getView_privacy() {
        return view_privacy;
    }

    public String getOwnerTitle() {
        return ownerTitle;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public String getImageUrl() {
        if (null != owner_image) {
            return owner_image.getNormal();
        }
        return null;
    }

    public List<Answer> getOptions() {
        return usertest;
    }

    public String getHeaderText(Context context) {
        if (null != contentTitle) {
            return SpanUtil.getHtmlString(context.getString(R.string.by_owner_in_category, ownerTitle, contentTitle));
        } else {
            return context.getString(R.string.by_owner, ownerTitle);
        }
    }

    private boolean isShowingQuestion;

    public void setQuestionVisibility(boolean isShowingQuestion) {
        this.isShowingQuestion = isShowingQuestion;
    }

    public boolean setQuestionVisibility() {
        if (isClosed != 0) {
            isShowingQuestion = false;
            return false;
        }
        this.isShowingQuestion = !hasVoted;
        return isShowingQuestion;
    }

    public boolean getQuestionVisibility() {
        return isShowingQuestion;
    }

    public boolean toggleQuestionVisibility() {
        isShowingQuestion = !isShowingQuestion;
        return isShowingQuestion;
    }

    //Custom Method for creating share object
    public Share getShare(String type) {
        Share share = new Share();
        share.setTitle(title);
        share.setDescription(description);
        share.setImageUrl(getImageUrl());
        share.setUrlParams(new UrlParams(type, pollId));
        return share;
    }
}

package com.sesolutions.responses.music;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ReactionPlugin;

import java.util.List;

/**
 * Created by root on 4/12/17.
 */

public class CommentLike {

    @SerializedName("result")
    private Result result;
    @SerializedName("session_id")
    private String sessionId;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }


    public static class Stats {
        @SerializedName("reaction_plugin")
        private List<ReactionPlugin> reactionPlugin;
        @SerializedName("reaction_type")
        private int reactionType;
        @SerializedName("is_like")
        private boolean isLike;
        @SerializedName("like_count")
        private int likeCount;
        @SerializedName("is_favourite")
        private boolean isFavourite;
        @SerializedName("favourite_count")
        private int favouriteCount;
        @SerializedName("can_comment")
        private boolean canComment;
        @SerializedName("can_delete")
        private boolean canDelete;
        @SerializedName("comment_Count")
        private int commentCount;
        @SerializedName("loggedin")
        private int loggedin;

        public List<ReactionPlugin> getReactionPlugin() {
            return reactionPlugin;
        }

        public void setReactionPlugin(List<ReactionPlugin> reactionPlugin) {
            this.reactionPlugin = reactionPlugin;
        }

        public int getReactionType() {
            return reactionType;
        }

        public void setReactionType(int reactionType) {
            this.reactionType = reactionType;
        }

        public boolean getIsLike() {
            return isLike;
        }

        public void setIsLike(boolean isLike) {
            this.isLike = isLike;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(int likeCount) {
            this.likeCount = likeCount;
        }

        public boolean getIsFavourite() {
            return isFavourite;
        }

        public void setIsFavourite(boolean isFavourite) {
            this.isFavourite = isFavourite;
        }

        public int getFavouriteCount() {
            return favouriteCount;
        }

        public void setFavouriteCount(int favouriteCount) {
            this.favouriteCount = favouriteCount;
        }

        public boolean getCanComment() {
            return canComment;
        }

        public void setCanComment(boolean canComment) {
            this.canComment = canComment;
        }

        public boolean getCanDelete() {
            return canDelete;
        }

        public void setCanDelete(boolean canDelete) {
            this.canDelete = canDelete;
        }

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        public int getLoggedin() {
            return loggedin;
        }

        public void setLoggedin(int loggedin) {
            this.loggedin = loggedin;
        }

        public int decreamentFavourite() {
            favouriteCount = favouriteCount - 1;
            return favouriteCount;
        }

        public int increamentFavourite() {
            favouriteCount = favouriteCount + 1;
            return favouriteCount;
        }
    }

    public static class Result {
        @SerializedName("stats")
        private Stats stats;
        @SerializedName("loggedin_user_id")
        private int loggedinUserId;
        @SerializedName("reaction_type")
        private int reactionType;
        private String reactionUserData;
        @SerializedName("is_like")
        private boolean isLike;
        private List<ReactionPlugin> reactionData;


        public String getReactionUserData() {
            return reactionUserData;
        }

        public void setReactionUserData(String reactionUserData) {
            this.reactionUserData = reactionUserData;
        }

        public boolean isLike() {
            return isLike;
        }

        public void setLike(boolean like) {
            isLike = like;
        }

        public Stats getStats() {
            return stats;
        }

        public int getReactionType() {
            return reactionType;
        }

        public void setReactionType(int reactionType) {
            this.reactionType = reactionType;
        }


        public List<ReactionPlugin> getReactionData() {
            return reactionData;
        }

        public void setReactionData(List<ReactionPlugin> reactionData) {
            this.reactionData = reactionData;
        }

        public void setStats(Stats stats) {
            this.stats = stats;
        }

        public int getLoggedinUserId() {
            return loggedinUserId;
        }

        public void setLoggedinUserId(int loggedinUserId) {
            this.loggedinUserId = loggedinUserId;
        }
    }
}

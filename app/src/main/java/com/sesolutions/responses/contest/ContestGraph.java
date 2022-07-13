package com.sesolutions.responses.contest;

import java.util.List;

public class ContestGraph {
    private List<String> date;
    private List<Float> voteCount;
    private List<Float> likeCount;
    private List<Float> commentCount;
    private List<Float> favouriteCount;
    private List<Float> viewCount;
    private String headingTitle;//": "Weekly Vote Report For testing photo entry",
    private String XAxisTitle;//": "Weekly Votes",
    private String likeHeadingTitle;//": "Weekly Like Report For testing photo entry",
    private String likeXAxisTitle;//": "Weekly Likes",
    private String commentHeadingTitle;//": "Weekly Comment Report For testing photo entry",
    private String commentXAxisTitle;//": "Weekly Comments",
    private String favouriteHeadingTitle;//": "Weekly Favourite Report For testing photo entry",
    private String favouriteXAxisTitle;//": "Weekly Favourites",
    private String viewHeadingTitle;//": "Weekly Views Report For testing photo entry",
    private String viewXAxisTitle;//": "Weekly Views"

    public List<String> getDate() {
        return date;
    }

    public List<Float> getVoteCount() {
        return voteCount;
    }

    public List<Float> getLikeCount() {
        return likeCount;
    }

    public List<Float> getCommentCount() {
        return commentCount;
    }

    public List<Float> getFavouriteCount() {
        return favouriteCount;
    }

    public List<Float> getViewCount() {
        return viewCount;
    }

    public String getHeadingTitle() {
        return headingTitle;
    }

    public String getXAxisTitle() {
        return XAxisTitle;
    }

    public String getLikeHeadingTitle() {
        return likeHeadingTitle;
    }

    public String getLikeXAxisTitle() {
        return likeXAxisTitle;
    }

    public String getCommentHeadingTitle() {
        return commentHeadingTitle;
    }

    public String getCommentXAxisTitle() {
        return commentXAxisTitle;
    }

    public String getFavouriteHeadingTitle() {
        return favouriteHeadingTitle;
    }

    public String getFavouriteXAxisTitle() {
        return favouriteXAxisTitle;
    }

    public String getViewHeadingTitle() {
        return viewHeadingTitle;
    }

    public String getViewXAxisTitle() {
        return viewXAxisTitle;
    }
}

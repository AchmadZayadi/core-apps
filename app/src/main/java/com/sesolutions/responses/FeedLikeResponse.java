package com.sesolutions.responses;

import com.sesolutions.responses.feed.Like;

import java.util.List;

/**
 * Created by root on 30/12/17.
 */

public class FeedLikeResponse extends ErrorResponse {
    private Result result;

    public Result getResult() {
        return result;
    }

    public class Result {
        public boolean is_like;
        public Like like;
        public String reactionUserData;
        public List<ReactionPlugin> reactionData;

    }
}

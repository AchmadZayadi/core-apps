package com.sesolutions.responses;

import com.sesolutions.responses.feed.Options;

import java.util.List;

public class ReviewResponse extends ErrorResponse {
    private Result result;

    public class Result extends PaginationHelper {

        private List<Review> reviews;
        private Review review;
        private Options button;

        public List<Review> getReviews() {
            return reviews;
        }

        public Review getReview() {
            return review;
        }

        public Options getButton() {
            return button;
        }
    }

    public Result getResult() {
        return result;
    }
}

package com.sesolutions.responses.event;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.CommonVO;
import com.sesolutions.responses.ErrorResponse;
import com.sesolutions.responses.PaginationHelper;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.contest.Transaction;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.page.NestedOptions;
import com.sesolutions.responses.videos.Videos;

import java.util.List;

public class EventResponse extends ErrorResponse {
    private Result result;

    public class Result extends PaginationHelper {
        private List<Options> options;

        //For View Event
        private Options callToAction;
        @SerializedName("related_events")
        private List<CommonVO> relatedPages;
        private CommonVO event;
        private CommonVO list;
        private List<Options> about;
        private List<Options> menus;
        private List<Albums> photo;
        private List<Videos> videos;
        private List<Transaction> transactions;

        //CORE EVENT
        @SerializedName("event_content")
        private CommonVO eventContent;
        //for discussion
        private List<Discussion> discussions;
        @SerializedName("post_button")
        private Options postButton;

        //for reviews
        private List<Reviews> reviews;
        private Reviews review;


        //for discussion view
        private List<Discussion> posts;
        private NestedOptions topic;


       /* @SerializedName("loggedin_user_id")
        private int loggedin_user_id;
        @SerializedName("total_page")
        private int totalPage;
        @SerializedName("current_page")
        private int currentPage;

        @SerializedName("next_page")
        private int nextPage;
        private int total;*/

        public CommonVO getEventContent() {
            return eventContent;
        }

        public List<Videos> getVideos() {
            return videos;
        }

        public Reviews getReview() {
            return review;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public List<Discussion> getPosts() {
            return posts;
        }

        public NestedOptions getTopic() {
            return topic;
        }

        public CommonVO getEvent() {
            return event;
        }

        public List<Reviews> getReviews() {
            return reviews;
        }

        public CommonVO getList() {
            return list;
        }

        public Options getCallToAction() {
            return callToAction;
        }

        public List<Options> getOptions() {
            return options;
        }

        public List<Albums> getPhoto() {
            return photo;
        }

        public List<Discussion> getDiscussions() {
            return discussions;
        }

        public Options getPostButton() {
            return postButton;
        }

        public List<CommonVO> getRelatedPages() {
            return relatedPages;
        }

        public List<Options> getAbout() {
            return about;
        }

        public List<Options> getMenus() {
            return menus;
        }

      /*  public int getTotal() {
            return total;
        }

        public int getLoggedin_user_id() {
            return this.loggedin_user_id;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public int getNextPage() {
            return nextPage;
        }*/
    }


    public Result getResult() {
        return result;
    }
}

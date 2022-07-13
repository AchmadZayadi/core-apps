package com.sesolutions.responses.event;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.utils.SpanUtil;

import java.util.List;

public class HostResponse {
    @SerializedName("session_id")
    private String sessionId;
    private Result result;

    public class Result {

        private List<Options> menus;
        private String followCount;// "0 Followed",
        private String is_content_follow;// false,
        private int content_follow_count;// 0,
        private String hostedEvent;// "1 Event Hosted",
        private String viewCount;// "4 views",
        private String favourite_count;// "0 favourites",
        private String description;// "<p>He is Awesome</p>",
        private String host_email;// "prinka.ggn@gmail.com",
        private String host_phone;// 2147483647,
        private String website_url;// "http://www.google.com",
        private String facebook_url;// "http://www.facebook.com",
        private String twitter_url;// "http://www.twitter.com",
        private String linkdin_url;// "http://www.linkedin.com",
        private String googleplus_url;// "http://www.google.com",
        private int follow_id;// "http://www.google.com",
        @SerializedName("host_name")
        private String hostName;
        private String image;

        public String getHostName() {
            return hostName;
        }

        public String getImage() {
            return image;
        }

        public String getFollowCount() {
            return followCount;
        }

        public void setFollow_id(int follow_id) {
            this.follow_id = follow_id;
        }

        public int getFollow_id() {
            return follow_id;
        }

        public boolean isContentFollow() {
            return "true".equals(is_content_follow);
        }

        public boolean canFollow() {
            return null != is_content_follow;
        }

        public int getContent_follow_count() {
            return content_follow_count;
        }

        public String getHostedEvent() {
            return hostedEvent;
        }

        public String getViewCount() {
            return viewCount;
        }

        public String getFavourite_count() {
            return favourite_count;
        }

        public String getDescription() {
            return SpanUtil.getHtmlString(description);
        }

        public String getHost_email() {
            return host_email;
        }

        public String getHost_phone() {
            return host_phone;
        }

        public String getWebsite_url() {
            return website_url;
        }

        public String getFacebook_url() {
            return facebook_url;
        }

        public String getTwitter_url() {
            return twitter_url;
        }

        public String getLinkdin_url() {
            return linkdin_url;
        }

        public String getGoogleplus_url() {
            return googleplus_url;
        }

        @SerializedName("loggedin_user_id")
        private int loggedin_user_id;
        @SerializedName("total_page")
        private int totalPage;
        @SerializedName("current_page")
        private int currentPage;

        @SerializedName("next_page")
        private int nextPage;
        private int total;


        public List<Options> getMenus() {
            return menus;
        }

        public int getTotal() {
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
        }

        public void toggleFollow() {
            is_content_follow = String.valueOf(!isContentFollow());
        }
    }


    public String getSessionId() {
        return sessionId;
    }

    public Result getResult() {
        return result;
    }
}

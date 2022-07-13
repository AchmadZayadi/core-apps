package com.sesolutions.responses.event;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.utils.SpanUtil;

import java.util.List;

public class Discussion {

    @SerializedName("topic_id")
    private int topicId;
    @SerializedName("event_id")
    private int eventId;
    @SerializedName("user_id")
    private int userId;

    @SerializedName("sticky")
    private int sticky;
    @SerializedName("closed")
    private int closed;
    @SerializedName("view_count")
    private int viewCount;
    @SerializedName("post_count")
    private int postCount;
    @SerializedName("lastpost_id")
    private int lastpostId;
    @SerializedName("lastposter_id")
    private int lastposterId;
    private String title;
    private String creation_date;
    private String modified_date;
    @SerializedName("reply_count")
    private String replyCount;
    @SerializedName("reply_label")
    private String replyLabel;
    @SerializedName("post_description")
    private String desc;
    @SerializedName("last_post")
    private LastPost lastPost;

    //for discussion view
    @SerializedName("post_id")
    private int postId;

    @SerializedName("body")
    private String body;
    @SerializedName("user_photo")
    private String userPhoto;
    private List<Options> options;


    public class LastPost {
        private String image;
        private String label;


        public String getImage() {
            return image;
        }

        public String getLabel() {
            return SpanUtil.getHtmlString(label);
        }


    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public int getPostId() {
        return postId;
    }

    public String getBody() {
        return SpanUtil.getHtmlString(body);
    }


    public List<Options> getOptions() {
        return options;
    }

    public int getTopicId() {
        return topicId;
    }

    public int getEventId() {
        return eventId;
    }

    public int getUserId() {
        return userId;
    }

    public int getSticky() {
        return sticky;
    }

    public int getClosed() {
        return closed;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getPostCount() {
        return postCount;
    }

    public int getLastpostId() {
        return lastpostId;
    }

    public int getLastposterId() {
        return lastposterId;
    }

    public String getTitle() {
        return title;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public String getModified_date() {
        return modified_date;
    }

    public String getReplyCount() {
        return replyCount;
    }

    public String getReplyLabel() {
        return replyLabel;
    }

    public String getDesc() {
        return SpanUtil.getHtmlString(desc);
    }

    public LastPost getLastPost() {
        return lastPost;
    }
}

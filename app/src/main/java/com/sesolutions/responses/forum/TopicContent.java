package com.sesolutions.responses.forum;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;

import java.util.List;

public class TopicContent {

    private int showAnimation;

    @Expose
    @SerializedName("buttons")
    private List<Options> buttons;
    @SerializedName("options")
    private List<Options> options;
    @Expose
    @SerializedName("like_count")
    private int like_count;
    @Expose
    @SerializedName("rating_count")
    private int rating_count;

    private List<Tag> tag;

    public List<Tag> getTag() {
        return tag;
    }

    @Expose
    @SerializedName("rating")
    private float rating;
    @Expose
    @SerializedName("is_rated")
    private boolean rated;

    @Expose
    @SerializedName("can_rate")
    private boolean canRate;
    private String subscribe;
    private String unsubscribe;

    @SerializedName("subscribe_id")
    private int subscribe_id = 0;

    @SerializedName("watch")
    private int watch = 0;

    @Expose
    @SerializedName("post_reply")
    private String post_reply;
    @Expose
    @SerializedName("back_to_topics")
    private String back_to_topics;
    @Expose
    @SerializedName("topic_id")
    private int topic_id;
    @Expose
    @SerializedName("topic_title")
    private String topic_title;

    public int isShowAnimation() {
        return showAnimation;
    }

    @SerializedName("share")
    private Share share;

    @SerializedName("is_content_like")
    public String isContentLike ;
    @SerializedName("sticky")
    private boolean sticky;
    @SerializedName("close")
    private boolean closed;
    @SerializedName("canEdit")
    private boolean canEdit;
    @SerializedName("canDelete")
    private boolean canDelete;
    @SerializedName("can_subscribe")
    private int canSubscribe;

    public boolean isCanEdit() {
        return canEdit;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public boolean isSticky() {
        return sticky;

    }

    public boolean isClosed() {
        return closed;
    }
    public int getCanSubscribe() {
        return canSubscribe;
    }

    public Share getShare() {
        return share;
    }

    public boolean isContentLike() {
        return "true".equals(isContentLike);
    }
    public void setContentLike(boolean contentLike) {
        isContentLike = String.valueOf(contentLike);
    }
    public boolean canLike() {
        return null != isContentLike;
    }

    public void setShowAnimation(int showAnimation) {
        this.showAnimation = showAnimation;
    }

    public List<Options> getButtons() {
        return buttons;
    }

    public List<Options> getOptions() {
        return options;
    }

    public int getLike_count() {
        return like_count;
    }

    public float getRating() {
        return rating;
    }

    public int getRating_count() {
        return rating_count;
    }
    public boolean isRated() {
        return rated;
    }

    public String getUnsubscribe() {
        return unsubscribe;
    }

    public boolean canRate() {
        return canRate;
    }


    public String getSubscribe() {
        return subscribe;
    }

    public int getSubscribe_id() {
        return subscribe_id;
    }

    public int getWatch() {
        return watch;
    }

    public void setSubscribe_id(int subscribe_id) {
        this.subscribe_id = subscribe_id;
    }
    public void setWatch(int watch) {
        this.watch = watch;
    }

    public String getPost_reply() {
        return post_reply;
    }

    public String getBack_to_topics() {
        return back_to_topics;
    }

    public int getTopic_id() {
        return topic_id;
    }

    public String getTopic_title() {
        return topic_title;
    }

    public static class Buttons {
        @Expose
        @SerializedName("label")
        private String label;
        @Expose
        @SerializedName("name")
        private String name;

        public String getLabel() {
            return label;
        }

        public String getName() {
            return name;
        }
    }

}

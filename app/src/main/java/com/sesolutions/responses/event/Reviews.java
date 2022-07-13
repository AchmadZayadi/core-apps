package com.sesolutions.responses.event;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.SesModel;
import com.sesolutions.responses.feed.Options;
import com.sesolutions.responses.feed.Share;
import com.sesolutions.utils.SpanUtil;

import java.util.List;

public class Reviews extends SesModel {
    @SerializedName("review_id")
    private int reviewId;//88,
    private int recommended;//1,
    @SerializedName("owner_id")
    private int ownerId;//1,
    @SerializedName("content_id")
    private int contentId;//32,
    @SerializedName("like_count")
    private int likeCount;//0,
    @SerializedName("comment_count")
    private int commentCount;//0,
    @SerializedName("view_count")
    private int viewCount;//0,
    private float rating;//4,
    @SerializedName("viewer_title")
    private String viewerTitle;//"Stanley R. Card"
    private String title;//"testing alpha",
    private String pros;//"gyasdgcgdys",
    private String cons;//"asgcdgdas",
    private String description;//" <p>asgcygsagcg</p>",
    @SerializedName("contentType")
    private String contentType;//"sesevent_event",
    @SerializedName("profreview_id")
    private int proreviewId;
    private String module_name;//"sesevent",
    private List<Options> options;
    @SerializedName("owner_image")
    private String ownerImage;
    private String image;
    private Share share;


    public Share getShare() {
        return share;
    }
    public int getProreviewId() {
        return proreviewId;
    }
    public String getOwnerImage() {
        return ownerImage;
    }

    public String getImage() {
        return image;
    }

    public List<Options> getOptions() {
        return options;
    }

    public int getReviewId() {
        return reviewId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getContentId() {
        return contentId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public boolean isRecommended() {
        return recommended == 1;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public float getRating() {
        return rating;
    }

    public String getViewer_title() {
        return viewerTitle;
    }

    public String getTitle() {
        return title;
    }

    public String getPros() {
        return pros;
    }

    public String getCons() {
        return cons;
    }

    public String getDescription() {
        return SpanUtil.getHtmlString(description);
    }

    public boolean isDescriptionsAvailable() {
        return null != description;
    }

    public String getContentType() {
        return contentType;
    }

    public String getModuleName() {
        return module_name;
    }
}

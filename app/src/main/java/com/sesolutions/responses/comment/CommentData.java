package com.sesolutions.responses.comment;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.FeedLikeResponse;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.responses.feed.Like;
import com.sesolutions.responses.feed.Mention;
import com.sesolutions.responses.feed.Options;

import java.util.List;

@Entity
public class CommentData {
    @PrimaryKey
    @SerializedName("comment_id")
    private int commentId;
    @SerializedName("resource_id")
    private int resourceId;

    // custom variable
    private int rId;
    private String rType;


    private String resourceType;
    @SerializedName("poster_type")
    private String posterType;
    @SerializedName("poster_id")
    private int posterId;
    @SerializedName("body")
    private String body;

    @SerializedName("gif_id")
    private String gif_id;
    @SerializedName("gif_url")
    private String gif_url;

    public String getGif_id() {
        return gif_id;
    }

    public void setGif_id(String gif_id) {
        this.gif_id = gif_id;
    }

    public String getGif_url() {
        return gif_url;
    }

    public void setGif_url(String gif_url) {
        this.gif_url = gif_url;
    }


    @SerializedName("creation_date")
    private String creationDate;
    @SerializedName("like_count")
    private int likeCount;
    @SerializedName("file_id")
    private int fileId;
    @SerializedName("parent_id")
    private int parentId;
    @SerializedName("emoji_id")
    private int emojiId;
    @SerializedName("reply_count")
    private int replyCount;
    @SerializedName("preview")
    private int preview;
    @SerializedName("showpreview")
    private int showpreview;
    @SerializedName("is_like")
    private boolean isLike;
    @SerializedName("hashTags")
    private List<String> hashtags;
    @SerializedName("mention")
    private List<Mention> mention;
    @SerializedName("emoji_image")
    private String emojiImage;
    @SerializedName("user_title")
    private String userTitle;
    @SerializedName("user_image")
    private String userImage;
    @SerializedName("can_delete")
    private boolean canDelete;
    @SerializedName("attachphotovideo")
    private List<AttachmentComment> attachPhotoVideo;
    private Link link;
    private List<Options> options;

    private Like like;
    private String reactionUserData;
    private List<ReactionPlugin> reactionData;

    private List<CommentData> replies;

    public CommentData() {
    }

    public CommentData(String body1, String title, String image, String date) {
        body = body1;
        userImage = image;
        userTitle = title;
        creationDate = date;

    }

    public CommentData(String body1, String title, String image, String date, boolean canD) {
        body = body1;
        userImage = image;
        userTitle = title;
        creationDate = date;
        canDelete = canD;
    }

    public int getRId() {
        return rId;
    }

    public void setRId(int rId) {
        this.rId = rId;
    }

    public String getRType() {
        return rType;
    }

    public void setRType(String rType) {
        this.rType = rType;
    }

    public void setReactionData(List<ReactionPlugin> reactionData) {
        this.reactionData = reactionData;
    }

    public void setOptions(List<Options> options) {
        this.options = options;
    }

    public void setLike(Like like) {
        this.like = like;
    }

    public void setReplies(List<CommentData> replies) {
        this.replies = replies;
    }

    public void setReactionUserData(String reactionUserData) {
        this.reactionUserData = reactionUserData;
    }

    public Like getLike() {
        return like;
    }

    public String getReactionUserData() {
        return reactionUserData;
    }

    public List<ReactionPlugin> getReactionData() {
        return reactionData;
    }

    public List<CommentData> getReplies() {
        return replies;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public List<AttachmentComment> getAttachPhotoVideo() {
        return attachPhotoVideo;
    }

    public void setAttachPhotoVideo(List<AttachmentComment> attachPhotoVideo) {
        this.attachPhotoVideo = attachPhotoVideo;
    }

    public String getEmojiImage() {
        return emojiImage;
    }

    public void setEmojiImage(String emojiImage) {
        this.emojiImage = emojiImage;
    }


    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getPosterType() {
        return posterType;
    }

    public void setPosterType(String posterType) {
        this.posterType = posterType;
    }

    public int getPosterId() {
        return posterId;
    }

    public void setPosterId(int posterId) {
        this.posterId = posterId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getEmojiId() {
        return emojiId;
    }

    public void setEmojiId(int emojiId) {
        this.emojiId = emojiId;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getPreview() {
        return preview;
    }

    public void setPreview(int preview) {
        this.preview = preview;
    }

    public int getShowpreview() {
        return showpreview;
    }

    public void setShowpreview(int showpreview) {
        this.showpreview = showpreview;
    }

    public boolean getIsLike() {
        return isLike;
    }

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public List<Mention> getMention() {
        return mention;
    }

    public boolean hasMention() {
        return null != mention && mention.size() > 0;
    }

    public boolean hasTags() {
        return null != hashtags && hashtags.size() > 0;
    }


    public boolean canSpan() {
        return hasTags() && hasMention();
    }

    public void setMention(List<Mention> mention) {
        this.mention = mention;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public void toggleLike() {
        if (isLike) {
            likeCount = likeCount - 1;
        } else {
            likeCount = likeCount + 1;
        }
        isLike = !isLike;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void updateObject(CommentData vo) {
        this.commentId = vo.commentId;
        this.resourceId = vo.resourceId;
        this.posterType = vo.posterType;
        this.posterId = vo.posterId;
        this.body = vo.body;
        this.creationDate = vo.creationDate;
        this.likeCount = vo.likeCount;
        this.fileId = vo.fileId;
        this.parentId = vo.parentId;
        this.emojiId = vo.emojiId;
        this.replyCount = vo.replyCount;
        this.preview = vo.preview;
        this.showpreview = vo.showpreview;
        this.isLike = vo.isLike;
        this.hashtags = vo.hashtags;
        this.mention = vo.mention;
        this.emojiImage = vo.emojiImage;
        this.userTitle = vo.userTitle;
        this.userImage = vo.userImage;
        this.canDelete = vo.canDelete;
        this.attachPhotoVideo = vo.attachPhotoVideo;
        this.link = vo.link;
        this.options = vo.options;
    }

    public void updateLikeTemp(boolean isLike, Like likeVo) {
        like = likeVo;
        this.isLike = isLike;
        if (isLike) {
            likeCount = likeCount + 1;
        } else {
            likeCount = likeCount - 1;
        }
    }

    public void updateFinalLike(FeedLikeResponse.Result result) {
        like = result.like;
        isLike = result.is_like;
        reactionData = result.reactionData;
        reactionUserData = result.reactionUserData;
    }

    public List<Options> getOptions() {
        return options;
    }
}

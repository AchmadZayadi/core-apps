package com.sesolutions.responses.comment;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.PaginationHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 27/11/17.
 */

public class Result extends PaginationHelper {
    private Comments comments;
    @SerializedName("comment_data")
    private List<CommentData> commentData;
    private List<CommentData> replies;

    @SerializedName("reply_comment")
    private boolean replyComment;
    @SerializedName("can_comment")
    private boolean canComment;
    @SerializedName("attachment_options")
    private List<String> attachmentOptions;
    @SerializedName("can_delete")
    private boolean canDelete;
    @SerializedName("enable")
    private Enable enable;
    @SerializedName("like_count")
    private int likeCount;

    //Custom creation of Result object
    public Result getClonedObject(CommentData commentData) {
        Result result = new Result();
        result.setEnable(enable);
        result.setAttachmentOptions(attachmentOptions);
        result.setCanComment(canComment);
        result.setReplyComment(replyComment);
        result.setCanDelete(canDelete);
        List<CommentData> data = new ArrayList<>();
        data.add(commentData);
        result.setCommentData(data);
        return result;
    }

    public List<CommentData> getReplies() {
        return replies;
    }

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public List<CommentData> getCommentData() {
        return commentData;
    }

    public void setCommentData(List<CommentData> commentData) {
        this.commentData = commentData;
    }

    public boolean getReplyComment() {
        return replyComment;
    }

    public void setReplyComment(boolean replyComment) {
        this.replyComment = replyComment;
    }

    public boolean getCanComment() {
        return canComment;
    }

    public void setCanComment(boolean canComment) {
        this.canComment = canComment;
    }

    public List<String> getAttachmentOptions() {
        return attachmentOptions;
    }

    public void setAttachmentOptions(List<String> attachmentOptions) {
        this.attachmentOptions = attachmentOptions;
    }

    public boolean getCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public Enable getEnable() {
        return enable;
    }

    public void setEnable(Enable enable) {
        this.enable = enable;
    }

}

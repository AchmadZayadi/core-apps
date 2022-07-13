package com.sesolutions.responses.photo;

import com.google.gson.annotations.SerializedName;
import com.sesolutions.responses.ReactionPlugin;
import com.sesolutions.responses.album.Albums;
import com.sesolutions.responses.feed.Options;

import java.util.List;

/**
 * Created by root on 12/12/17.
 */

public class PhotoResponse {
    private Result result;


    public class Result {
        List<Options> menus;
        @SerializedName("can_comment")
        private boolean canComment;
        @SerializedName("can_tag")
        private boolean canTag;
        @SerializedName("can_untag")
        private boolean canUntag;
        @SerializedName("share")
        private List<Options> shareOptions;
        @SerializedName("reaction_plugin")
        private List<ReactionPlugin> reactionPlugin;
        private List<Albums> photos;

        public List<Options> getMenus() {
            return menus;
        }

        public void setMenus(List<Options> menus) {
            this.menus = menus;
        }

        public boolean isCanComment() {
            return canComment;
        }

        public void setCanComment(boolean canComment) {
            this.canComment = canComment;
        }

        public boolean isCanTag() {
            return canTag;
        }

        public void setCanTag(boolean canTag) {
            this.canTag = canTag;
        }

        public boolean isCanUntag() {
            return canUntag;
        }

        public void setCanUntag(boolean canUntag) {
            this.canUntag = canUntag;
        }

        public List<Options> getShareOptions() {
            return shareOptions;
        }

        public void setShareOptions(List<Options> shareOptions) {
            this.shareOptions = shareOptions;
        }

        public List<ReactionPlugin> getReactionPlugin() {
            return reactionPlugin;
        }

        public void setReactionPlugin(List<ReactionPlugin> reactionPlugin) {
            this.reactionPlugin = reactionPlugin;
        }

        public List<Albums> getPhotos() {
            return photos;
        }

        public void setPhotos(List<Albums> photos) {
            this.photos = photos;
        }
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}

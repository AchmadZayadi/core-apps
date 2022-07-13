package com.sesolutions.ui.storyview

import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.FeedLikeResponse
import com.sesolutions.responses.ReactionPlugin
import com.sesolutions.responses.feed.Like
import com.sesolutions.responses.feed.Options

class StoryContent {
    private var animation: Int = 0
    @SerializedName("story_id")
    val storyId: Int = 0
    val comment: String? = null
    @SerializedName("view_count")
    val viewCount: Int = 0
    @SerializedName("comment_count")
    val commentCount: Int = 0
    @SerializedName("like_count")
    val likeCount: Int = 0
    @SerializedName("creation_date")
    val createdDate: String? = null
    val isViewed: Boolean = false
    @SerializedName("media_url")
    val mediaUrl: String? = null
    @SerializedName("is_video")
    val isVideo: Boolean = false
    @SerializedName("can_comment")
    private val canComment: Boolean = false
    private var highlight: Int = 0
    var options: MutableList<Options>? = null


    //reaction conetnt
    var reactionData: List<ReactionPlugin>? = null
        private set
    var reactionUserData: String? = null
        private set
    private var like: Like? = null
    private var is_like: Boolean = false

    val isHighlighted: Boolean
        get() = highlight == 1

    /* public StoryContent(int id, String image, String createdDate, boolean isVideo) {
         this.video = isVideo;
         this.createdDate = createdDate;
         this.storyId = id;
         this.image = image;
     }
 */

    fun toggleLike(reactionVo: ReactionPlugin) {
        if (is_like) {
            is_like = false
            like = null
        } else {
            is_like = true
            like = Like(reactionVo.image, reactionVo.title)
            // like.setType(reactionVo.get());
        }
    }

    fun updateLikeTemp(isLike: Boolean, likeVo: Like) {
        like = likeVo
        is_like = isLike
    }

    fun updateFinalLike(result: FeedLikeResponse.Result) {
        like = result.like
        is_like = result.is_like
        reactionData = result.reactionData
        reactionUserData = result.reactionUserData
    }

    fun canReact(): Boolean {
        return canComment
    }

    fun showAnimation(): Int {
        return animation
    }

    fun toggleHighlight() {
        highlight = if (highlight == 1) 0 else 1
        animation = 1
        // return highlight==0;
    }

    fun setAnimation(animation: Int) {
        this.animation = animation
    }

    fun setHighlight(highlight: Int) {
        this.highlight = highlight
    }
}

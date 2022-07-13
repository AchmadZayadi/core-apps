package com.sesolutions.ui.storyview

import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.feed.Options
import com.sesolutions.utils.CustomLog

class StoryModel(@field:SerializedName("user_image") val userImage: String,
                 var username: String,
                 @field:SerializedName("user_id") val userId: Int) {
    @SerializedName("story_content")
    var images: List<StoryContent>? = null

    @SerializedName("activity_id")
    var activityId: Int? = null
    @SerializedName("is_live")
    var isLive: Boolean? = false

    //options for muted member
    var options: Options? = null

    @SerializedName("user_title")
    val userTitle: String? = null

    val firstStoryImage: String
        get() {
            val s: String
            if (null != images) {
                s = if (null != images!![0].mediaUrl)
                    images!![0].mediaUrl!!
                else
                    userImage
                CustomLog.e("images", "" + s)
            } else {
                s = userImage
                CustomLog.e("userImage", "" + s)
            }
            return s
        }

    fun toggleMuteOption(label1: String, label2: String) {
        if ("mute" == options!!.name) {
            options!!.name = "unmute"
            options!!.label = label1
        } else {
            options!!.name = "mute"
            options!!.label = label2
            options!!.muteId = -1
        }
    }

    fun toggleMuteOption(option: Options) {
        for (vo in images!!) {
            vo.options?.set(0, option)
        }
    }
}

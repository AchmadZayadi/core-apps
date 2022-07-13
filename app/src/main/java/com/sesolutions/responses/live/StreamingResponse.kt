package com.sesolutions.responses.live

import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.ErrorResponse
import com.sesolutions.responses.feed.Activity

data class StreamingResponse(
        val result: Result? = null,
        @SerializedName("session_id")
        val session_id: String? = null) : ErrorResponse() {

    data class Result(
            val message: String? = null,
            val status: String? = null,
            @SerializedName("loggedin_user_id")
            val loggedInUserId: Int?,
            @SerializedName("action_id")
            val actionId: Int?,
            @SerializedName("elivehost_id")
            val eliveHostId: Int?,
            @SerializedName("user_id")
            val userId: Int?,
            @SerializedName("activity_id")
            val activityId: Int?,
            val datetime: String?,
            @SerializedName("video_id")
            val videoId : Int?,
            val activity: Activity?,
            val canSave : Boolean,
            val canPost : Boolean,
            val canShareInStory : Boolean,
            val maxStreamDurations : Int,
            val success : Boolean?,
            val sid : String?,
            val cause : String? = null
    )
}
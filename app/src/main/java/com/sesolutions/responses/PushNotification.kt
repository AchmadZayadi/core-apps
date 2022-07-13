package com.sesolutions.responses

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

import org.apache.commons.lang3.StringEscapeUtils

/**
 * Created by root on 16/1/18.
 */

class PushNotification {

    @SerializedName("msgcnt")
    private val msgcnt: String? = null
    @SerializedName("vibrate")
    private val vibrate: String? = null
    @SerializedName("sound")
    private val sound: String? = null

    @SerializedName("userInfo")
    private val userinfo: String? = null

    @SerializedName("title")
    val title: String? = null

    @SerializedName("body")
    val body: String? = null


    val subData: SubData?
        get() {
            var sb: SubData? = null
            if (userinfo != null) {
                sb = Gson().fromJson(StringEscapeUtils.unescapeJson(userinfo), SubData::class.java)
            }
            return sb
        }

    inner class SubData {
        val subject_id: Int = 0
        val object_type: String? = null
        val href: String? = null
        val object_id: Int = 0
        val isCommentLike: Boolean = false

        @SerializedName("user_name")
        val sender : String? = null

        @SerializedName("activity_action_id")
        val activityId: Int = 0
        @SerializedName("host_id")
        val hostId: Int = 0

    }

}

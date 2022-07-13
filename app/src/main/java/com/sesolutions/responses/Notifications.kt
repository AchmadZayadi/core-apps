package com.sesolutions.responses

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.feed.Images
import com.sesolutions.responses.videos.Videos

class Notifications {
    @SerializedName("notification_id")
    var notificationId: Int = 0
    @SerializedName("object_id")
    var objectId: Int = 0
    @SerializedName("subject_id")
    var subjectId: Int = 0
    @SerializedName("notification_icon")
    var notificationIcon: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("duration")
    var duration: Int? = null
    @SerializedName("body")
    var body: String? = null
    @SerializedName("user_image")
    var userImage: String? = null
    @SerializedName("user_name")
    var userName: String? = null
    @SerializedName("href")
    var href: String? = null
    @SerializedName("object_type")
    var objectType: String? = null
    @SerializedName("read")
    var read: Int = 0
    @SerializedName("date")
    var date: String? = null

   @SerializedName("pro_latitude")
    var pro_latitude: String? = null

    @SerializedName("pro_longitude")
    var pro_longitude: String? = null

    @SerializedName("isCommentLike")
    var iscommentlike: Boolean = false
    @SerializedName("activity_action_id")
    val activityId: Int = 0
    @SerializedName("host_id")
    val hostId: Int = 0
    @SerializedName("music_id")
    val music_id: Int = 0
    private val images: Images? = null
    @SerializedName("user_id")
    var userId: Int = 0
    @SerializedName("notification_video")
    var notification_video: Videos? = null


    var age: String? = null
    var location: String? = null
    var mutualFriends: String? = null
    private val membership: JsonElement? = null
    private var member: Membership? = null
    private var verify: Membership? = null
    var follow: Membership? = null
    var block: Membership? = null



    fun getVideo(): Videos?{
        return notification_video
    }

    fun getMainImageUrl(): String? {
        return if (images != null) {
            images.getMain()
        } else {
            ""
        }
    }

    fun getMembership(): Membership? {
        if (null == member && membership!!.isJsonObject)
            member = Gson().fromJson(membership.toString(), Membership::class.java)
        return member
    }

    fun setMembership(membership: Membership) {
        this.member = membership
    }

    /*public void toggleFollow() {
        if (null != follow && follow.getAction().equals("follow")) {
            follow.setAction();
        } else if (null != follow) {

        }
    }*/
}

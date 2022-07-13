package com.sesolutions.responses

import com.google.gson.annotations.SerializedName

import java.io.Serializable

/**
 * Created by root on 9/11/17.
 */

class MessageInbox : Serializable {

    @SerializedName("conversation_id")
    var conversationId: Int = 0
    @SerializedName("sender")
    var sender: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("read")
    var read: Int = 0
    @SerializedName("user_id")
    var userId: Int = 0
    @SerializedName("sender_id")
    var senderId: Int = 0
    @SerializedName("user_image")
    var userImage: String? = null
    @SerializedName("body")
    var body: String? = null
    @SerializedName("date")
    var date: String? = null
    @SerializedName("mine")
    var mine: Int = 0

    @SerializedName("attachment")
    var attachments: Attachments? = null
}

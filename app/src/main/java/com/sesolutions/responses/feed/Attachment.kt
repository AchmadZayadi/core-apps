package com.sesolutions.responses.feed

import com.google.gson.annotations.SerializedName
import com.sesolutions.utils.Constant
import com.sesolutions.utils.SpanUtil

class Attachment {
    var totalImagesCount: Int = 0

    @SerializedName("attachment_id")
    var attachment_id: Int = 0
    @SerializedName("href")
    var href: String? = null

    @SerializedName("images")
    var images: List<Images>? = null
    @SerializedName("attachmentType")
    var attachmentType = ""
    @SerializedName("title")
    var title: String? = null
    @SerializedName("description")
    var description: String? = null
        get() = SpanUtil.getHtmlString(field)

    var buysell_id: Int = 0
    var price: String? = null
    @SerializedName("buy_url")
    val buyUrl: String? = null
    @SerializedName("video_url")
    val videoUrl: String? = null
    var owner_id: String? = null
    var owner_title: String? = null
    @SerializedName("can_message_owner")
    var isCan_message_owner: Boolean = false

    var is_can_play: Boolean = false
    @SerializedName("can_mark_sold")
    var isCan_mark_sold: Boolean = false
    @SerializedName("sold")
    var isSold: Boolean = false
    var location: String? = null
    var preview_url: String? = null
    var file_type: String? = null
    var file_type_image: String? = null

    //for quote
    var source: String? = null
    var mediaType: String? = null
    val calltoaction: Options? = null
    @SerializedName("call_to_action_overlay")
    val callToActionOverlay: String? = null

    //for CommunityAds
    val src: String? = null
    @SerializedName("url_description")
    val urlDescription: String? = null

    val isPhoto: Boolean
        get() = mediaType != null && mediaType == Constant.KEY_PHOTO

    var reaction_image: String? = null

    fun toggleSold() {
        isSold = !isSold
        isCan_mark_sold = false
    }
}

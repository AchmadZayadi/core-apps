package com.sesolutions.responses.feed

import com.google.gson.annotations.SerializedName

/**
 * Created by root on 13/12/17.
 */

class AttachmentData {
    @SerializedName("photo_id")
    var photoId: Int = 0
    @SerializedName("album_id")
    var albumId: Int = 0
    var type: String? = null
}

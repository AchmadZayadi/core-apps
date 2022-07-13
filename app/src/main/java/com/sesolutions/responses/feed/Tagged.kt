package com.sesolutions.responses.feed

import com.google.gson.annotations.SerializedName

/**
 * Created by root on 16/11/17.
 */

class Tagged {

    @SerializedName("name")
    var name: String? = null
    @SerializedName("user_id")
    var userId: Int = 0
    @SerializedName("image_url")
    var imageUrl: String? = null
}

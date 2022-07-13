package com.sesolutions.responses.feed

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class Item_user : Serializable {
    @SerializedName("user_id")
    var user_id: Int = 0
    @SerializedName("title")
    var title: String? = null
    @SerializedName("user_image")
    var user_image: String? = null
    @SerializedName("user_type")
    val type: String? = null

    constructor(user_id: Int, title: String, user_image: String) {
        this.user_id = user_id
        this.title = title
        this.user_image = user_image
    }

    constructor() {}
}

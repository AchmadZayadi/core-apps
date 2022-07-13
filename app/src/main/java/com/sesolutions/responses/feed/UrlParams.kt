package com.sesolutions.responses.feed

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class UrlParams : Serializable {
    @SerializedName("type")
    var type: String? = null
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("user_id")
    var userId: Int = 0
    var subject: String? = null
    var action: String? = null

    constructor(type: String, id: Int) {
        this.type = type
        this.id = id
    }
    constructor() {}
}

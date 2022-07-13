package com.sesolutions.responses.feed

import com.google.gson.annotations.SerializedName

/**
 * Created by root on 14/11/17.
 */

class Mention {

    @SerializedName("word")
    var word: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("module")
    var module: String? = null
    @SerializedName("href")
    var href: String? = null
    @SerializedName("user_id")
    var userId: Int = 0

    //manually added
    var startIndex: Int = 0
    var endIndex: Int = 0
}

package com.sesolutions.responses.feed

import com.google.gson.annotations.SerializedName

/**
 * Created by root on 14/11/17.
 */

class ActivityType {
    @SerializedName("title")
    var title: String? = null
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("type")
    var type: String? = null
    @SerializedName("module")
    var module: String? = null
    @SerializedName("href")
    var href: String? = null
    @SerializedName("key")
    var key: String? = null
    var seprator: String? = null
    var value: String? = null

    //Manulaly added
    var startIndex = -1
    var endIndex: Int = 0


}

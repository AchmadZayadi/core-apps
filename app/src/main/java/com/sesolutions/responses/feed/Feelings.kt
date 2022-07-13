package com.sesolutions.responses.feed

import com.google.gson.annotations.SerializedName

/**
 * Created by root on 15/11/17.
 */

class Feelings {

    @SerializedName("title")
    var title: String? = null
    @SerializedName("feeling_title")
    var feeling_title: String? = null
    @SerializedName("icon")
    var icon: String? = null
    @SerializedName("is_string")
    var is_string: String? = null
}

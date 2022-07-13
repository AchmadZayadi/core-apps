package com.sesolutions.responses.feed

import com.google.gson.annotations.SerializedName
import com.sesolutions.utils.SpanUtil

class Share {
    @SerializedName("name")
    var name: String? = null
    @SerializedName("label")
    var label: String? = null
    @SerializedName("imageUrl")
    var imageUrl: String? = null
    @SerializedName("url")
    var url: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("description")
    var description: String? = null
        get() = SpanUtil.getHtmlString(field)
    val setting: String? = null
    @SerializedName("urlParams")
    var urlParams: UrlParams? = null
}

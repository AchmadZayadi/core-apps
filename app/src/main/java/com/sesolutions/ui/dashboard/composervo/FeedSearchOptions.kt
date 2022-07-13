package com.sesolutions.ui.dashboard.composervo

import com.google.gson.annotations.SerializedName

import java.io.Serializable

class FeedSearchOptions {
    @SerializedName("image")
    val image: String? = null
    @SerializedName("key")
    val key: String? = null
    @SerializedName("value")
    val value: String? = null
    var isSelected: Boolean = false
}

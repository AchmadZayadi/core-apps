package com.sesolutions.responses.feed

import com.google.gson.annotations.SerializedName

internal class CommunityHiddenData {
    val heading: String? = null
    val description: String? = null
    val options: Map<String, String>? = null
    @SerializedName("other_text")
    val otherText: String? = null
    @SerializedName("submit_button_text")
    val submitButtonText: String? = null
    @SerializedName("success_text")
    val successText: String? = null
}

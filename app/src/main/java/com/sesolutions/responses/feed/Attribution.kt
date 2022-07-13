package com.sesolutions.responses.feed

import com.google.gson.annotations.SerializedName

class Attribution {

    @SerializedName("title")
    var title: String? = null
    @SerializedName("photo")
    var photo: String? = null
    @SerializedName("guid")
    val guid: String? = null
    @SerializedName("selected_id")
    var selectedId: String? = null
    @SerializedName("selected_type")
    var selectedType: String? = null
    @SerializedName("selected_guid")
    var selectedGuid: String? = null

}

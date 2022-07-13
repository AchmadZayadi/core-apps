package com.sesolutions.responses.forum

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Tag {
    @Expose
    @SerializedName("guid")
    val guid: String? = null
    @Expose
    @SerializedName("href")
    val href: String? = null
    @Expose
    @SerializedName("text")
    val text: String? = null
    @Expose
    @SerializedName("id")
    val id: Int = 0
    @Expose
    @SerializedName("creation_date")
    val creationDate: String? = null
    @Expose
    @SerializedName("tag_id")
    val tagId: Int = 0
    @Expose
    @SerializedName("tag_type")
    val tagType: String? = null
    @Expose
    @SerializedName("tagger_id")
    val taggerId: Int = 0
    @Expose
    @SerializedName("tagger_type")
    val taggerType: String? = null
    @Expose
    @SerializedName("resource_id")
    val resourceId: Int = 0
    @Expose
    @SerializedName("resource_type")
    val resourceType: String? = null
    @Expose
    @SerializedName("tagmap_id")
    val tagmapId: Int = 0
}

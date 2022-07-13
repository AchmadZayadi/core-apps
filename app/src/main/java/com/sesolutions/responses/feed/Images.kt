package com.sesolutions.responses.feed

import com.google.gson.annotations.SerializedName

class Images {
    val images : Images? = null
    @SerializedName("main")
    private var main: String? = null
    @SerializedName("icon")
    var icon: String? = null
        get() = if (null != field)
            field
        else
            getNormal()
    @SerializedName("normal")
    private var normal: String? = null
    @SerializedName("title")
    private var title: String? = null
    private val profile: String? = null

    @SerializedName("normalmain")
    private val normalmain: String? = null

    @SerializedName("url")
    private val url: String? = null

    val attachmentData: AttachmentData? = null

    fun getProfile(): String? {
        return profile ?: if (null != normal) {
            normal
        } else
            main

    }

    fun getUrl(): String? {
        return if (null != url) url else main
    }

    fun getNormal(): String? {
        return if (null != normal) normal else main
    }

    fun getMain(): String? {
        return if (null == main) normalmain else main
    }
    fun gettitle(): String?{
        return title
    }

    fun setMain(main: String) {
        this.main = main
        this.normal = main
    }

    fun setMainImage(main: String): Images {
        this.main = main
        this.normal = main
        return this
    }


    fun setNormal(normal: String) {
        this.normal = normal
    }
}

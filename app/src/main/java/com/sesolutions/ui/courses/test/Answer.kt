package com.sesolutions.ui.courses.test

import com.google.gson.annotations.SerializedName

data class Answer(

        @SerializedName("key")
        val key: String? = null,
        @SerializedName("value")
        val value: String? = null,
        var isSelected: Boolean = false
)
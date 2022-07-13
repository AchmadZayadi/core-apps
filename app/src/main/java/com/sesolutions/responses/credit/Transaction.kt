package com.sesolutions.responses.credit

import com.google.gson.annotations.SerializedName

class Transaction {
    @SerializedName("credit_id")
    val creditId: Int = 0
    val type: String? = null
    @SerializedName("point_type")
    val pointType: String? = null
    @SerializedName("date_title")
    val dateStr: String? = null
    private val credit: String? = null
    val language: String? = null
    @SerializedName("owner_name")
    val ownerName: String? = null

    val positive: String?
        get() = if ("credit" == pointType)
            credit
        else
            "-"

    val negetive: String?
        get() = if ("deduction" == pointType)
            credit
        else
            "-"
}

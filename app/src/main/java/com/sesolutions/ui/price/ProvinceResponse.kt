package com.sesolutions.ui.price

import com.google.gson.annotations.SerializedName

class ProvinceResponse {
    @SerializedName("province")
    val harga: MutableList<String> = mutableListOf()

    var city_name: String? = null


}
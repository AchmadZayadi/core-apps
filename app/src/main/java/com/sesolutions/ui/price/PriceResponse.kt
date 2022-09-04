package com.sesolutions.ui.price

import com.google.gson.annotations.SerializedName

class PriceResponse {
    @SerializedName("error")
    val error: ErrorItemResponse = ErrorItemResponse()

    @SerializedName("harga")
    val harga: MutableList<PriceDataResponse> = mutableListOf()
}

class PriceDataResponse {
    @SerializedName("city_name")
    val city_name: String? = null

    @SerializedName("price_items")
    val price_items: PriceItemResponse = PriceItemResponse()
}

class PriceItemResponse {
    @SerializedName("item_name")
    val item_name: String? = null

    @SerializedName("item_price")
    val item_price: String? = null
}

class ErrorItemResponse {
    @SerializedName("message")
    val message: String? = null

}
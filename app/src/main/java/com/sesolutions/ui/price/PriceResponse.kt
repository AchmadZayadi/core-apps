package com.sesolutions.ui.price

import com.google.gson.annotations.SerializedName

class PriceResponse {
    @SerializedName("error")
    var error: ErrorItemResponse = ErrorItemResponse()

    @SerializedName("harga")
    var harga: MutableList<PriceDataResponse> = mutableListOf()
}

class PriceDataResponse {
    @SerializedName("city_name")
    val city_name: String? = null

    @SerializedName("price_items")
    var price_items: PriceItemResponse = PriceItemResponse()
}

class PriceItemResponse {
    @SerializedName("item_name")
    var item_name: String? = null

    @SerializedName("item_price")
    var item_price: String? = null
}

class ErrorItemResponse {
    @SerializedName("message")
    var message: String? = null

}
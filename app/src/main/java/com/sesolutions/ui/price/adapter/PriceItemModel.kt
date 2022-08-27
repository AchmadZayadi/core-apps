package com.sesolutions.ui.price.adapter

import com.sesolutions.ui.price.PriceItemResponse

data class PriceItemModel(
    var city_name: String? = null,
    val price_items: MutableList<PriceItemResponse> = mutableListOf()
)

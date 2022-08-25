package com.sesolutions.ui.price

import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.feed.Result

class PriceResponse {

    @SerializedName("result")
    var result: PriceModel? = null
}
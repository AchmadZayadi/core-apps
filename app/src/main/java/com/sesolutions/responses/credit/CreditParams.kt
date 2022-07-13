package com.sesolutions.responses.credit

import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.CustomParam
import com.sesolutions.utils.CustomLog

class CreditParams : CustomParam() {
    val currencySymbol: String? = null
    val currencyValue: Float = 0.0F
    private val creditvalue: String? = null
    val action: String? = null
    @SerializedName("gateway_id")
    val gatewayId: Int = 0

    val creditValue: Int
        get() = Integer.parseInt(creditvalue!!)

    fun calculatePrice(pointStr: String): String {
        try {
            val point = java.lang.Float.parseFloat(pointStr)
            val p = point * currencyValue / creditValue
            CustomLog.e("price", "" + p)
            return currencySymbol!! + p
        } catch (e: NumberFormatException) {
            return currencySymbol!! + "0.0"
        }

    }
}

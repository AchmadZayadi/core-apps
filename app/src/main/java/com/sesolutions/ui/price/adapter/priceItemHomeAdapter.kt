package com.sesolutions.ui.price.adapter

import android.widget.TextView
import com.dizcoding.adapterdelegate.bind
import com.dizcoding.adapterdelegate.itemDelegate
import com.sesolutions.R
import com.sesolutions.ui.price.PriceItemResponse
import com.sesolutions.utils.Util

fun priceItemHomeAdapter() = itemDelegate<PriceItemResponse>(R.layout.item_price_home_item)
    .bind {
        val tvPriceValue = containerView.findViewById<TextView>(R.id.tvPriceValue)
        val tvPriceName = containerView.findViewById<TextView>(R.id.tvPriceName)
        tvPriceName.text = it.item_name + " : "
        if (it.item_price == "-") {
            tvPriceValue.text = it.item_price
        } else {
            tvPriceValue.text = Util.formatRupiah(it.item_price?.toDouble())
        }
    }

private fun isOdd(`val`: Int): Boolean {
    return `val` and 0x01 != 0
}
package com.sesolutions.ui.price.adapter

import android.graphics.Color
import android.widget.RelativeLayout
import android.widget.TextView
import com.dizcoding.adapterdelegate.bind
import com.dizcoding.adapterdelegate.itemDelegate
import com.sesolutions.R
import com.sesolutions.ui.price.PriceItemResponse
import com.sesolutions.utils.Util

fun priceItemAdapter() = itemDelegate<PriceItemResponse>(R.layout.item_price_item)
    .bind {
        val tvPriceValue = containerView.findViewById<TextView>(R.id.tvPriceValue)
        val tvPriceName = containerView.findViewById<TextView>(R.id.tvPriceName)
        val layout = containerView.findViewById<RelativeLayout>(R.id.layout_background)
        tvPriceName.text = it.item_name
        if (it.item_price == "-") {
            tvPriceValue.text = it.item_price
        } else {
            tvPriceValue.text = Util.formatRupiah(it.item_price?.toDouble())
        }

        if (isOdd(layoutPosition)) {
            layout.setBackgroundColor(Color.parseColor("#80e8e3"))
        } else {
            layout.setBackgroundColor(Color.parseColor("#d7e0e0"))
        }
    }

private fun isOdd(`val`: Int): Boolean {
    return `val` and 0x01 != 0
}
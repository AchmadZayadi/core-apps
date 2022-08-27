package com.sesolutions.ui.price.adapter

import android.widget.TextView
import com.dizcoding.adapterdelegate.bind
import com.dizcoding.adapterdelegate.itemDelegate
import com.sesolutions.R
import com.sesolutions.ui.price.PriceItemResponse

fun priceItemAdapter() = itemDelegate<PriceItemResponse>(R.layout.item_price_item)
    .bind {
        val tvPriceValue = containerView.findViewById<TextView>(R.id.tvPriceValue)
        val tvPriceName = containerView.findViewById<TextView>(R.id.tvPriceName)
        tvPriceName.text = it.item_name
        if (it.item_price == "-"){
            tvPriceValue.text = it.item_price
        }else{
            tvPriceValue.text = "Rp. "+it.item_price
        }
    }
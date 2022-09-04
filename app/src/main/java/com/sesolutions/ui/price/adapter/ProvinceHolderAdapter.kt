package com.sesolutions.ui.price.adapter

import android.view.View
import android.widget.TextView
import com.dizcoding.adapterdelegate.bind
import com.dizcoding.adapterdelegate.click
import com.dizcoding.adapterdelegate.itemDelegate
import com.sesolutions.R

fun ProvinceHolderAdapter(itemClick: (String) -> Unit) =
    itemDelegate<String>(R.layout.item_province)
        .click(itemClick)
        .bind {
            val tvCityName = containerView.findViewById<TextView>(R.id.tvProvince)
            if (it.equals("null")) {
                tvCityName.visibility = View.GONE
            } else {
                tvCityName.text = it
            }
        }
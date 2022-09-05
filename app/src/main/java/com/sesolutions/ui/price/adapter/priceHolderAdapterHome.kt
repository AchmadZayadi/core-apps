package com.sesolutions.ui.price.adapter

import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dizcoding.adapterdelegate.DelegatesAdapter
import com.dizcoding.adapterdelegate.bind
import com.dizcoding.adapterdelegate.itemDelegate
import com.sesolutions.R
import com.sesolutions.ui.price.PriceItemResponse

fun priceHolderAdapterHome() = itemDelegate<PriceItemModel>(R.layout.item_price_home_holder)
    .bind {
        val tvCityName = containerView.findViewById<TextView>(R.id.tvCityName)
        tvCityName.text = it.city_name
        val rvItems = containerView.findViewById<RecyclerView>(R.id.rvPriceItems)

        val adapter: DelegatesAdapter<PriceItemResponse> = DelegatesAdapter(
            priceItemHomeAdapter()
        )
        rvItems.layoutManager =
            LinearLayoutManager(containerView.context, LinearLayoutManager.HORIZONTAL, false)
        rvItems.adapter = adapter
        adapter.submitList(it.price_items)
    }


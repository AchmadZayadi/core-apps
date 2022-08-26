package com.dizcoding.adapterdelegate

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface ItemDelegate<I, H : RecyclerView.ViewHolder> {
    fun itemType(): Class<out I>
    fun createViewHolder(parent: ViewGroup): H
    fun bindView(position: Int, item: I, holder: H)
}
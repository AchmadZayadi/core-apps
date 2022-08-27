package com.dizcoding.adapterdelegate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes

class LayoutItemDelegate<I>(
    private val type: Class<out I>,
    @LayoutRes private val layoutId: Int
) : ExtensionItemDelegate<I> {

    override fun itemType() = type

    override fun createViewHolder(parent: ViewGroup) =
        ExtensionViewHolder<I>(LayoutInflater.from(parent.context).inflate(layoutId, parent, false))

    override fun bindView(position: Int, item: I, holder: ExtensionViewHolder<I>) {
        holder.holdItem = item
    }
}
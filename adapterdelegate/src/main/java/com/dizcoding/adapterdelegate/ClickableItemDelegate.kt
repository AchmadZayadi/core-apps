package com.dizcoding.adapterdelegate

import android.view.ViewGroup

class ClickableItemDelegate<I>(
    private val origin: ExtensionItemDelegate<I>,
    private val onClick: (I) -> Unit
) : ExtensionItemDelegate<I> by origin {
    override fun createViewHolder(parent: ViewGroup) =
        origin.createViewHolder(parent).apply {
            itemView bindClick onClick
        }
}
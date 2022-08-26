package com.dizcoding.adapterdelegate

import android.view.ViewGroup

class CreateItemDelegate<I>(
    private val origin: ExtensionItemDelegate<I>,
    private val createBlock: ExtensionViewHolder<I>.() -> Unit
) : ExtensionItemDelegate<I> by origin {

    override fun createViewHolder(parent: ViewGroup)  = origin.createViewHolder(parent).apply(createBlock)
}
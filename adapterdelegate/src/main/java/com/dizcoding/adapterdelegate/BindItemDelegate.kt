package com.dizcoding.adapterdelegate

class BindItemDelegate<I>(
    private val origin: ExtensionItemDelegate<I>,
    private val bindBlock: ExtensionViewHolder<I>.(I) -> Unit
) : ExtensionItemDelegate<I> by origin {

    override fun bindView(position: Int, item: I, holder: ExtensionViewHolder<I>) {
        origin.bindView(position, item, holder)
        holder.holdItem?.let { holder.bindBlock(it) }
    }
}
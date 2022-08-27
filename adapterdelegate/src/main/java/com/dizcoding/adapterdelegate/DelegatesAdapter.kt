package com.dizcoding.adapterdelegate

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DelegatesAdapter<I : Any>(
    vararg delegates: ItemDelegate<out I, *>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val delegatesIndexMap: Map<Class<*>, Int>
    private val delegatesList: List<ItemDelegate<I, RecyclerView.ViewHolder>>

    var items = emptyList<I>()

    init {
        val map = mutableMapOf<Class<*>, Int>()
        delegates.forEachIndexed { index, delegate ->
            if (map.put(delegate.itemType(), index) != null) {
                throw IllegalArgumentException()
            }
        }

        delegatesIndexMap = map
        delegatesList = delegates.map {
            @Suppress("UNCHECKED_CAST")
            it as? ItemDelegate<I, RecyclerView.ViewHolder>
                ?: throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int) =
        delegatesIndexMap[items[position]::class.java] ?: throw IllegalArgumentException()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        delegatesList[viewType].createViewHolder(parent)

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegatesList[getItemViewType(position)].bindView(position, items[position], holder)
    }

    fun submitList(list: List<I>) {
        items = list
        notifyDataSetChanged()
    }
}
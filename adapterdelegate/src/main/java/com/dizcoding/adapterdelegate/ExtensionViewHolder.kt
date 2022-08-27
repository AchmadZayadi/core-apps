package com.dizcoding.adapterdelegate

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ExtensionViewHolder<I>(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {
    var holdItem: I? = null

    inline infix fun View.bindClick(
        crossinline onClick: (I) -> Unit
    ) = setOnClickListener { holdItem?.let(onClick) }
}

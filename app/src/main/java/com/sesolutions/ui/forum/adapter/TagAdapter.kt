package com.sesolutions.ui.forum.adapter

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.sesolutions.R
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.forum.ForumResponse
import com.sesolutions.responses.forum.Tag
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.forum.ForumUtil
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.Util

class TagAdapter(private val list: List<Tag>,
                 private val context: Context,
                 private val listener: OnUserClickedListener<Int, Any>,
                 private val loadListener: OnLoadMoreListener) : androidx.recyclerview.widget.RecyclerView.Adapter<TagAdapter.CategoryHolder>() {

    private var themeManager: ThemeManager = ThemeManager()

    override fun onViewAttachedToWindow(holder: CategoryHolder) {
        super.onViewAttachedToWindow(holder)
        if (list.size - 1 == holder.adapterPosition) {
            loadListener.onLoadMore()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {

        return CategoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {

        themeManager.applyTheme(holder.itemView as ViewGroup, context)

        try {
            val cVo = list[position]

            holder.tvTag.text = cVo.text
            holder.cvMain.setOnClickListener { listener.onItemClicked(Constant.Events.SEARCH, cVo.text, 0)}

        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    class CategoryHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
//
        var cvMain: LinearLayout = itemView.findViewById(R.id.cvMain)
//        var ivImage: ImageView = itemView.findViewById(R.id.ivOptionImage)
        var tvTag: TextView = itemView.findViewById(R.id.tvTag)

    }
}
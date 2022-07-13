package com.sesolutions.ui.forum.adapter

import android.content.Context
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
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.forum.ForumCategoryViewFragment
import com.sesolutions.ui.forum.ForumUtil
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog

class BreadCrumbAdapter(private val list: List<ForumResponse.Category>,
                        private val context: Context,
                        private val listener: OnUserClickedListener<Int, Any>,
                        private val loadListener: OnLoadMoreListener) : androidx.recyclerview.widget.RecyclerView.Adapter<BreadCrumbAdapter.CategoryHolder>() {

    @kotlin.jvm.JvmField
    val VT_CATEGORIES: String = "-3"
    val VT_CATEGORY = "-2"
    val VT_SUGGESTION = "-1"
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

        return CategoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_text_image, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {

        themeManager.applyTheme(holder.itemView as ViewGroup, context)

        try {
            val cVo = list[position]
            holder.tvCategoryName.text = cVo.categoryName

            if (position == list.size - 1)
                holder.ivImage.visibility = View.GONE

           /* holder.llTextImage.setOnClickListener {
                for(x in holder.adapterPosition+1 until ForumCategoryViewFragment.breadCrumbList.size)
                    ForumCategoryViewFragment.breadCrumbList.remove(ForumCategoryViewFragment.breadCrumbList[x])
                listener.onItemClicked(Constant.Events.BREADCRUMB, cVo.type, cVo.categoryId!!)
            }*/
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    class CategoryHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        var llTextImage: LinearLayout = itemView.findViewById(R.id.ll_text_image)
        var tvCategoryName: TextView = itemView.findViewById(R.id.tvOptionText)
        var ivImage: ImageView = itemView.findViewById(R.id.ivOptionImage)
    }
}

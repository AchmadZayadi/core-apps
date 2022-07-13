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
import com.sesolutions.responses.forum.ForumResponse2
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.forum.ForumCategoryViewFragment
import com.sesolutions.ui.forum.ForumUtil
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.Util

class SubCategoryAdapter2(private val list: List<ForumResponse2.Category>,
                          private val context: Context,
                          private val listener: OnUserClickedListener<Int, Any>,
                          private val loadListener: OnLoadMoreListener) : androidx.recyclerview.widget.RecyclerView.Adapter<SubCategoryAdapter2.CategoryHolder>() {

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

        return CategoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_image_text_horizontal, parent, false))
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {

        themeManager.applyTheme(holder.itemView as ViewGroup, context)

        try {
            val cVo = list[position]
            Util.showImageWithGlide(holder.ivImage, cVo.catIcon, context, R.drawable.funny_16)
            holder.tvCategoryName.text = cVo.categoryName
            holder.llSubCategory.setOnClickListener {
                ForumCategoryViewFragment.breadCrumbList2.add(cVo)
                listener.onItemClicked(Constant.Events.SUB_CATEGORY, cVo.type, cVo.categoryId!!)
            }

        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    class CategoryHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        var llSubCategory: LinearLayout = itemView.findViewById(R.id.llSubCategory)
        var ivImage: ImageView = itemView.findViewById(R.id.ivOptionImage)
        var tvCategoryName: TextView = itemView.findViewById(R.id.tvOptionText)

    }
}

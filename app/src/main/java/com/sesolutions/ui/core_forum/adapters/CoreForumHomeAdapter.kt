package com.sesolutions.ui.core_forum.adapters
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sesolutions.R
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.feed.Options
import com.sesolutions.responses.forum.ForumResponse
import com.sesolutions.responses.forum.ForumVo
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.core_forum.CoreForumAdapter
import com.sesolutions.utils.Constant

import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.SPref
import kotlin.random.Random

class CoreForumHomeAdapter(private val list: List<ForumResponse.Category>,
                       private val context: Context,
                       private val listener: OnUserClickedListener<Int, Any>,
                       private val loadListener: OnLoadMoreListener) : androidx.recyclerview.widget.RecyclerView.Adapter<CoreForumHomeAdapter.CategoryHolder>() {

    @kotlin.jvm.JvmField
    val VT_CATEGORIES: String = "-3"
    val VT_CATEGORY = "-2"
    val VT_SUGGESTION = "-1"
    private var themeManager: ThemeManager = ThemeManager()
    private var type: String? = null

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

    fun setType(type: String) {
        this.type = type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_core_forum_category, parent, false)
        return CategoryHolder(view)

    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {

        themeManager.applyTheme(holder.itemView as ViewGroup, context)

        try {
            val cVo = list[position]
            holder.tvCategoryName.text = cVo.categoryName
            holder.rltopid.setBackgroundColor(Color.parseColor(Constant.backgroundColor))
//            holder.tvDescription.text = cVo.description
            //Set forums content
            if ( cVo.forums!!.isNotEmpty()) {
                holder.llSubCategory.visibility = View.VISIBLE
                if (holder.subAdapter == null) {
                    /*set child item list*/
                    holder.rvChild.setHasFixedSize(true)
                    holder.rvChild.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
                    (holder.rvChild.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
                    holder.subAdapter = CoreForumAdapter(cVo.forums, context, listener, loadListener)
                    holder.rvChild.adapter = holder.subAdapter
                } else {
                    holder.subAdapter!!.notifyDataSetChanged()
                }
            } else
                holder.llSubCategory.visibility = View.GONE
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }


    class CategoryHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var cvMain: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cvMain)
        var tvCategoryName: TextView = itemView.findViewById(R.id.tvTitle)
        var rltopid: LinearLayoutCompat = itemView.findViewById(R.id.rltopid)
//        var tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        var rvChild: androidx.recyclerview.widget.RecyclerView = itemView.findViewById(R.id.rvChild)
        var llSubCategory: LinearLayoutCompat = itemView.findViewById(R.id.llSubCategory)
        var subAdapter: CoreForumAdapter? = null
    }
}

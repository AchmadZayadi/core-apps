package com.sesolutions.ui.forum.adapter

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
import com.sesolutions.responses.forum.ForumResponse2
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.customviews.FeedOptionPopup
import com.sesolutions.ui.customviews.RelativePopupWindow
import com.sesolutions.ui.forum.ForumCategoryViewFragment
import com.sesolutions.ui.forum.ForumUtil
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.SPref
import kotlin.random.Random

class ForumHomeAdapter2(private val list: List<ForumResponse2.Category>,
                        private val context: Context,
                        private val listener: OnUserClickedListener<Int, Any>,
                        private val loadListener: OnLoadMoreListener) : androidx.recyclerview.widget.RecyclerView.Adapter<ForumHomeAdapter2.CategoryHolder>() {

    private lateinit var iconFont: Typeface
    @kotlin.jvm.JvmField
    val VT_CATEGORIES: String = "-3"
    val VT_CATEGORY = "-2"
    val VT_SUGGESTION = "-1"
    private var themeManager: ThemeManager = ThemeManager()
    private var isUserLoggedIn: Boolean = SPref.getInstance().isLoggedIn(context)
    private var addDrawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.music_add)
    private var dLike: Drawable? = ContextCompat.getDrawable(context, R.drawable.music_like)
    private var dLikeSelected: Drawable? = ContextCompat.getDrawable(context, R.drawable.music_like_selected)
    private var dFavSelected: Drawable? = ContextCompat.getDrawable(context, R.drawable.music_favourite)
    private var dFollow: Drawable? = ContextCompat.getDrawable(context, R.drawable.music_favourite_selected)
    private var dFollowSelected: Drawable? = ContextCompat.getDrawable(context, R.drawable.follow_artist)
    private var dFav: Drawable? = ContextCompat.getDrawable(context, R.drawable.follow_artist_selected)
    private var TXT_BY: String = context.resources.getString(R.string.TXT_BY)
    private var TXT_IN: String = context.resources.getString(R.string.IN_)
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

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forum_category, parent, false)
        return CategoryHolder(view)

    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {

        themeManager.applyTheme(holder.itemView as ViewGroup, context)

        try {
            val cVo = list[position]

            val colors = arrayOf("#d5a900", "#e4007c", "#090088", "#1ee3cf", "#bb7171", "#58b368", "#dd4a14", "#ff502f", "#373a6d",
                    "#e41749", "#c40b13", "#560764", "#c7004c", "#00a8b5", "#0b8457", "#6927ff", "#113f67", "#005792", "#c82121", "#ff0000", "#930077")
            val randomNumber = Random.nextInt(colors.size)
            holder.randomColor.setBackgroundColor(Color.parseColor(colors[randomNumber]))
            holder.tvCategoryName.text = cVo.categoryName
            holder.tvDescription.text = cVo.description
            // set sub categories

            if ( cVo.subcat!!.isNotEmpty()) {
                holder.llSubCategory.visibility = View.VISIBLE
                if (holder.subAdapter == null) {
                    /*set child item list*/
                    holder.rvChild.setHasFixedSize(true)
                    holder.rvChild.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
                    (holder.rvChild.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
                    holder.subAdapter = SubCategoryAdapter2(cVo.subcat!!, context, listener, loadListener)
                    holder.rvChild.adapter = holder.subAdapter
                } else {
                    holder.subAdapter!!.notifyDataSetChanged()
                }
            } else
                holder.llSubCategory.visibility = View.GONE

            holder.cvMain.setOnClickListener {
                listener.onItemClicked(Constant.Events.CATEGORY, cVo.type, cVo.categoryId!!)
                ForumCategoryViewFragment.breadCrumbList2.add(cVo)
//                ForumCategoryViewFragment.breadCrumbAdapter?.notifyDataSetChanged()
            }

        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    private fun showOptionsPopUp(v: View, position: Int, options: List<Options>) {
        try {
            val popup = FeedOptionPopup(v.context, position, listener, options)
            // popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //popup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            val vertPos = RelativePopupWindow.VerticalPosition.CENTER
            val horizPos = RelativePopupWindow.HorizontalPosition.ALIGN_LEFT
            popup.showOnAnchor(v, vertPos, horizPos, true)
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    class CategoryHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        var cvMain: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cvMain)
        var randomColor: View = itemView.findViewById(R.id.randomColor)
        var tvCategoryName: TextView = itemView.findViewById(R.id.tvTitle)
        var tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        var rvChild: androidx.recyclerview.widget.RecyclerView = itemView.findViewById(R.id.rvChild)
        var llSubCategory: LinearLayoutCompat = itemView.findViewById(R.id.llSubCategory)
        var subAdapter: SubCategoryAdapter2? = null
    }
}

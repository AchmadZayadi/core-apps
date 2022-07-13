package com.sesolutions.ui.core_forum

import android.content.Context
import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sesolutions.R
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.forum.ForumResponse
import com.sesolutions.responses.forum.ForumResponse2
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.utils.*

class CoreForumsViewPageAdapter2(private val list: List<ForumResponse2.Topic>,
                                 private val context: Context,
                                 private val listener: OnUserClickedListener<Int, Any>,
                                 private val loadListener: OnLoadMoreListener) : androidx.recyclerview.widget.RecyclerView.Adapter<CoreForumsViewPageAdapter2.CategoryHolder>() {

    private val iconFont: Typeface = FontManager.getTypeface(context, FontManager.FONTAWESOME)
    val VT_GRID_VIEW = "-4"
    val VT_CATEGORIES = "-3"
    val VT_CATEGORY = "-2"
    val VT_SUGGESTION = "-1"
    private val themeManager: ThemeManager = ThemeManager()

    override fun onViewAttachedToWindow(holder: CategoryHolder) {
        super.onViewAttachedToWindow(holder)
        if (list.size - 1 == holder.adapterPosition) {
            loadListener.onLoadMore()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        return CategoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_topic_core, parent, false))
    }

    override fun onBindViewHolder(parentHolder: CategoryHolder, position: Int) {

        themeManager.applyTheme(parentHolder.itemView as ViewGroup, context)

        try {
            val vo = list[position]
            parentHolder.tvCategoryName.text = vo.title
            parentHolder.tvOwnerName.text = vo.ownerTitle

            parentHolder.tvDate1.text = Util.changeDateFormat(context, vo.creation_date)
            parentHolder.tvUser.text = vo.last_post!![0].user_title
            //            holder3.tvDate2.setText(Util.changeDateFormat(context, vo.getLast_post().get(0).getCreation_date()));
            parentHolder.tvViewCount.text = vo.view_count.toString()
            if (vo.view_count > 1)
                parentHolder.tvViews.text = "Views"
            parentHolder.tvReplyCount.text = (vo.post_count - 1).toString()
//            if(vo.sticky == 1){
//                parentHolder.ivSticky.visibility = View.VISIBLE
//                parentHolder.ivSticky.layoutParams
//            }else{
//                parentHolder.ivSticky.visibility = View.GONE
//            }
            if(vo.closed == 1){
                parentHolder.ivLock.visibility = View.VISIBLE
                parentHolder.ivLock.layoutParams
            }else{
                parentHolder.ivLock.visibility = View.GONE
            }
            if(vo.closed == 1 && vo.sticky == 1){
                parentHolder.ivLock.visibility = View.VISIBLE
//                parentHolder.ivSticky.visibility = View.GONE
            }
            if (vo.post_count > 2)
                parentHolder.tvReplies.text = "Replies"
            Util.showImageWithGlide(parentHolder.ivImage, vo.owneImages!!.icon!!, context, R.drawable.placeholder_square)
            parentHolder.ivImage.setOnClickListener{ listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, "", vo.user_id) }
            parentHolder.cvMain.setOnClickListener { v -> listener.onItemClicked(Constant.Events.TOPIC_OPTION, "", vo.topic_id) }

        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class CategoryHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        var tvCategoryName: TextView = itemView.findViewById(R.id.tvTitle)
        var tvViewCount: TextView = itemView.findViewById(R.id.tvViewCount)
        var tvViews: TextView = itemView.findViewById(R.id.tvViews)
        var tvReplies: TextView = itemView.findViewById(R.id.tvReplies)
        var tvReplyCount: TextView = itemView.findViewById(R.id.tvReplyCount)
        var tvOwnerName: TextView = itemView.findViewById(R.id.tvOwnerName)
        var tvDate1: TextView = itemView.findViewById(R.id.tvDate1)
        var tvUser: TextView = itemView.findViewById(R.id.tvUser)
        var cvMain: View = itemView.findViewById(R.id.cvMain)
        var ivImage: ImageView = itemView.findViewById(R.id.ivImage)
//        var ivSticky: ImageView = itemView.findViewById(R.id.ivSticky)
        var ivLock: ImageView = itemView.findViewById(R.id.ivLock)

    }

}
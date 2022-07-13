package com.sesolutions.ui.core_forum

import android.content.Context
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
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.FontManager
import com.sesolutions.utils.Util
import org.apache.commons.lang.StringEscapeUtils

class CoreSearchForumAdapter(private val list: List<ForumResponse2.Topic>,
                             private val context: Context,
                             private val listener: OnUserClickedListener<Int, Any>,
                             private val loadListener: OnLoadMoreListener) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    @kotlin.jvm.JvmField
    val VT_CATEGORIES: String = "-3"
    val VT_CATEGORY = "-2"
    val VT_SUGGESTION = "-1"
    private var themeManager: ThemeManager = ThemeManager()
    private val iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME)

    override fun onViewAttachedToWindow(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        return TopicHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_topic_search_core, parent, false))
    }

    override fun onBindViewHolder(parentHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

        themeManager.applyTheme(parentHolder.itemView as ViewGroup, context)

        try {
            val holder3 = parentHolder as TopicHolder
            val vo = list[position]
            if (vo.resource_type.equals(Constant.ResourceType.FORUM_CTOPIC)) {
                holder3.ivImage.visibility = View.VISIBLE

                val fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(vo.description)
                holder3.tvDescription.text =  fromServerUnicodeDecoded;
                holder3.tvCategoryName.text = vo.title
                holder3.tvViewCount.text = vo.view_count.toString()
                if (vo.view_count > 1)
                    holder3.tvViews.text = "Views"
                holder3.tvReplyCount.text = (vo.post_count - 1).toString()
                if (vo.post_count > 2)
                    holder3.tvReplies.text = "Replies"
                Util.showImageWithGlide(holder3.ivImage, vo.owneImages!!.main, context, R.drawable.placeholder_square)

            } else if (vo.resource_type.equals(Constant.ResourceType.FORUM_POST)) {
                holder3.ivImage.visibility = View.GONE
                holder3.tvCategoryName.text = "     " + vo.topicTitle
                val fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(vo.description)
                holder3.tvDescription.text =  fromServerUnicodeDecoded;
               // holder3.tvDescription.text = "     " + vo.description
                holder3.tvViews1.visibility = View.GONE
                holder3.tvReplies1.visibility = View.GONE
            }
            holder3.cvMain.setOnClickListener { listener.onItemClicked(Constant.Events.TOPIC_OPTION, "", vo.topic_id) }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    inner class TopicHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        internal var cvMain: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cvMain)
        internal var tvCategoryName: TextView = itemView.findViewById(R.id.tvTitle)
        internal var tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        internal var tvViewCount: TextView = itemView.findViewById(R.id.tvViewCount)
        internal var tvViews: TextView = itemView.findViewById(R.id.tvViews)
        internal var tvReplies: TextView = itemView.findViewById(R.id.tvReplies)
        internal var tvReplyCount: TextView = itemView.findViewById(R.id.tvReplyCount)
        internal var ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        internal var tvViews1 : LinearLayout = itemView.findViewById(R.id.views)
        internal var tvReplies1 : LinearLayout = itemView.findViewById(R.id.replies)
    }
}

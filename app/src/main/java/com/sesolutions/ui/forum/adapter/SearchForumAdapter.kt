package com.sesolutions.ui.forum.adapter

import android.content.Context
import android.graphics.Typeface
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.StyleSpan
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
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.FontManager
import com.sesolutions.utils.Util

class SearchForumAdapter(private val list: List<ForumResponse.Topic>,
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

        return TopicHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_topic_search, parent, false))
    }

    override fun onBindViewHolder(parentHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

        themeManager.applyTheme(parentHolder.itemView as ViewGroup, context)

        try {
            val holder3 = parentHolder as TopicHolder
            val vo = list[position]
            if (vo.resource_type.equals(Constant.ResourceType.FORUM_TOPIC)) {

                holder3.llTopicSearchResult.visibility = View.VISIBLE
                holder3.ivImage.visibility = View.VISIBLE
                holder3.llPostSearchResult.visibility = View.GONE

                holder3.tvCategoryName.text = vo.title
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    holder3.tvDescription.text = Html.fromHtml(vo.description, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    holder3.tvDescription.text = Html.fromHtml(vo.description)
                }
                holder3.tvDescription.movementMethod = LinkMovementMethod.getInstance()
                holder3.tvDate1.text = Util.changeDateFormat(context, vo.creation_date)
                holder3.tvLikeCount.text = vo.like_count
                holder3.tvViewCount.text = vo.view_count.toString()
                if (vo.view_count > 1)
                    holder3.tvViews.text = "Views"
                holder3.tvReplyCount.text = (vo.post_count - 1).toString()
                if (vo.post_count > 2)
                    holder3.tvReplies.text = "Replies"
                Util.showImageWithGlide(holder3.ivImage, vo.owneImages!!, context, R.drawable.placeholder_square)

            } else if (vo.resource_type.equals(Constant.ResourceType.SESFORUM_POST)) {
                holder3.llTopicSearchResult.visibility = View.GONE
                holder3.llPostSearchResult.visibility = View.VISIBLE
                holder3.ivImage.visibility = View.GONE
                holder3.tvStats.typeface = iconFont

                val spanStr = SpannableStringBuilder()
                        .append(vo.topicTitle)
                spanStr.setSpan(StyleSpan(Typeface.BOLD), 12, vo.topicTitle!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                holder3.tvTopic.text = spanStr
                holder3.tvCreatedOn.text = Util.changeDateFormat(context, vo.creation_date)
//                holder3.tvDesc.text = vo.description

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    holder3.tvDesc.text = Html.fromHtml(vo.description, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    holder3.tvDesc.text = Html.fromHtml(vo.description)
                }
                val details: String = ("  \uf164 " + vo.like_count + "  \uf118 " + vo.thankCount)
                holder3.tvStats.text = details
            }
            holder3.cvMain.setOnClickListener { listener.onItemClicked(Constant.Events.TOPIC_OPTION, "", vo.topic_id) }

        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    inner class TopicHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        internal var cvMain: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cvMain)
        internal val llTopicSearchResult: LinearLayout = itemView.findViewById(R.id.llTopicSearchResult)
        internal val llPostSearchResult: LinearLayout = itemView.findViewById(R.id.llPostSearchResult)
        internal var tvCategoryName: TextView = itemView.findViewById(R.id.tvTitle)
        internal var tvTopic: TextView = itemView.findViewById(R.id.tvTopic)
        internal var tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        internal var tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
        internal var tvViewCount: TextView = itemView.findViewById(R.id.tvViewCount)
        internal var tvViews: TextView = itemView.findViewById(R.id.tvViews)
        internal var tvReplies: TextView = itemView.findViewById(R.id.tvReplies)
        internal var tvReplyCount: TextView = itemView.findViewById(R.id.tvReplyCount)
        internal var tvStats: TextView = itemView.findViewById(R.id.tvStats)
        internal var tvDate1: TextView = itemView.findViewById(R.id.tvDate1)
        internal var tvLikeCount: TextView = itemView.findViewById(R.id.tvLikeCount)
        internal var tvCreatedOn: TextView = itemView.findViewById(R.id.tvCreatedOn)
        internal var ivImage: ImageView = itemView.findViewById(R.id.ivImage)

    }
}

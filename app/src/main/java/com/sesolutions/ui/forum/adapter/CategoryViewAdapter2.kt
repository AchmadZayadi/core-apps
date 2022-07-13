package com.sesolutions.ui.forum.adapter

import android.content.Context
import android.graphics.Color
import androidx.appcompat.widget.*
import android.text.Html
import android.text.method.LinkMovementMethod
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
import com.sesolutions.responses.forum.ForumVo
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.ui.forum.ForumCategoryViewFragment
import com.sesolutions.ui.forum.ForumUtil
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.Util
import kotlin.random.Random

class CategoryViewAdapter2(private val list: List<ForumVo>,
                           private val context: Context,
                           private val listener: OnUserClickedListener<Int, Any>,
                           private val loadListener: OnLoadMoreListener) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private val themeManager = ThemeManager()
    val VT_HEADING = "-4"
    val VT_CATEGORY = "-3"
    val VT_FORUM = "-2"
    val VT_TOPIC = "-1"

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        return when (list[viewType].type) {
            VT_HEADING -> HeadingHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_category, parent, false))

            VT_FORUM -> ForumHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_forum_new, parent, false))

            VT_TOPIC -> TopicHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false))

            else -> CategoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_forum_category, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(parentHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                                  position: Int) {
        themeManager.applyTheme(parentHolder.itemView as ViewGroup, context)

        try {
            when (list[position].type) {
                VT_HEADING -> {
                    val holder = parentHolder as HeadingHolder
                    val forumVo = list[position]
                    val title = forumVo.getValue<String>()

                    holder.heading.text = title
                }
                VT_FORUM -> {
                    val holder1 = parentHolder as ForumHolder
                    val forumVo = list[position]
                    val forum = forumVo.getValue<ForumResponse2.ForumContent>()

                    holder1.tvTitle.text = forum.title
                    holder1.tvDescription.text = forum.description
                    holder1.tvPostCount.text = forum.post_count.toString()
                    if (forum.post_count> 1)
                        holder1.tvPosts.text = "Posts"
                    holder1.tvTopicCount.text = forum.topic_count.toString()
                    if (forum.topic_count > 2)
                        holder1.tvTopics.text = "Topics"

                    Util.showImageWithGlide(holder1.ivImage, forum.forumIcon, context, R.drawable.placeholder_square)
                    holder1.cvMain.setOnClickListener { v -> listener.onItemClicked(Constant.Events.MUSIC_MAIN, forum.title, forum.forum_id /*holder.getAdapterPosition()*/) }

                }
                VT_TOPIC -> {
                    val holder3 = parentHolder as TopicHolder
                    val fVo = list[position]
                    val vo = fVo.getValue<ForumResponse2.Topic>()
                    holder3.tvCategoryName.text = vo.title
                    holder3.tvOwnerName.text = vo.ownerTitle

                    holder3.tvDate1.text = Util.changeDateFormat(context, vo.creation_date)
                    holder3.tvUser.text = vo.last_post!![0].user_title
                    holder3.tvViewCount.text = vo.view_count.toString()
                    if (vo.view_count > 1)
                        holder3.tvViews.text = "Views"
                    holder3.tvReplyCount.text = (vo.post_count - 1).toString()
                    if (vo.post_count > 2)
                        holder3.tvReplies.text = "Replies"

                    Util.showImageWithGlide(holder3.ivImage, vo.owneImages!!.icon, context, R.drawable.placeholder_square)
                    holder3.cvMain.setOnClickListener { listener.onItemClicked(Constant.Events.TOPIC_OPTION, "", vo.topic_id) }
                    holder3.ivImage.setOnClickListener{ listener.onItemClicked(Constant.Events.CLICKED_HEADER_IMAGE, "", vo.user_id) }

                }
                else -> {
                    val holder = parentHolder as CategoryHolder
                    themeManager.applyTheme(holder.itemView as ViewGroup, context)
                    val content = list[position]
                    val vo = content.getValue<ForumResponse2.Category>()

                    val colors = arrayOf("#d5a900", "#e4007c", "#090088", "#1ee3cf", "#bb7171", "#58b368", "#dd4a14", "#ff502f", "#373a6d",
                            "#e41749", "#c40b13", "#560764", "#c7004c", "#00a8b5", "#0b8457", "#6927ff", "#113f67", "#005792", "#c82121", "#ff0000", "#930077")
                    val randomNumber = Random.nextInt(colors.size)
                    holder.randomColor.setBackgroundColor(Color.parseColor(colors[randomNumber]))
                    holder.tvCategoryName.text = vo.title

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        holder.tvDescription.text = Html.fromHtml(vo.description, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        holder.tvDescription.text = Html.fromHtml(vo.description)
                    }
                    holder.tvDescription.movementMethod = LinkMovementMethod.getInstance()

                    // set sub categories

                    if (null != vo.subsubcat && vo.subsubcat.isNotEmpty()) {
                        holder.llSubCategory.visibility = View.VISIBLE
                        if (holder.subAdapter == null) {
                            /*set child item list*/
                            holder.rvChild.setHasFixedSize(true)
                            holder.rvChild.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
                            (holder.rvChild.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
                            holder.subAdapter = SubCategoryAdapter2(vo.subsubcat, context, listener, loadListener)
                            holder.rvChild.adapter = holder.subAdapter
                        } else {
                            holder.subAdapter!!.notifyDataSetChanged()
                        }
                    } else
                        holder.llSubCategory.visibility = View.GONE

                    holder.cvMain.setOnClickListener { v ->
                        listener.onItemClicked(Constant.Events.CATEGORY, vo.type, vo.categoryId!!)
                        ForumCategoryViewFragment.breadCrumbList2.add(vo)
                    }
                }
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    inner class HeadingHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var heading: AppCompatTextView = itemView.findViewById(R.id.tv_category_heading)
    }

    inner class CategoryHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        var cvMain: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cvMain)
        var randomColor: View = itemView.findViewById(R.id.randomColor)
        var tvCategoryName: TextView = itemView.findViewById(R.id.tvTitle)
        var tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        var llSubCategory: LinearLayoutCompat = itemView.findViewById(R.id.llSubCategory)
        var rvChild: androidx.recyclerview.widget.RecyclerView = itemView.findViewById(R.id.rvChild)
        var subAdapter: SubCategoryAdapter2? = null
    }

    inner class ForumHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        internal var cvMain: View = itemView.findViewById(R.id.cvMain)
        internal var ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        internal var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        internal var tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        internal var tvPostCount: TextView = itemView.findViewById(R.id.tvPostCount)
        internal var tvPosts: TextView = itemView.findViewById(R.id.tvPosts)
        internal var tvTopicCount: TextView = itemView.findViewById(R.id.tvTopicCount)
        internal var tvTopics: TextView = itemView.findViewById(R.id.tvTopics)

    }

    inner class TopicHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        internal var cvMain: androidx.cardview.widget.CardView = itemView.findViewById(R.id.cvMain)
        internal var tvCategoryName: TextView = itemView.findViewById(R.id.tvTitle)
        internal var tvViewCount: TextView = itemView.findViewById(R.id.tvViewCount)
        internal var tvViews: TextView = itemView.findViewById(R.id.tvViews)
        internal var tvReplies: TextView = itemView.findViewById(R.id.tvReplies)
        internal var tvReplyCount: TextView = itemView.findViewById(R.id.tvReplyCount)
        internal var tvOwnerName: TextView = itemView.findViewById(R.id.tvOwnerName)
        internal var tvDate1: TextView = itemView.findViewById(R.id.tvDate1)
        internal var tvUser: TextView = itemView.findViewById(R.id.tvUser)
        internal var ivImage: ImageView = itemView.findViewById(R.id.ivImage)

    }
}
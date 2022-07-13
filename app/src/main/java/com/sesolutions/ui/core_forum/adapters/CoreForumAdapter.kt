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
import com.sesolutions.responses.forum.ForumVo
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.utils.*

class CoreForumAdapter(private val list: List<ForumResponse.ForumContent>,
                   private val context: Context,
                   private val listener: OnUserClickedListener<Int, Any>,
                   private val loadListener: OnLoadMoreListener) : androidx.recyclerview.widget.RecyclerView.Adapter<CoreForumAdapter.CoreForumHolder>() {

    private val iconFont: Typeface = FontManager.getTypeface(context, FontManager.FONTAWESOME)
    val VT_GRID_VIEW = "-4"
    val VT_CATEGORIES = "-3"
    val VT_CATEGORY = "-2"
    val VT_SUGGESTION = "-1"
    private val themeManager: ThemeManager = ThemeManager()

    override fun onViewAttachedToWindow(holder: CoreForumHolder) {
        super.onViewAttachedToWindow(holder)
        if (list.size - 1 == holder.adapterPosition) {
            loadListener.onLoadMore()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoreForumHolder {
        return CoreForumHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_forum_core, parent, false))
    }

    override fun onBindViewHolder(parentHolder: CoreForumHolder, position: Int) {

        themeManager.applyTheme(parentHolder.itemView as ViewGroup, context)

        try {
            val vo = list[position]
            parentHolder.tvTitle.text = vo.title
            parentHolder.tvDescription.text = vo.description
            parentHolder.tvPostCount.text = vo.post_count.toString()
            if (vo.post_count > 1)
                parentHolder.tvPosts.text = "Posts"
            parentHolder.tvTopicCount.text = vo.topic_count.toString()
            if (vo.topic_count > 2)
                parentHolder.tvTopics.text = "Topics"
            Util.showImageWithGlide(parentHolder.ivImage, vo.forumIconcore, context, R.drawable.placeholder_square)
            parentHolder.cvMain.setOnClickListener { v ->
                listener.onItemClicked(Constant.Events.MUSIC_MAIN, vo.title, vo.forum_id /*holder.getAdapterPosition()*/)

            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class CoreForumHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        var cvMain: View = itemView.findViewById(R.id.cvMain)
        var ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        var tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        var tvPostCount: TextView = itemView.findViewById(R.id.tvPostCount)
        var tvPosts: TextView = itemView.findViewById(R.id.tvPosts)
        var tvTopicCount: TextView = itemView.findViewById(R.id.tvTopicCount)
        var tvTopics: TextView = itemView.findViewById(R.id.tvTopics)
    }
}
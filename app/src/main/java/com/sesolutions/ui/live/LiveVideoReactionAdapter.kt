package com.sesolutions.ui.live

import android.content.Context
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView

import com.sesolutions.R
import com.sesolutions.animate.bang.SmallBangView
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.ReactionPlugin
import com.sesolutions.ui.customviews.CustomTextWatcherAdapter
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.Util

class LiveVideoReactionAdapter(private val list: List<ReactionPlugin>,
                               private val context: Context,
                               private val listener: OnUserClickedListener<Int, Any>) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_live_video_comment, parent, false)
            return HeaderHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reaction_icon, parent, false)
            return ReactionHolder(view)
        }
    }


    override fun onBindViewHolder(parentHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {

        try {
            if (position == 0) {
                val holder = parentHolder as HeaderHolder
//                holder.etComment.setOnClickListener { v -> listener.onItemClicked(Constant.Events.REPLY, null, 0) }
                holder.tvPost.setOnClickListener {
                    listener.onItemClicked(Constant.Events.COMMENT, holder.etComment.text.toString(),0)
                    holder.etComment.setText(Constant.EMPTY)
                }
            } else {
                val holder = parentHolder as ReactionHolder
                Util.showImageWithGlide(holder.ivReaction, list[holder.adapterPosition].image)
                //((ReactionHolder) holder).ivReaction.setOnClickListener(v -> listener.onItemClicked(Constant.Events.IMAGE_5, null, holder.getAdapterPosition()));
                holder.sbvReaction.setOnClickListener { v ->
                    listener.onItemClicked(Constant.Events.MUSIC_LIKE, null, holder.adapterPosition)
                    (v as SmallBangView).likeAnimation()
                }

            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class HeaderHolder internal constructor(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        internal lateinit var etComment: AppCompatEditText
        internal lateinit var tvPost: AppCompatTextView
        init {
            try {
                etComment = itemView.findViewById(R.id.etComment)
                tvPost = itemView.findViewById(R.id.tvPost)
                etComment.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                        etComment.width = wm.defaultDisplay.width - 160
                    } else {
                        etComment.width = 200
                    }
                }
                etComment.addTextChangedListener(object : CustomTextWatcherAdapter() {
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        tvPost.visibility = if (s != null && s.isNotEmpty()) View.VISIBLE else View.GONE
                    }
                })

            } catch (e: Exception) {
                CustomLog.e(e)
            }
        }
    }

    inner class ReactionHolder internal constructor(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        internal lateinit var ivReaction: ImageView
        internal lateinit var sbvReaction: SmallBangView
        init {
            try {
                ivReaction = itemView.findViewById(R.id.ivReaction)
                sbvReaction = itemView.findViewById(R.id.sbvReaction)
            } catch (e: Exception) {
                CustomLog.e(e)
            }
        }
    }
}

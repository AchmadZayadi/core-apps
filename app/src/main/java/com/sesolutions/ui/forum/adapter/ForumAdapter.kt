package com.sesolutions.ui.forum.adapter

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
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.utils.*

class ForumAdapter(private val list: List<ForumResponse.Topic>,
                   private val context: Context,
                   private val listener: OnUserClickedListener<Int, Any>,
                   private val loadListener: OnLoadMoreListener) : androidx.recyclerview.widget.RecyclerView.Adapter<ForumAdapter.CategoryHolder>() {

    private val iconFont: Typeface = FontManager.getTypeface(context, FontManager.FONTAWESOME)
    val VT_GRID_VIEW = "-4"
    val VT_CATEGORIES = "-3"
    val VT_CATEGORY = "-2"
    val VT_SUGGESTION = "-1"
    private val themeManager: ThemeManager = ThemeManager()
    private val isUserLoggedIn: Boolean = SPref.getInstance().isLoggedIn(context)

    private val TXT_BY: String = context.resources.getString(R.string.TXT_BY)
    private val TXT_IN: String = context.resources.getString(R.string.IN_)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {

        return CategoryHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false))
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
            if (vo.post_count > 2)
                parentHolder.tvReplies.text = "Replies"

            //            if (vo.isShowRating()) {
            //
            //                if (vo.getRating() > -1) {
            //                    holder3.llStar.setVisibility(View.VISIBLE);
            //                    Drawable dFilledStar = ContextCompat.getDrawable(context, R.drawable.star_filled);
            //                    float rating = vo.getRating();
            //                    if (rating > 0) {
            //                        holder3.ivStar1.setImageDrawable(dFilledStar);
            //                        if (rating > 1) {
            //                            holder3.ivStar2.setImageDrawable(dFilledStar);
            //                            if (rating > 2) {
            //                                holder3.ivStar3.setImageDrawable(dFilledStar);
            //                                if (rating > 3) {
            //                                    holder3.ivStar4.setImageDrawable(dFilledStar);
            //                                    if (rating > 4) {
            //                                        holder3.ivStar5.setImageDrawable(dFilledStar);
            //                                    }
            //                                }
            //                            }
            //                        }
            //                    }
            //                } else {
            //                    holder3.llStar.setVisibility(View.GONE);
            //                }
            //            }

            Util.showImageWithGlide(parentHolder.ivImage, vo.owneImages!!, context, R.drawable.placeholder_square)
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

    }

    fun setRatingStars(newRating: Int) {
        //        if (-1 != newRating) {
        //            if (topic.getRating().getCode() != 100) {
        //                Util.showSnackbar(v, topic.getRating().getMessage());
        //            } else {
        //                topic.getRating().setTotalRatingAverage(newRating);
        //            }
        //        }
        //        if (topic.getRating()) {
        //            v.findViewById(R.id.llStar).setVisibility(View.VISIBLE);
        //            Drawable dFilledStar = ContextCompat.getDrawable(context, R.drawable.star_filled);
        //            float rating = newRating; //topic.getRating().getTotalRatingAverage();
        //            if (rating > 0) {
        //                ((ImageView) v.findViewById(R.id.ivStar1)).setImageDrawable(dFilledStar);
        //                if (rating > 1) {
        //                    ((ImageView) v.findViewById(R.id.ivStar2)).setImageDrawable(dFilledStar);
        //                    if (rating > 2) {
        //                        ((ImageView) v.findViewById(R.id.ivStar3)).setImageDrawable(dFilledStar);
        //                        if (rating > 3) {
        //                            ((ImageView) v.findViewById(R.id.ivStar4)).setImageDrawable(dFilledStar);
        //                            if (rating > 4) {
        //                                ((ImageView) v.findViewById(R.id.ivStar5)).setImageDrawable(dFilledStar);
        //                            }
        //                        }
        //                    }
        //                }
        //            }
        //        } else {
        //            v.findViewById(R.id.llStar).setVisibility(View.GONE);
        //        }
    }
}
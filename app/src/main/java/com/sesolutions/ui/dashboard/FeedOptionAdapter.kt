package com.sesolutions.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.sesolutions.R
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.ui.dashboard.composervo.FeedSearchOptions
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog

class FeedOptionAdapter(val list: List<FeedSearchOptions>,
                        private val context: Context,
                        private val listener: OnUserClickedListener<Int, Any>) : RecyclerView.Adapter<FeedOptionAdapter.ContactHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {

        return ContactHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_feed_option_chip, parent, false))

    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {

        try {

            holder.bindData(list[position])
            if(list[position].isSelected){
                holder.tvFeedText.setBackgroundColor(Color.parseColor(Constant.colorPrimary))
                holder.cvMain.setCardBackgroundColor(Color.parseColor(Constant.colorPrimary))
                holder.tvFeedText.setTextColor(Color.parseColor(Constant.foregroundColor))
            }else{
                holder.tvFeedText.setBackgroundColor(Color.parseColor(Constant.foregroundColor))
                holder.cvMain.setCardBackgroundColor(Color.parseColor(Constant.foregroundColor))
                holder.tvFeedText.setTextColor(Color.parseColor(Constant.colorPrimary))
            }

            /*
            holder.ivForeground.setBackgroundColor(foregroundColor);
            holder.ivForeground.setVisibility(vo.isSelected() ? View . GONE : View . VISIBLE);
            Util.showImageWithGlide(holder.ivFeedImage, vo.getImage(), context, R.drawable.placeholder_3_2);

            Glide.with(context)
                    .asBitmap()
                    .load(vo.image)
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
                            holder.cvMain.setCardBackgroundColor(Color.parseColor(Constant.colorPrimary))
                            return false
                        }

                        override fun onResourceReady(resource: Bitmap?, model: Any, target: Target<Bitmap>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            if (resource != null) {
                                holder.cvMain.setCardBackgroundColor(Palette.from(resource).generate().getVibrantColor(Color.parseColor(Constant.colorPrimary)))
                            }
                            return false
                        }
                    })
                    .into(holder.ivFeedImage)*/

        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun getItemCount(): Int { return list.size }

    inner class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var llMain: LinearLayoutCompat = itemView.findViewById(R.id.llMain)
        // protected View ivForeground;
        var cvMain: CardView = itemView.findViewById(R.id.cvMain)
        var tvFeedText: TextView = itemView.findViewById(R.id.tvFeedText)

        init {
            llMain.setOnClickListener {
                listener.onItemClicked(Constant.Events.FEED_FILTER_OPTION, "" + StaticShare.LAST_POSITION, adapterPosition)
                list[StaticShare.LAST_POSITION].isSelected = false
                StaticShare.LAST_POSITION = adapterPosition
                list[adapterPosition].isSelected = true
                notifyDataSetChanged()
            }
        }

        fun bindData(vo: FeedSearchOptions) {

            tvFeedText.setTextColor(Color.parseColor(Constant.text_color_1))
            tvFeedText.text = vo.value
            tvFeedText.setBackgroundColor(Color.parseColor(Constant.foregroundColor))
            cvMain.setCardBackgroundColor(Color.parseColor(Constant.foregroundColor))
            cvMain.cardElevation = if (vo.isSelected) 4f else 0f
        }
    }
}

package com.sesolutions.ui.store.account

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sesolutions.R
import com.sesolutions.responses.store.ViewOrder
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.Util

class ViewOrderAdapter(private var list: List<ViewOrder.Result.Products.ProductsData>,
                       private val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<ViewOrderAdapter.ContactHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_order, parent, false)
        return ContactHolder(view)
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {

        try {
            val vo = list[position]

            // todo sett image here
            Util.showImageWithGlide(holder.ivImage, vo.images.main.main, context, R.drawable.placeholder_menu)
            holder.tvTitle.text = vo.title
            holder.tvprice.text = vo.price
            holder.tvCount.text = vo.itemCount.toString()
            holder.tvTotal.text = vo.total

        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ContactHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        internal var ivImage: ImageView
        internal var tvTitle: TextView
        internal var tvprice: TextView
        internal var tvTotal: TextView
        internal var tvCount: TextView

        init {

            tvTitle = itemView.findViewById(R.id.tvTitle)
            ivImage = itemView.findViewById(R.id.ivImage)
            tvprice = itemView.findViewById(R.id.tv_price)
            tvTotal = itemView.findViewById(R.id.tvTotal)
            tvCount = itemView.findViewById(R.id.tvCount)


        }
    }
}

package com.sesolutions.ui.courses.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.sesolutions.R
import com.sesolutions.responses.store.checkout.CheckoutResponse

class PriceAdapter(private val list: List<CheckoutResponse.Result.PriceDetails>)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie: CheckoutResponse.Result.PriceDetails = list[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = list.size

}

class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(inflater.inflate(R.layout.layout_text_text_horizontal, parent, false)) {
    private var mTitleView: TextView? = null
    private var mYearView: TextView? = null


    init {
        mTitleView = itemView.findViewById(R.id.tv1)
        mYearView = itemView.findViewById(R.id.tv2)
    }

    fun bind(movie: CheckoutResponse.Result.PriceDetails) {
        mTitleView?.text = movie.title
        mYearView?.text = movie.price
    }

}
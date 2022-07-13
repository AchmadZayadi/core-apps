package com.sesolutions.ui.store.account

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.sesolutions.R
import com.sesolutions.responses.Courses.ViewCourseOrder
import com.sesolutions.responses.store.ViewOrder

class OrderInfoAdapter(private val list: List<ViewOrder.Result.Otherinfo>)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie: ViewOrder.Result.Otherinfo = list[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = list.size

}

class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(inflater.inflate(R.layout.item_order_info, parent, false)) {
    private var mTitleView: TextView? = null
    private var mYearView: TextView? = null


    init {
        mTitleView = itemView.findViewById(R.id.label)
        mYearView = itemView.findViewById(R.id.value)
    }

    fun bind(movie: ViewOrder.Result.Otherinfo) {
        mTitleView?.text = movie.name
        mYearView?.text = movie.label
    }

}
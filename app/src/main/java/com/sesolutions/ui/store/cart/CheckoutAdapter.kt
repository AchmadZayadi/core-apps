package com.sesolutions.ui.store.cart

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sesolutions.R
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.store.checkout.CheckoutResponse
import com.sesolutions.utils.CustomLog

class CheckoutAdapter(private var list: List<CheckoutResponse.Result.CartData>,
                      private val context: Context,
                      private var loadListener: OnLoadMoreListener,
                      private val listener: OnUserClickedListener<Int, Any>) : androidx.recyclerview.widget.RecyclerView.Adapter<CheckoutAdapter.CategoryHolder>(){

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

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkout, parent, false)
        return CategoryHolder(view)
    }


    override fun onBindViewHolder(parentHolder: CategoryHolder, position: Int) {

//        themeManager.applyTheme(parentHolder.itemView as ViewGroup, context)

        try {
            val cVo = list[position]

            if (parentHolder.childAdapter == null) {
                parentHolder.tvStoreName.text = cVo.storeTitle
                /*set child item list*/
                parentHolder.rvChild.setHasFixedSize(true)
                val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
                parentHolder.rvChild.layoutManager = layoutManager
                parentHolder.childAdapter = CheckoutChildAdapter(cVo.productData,context, listener)
                parentHolder.rvChild.adapter = parentHolder.childAdapter
//                holder3.tvSubTotal.text = cVo.subTotal
            } else {
                parentHolder.childAdapter!!.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setType(type: String) {
        this.type = type
    }


    class CategoryHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        var rvChild: androidx.recyclerview.widget.RecyclerView
        var childAdapter: CheckoutChildAdapter? = null
        var tvStoreName: TextView
//        var tvSubTotal: TextView

        init {
            tvStoreName = itemView.findViewById(R.id.tvStoreName)
            rvChild = itemView.findViewById(R.id.rvChild)
//            tvSubTotal = itemView.findViewById(R.id.tvSubTotal)
        }
    }
}
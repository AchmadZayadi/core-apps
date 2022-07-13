package com.sesolutions.ui.courses.cart


import android.annotation.SuppressLint
import android.content.Context
import com.google.android.material.card.MaterialCardView
import androidx.appcompat.widget.AppCompatSpinner
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.sesolutions.R
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.store.checkout.CheckoutResponse
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.Util

class CheckoutChildAdapter(private var list: List<CheckoutResponse.Result.CartData.ProductData>,
                           private var context: Context,
                           private var listener: OnUserClickedListener<Int, Any>) : androidx.recyclerview.widget.RecyclerView.Adapter<CheckoutChildAdapter.CategoryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CategoryHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkout_productdata, parent, false)
        return CategoryHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {

        try {
            val cVo = list[position]

            if (list.size > 1)
                holder.divider.visibility = View.VISIBLE

            setSpinnerData(holder, holder.adapterPosition, cVo)
            Util.showImageWithGlide(holder.ivImage, cVo.productImages.main, context, R.drawable.placeholder_square)
            holder.tvTitle.text = cVo.title
            holder.tvPrice.text = cVo.price
//            holder.qtyCount.setStartCounterValue(cVo.quantity.toString())

            holder.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                @SuppressLint("NewApi")
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                    val selectedItem = parent?.getItemAtPosition(position).toString().toInt()

                    if (CourseCheckoutFragment.qtyMap.containsKey("quantity_${cVo.productId}")){
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        CourseCheckoutFragment.qtyMap.replace("quantity_${cVo.productId}", selectedItem)
//                        }

                    }else {
                        CourseCheckoutFragment.qtyMap["quantity_${cVo.buttons[0].id}"] = selectedItem
                    }
                }
            }
            holder.mcvAddToWishlist.setOnClickListener { listener.onItemClicked(Constant.Events.ADD_TO_WISHLIST, null, cVo.buttons[0].id) }
            holder.mcvRemove.setOnClickListener { listener.onItemClicked(Constant.Events.MEMBER_REMOVE, null, cVo.buttons[0].id) }
//            holder.inc_button.setOnClickListener { listener.onItemClicked(Constant.Events.ATTRIBUTION_CHANGE, holder.qtyCount.counterValue, cVo.productId) }

        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class CategoryHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        var ivImage: ImageView
        var tvTitle: TextView
        var tvPrice: TextView
        var mcvRemove: MaterialCardView
        var mcvAddToWishlist: MaterialCardView
        var divider: View
        var spinner: AppCompatSpinner
//        var dec_button: ImageButton
//        var qtyCount: IncDecView
//        var inc_button: ImageButton

        init {
            ivImage = itemView.findViewById(R.id.ivImage)
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvPrice = itemView.findViewById(R.id.tvPrice)
            mcvRemove = itemView.findViewById(R.id.mcvRemove)
            mcvAddToWishlist = itemView.findViewById(R.id.mcvAddToWishlist)
            divider = itemView.findViewById(R.id.divider)
            spinner = itemView.findViewById(R.id.spinner)
//            qtyCount = itemView.findViewById(R.id.qtyCount)
//            dec_button = itemView.findViewById(R.id.dec_button)
//            inc_button = itemView.findViewById(R.id.inc_button)

        }
    }


    private fun setSpinnerData(v: CategoryHolder, adapterPosition:Int ,cVo: CheckoutResponse.Result.CartData.ProductData) {

        val spinner = v.spinner
        val graphOptionsList = listOf<Int>(1, 2, 3, 4, 5, 6,7,8,9,10)

        val graphOptionsAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, graphOptionsList)
        // Drop down layout style - list view with radio button
        graphOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // attaching data adapter to spinner
        spinner.adapter = graphOptionsAdapter
        spinner.setSelection(cVo.quantity -1, true)

    }

}
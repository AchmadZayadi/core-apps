package com.sesolutions.ui.profile

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.sesolutions.R
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.ui.courses.test.Answer
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog

class InterestAdapter(val list: List<Answer>,
                      private val context: Context,
                      private val listener: OnUserClickedListener<Int, Any>,
                      private val onItemCheckListener: OnItemCheckListener) : RecyclerView.Adapter<InterestAdapter.ContactHolder>() {

    interface OnItemCheckListener {
        fun onItemCheck(item: Any?, position: Int)
        fun onItemUncheck(item: Any?, position: Int)
    }

    private var arrayList = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {

        return ContactHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_interest_card, parent, false))

    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {

        try {

            holder.bindData(list[position])

        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var llMain: LinearLayoutCompat = itemView.findViewById(R.id.llMain)
        // protected View ivForeground;
        var cvMain: LinearLayout = itemView.findViewById(R.id.cvMain)
        var tvFeedText: TextView = itemView.findViewById(R.id.tvFeedText)

        init {
            llMain.setOnClickListener {

                val key = list[adapterPosition].key!!
                if (arrayList.contains(key)) {
                    list[adapterPosition].isSelected = false
                    arrayList.remove(key)
                    onItemCheckListener.onItemUncheck(key, 0)
                } else {
                    arrayList.add(key)
                    list[adapterPosition].isSelected = true
                    onItemCheckListener.onItemCheck(key, 0)
                }
                notifyDataSetChanged()
            }
        }

        fun bindData(vo: Answer) {

            tvFeedText.setTextColor(Color.parseColor(Constant.text_color_1))
            tvFeedText.text = vo.value
            tvFeedText.setBackgroundColor(Color.parseColor(Constant.foregroundColor))
            cvMain.setBackgroundColor(if (vo.isSelected) Color.parseColor(Constant.colorPrimary) else Color.parseColor(Constant.foregroundColor))

        }
    }
}
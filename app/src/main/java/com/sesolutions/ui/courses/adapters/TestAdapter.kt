package com.sesolutions.ui.courses.adapters

import android.content.Context
import android.graphics.Typeface
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sesolutions.R
import com.sesolutions.listeners.OnLoadMoreListener
import com.sesolutions.listeners.OnUserClickedListener
import com.sesolutions.responses.Courses.Test.TestContent
import com.sesolutions.responses.Courses.Test.TestVo
import com.sesolutions.thememanager.ThemeManager
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import com.sesolutions.utils.FontManager

class TestAdapter(private val list: List<TestVo>,
                  private val context: Context,
                  private val listener: OnUserClickedListener<Int, Any>,
                  private val loadListener: OnLoadMoreListener) : RecyclerView.Adapter<TestAdapter.ContactHolder>() {

    private val iconFont: Typeface
    private val themeManager: ThemeManager
    override fun onViewAttachedToWindow(holder: ContactHolder) {
        super.onViewAttachedToWindow(holder)
        if (list.size - 1 == holder.adapterPosition) {
            loadListener.onLoadMore()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test, parent, false)
        return ContactHolder(view)
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        themeManager.applyTheme(holder.itemView as ViewGroup, context)
        try {

            val vo = list[position].getValue<TestContent>()
            if (vo.can_give_test == 1) {
                holder.tvTest!!.visibility = View.VISIBLE
                holder.tvTest!!.setOnClickListener { v: View? -> listener.onItemClicked(Constant.Events.GIVE_TEST, holder, holder.adapterPosition) }
            } else {
                holder.tvTest!!.visibility = View.GONE
            }
            if (vo.resultVisibility == 1) {
                holder.tvResult!!.visibility = View.VISIBLE
                holder.tvResult!!.setOnClickListener { v: View? -> listener.onItemClicked(Constant.Events.RESULT, holder, holder.adapterPosition) }
            } else {
                holder.tvResult!!.visibility = View.GONE
            }
            holder.tvTitle!!.text = vo.title
            holder.tvQuestions!!.typeface = iconFont
            val stats = "\uf059 " + vo.questions + "  \uf06e " + vo.time + " minutes"
            holder.tvQuestions!!.text = stats
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView? = null
        var tvQuestions: TextView? = null
        var tvTest: TextView? = null
        var tvResult: TextView? = null
        private var cvMain: View? = null

        init {
            try {
                cvMain = itemView.findViewById(R.id.cvMain)
                tvTitle = itemView.findViewById(R.id.tvTitle)
                tvQuestions = itemView.findViewById(R.id.tvQuestions)
                tvTest = itemView.findViewById(R.id.tvTest)
                tvResult = itemView.findViewById(R.id.tvResult)
            } catch (e: Exception) {
                CustomLog.e(e)
            }
        }
    }

    init {
        iconFont = FontManager.getTypeface(context, FontManager.FONTAWESOME)
        themeManager = ThemeManager()
    }
}
package com.sesolutions.ui.courses.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.sesolutions.R
import com.sesolutions.ui.courses.test.Answer
import com.sesolutions.ui.dashboard.StaticShare
import com.sesolutions.utils.Constant
import com.sesolutions.utils.CustomLog
import kotlin.collections.ArrayList

class TestOptionsAdapter(
        private val answerList: MutableList<Answer>,
        private val type: MutableList<String>,
        private val currentQuestion: MutableList<Int>,
        private val context: Context,
        private val onItemCheckListener: OnItemCheckListener) : RecyclerView.Adapter<TestOptionsAdapter.OptionHolder>() {
    interface OnItemCheckListener {
        fun onItemCheck(item: Any?, position: Int)
        fun onItemUncheck(item: Any?, position: Int)
    }

    private var arrayListUser = ArrayList<String>()
    private var isRadio = false
    private var lastPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test_option, parent, false)
        return OptionHolder(view)
    }

    override fun onBindViewHolder(holder: OptionHolder, position: Int) {
        try {
            holder.bindData(answerList[position])
        } catch (e: Exception) {
            CustomLog.e(e)
        }
    }

    override fun getItemCount(): Int {
        return answerList.size
    }

    fun setEmptyList() {
        arrayListUser = ArrayList()
    }

    inner class OptionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var cvMain: CardView = itemView.findViewById(R.id.cvMain)
        var tvQuestion: WebView = itemView.findViewById(R.id.tvQuestion)
        var checkBox: CheckBox = itemView.findViewById(R.id.cbAnswer)
        var radioButton: RadioButton = itemView.findViewById(R.id.rbAnswer)

        init {
            try {
                checkBox.isClickable = false
                radioButton.isClickable = false

                cvMain.setOnClickListener {

                    val key = answerList[adapterPosition].key!!
                    val previousKey = answerList[lastPosition].key!!

                    if (isRadio) {

                        if (adapterPosition == lastPosition) {

                            answerList[adapterPosition].isSelected = !answerList[lastPosition].isSelected

                            if (arrayListUser.contains(previousKey)) {
                                arrayListUser.remove(previousKey)
                                onItemCheckListener.onItemUncheck(arrayListUser, currentQuestion[0])
                            } else {
                                arrayListUser.add(key)
                                onItemCheckListener.onItemCheck(arrayListUser, currentQuestion[0])
                            }
                        } else {
                            answerList[lastPosition].isSelected = false
                            answerList[adapterPosition].isSelected = true

                            if (arrayListUser.contains(previousKey)) {
                                arrayListUser.remove(previousKey)
                                onItemCheckListener.onItemUncheck(arrayListUser, currentQuestion[0])
                            }
                            arrayListUser.add(key)
                            onItemCheckListener.onItemCheck(arrayListUser, currentQuestion[0])
                        }
                        lastPosition = adapterPosition

                    } else {
                        if (arrayListUser.contains(key)) {
                            answerList[adapterPosition].isSelected = false
                            arrayListUser.remove(key)
                            onItemCheckListener.onItemUncheck(arrayListUser, currentQuestion[0])
                        } else {
                            arrayListUser.add(key)
                            answerList[adapterPosition].isSelected = true
                            onItemCheckListener.onItemCheck(arrayListUser, currentQuestion[0])
                        }
                    }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                CustomLog.e(e)
            }
        }

        fun bindData(answer: Answer) {

            if (type.isNotEmpty() && type[0] == "checkbox") {
                isRadio = false
                checkBox.isChecked = false
                checkBox.visibility = View.VISIBLE
                radioButton.visibility = View.GONE
            } else {
                isRadio = true
                radioButton.isChecked = false
                checkBox.visibility = View.GONE
                radioButton.visibility = View.VISIBLE
            }
            answer.value?.let { tvQuestion.loadData(it, "text/html", "UTF-8") }

            if (isRadio)
                radioButton.isChecked = answer.isSelected
            else
                checkBox.isChecked = answer.isSelected

        }

    }
}
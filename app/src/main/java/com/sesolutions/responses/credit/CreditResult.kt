package com.sesolutions.responses.credit

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.PaginationHelper
import com.sesolutions.responses.feed.Options
import com.sesolutions.ui.welcome.Dummy

class CreditResult : PaginationHelper() {
    private val terms: JsonElement? = null
    private val guideline: Guideline? = null
    val menus: List<Options>? = null
    val body: List<Options>? = null
    val header: List<Options>? = null
    val items: List<LeaderBoard>? = null
    val transactions: List<Transaction>? = null
    @SerializedName("current_badge")
    val currentBadge: List<Badge>? = null
    @SerializedName("all_badges")
    val allBadges: List<Badge>? = null
    @SerializedName("earn_credit")
    val earnCredit: List<EarnCredit>? = null
    val form: Dummy.Formfields? = null


    val isTransactionEmpty: Boolean get() = null != transactions && transactions.size > 0
    val isCreditNotEmpty: Boolean get() = null != earnCredit && earnCredit.size > 0


    fun getTerms(): String {
        return terms!!.asString
    }

    fun getGuideline(): Guideline {
        return Gson().fromJson(terms, Guideline::class.java)
    }
}


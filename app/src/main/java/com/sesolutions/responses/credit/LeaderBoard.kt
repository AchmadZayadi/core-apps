package com.sesolutions.responses.credit

import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.Notifications

class LeaderBoard {
    @SerializedName("total_credit")
    val totalCredit: String? = null
    val displayname: String? = null
    @SerializedName("user_detail")
    val user: Notifications? = null
    @SerializedName("badge_count")
    val badgeCount: String? = null
}

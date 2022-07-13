package com.sesolutions.responses.feed

import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.Membership

/**
 * Created by AheadSoft on 13-04-2018.
 */

class PeopleSuggestion {
    @SerializedName("user_id")
    val userId: Int = 0
    val title: String? = null
    val user_image: String? = null
    val mutualFriends: String? = null
    val membership: Membership? = null
}

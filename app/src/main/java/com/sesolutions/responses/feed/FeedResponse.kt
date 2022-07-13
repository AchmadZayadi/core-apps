package com.sesolutions.responses.feed

import com.google.gson.annotations.SerializedName

/**
 * Created by root on 14/11/17.
 */

class FeedResponse {

    @SerializedName("result")
    var result: Result? = null
    @SerializedName("session_id")
    var session_id: String? = null
}

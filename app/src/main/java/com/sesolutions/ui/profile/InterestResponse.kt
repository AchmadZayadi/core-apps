package com.sesolutions.ui.profile


import com.google.gson.annotations.SerializedName
import com.sesolutions.responses.ErrorResponse
import com.sesolutions.ui.welcome.Dummy

data class InterestResponse(
        @SerializedName("result")
        var result: Result?,
        @SerializedName("session_id")
        var sessionId: String?
): ErrorResponse() {
    data class Result(
            @SerializedName("customParams")
            var customParams: CustomParams?,
            @SerializedName("formFields")
            var formFields: List<Dummy.Formfields?>?,
            @SerializedName("loggedin_user_id")
            var loggedinUserId: Int?
    ) {
        data class CustomParams(
                @SerializedName("minimum_interest")
                var minimumInterest: String?,
                @SerializedName("resources_type")
                var resourcesType: String?
        )
    }
}
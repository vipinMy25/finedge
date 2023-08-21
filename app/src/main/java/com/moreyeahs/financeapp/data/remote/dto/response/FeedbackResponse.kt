package com.moreyeahs.financeapp.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class FeedbackResponse(

    @field:SerializedName("FeedBack")
    val feedBack: FeedBack? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: Boolean? = null

) {
    data class FeedBack(

        @field:SerializedName("user_Id")
        val userId: String? = null,

        @field:SerializedName("Rating")
        val rating: Int? = null,

        @field:SerializedName("FeedBackText")
        val feedBackText: String? = null,

        @field:SerializedName("_id")
        val id: String? = null
    )
}

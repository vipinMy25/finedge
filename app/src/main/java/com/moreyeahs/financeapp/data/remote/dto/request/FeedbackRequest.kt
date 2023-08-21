package com.moreyeahs.financeapp.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class FeedbackRequest(

	@field:SerializedName("user_Id")
	val userId: String? = null,

	@field:SerializedName("Rating")
	val rating: String? = null,

	@field:SerializedName("FeedBackText")
	val feedBackText: String? = null
)

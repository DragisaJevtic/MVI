package com.example.supertestapplication.repository.network.responses

import com.google.gson.annotations.SerializedName

data class SearchUserResponse(
    @SerializedName("items")
    val userItems: List<UserSearchResponseItem>?,
    @SerializedName("total_count")
    val totalCount: Int?
)

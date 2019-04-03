package com.example.logan.promdate.data

import com.google.gson.annotations.SerializedName

class DefaultResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("result") var result: String)

class FeedResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("result") var result: FeedInnerResponse
)

class FeedInnerResponse(
    @SerializedName("matched")   var matchedUsers: List<User>,
    @SerializedName("unmatched") var unmatchedUsers: List<User>)

package com.example.logan.promdate

import com.google.gson.annotations.SerializedName

class DefaultResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("result") var result: String)
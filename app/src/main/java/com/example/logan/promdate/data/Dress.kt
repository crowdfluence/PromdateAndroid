package com.example.logan.promdate.data
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Dress(
    @SerializedName("ID")  var id: Int = -1): Parcelable
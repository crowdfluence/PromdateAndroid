package com.example.logan.promdate.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

//stores a team that can be easily converted to json
@Parcelize
data class User(@SerializedName("ID")            var id: Int = -1,
           @SerializedName("Email")              var email: String = "",
           @SerializedName("FirstName")          var firstName: String = "",
           @SerializedName("LastName")           var lastName: String = "",
           @SerializedName("Gender")             var gender: String? = null,
           @SerializedName("Grade")              var grade: Int? = null,
           @SerializedName("Biography")          var bio: String? = null,
           @SerializedName("SocialInstagram")    var instagram: String? = null,
           @SerializedName("SocialSnapchat")     var snapchat: String? = null,
           @SerializedName("SocialTwitter")      var twitter: String? = null,
           @SerializedName("DressID")            var dressId: Int = -1,
           @SerializedName("GroupIDs")           var groupIds: List<Int> = listOf(),
           @SerializedName("SchoolID")           var schoolId: Int = -1,
           @SerializedName("Matched")            var matched: Int = -1,
           @SerializedName("PartnerID")          var partnerId: Int = -1,
           @SerializedName("ProfilePicture")     var profilePictureUrl: String = ""): Parcelable
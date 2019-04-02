package com.example.logan.promdate

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

//stores a team that can be easily converted to json
@Parcelize
class User(@SerializedName("UserId")             var id: Int = -1,
           @SerializedName("Email")              var email: String = "",
           @SerializedName("FirstName")          var firstName: String = "",
           @SerializedName("LastName")           var lastName: String = "",
           @SerializedName("Gender")             var gender: String = "",
           @SerializedName("Grade")              var grade: Int = -1,
           @SerializedName("Biography")          var bio: String = "",
           @SerializedName("SocialInstagram")    var instagram: String = "",
           @SerializedName("SocialSnapchat")     var snapchat: String = "",
           @SerializedName("SocialTwitter")      var twitter: String = "",
           @SerializedName("DressID")            var dressId: Int = -1,
           @SerializedName("GroupIDs")           var groupIds: List<Int> = listOf(),
           @SerializedName("SchoolID")           var schoolId: Int = -1,
           @SerializedName("Matched")            var matched: Int = -1,
           @SerializedName("PartnerID")          var partnerId: Int = -1,
           @SerializedName("ProfilePicture")     var profilePictureUrl: String = ""): Parcelable
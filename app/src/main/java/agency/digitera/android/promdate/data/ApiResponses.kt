package agency.digitera.android.promdate.data

import com.google.gson.annotations.SerializedName

class DefaultResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("result") var result: String
)

class FeedResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("result") var result: FeedInnerResponse
)

class FeedInnerResponse(
    @SerializedName("couple")   var couples: List<List<User>>,
    @SerializedName("single")   var singles: List<User>,
    @SerializedName("wishlist") var wishlist: List<User>
)

class UserResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("result") var result: FullUser
)

class FullUser(
    @SerializedName("user")    var self: User,
    @SerializedName("partner") var partner: User?,
    @SerializedName("dress")   var dress: Dress?,
    @SerializedName("school")  var school: School
)

class UpdateResponse(
    @SerializedName("status")  var status: Int,
    @SerializedName("updated") var updated: List<String>,
    @SerializedName("errors")  var errors: List<String>
)

class NotificationResponse(
    @SerializedName("status") var status: Int,
    @SerializedName("result") var result: NotificationInnerResponse
)

class NotificationInnerResponse(
    @SerializedName("notifications") var notifications: List<Notification>
)

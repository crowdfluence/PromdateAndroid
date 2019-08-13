package agency.digitera.android.promdate.data.models

import agency.digitera.android.promdate.data.entities.User
import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("ID")
    var id: Int = -1,

    @SerializedName("UserID")
    var senderId: Int = -1,

    @SerializedName("Type")
    var type: Int = -1,

    @SerializedName("Viewed")
    var viewed: Int = -1,

    @SerializedName("CreationTime")
    var creationTime: Long = -1,

    @SerializedName("Message")
    var message: String = "",

    @SerializedName("Parameters")
    var parameters: String = "",

    @SerializedName("ParametersJSON")
    var body: List<NotificationBody> = listOf()
)

data class NotificationBody(
    @SerializedName("initiator") var id: Int = -1,
    @SerializedName("initiator-data") var sender: User = User()
)
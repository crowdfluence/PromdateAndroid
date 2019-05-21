package agency.digitera.android.promdate.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification(@SerializedName("ID")         var id: Int = -1,
                @SerializedName("UserID")             var senderId: Int = -1,
                @SerializedName("Type")               var type: Int = -1,
                @SerializedName("Viewed")             var viewed: Int = -1,
                @SerializedName("CreationTime")       var creationTime: Int = -1,
                @SerializedName("Message")            var message: String = "",
                @SerializedName("Parameters")         var parameters: String = "",
                @SerializedName("User")               var sender: User = User()
): Parcelable
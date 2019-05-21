package agency.digitera.android.promdate.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class School(
    @SerializedName("ID")        var id: Int = -1,
    @SerializedName("Name")      var name: String = "",
    @SerializedName("RegionID")  var regionId: Int = -1): Parcelable
package agency.digitera.android.promdate.data
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Dress(
    @SerializedName("ID")            var id: Int = -1,
    @SerializedName("Name")          var name: String = "",
    @SerializedName("Brand")         var brand: String = "",
    @SerializedName("ImageUrl")      var imgUrl: String = "",
    @SerializedName("ModelNumber")   var modelNumber: String = "",
    @SerializedName("Tags")          var tags: List<String> = listOf(),
    @SerializedName("Bio")           var bio: String = "",
    @SerializedName("TimesSelected") var timesSelected: Int = -1): Parcelable
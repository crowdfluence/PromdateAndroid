package agency.digitera.android.promdate.data

const val INSTAGRAM = 0
const val SNAPCHAT = 1
const val TWITTER = 2

data class UserSocial(
    val socialMedia: Int,
    val nameSocial: String? = null
)
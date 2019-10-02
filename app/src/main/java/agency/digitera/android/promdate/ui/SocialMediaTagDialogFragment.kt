package agency.digitera.android.promdate.ui

import agency.digitera.android.promdate.R
import agency.digitera.android.promdate.data.INSTAGRAM
import agency.digitera.android.promdate.data.SNAPCHAT
import agency.digitera.android.promdate.data.TWITTER
import agency.digitera.android.promdate.data.UserSocial
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_social_media_tag.*

class SocialMediaTagDialogFragment(private val idSocialMedia: Int) : BottomSheetDialogFragment() {

    var onClickAddAccount: (UserSocial) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(
            R.layout.dialog_social_media_tag, container,
            false
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (idSocialMedia) {
            R.id.instagram -> {
                social_media_image.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.instagram_logo
                    )
                )
                social_media_edit.hint = resources.getString(R.string.twitter)
            }
            R.id.snapchat -> {
                social_media_image.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.snapchat_logo
                    )
                )
                social_media_edit.setCompoundDrawables(null, null, null, null)
                social_media_edit.hint = resources.getString(R.string.snapchat)
            }
            R.id.twitter -> {
                social_media_image.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.twitter_logo
                    )
                )
                social_media_edit.hint = resources.getString(R.string.twitter)
            }
        }

        addButton.setOnClickListener {
            when (idSocialMedia) {
                R.id.instagram -> onClickAddAccount(
                    UserSocial(
                        INSTAGRAM,
                        social_media_edit.text.toString()
                    )
                )
                R.id.snapchat -> onClickAddAccount(
                    UserSocial(
                        SNAPCHAT,
                        social_media_edit.text.toString()
                    )
                )
                R.id.twitter -> onClickAddAccount(
                    UserSocial(
                        TWITTER,
                        social_media_edit.text.toString()
                    )
                )
            }

            dismiss()
        }
    }
}
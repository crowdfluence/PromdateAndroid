package agency.digitera.android.promdate.ui

import agency.digitera.android.promdate.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_social_media.*

class SocialMediaDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    var onSocialMediaClicked: (Int) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(
            R.layout.dialog_social_media, container,
            false
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        instagram.setOnClickListener(this)
        snapchat.setOnClickListener(this)
        twitter.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        onSocialMediaClicked(view.id)
        dismiss()
    }

}
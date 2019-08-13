package agency.digitera.android.promdate.util.dialog

import agency.digitera.android.promdate.R
import agency.digitera.android.promdate.data.entities.Couple
import agency.digitera.android.promdate.data.entities.User
import agency.digitera.android.promdate.util.LoadUrl
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_couple.*


class CoupleDialogFragment(private val couple: Couple) : BottomSheetDialogFragment(), View.OnClickListener {

    var onPartnerClick: (User) -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(
            R.layout.dialog_couple, container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        partner_1_layout.setOnClickListener(this)
        partner_2_layout.setOnClickListener(this)

        //set up profiles
        name_1_text.text = getString(R.string.full_name, couple.user1.firstName, couple.user1.lastName)
        //grade text isn't visible if user hasn't set their grade
        grade_1_text.visibility = if (couple.user1.grade == null) View.GONE else View.VISIBLE
        grade_1_text.text = getString(R.string.grade_number, couple.user1.grade)
        bio_1_text.text = couple.user1.bio
        LoadUrl.loadProfilePicture(
            context!!,
            profile_picture_1_image,
            couple.user1.profilePictureUrl
        )

        name_2_text.text = getString(R.string.full_name, couple.user2.firstName, couple.user2.lastName)
        grade_2_text.visibility = if (couple.user2.grade == null) View.GONE else View.VISIBLE
        grade_2_text.text = getString(R.string.grade_number, couple.user2.grade)
        bio_2_text.text = couple.user2.bio
        LoadUrl.loadProfilePicture(
            context!!,
            profile_picture_2_image,
            couple.user2.profilePictureUrl
        )

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.partner_1_layout -> onPartnerClick(couple.user1)
            R.id.partner_2_layout -> onPartnerClick(couple.user2)
        }
        dismiss()
    }
}
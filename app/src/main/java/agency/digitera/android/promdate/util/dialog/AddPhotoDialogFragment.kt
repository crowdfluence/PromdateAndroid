package agency.digitera.android.promdate.util.dialog

import agency.digitera.android.promdate.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_image_selection.*

class AddPhotoDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var cameraOnClick: () -> Unit = {}
    private var galleryOnClick: () -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(
            R.layout.dialog_image_selection, container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        from_gallery_text.setOnClickListener(this)
        from_camera_text.setOnClickListener(this)
    }

    fun setOnCameraClick(cameraFun: () -> Unit) {
        cameraOnClick = cameraFun
    }

    fun setOnGalleryClick(galleryFun: () -> Unit) {
        galleryOnClick = galleryFun
    }

    override fun onClick(v: View) {
        dismiss()
        when (v.id) {
            R.id.from_gallery_text -> galleryOnClick()
            R.id.from_camera_text -> cameraOnClick()
        }
    }
}
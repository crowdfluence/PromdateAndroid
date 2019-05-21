package agency.digitera.android.promdate.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import agency.digitera.android.promdate.R
import kotlinx.android.synthetic.main.dialog_confirmation.*

class ConfirmationDialog(private val msg: String) : DialogFragment(), View.OnClickListener {

    private var positiveClick: () -> Unit = {}
    private var negativeClick: () -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(
            R.layout.dialog_confirmation, container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        confirmation_prompt_text.text = msg
        positive_text.setOnClickListener(this)
        negative_text.setOnClickListener(this)
    }

    fun setPositiveClick(func: () -> Unit) {
        positiveClick = func
    }

    fun setNegativeClick(func: () -> Unit) {
        positiveClick = func
    }

    override fun onClick(v: View?) {
        dismiss()
        when (v?.id) {
            R.id.negative_text -> negativeClick()
            R.id.positive_text -> positiveClick()
        }
    }

    companion object {
        fun newInstance(msg: String): ConfirmationDialog {
            return ConfirmationDialog(msg)
        }
    }

}
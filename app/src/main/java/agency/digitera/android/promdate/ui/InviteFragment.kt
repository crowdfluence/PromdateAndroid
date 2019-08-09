package agency.digitera.android.promdate.ui

import agency.digitera.android.promdate.DrawerInterface
import agency.digitera.android.promdate.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_invite.*

class InviteFragment : Fragment() {

    private lateinit var drawerInterface: DrawerInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            drawerInterface = activity as DrawerInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement DrawerInterface")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        drawerInterface.lockDrawer()
        return inflater.inflate(R.layout.fragment_invite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up toolbar at top of layout
        val appCompatActivity = activity as AppCompatActivity
        /*val toolbar: Toolbar = toolbar as Toolbar
        toolbar.title = getString(R.string.invite)
        appCompatActivity.setSupportActionBar(toolbar)*/

        //set up back arrow
        appCompatActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        /*invite_button.setOnClickListener {
            invite(it)
        }*/
    }

    private fun invite(view: View) {
        ShareCompat.IntentBuilder.from(activity)
            .setType("text/plain")
            .setChooserTitle(getString(R.string.invite_chooser))
            .setText(getString(R.string.invite_message))
            .startChooser()
    }
}
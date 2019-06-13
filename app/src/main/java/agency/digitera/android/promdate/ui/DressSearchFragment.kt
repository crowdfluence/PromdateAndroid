package agency.digitera.android.promdate.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import agency.digitera.android.promdate.DrawerInterface
import agency.digitera.android.promdate.R
import kotlinx.android.synthetic.main.fragment_dress_search.*

class DressSearchFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_dress_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up toolbar at top of layout
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = toolbar as Toolbar
        toolbar.title = getString(R.string.dress_search)
        appCompatActivity.setSupportActionBar(toolbar)

        appCompatActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        //set up login button
        search_button.setOnClickListener {
            search(it)
        }
    }

    private fun search(view: View) {
        findNavController().navigate(R.id.nav_dress_list)
    }
}

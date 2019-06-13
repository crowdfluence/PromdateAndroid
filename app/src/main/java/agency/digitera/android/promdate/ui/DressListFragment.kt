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
import agency.digitera.android.promdate.adapters.DressAdapter
import agency.digitera.android.promdate.data.Dress
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dress_list.*
import kotlinx.android.synthetic.main.fragment_dress_search.toolbar

class DressListFragment : Fragment() {
    private lateinit var viewAdapter: DressAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var drawerInterface: DrawerInterface
    private val dresses = ArrayList<Dress>()

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
        return inflater.inflate(R.layout.fragment_dress_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up toolbar at top of layout
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = toolbar as Toolbar
        toolbar.title = getString(R.string.search_results)
        appCompatActivity.setSupportActionBar(toolbar)

        appCompatActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        //sets up recycler view
        viewManager = LinearLayoutManager(context)
        viewAdapter = DressAdapter(dresses) {
            onDressClick(it) //sets onClick function for each item in the list
        }
        dress_recycler.apply {
            layoutManager = viewManager
            itemAnimator = DefaultItemAnimator()
            adapter = viewAdapter
            val dividerItemDecoration = DividerItemDecoration(
                dress_recycler.context,
                LinearLayoutManager.VERTICAL
            )
            addItemDecoration(dividerItemDecoration)
        }

        //set up swipe to refresh
        swipe_refresh.setOnRefreshListener {
            loadDresses()
            swipe_refresh.isRefreshing = false
        }

        loadDresses()
    }

    private fun loadDresses() {
        val names = listOf("Nice Dress", "Pretty dress", "Beautiful dress", "Cute dress", "Hot dress", "Sexy dress", "Yet another dress", "Too many dresses!", "Something possibly nice", "Finally the last dress" )
        val modelNums = listOf("#001", "#002", "#003", "#004", "#005", "#006", "Bond, James Bond", "#008", "#009", "#010")
        for (i in 0 until names.size) {
            val newDress = Dress()
            newDress.name = names[i]
            newDress.modelNumber = modelNums[i]
            dresses.add(newDress)
        }
        viewAdapter.notifyDataSetChanged()
    }

    private fun onDressClick(dress: Dress) {
        findNavController().navigate(R.id.nav_dress_profile)
    }
}

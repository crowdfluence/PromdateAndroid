package agency.digitera.android.promdate.ui

import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import agency.digitera.android.promdate.BadTokenException
import agency.digitera.android.promdate.MainActivity
import agency.digitera.android.promdate.adapters.CoupleAdapter
import agency.digitera.android.promdate.R
import agency.digitera.android.promdate.TabInterface
import agency.digitera.android.promdate.data.Couple
import agency.digitera.android.promdate.data.CoupleBoundaryCallback
import agency.digitera.android.promdate.data.User
import agency.digitera.android.promdate.util.CheckInternet
import agency.digitera.android.promdate.util.CoupleDialogFragment
import android.util.Log
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.fragment_scrollable_tab.*
import java.util.concurrent.Executors


class CouplesTabFragment : Fragment(), TabInterface {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: CoupleAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var liveData: LiveData<PagedList<Couple>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scrollable_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //sets up recycler view
        viewManager = LinearLayoutManager(context)
        viewAdapter = CoupleAdapter {
            onCouplesClick(it) //sets onClick function for each item in the list
        }
        recyclerView = view.findViewById<RecyclerView>(R.id.user_recycler).apply {
            layoutManager = viewManager
            itemAnimator = DefaultItemAnimator()
            adapter = viewAdapter
        }

        //set up swipe to refresh
        swipe_refresh.setOnRefreshListener {
            invalidate()
            swipe_refresh.isRefreshing = false
        }

        initializeList()
    }

    private fun initializeList() {
        //set up list of couples; load initial data
        val config = PagedList.Config.Builder()
            .setPageSize(8)
            .setEnablePlaceholders(false)
            .build()

        try {
            liveData = initializedPagedListBuilder(config).build()
        }
        catch (e: BadTokenException) {
            return
        }

        liveData.observe(this, Observer<PagedList<Couple>> { pagedList ->
            viewAdapter.submitList(pagedList)
        })
    }

    override fun invalidate() {
        //forces entire list to refresh
        if (!this::liveData.isInitialized) {
            initializeList()
        } else {
            if (CheckInternet.isNetworkAvailable(context!!)) {
                val executor = Executors.newSingleThreadExecutor()
                executor.execute {
                    CoupleBoundaryCallback.maxLoaded = 0
                    (activity as MainActivity).couplesDb.coupleDao().clearDatabase()
                    swipe_refresh.isRefreshing = false
                }
            }
            else {
                Snackbar.make(
                    constraint_layout,
                    R.string.no_internet,
                    Snackbar.LENGTH_LONG
                ).show()
                swipe_refresh.isRefreshing = false
            }
        }
    }

    @Throws(BadTokenException::class)
    private fun initializedPagedListBuilder(config: PagedList.Config):
            LivePagedListBuilder<Int, Couple> {

        val livePageListBuilder = LivePagedListBuilder<Int, Couple>(
            (activity as MainActivity).couplesDb.coupleDao().couples(),
            config
        )

        //get token
        val sp: SharedPreferences =
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                ?: run {
                    val navOptions = NavOptions.Builder().setPopUpTo(R.id.feedFragment, true).build()
                    findNavController().navigate(R.id.nav_logout, null, navOptions)
                    throw BadTokenException()
                }
        val token = sp.getString("token", null) ?: ""

        livePageListBuilder.setBoundaryCallback(CoupleBoundaryCallback((activity as MainActivity).couplesDb, token))
        return livePageListBuilder
    }

    private fun onCouplesClick(couple: Couple) {

        //create function to run on user selection
        val onUserSelected = fun(user: User) {
            //create action to go to singles page for selected user
            val action = FeedFragmentDirections.navProfile(
                user.id,
                1,
                getString(R.string.full_name, user.firstName, user.lastName)
            )
            findNavController().navigate(action)
        }

        //create dialog
        CoupleDialogFragment(couple).apply {
            onPartnerClick = onUserSelected
        }.also { dialog ->
            dialog.show(
                fragmentManager ?: throw Exception("Fragment manager not found"),
                "couple_dialog_fragment"
            )
        }
    }
}
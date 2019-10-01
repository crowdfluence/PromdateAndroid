package agency.digitera.android.promdate.ui

import agency.digitera.android.promdate.MainActivity
import agency.digitera.android.promdate.R
import agency.digitera.android.promdate.TabInterface
import agency.digitera.android.promdate.adapters.SingleAdapter
import agency.digitera.android.promdate.data.SingleBoundaryCallback
import agency.digitera.android.promdate.data.User
import agency.digitera.android.promdate.util.BadTokenException
import agency.digitera.android.promdate.util.CheckInternet
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_scrollable_tab.*
import java.util.concurrent.Executors


class SinglesTabFragment : Fragment(), TabInterface {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: SingleAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var liveData: LiveData<PagedList<User>>
    private var firstVisiblePos: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scrollable_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //sets up recycler view
        viewManager = LinearLayoutManager(context)
        viewAdapter = SingleAdapter {
            onUserClick(it) //sets onClick function for each item in the list
        }
        recyclerView = view.findViewById<RecyclerView>(R.id.user_recycler).apply {
            layoutManager = viewManager
            itemAnimator = DefaultItemAnimator()
            adapter = viewAdapter
        }

        //set up swipe to refresh
        swipe_refresh.setOnRefreshListener {
            invalidate()
        }

        initializeList()
    }

    private fun initializeList() {
        val config = PagedList.Config.Builder()
            .setPageSize(16)
            .setEnablePlaceholders(false)
            .build()

        try {
            liveData = initializedPagedListBuilder(config).build()
        }
        catch (e: BadTokenException) {
            return
        }

        liveData.observe(this, Observer<PagedList<User>> { pagedList ->
            viewAdapter.submitList(pagedList)
            firstVisiblePos?.let {
                recyclerView.layoutManager?.scrollToPosition(it)
                firstVisiblePos = null
            }
        })
    }

    override fun onPause() {
        super.onPause()
        val firstPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        if (firstPosition > 0)
            firstVisiblePos = firstPosition
    }

    override fun invalidate() {
        if (!this::liveData.isInitialized) {
            initializeList()
        } else {
            if (CheckInternet.isNetworkAvailable(context)) {
                val executor = Executors.newSingleThreadExecutor()
                executor.execute {
                    SingleBoundaryCallback.maxLoaded = 0
                    (activity as MainActivity).singlesDb.singleDao().clearDatabase()
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
    private fun initializedPagedListBuilder(config: PagedList.Config): LivePagedListBuilder<Int, User> {
        val livePageListBuilder = LivePagedListBuilder<Int, User>(
            (activity as MainActivity).singlesDb.singleDao().singles(),
            config)

        //get token
        val sp: SharedPreferences =
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                ?: run {
                    val navOptions = NavOptions.Builder().setPopUpTo(R.id.feedFragment, true).build()
                    findNavController().navigate(R.id.nav_logout, null, navOptions)
                    throw BadTokenException()
                }
        val token = sp.getString("token", null) ?: ""

        livePageListBuilder.setBoundaryCallback(SingleBoundaryCallback((activity as MainActivity).singlesDb, token))
        return livePageListBuilder
    }

    private fun onUserClick(user: User) {
        //open user profile
        val action = FeedFragmentDirections.navProfile(
            user.id,
            0,
            user.firstName + " " + user.lastName
        )
        findNavController().navigate(action)
    }
}
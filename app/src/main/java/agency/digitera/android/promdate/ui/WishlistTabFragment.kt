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
import androidx.navigation.fragment.findNavController
import agency.digitera.android.promdate.*
import agency.digitera.android.promdate.adapters.SingleAdapter
import agency.digitera.android.promdate.data.User
import agency.digitera.android.promdate.data.WishlistBoundaryCallback
import agency.digitera.android.promdate.util.BadTokenException
import agency.digitera.android.promdate.util.CheckInternet
import androidx.navigation.NavOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_scrollable_tab.*
import java.util.concurrent.Executors


class WishlistTabFragment : Fragment(), TabInterface {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: SingleAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var liveData: LiveData<PagedList<User>>

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
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val lastKey = viewAdapter.currentList?.lastKey as Int

        outState.putInt("lastKey", lastKey)
        outState.putParcelable("layout_manager_state", recyclerView.layoutManager?.onSaveInstanceState())
    }

    override fun invalidate() {
        if (!this::liveData.isInitialized) {
            initializeList()
        } else {
            if (CheckInternet.isNetworkAvailable(context!!)) {
                val executor = Executors.newSingleThreadExecutor()
                executor.execute {
                    WishlistBoundaryCallback.maxLoaded = 0
                    (activity as MainActivity).wishlistDb.userDao().clearDatabase()
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
            (activity as MainActivity).wishlistDb.userDao().users(),
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

        livePageListBuilder.setBoundaryCallback(WishlistBoundaryCallback((activity as MainActivity).wishlistDb, token))
        return livePageListBuilder
    }

    private fun onUserClick(user: User) {
        //open user profile
        val action = FeedFragmentDirections.navProfile(
            user.id,
            user.matched,
            user.firstName + " " + user.lastName
        )
        findNavController().navigate(action)
    }
}
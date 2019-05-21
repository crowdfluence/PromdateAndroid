package agency.digitera.android.promdate.ui

import androidx.lifecycle.Observer
import androidx.paging.DataSource
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
import agency.digitera.android.promdate.CoupleAdapter
import agency.digitera.android.promdate.R
import agency.digitera.android.promdate.TabInterface
import agency.digitera.android.promdate.data.CouplesDataSource
import agency.digitera.android.promdate.data.User
import kotlinx.android.synthetic.main.fragment_scrollable_tab.*


class CouplesTabFragment : Fragment(), TabInterface {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: CoupleAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var liveData: LiveData<PagedList<List<User>>>

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
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .build()

        liveData = initializedPagedListBuilder(config).build()

        liveData.observe(this, Observer<PagedList<List<User>>> { pagedList ->
            viewAdapter.submitList(pagedList)
        })
    }

    override fun invalidate() {
        //forces entire list to refresh
        if (!this::liveData.isInitialized) {
            initializeList()
        } else {
            liveData.value!!.dataSource.invalidate()
        }
    }

    private fun initializedPagedListBuilder(config: PagedList.Config):
            LivePagedListBuilder<Int, List<User>> {

        val dataSourceFactory = object : DataSource.Factory<Int, List<User>>() {
            override fun create(): DataSource<Int, List<User>> {
                val sp: SharedPreferences =
                    context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                        ?: throw BadTokenException() //TODO: Return to login
                return CouplesDataSource(sp.getString("token", null) ?: "")
            }
        }
        return LivePagedListBuilder<Int, List<User>>(dataSourceFactory, config)

    }

    private fun onCouplesClick(couple: List<User>) {
        //open couples profile
    }
}
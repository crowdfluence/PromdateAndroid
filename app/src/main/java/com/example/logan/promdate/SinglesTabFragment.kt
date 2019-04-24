package com.example.logan.promdate

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
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import com.example.logan.promdate.data.SinglesDataSource
import com.example.logan.promdate.data.User
import kotlinx.android.synthetic.main.activity_main.*

class SinglesTabFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: SingleAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

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

        initializeList()
    }

    private fun initializeList() {
        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(true)
            .build()

        val liveData = initializedPagedListBuilder(config).build()

        liveData.observe(this, Observer<PagedList<User>> { pagedList ->
            viewAdapter.submitList(pagedList)
        })
    }

    private fun initializedPagedListBuilder(config: PagedList.Config):
            LivePagedListBuilder<Int, User> {

        val dataSourceFactory = object : DataSource.Factory<Int, User>() {
            override fun create(): DataSource<Int, User> {
                val sp: SharedPreferences = context?.getSharedPreferences("login", Context.MODE_PRIVATE) ?: throw BadTokenException() //TODO: Return to login on failed token
                return SinglesDataSource(sp.getString("token", null) ?: "")
            }
        }
        return LivePagedListBuilder<Int, User>(dataSourceFactory, config)

    }

    private fun onUserClick(user: User) {
        //open user profile
        val action = FeedFragmentDirections.navProfile(-1)
        findNavController().navigate(action)
        drawer_layout.closeDrawer(GravityCompat.START)
    }
}
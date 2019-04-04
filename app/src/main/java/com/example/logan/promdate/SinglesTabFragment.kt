package com.example.logan.promdate

import android.arch.lifecycle.Observer
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.logan.promdate.data.SinglesDataSource
import com.example.logan.promdate.data.User

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
            .setPageSize(35)
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
                return SinglesDataSource(context?.filesDir)
            }
        }
        return LivePagedListBuilder<Int, User>(dataSourceFactory, config)

    }

    private fun onUserClick(user: User) {
        //not finished yet
    }
}
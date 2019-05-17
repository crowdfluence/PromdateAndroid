package com.example.logan.promdate.ui

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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.example.logan.promdate.*
import com.example.logan.promdate.data.Notification
import com.example.logan.promdate.data.User
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.fragment_notifications.swipe_refresh
import kotlinx.android.synthetic.main.fragment_scrollable_tab.*


class NotificationsFragment : Fragment() {
    private lateinit var viewAdapter: NotificationsAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var drawerInterface: DrawerInterface
    private val notifications = ArrayList<Notification>()

    override fun onAttach(context: Context) {
        //get drawerInterface to setup navigation drawer
        super.onAttach(context)
        try {
            drawerInterface = activity as DrawerInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement DrawerInterface")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        drawerInterface.lockDrawer()
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up toolbar
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = toolbar as Toolbar
        toolbar.title = getString(R.string.notifications)
        appCompatActivity.setSupportActionBar(toolbar)

        //sets up recycler view
        viewManager = LinearLayoutManager(context)
        viewAdapter = NotificationsAdapter(notifications) {
            onNotificationClick(it) //sets onClick function for each item in the list
        }
        user_recycler.apply {
            layoutManager = viewManager
            itemAnimator = DefaultItemAnimator()
            adapter = viewAdapter
        }

        //set up swipe to refresh
        swipe_refresh.setOnRefreshListener {
            swipe_refresh.isRefreshing = false
        }

        loadNotifications()
    }

    private fun loadNotifications() {
        
    }

    private fun onNotificationClick(notification: Notification) {
        //TODO: Make this
    }
}
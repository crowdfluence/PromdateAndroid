package agency.digitera.android.promdate.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import agency.digitera.android.promdate.*
import agency.digitera.android.promdate.adapters.NotificationsAdapter
import agency.digitera.android.promdate.data.Notification
import agency.digitera.android.promdate.data.NotificationResponse
import agency.digitera.android.promdate.util.ApiAccessor
import agency.digitera.android.promdate.util.MissingSpException
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_notifications.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.DividerItemDecoration


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
/*
        //set up toolbar
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = toolbar as Toolbar
        toolbar.title = getString(R.string.notifications)
        appCompatActivity.setSupportActionBar(toolbar)

        //set up back arrow
        appCompatActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        //sets up recycler view
        viewManager = LinearLayoutManager(context)
        viewAdapter = NotificationsAdapter(notifications) {
            onNotificationClick(it) //sets onClick function for each item in the list
        }
        notification_recycler.apply {
            layoutManager = viewManager
            itemAnimator = DefaultItemAnimator()
            adapter = viewAdapter
            val dividerItemDecoration = DividerItemDecoration(
                notification_recycler.context,
                LinearLayoutManager.VERTICAL
            )
            addItemDecoration(dividerItemDecoration)
        }

        //set up swipe to refresh
        swipe_refresh.setOnRefreshListener {
            loadNotifications()
            swipe_refresh.isRefreshing = false
        }

        loadNotifications()*/
    }

    private fun loadNotifications() {
        val accessor = ApiAccessor()

       /* //get token
        val sp: SharedPreferences =
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: throw MissingSpException()
        val token = sp.getString("token", null) ?: ""

        //send request
        val call = accessor.apiService.getNotifications(token)

        swipe_refresh.isRefreshing = true

        call.enqueue(object : Callback<NotificationResponse> {
            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                swipe_refresh.isRefreshing = false
                Log.e(
                    "NotificationFragment",
                    "Failed to get data! ${t.localizedMessage}, ${t.javaClass.canonicalName}"
                )
                Snackbar.make(
                    constraint_layout, R.string.no_internet,
                    Snackbar.LENGTH_LONG
                ).show()
                //TODO: No internet page
            }

            override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>) {
                swipe_refresh.isRefreshing = false

                val serverResponse = response.body()
                if (serverResponse != null && serverResponse.status == 200) {
                    notifications.clear()
                    notifications.addAll(serverResponse.result.notifications)

                    viewAdapter.notifyDataSetChanged()

                    if (notifications.size == 0) {
                        no_notifications.visibility = View.VISIBLE
                        notification_recycler.visibility = View.GONE
                    }
                    else {
                        no_notifications.visibility = View.GONE
                        notification_recycler.visibility = View.VISIBLE
                    }

                } else {
                    Snackbar.make(
                        constraint_layout, R.string.unexpected_error,
                        Snackbar.LENGTH_LONG
                    ).show()
                    //TODO: Something went wrong page
                }
            }
        })*/
    }

    private fun onNotificationClick(notification: Notification) {
        //open user profile
        val action = NotificationsFragmentDirections.navProfile(
            notification.body[0].sender.id,
            notification.body[0].sender.matched,
            notification.body[0].sender.firstName + " " + notification.body[0].sender.lastName
        )
        findNavController().navigate(action)
    }
}
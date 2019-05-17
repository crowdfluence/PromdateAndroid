package com.example.logan.promdate

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.logan.promdate.data.Notification
import com.example.logan.promdate.util.LoadUrl
import kotlinx.android.synthetic.main.item_notification.*
import kotlinx.android.synthetic.main.item_notification.view.*

//adapter for the collection list recyclerview
class NotificationsAdapter(private val notifications: ArrayList<Notification>,
                           private val clickListener: (Notification) -> Unit): RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    //sets content of view
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(notification: Notification, clickListener: (Notification) -> Unit) {
            with(itemView) {
                title_text.text = resources.getStringArray(R.array.notification_types_array)[notification.type]
                body_text.text = notification.message //TODO: Change to string template
                LoadUrl.loadUrl(context, sender_image, notification.sender.profilePictureUrl)
                icon_image.setImageDrawable(context.getDrawable(R.drawable.ic_broken_heart))
                setOnClickListener { clickListener(notification) }
            }
        }
    }

    //create new view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false) as View

        return ViewHolder(itemView)
    }

    //replace contents of view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifications[position], clickListener)
    }

    //return size of dataset
    override fun getItemCount() = notifications.size
}
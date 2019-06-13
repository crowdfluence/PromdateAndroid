package agency.digitera.android.promdate.adapters

import agency.digitera.android.promdate.R
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agency.digitera.android.promdate.data.Notification
import agency.digitera.android.promdate.util.LoadUrl
import android.content.Context
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationsAdapter(private val notifications: ArrayList<Notification>,
                           private val clickListener: (Notification) -> Unit): RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    //sets content of view
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(notification: Notification, clickListener: (Notification) -> Unit) {
            with(itemView) {
                title_text.text = resources.getStringArray(R.array.notification_types_array)[notification.type - 1] //TODO: Get max to change
                val bodyText = resources.getStringArray(R.array.notification_messages_array)[notification.type - 1]
                body_text.text = String.format(bodyText, notification.body[0].sender.firstName, notification.body[0].sender.lastName)
                LoadUrl.loadProfilePicture(context, sender_image, notification.body[0].sender.profilePictureUrl)
                icon_image.setImageDrawable(when (notification.type) {
                    1, 2 -> context.getDrawable(R.drawable.ic_heart_red)
                    3, 4 -> context.getDrawable(R.drawable.ic_broken_heart)
                    else -> null
                })
                setOnClickListener { clickListener(notification) }
                time_text.text = getTime(System.currentTimeMillis() / 1000L - notification.creationTime, context)
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

    fun getTime(time: Long, context: Context): String {
        var remainingTime = time

        if (remainingTime <= 60) {
            return context.getString(R.string.time_seconds, remainingTime)
        }
        remainingTime /= 60
        if (remainingTime <= 60) {
            return context.getString(R.string.time_minutes, remainingTime)
        }
        remainingTime /= 60
        if (remainingTime <= 24) {
            return context.getString(R.string.time_hours, remainingTime)
        }
        remainingTime /= 24
        if (remainingTime <= 7) {
            return context.getString(R.string.time_days, remainingTime)
        }
        remainingTime /= 7
        if (remainingTime <= 52) {
            return context.getString(R.string.time_weeks, remainingTime)
        }
        remainingTime *= 7 / 365
        return context.getString(R.string.time_years, remainingTime)
    }
}
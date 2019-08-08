package agency.digitera.android.promdate.ui

import agency.digitera.android.promdate.App
import agency.digitera.android.promdate.DrawerInterface
import agency.digitera.android.promdate.R
import android.app.Notification
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Build
import android.os.Parcelable
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
class NotificationManagerCompat
//open class Notification : Parcelable




public class NotifyFragment : AppCompatActivity() {
    private var notificationManager: NotificationManagerCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = NotificationManagerCompat.from(this)
    }

    public fun unmatched() {

        val title = "Unmatched"
        val message = "You just lost your date to prom. :("

        val notification = NotificationCompat.Builder(this,App.CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_broken_heart)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()
        notificationManager!!.notify(1,notification)

    }

    public fun noteUnmatched() {
        unmatched()
    }

    public fun matchRequest() {

        val title = "Match Request"
        val message = "Congrats, you were just offered a request to prom"

        val notification = NotificationCompat.Builder(this,App.CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_heart_red)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()

        notificationManager!!.notify(2,notification)

    }

    public fun notematchRequest() {
        matchRequest()
    }

    public fun matchRejected() {

        val title = "Match Rejected"
        val message = "I'm sorry you were just rejected"

        val notification = NotificationCompat.Builder(this,App.CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_broken_heart)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()
        notificationManager!!.notify(3,notification)

    }

    public fun notematchRejected() {
        matchRejected()
    }

    public fun matchApproved() {

        val title = "Match Approved"
        val message = "Congrats you have a date to your prom"

        val notification = NotificationCompat.Builder(this,App.CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_heart_red)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()
        notificationManager!!.notify(4,notification)

    }

    public fun notematchApproved() {
        matchApproved()
    }

    public fun note() {
        matchRequest()
        matchRejected()
        unmatched()
    }
}

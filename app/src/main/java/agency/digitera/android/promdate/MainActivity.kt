package agency.digitera.android.promdate

import agency.digitera.android.promdate.data.CoupleDb
import agency.digitera.android.promdate.data.SingleDb
import agency.digitera.android.promdate.data.WishlistDb
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import agency.digitera.android.promdate.ui.FeedFragmentDirections
import agency.digitera.android.promdate.util.ConfirmationDialog
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.fragment_feed.*


class MainActivity : AppCompatActivity(), DrawerInterface {

    lateinit var singlesDb: SingleDb
    lateinit var wishlistDb: WishlistDb
    lateinit var couplesDb: CoupleDb

    private var notificationManager: NotificationManagerCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = NotificationManagerCompat.from(this)

        singlesDb = SingleDb.create(this)
        wishlistDb = WishlistDb.create(this)
        couplesDb = CoupleDb.create(this)
    }


    public fun unmatched() {

        val title = "Unmatched"
        val message = "You just lost your date to prom. :("

        val notification = NotificationCompat.Builder(this, App.CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_broken_heart)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()
        notificationManager!!.notify(1, notification)

    }
public fun noteUnmatched(){
    unmatched()
}

    public fun matchRequest() {

        val title = "Match Request"
        val message = "Congrats, you were just offered a request to prom"

        val notification = NotificationCompat.Builder(this, App.CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_heart_red)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()

        notificationManager!!.notify(2, notification)

    }
    public fun notematchRequest(){
        matchRequest()
    }
    public fun matchRejected() {

        val title = "Match Rejected"
        val message = "I'm sorry you were just rejected"

        val notification = NotificationCompat.Builder(this, App.CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_broken_heart)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()
        notificationManager!!.notify(3, notification)

    }

    public fun notematchRejected(){
        matchRejected()
    }

    public fun matchApproved() {

        val title = "Match Approved"
        val message = "Congrats you have a date to your prom"

        val notification = NotificationCompat.Builder(this, App.CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_heart_red)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()
        notificationManager!!.notify(4, notification)

    }
    public fun notematchApproved(){
        matchApproved()
    }
    public fun note(){
        matchRequest()
        matchRejected()
        unmatched()
    }

    override fun onSupportNavigateUp() =
        findNavController(R.id.nav_host_fragment).navigateUp()

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun lockDrawer() {
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun unlockDrawer() {
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    //sets up drawer with toolbar
    override fun setupDrawer(toolbar: Toolbar, currentLocation: Int) {
        val navController = findNavController(R.id.nav_host_fragment)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setupWithNavController(navController)

        nav_view.menu.getItem(currentLocation).isChecked = true

        val nv = nav_view
        nv.setNavigationItemSelectedListener { item ->
            drawer_layout.closeDrawer(GravityCompat.START)
            when (item.itemId) {
                R.id.nav_profile -> {
                    val action = FeedFragmentDirections.navProfile(-1)
                    findNavController(R.id.nav_host_fragment).navigate(action) //TODO: Change to logged in user
                    drawer_layout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_logout -> {
                    ConfirmationDialog(getString(R.string.confirm_logout)).apply {
                        setPositiveClick {
                            val sp: SharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                            sp.edit().putString("token", null).apply()

                            findNavController(R.id.nav_host_fragment).navigate(R.id.nav_logout)
                        }
                    }.also { dialog ->
                        dialog.show(
                            supportFragmentManager,
                            "confirm_logout_dialog_fragment"
                        )
                    }

                }
                R.id.nav_settings -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_settings)

                    with(NotificationManagerCompat.from(this)){
                    }


                }
                R.id.nav_notifications -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_notifications)
                }
                R.id.nav_invite -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_invite)
                }
                R.id.nav_dress_registry -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_dress_search)
                }
            }
            true
        }
    }
}

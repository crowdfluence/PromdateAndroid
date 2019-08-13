package agency.digitera.android.promdate

import agency.digitera.android.promdate.data.db.CoupleDb
import agency.digitera.android.promdate.data.db.SingleDb
import agency.digitera.android.promdate.data.db.WishlistDb
import agency.digitera.android.promdate.ui.tabs.FeedFragmentDirections
import agency.digitera.android.promdate.util.dialog.ConfirmationDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), DrawerInterface {

    lateinit var singlesDb: SingleDb
    lateinit var wishlistDb: WishlistDb
    lateinit var couplesDb: CoupleDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        singlesDb = SingleDb.create(this)
        wishlistDb = WishlistDb.create(this)
        couplesDb = CoupleDb.create(this)
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
                            val sp: SharedPreferences =
                                getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
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

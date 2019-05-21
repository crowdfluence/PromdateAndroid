package agency.digitera.android.promdate

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
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), DrawerInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
                    val sp: SharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    sp.edit().putString("token", null).apply()

                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_logout)
                }
                R.id.nav_settings -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_settings)
                }
                R.id.nav_notifications -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.nav_notifications)
                }
            }
            true
        }
    }
}

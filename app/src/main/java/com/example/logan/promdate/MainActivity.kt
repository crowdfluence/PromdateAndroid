package com.example.logan.promdate

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.logan.promdate.R.styleable.NavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*


class MainActivity : AppCompatActivity() {
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

    fun setupNavDrawer(toolbar: Toolbar) {
        val navController = findNavController(R.id.nav_host_fragment)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        drawer_layout.nav_view.setupWithNavController(navController)

        drawer_layout.nav_view.menu.getItem(0).isChecked = true

        val nv = findViewById<NavigationView>(R.id.nav_view)
        nv.setNavigationItemSelectedListener { item ->
                val id = item.itemId
                when (id) {
                    R.id.nav_profile -> {
                        val action = FeedFragmentDirections.navProfile(0)
                        findNavController(R.id.nav_host_fragment).navigate(action) //TODO: Change to logged in user
                        drawer_layout.closeDrawer(GravityCompat.START)
                    }
                }
                true
        }
    }
}

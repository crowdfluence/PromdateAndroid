package com.example.logan.promdate

import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main_feed.*


class MainFeedActivity : AppCompatActivity() {

    private var pagerAdapter: TabAdapter? = null
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_feed)

        //set up navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setCheckedItem(0)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }

        //set up toolbar at top of layout
        val toolbar: Toolbar = findViewById(R.id.include)
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)

        //add menu button to toolbar
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        //set adapter to return single/couple fragments
        pagerAdapter = TabAdapter(supportFragmentManager)

        //set up view pager with selections adapter
        container.adapter = pagerAdapter

        //set up tab layout
        tab_layout.setupWithViewPager(container)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main_feed_appbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> true
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class TabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.singles)
                1 -> getString(R.string.couples)
                else -> null
            }
        }

        override fun getItem(position: Int): Fragment? {
            return when (position) {
                0 -> SinglesTabFragment()
                1 -> CouplesTabFragment()
                else -> throw Exception("Invalid tab value")
            }
        }

        override fun getCount(): Int = 2
    }
}

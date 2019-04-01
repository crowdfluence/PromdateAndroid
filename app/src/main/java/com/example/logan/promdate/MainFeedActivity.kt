package com.example.logan.promdate

import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main_feed.*
import android.support.design.widget.TabLayout


class MainFeedActivity : AppCompatActivity() {

    private var pagerAdapter: TabAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_feed)

        //set up toolbar at top of layout
        val toolbar: Toolbar = findViewById(R.id.include)
        toolbar.title = getString(R.string.app_name)
        setSupportActionBar(toolbar)

        //add menu button to toolbar
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        pagerAdapter = TabAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = pagerAdapter

        //set up tab layout
        tab_layout.setupWithViewPager(container)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main_feed, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_search) {
            return true
        }

        return super.onOptionsItemSelected(item)
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
                else -> null
            }
        }

        override fun getCount(): Int = 2
    }
}

package com.example.logan.promdate

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_feed.*


class FeedFragment : Fragment() {

    private var pagerAdapter: TabAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up toolbar at top of layout
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = include as Toolbar
        toolbar.title = getString(R.string.app_name)
        appCompatActivity.setSupportActionBar(toolbar)
        (activity as MainActivity).setupNavDrawer(toolbar)

        //add menu button to toolbar
        appCompatActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        //set adapter to return single/couple fragments
        pagerAdapter = TabAdapter(activity?.supportFragmentManager ?: throw Exception("WTF")) //TODO: Change

        //set up view pager with selections adapter
        container.adapter = pagerAdapter

        //set up tab layout
        tab_layout.setupWithViewPager(container)
    }

    inner class TabAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm) {

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.singles)
                1 -> getString(R.string.couples)
                else -> null
            }
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> SinglesTabFragment()
                1 -> CouplesTabFragment()
                else -> throw Exception("Invalid tab value")
            }
        }

        override fun getCount(): Int = 2
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_main_feed_appbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //controls what happens when buttons on toolbar are selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}


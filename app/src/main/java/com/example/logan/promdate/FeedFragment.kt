package com.example.logan.promdate

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.fragment_feed.*
import android.content.Context
import kotlinx.android.synthetic.main.activity_main.*

class FeedFragment : Fragment() {

    private var pagerAdapter: TabAdapter? = null
    private lateinit var drawerInterface: DrawerInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            drawerInterface = activity as DrawerInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement DrawerInterface")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        drawerInterface.unlockDrawer()
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up toolbar at top of layout
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = include as Toolbar
        toolbar.title = getString(R.string.app_name)
        appCompatActivity.setSupportActionBar(toolbar)
        drawerInterface.setupDrawer(toolbar, 0)

        //add menu button to toolbar
        appCompatActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        //set adapter to return single/couple fragments
        pagerAdapter = TabAdapter(childFragmentManager)

        //set up view pager with selections adapter
        view_pager.adapter = pagerAdapter

        //set up tab layout
        tab_layout.setupWithViewPager(view_pager)
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


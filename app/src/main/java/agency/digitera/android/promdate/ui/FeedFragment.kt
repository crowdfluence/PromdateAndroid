package agency.digitera.android.promdate.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.fragment_feed.*
import android.content.Context
import android.content.SharedPreferences
import agency.digitera.android.promdate.util.ApiAccessor
import agency.digitera.android.promdate.DrawerInterface
import agency.digitera.android.promdate.R
import agency.digitera.android.promdate.data.UserResponse
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedFragment : Fragment() {

    private var pagerAdapter: TabAdapter? = null
    private lateinit var drawerInterface: DrawerInterface

    override fun onAttach(context: Context) {
        //get drawerInterface to setup navigation drawer
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
        val toolbar: Toolbar = toolbar as Toolbar
        toolbar.title = getString(R.string.app_name)
        appCompatActivity.setSupportActionBar(toolbar)
        drawerInterface.setupDrawer(toolbar, 0)

        //add menu button to toolbar
        appCompatActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        //TODO: Make scroll position get preserved (room library?)

        //set adapter to return single/couple fragments
        pagerAdapter = TabAdapter(childFragmentManager)

        //set up view pager with selections adapter
        view_pager.adapter = pagerAdapter

        //set up tab layout
        tab_layout.setupWithViewPager(view_pager)

        //gets user id to store in file
        val sp: SharedPreferences? = context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val token: String = sp?.getString("token", null) ?: ""

        val accessor = ApiAccessor()
        val call = accessor.apiService.getUser(token)
        call.enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Snackbar.make(main_content, R.string.no_internet,
                    Snackbar.LENGTH_LONG)
                    .show()
            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val serverResponse = response.body()
                if (serverResponse != null && serverResponse.status == 200) {
                    sp?.edit()?.putInt("userId", serverResponse.result.self.id)?.apply()
                }
                else {
                    Snackbar.make(main_content, R.string.unexpected_error,
                        Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        })
    }

    override fun onResume() {
        drawerInterface.unlockDrawer()
        super.onResume()
    }

    inner class TabAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.couples)
                1 -> getString(R.string.singles)
                2 -> getString(R.string.wishlist)
                else -> null
            }
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> CouplesTabFragment()
                1 -> SinglesTabFragment()
                2 -> WishlistTabFragment()
                else -> throw Exception("Invalid tab value")
            }
        }

        override fun getCount(): Int = 3
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_feed, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //controls what happens when buttons on toolbar are selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}


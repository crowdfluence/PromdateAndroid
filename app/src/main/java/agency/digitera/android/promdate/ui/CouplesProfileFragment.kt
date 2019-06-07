package agency.digitera.android.promdate.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import agency.digitera.android.promdate.*
import agency.digitera.android.promdate.data.FullUser
import agency.digitera.android.promdate.data.UserResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_couple.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import agency.digitera.android.promdate.util.ApiAccessor
import agency.digitera.android.promdate.util.LoadUrl


class CouplesProfileFragment : Fragment() {

    private val couplesFragArgs: CouplesProfileFragmentArgs by navArgs()
    private lateinit var drawerInterface: DrawerInterface
    private var requestCompleted = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            drawerInterface = activity as DrawerInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement MyInterface")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        drawerInterface.lockDrawer()
        return inflater.inflate(R.layout.fragment_couple, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up toolbar at top of layout
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = toolbar as Toolbar
        toolbar.title = couplesFragArgs.userNames
        appCompatActivity.setSupportActionBar(toolbar)

        //set up back arrow
        appCompatActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        loadUser(couplesFragArgs.user1Id, 0)
        loadUser(couplesFragArgs.user2Id, 1)
    }

    private fun loadUser(id: Int, userPosition: Int) {
        //load user data
        val accessor = ApiAccessor()
        val sp: SharedPreferences =
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: throw MissingSpException()
        val token = sp.getString("token", null) ?: ""

        val call = accessor.apiService.getUser(token, id)

        loading_pb.visibility = View.VISIBLE

        call.enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(
                    "ProfileFragmentOnCreate",
                    "Failed to get data! ${t.localizedMessage}, ${t.javaClass.canonicalName}"
                )
                Snackbar.make(constraint_layout, R.string.no_internet,
                    Snackbar.LENGTH_LONG)
                    .show()
                if (requestCompleted) {
                    loading_pb.visibility = View.GONE
                }
                else {
                    requestCompleted = true
                }
                //TODO: Proper no internet page
            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val serverResponse = response.body()
                if (serverResponse != null && serverResponse.status == 200) {
                    val user: FullUser = serverResponse.result

                    //set up user profile with user's information
                    when (userPosition) {
                        0 -> {
                            if (user.self.profilePictureUrl.isNotEmpty()) {
                                LoadUrl.loadUrl(context!!, profile_picture_1_image, user.self.profilePictureUrl)
                            }
                            name_1_text . text = context ?. getString (R.string.full_name, user.self.firstName, user.self.lastName)
                            school_1_text.text = user.school.name
                            grade_1_text.text = context?.getString(R.string.grade_variable, user.self.grade)
                            profile_picture_1_image.setOnClickListener {
                                val action = FeedFragmentDirections.navProfile(
                                    user.self.id,
                                    1,
                                    user.self.firstName + " " + user.self.lastName
                                )
                                findNavController().navigate(action)
                            }
                        }
                        1 -> {
                            if (user.self.profilePictureUrl.isNotEmpty()) {
                                LoadUrl.loadUrl(context!!, profile_picture_2_image, user.self.profilePictureUrl)
                            }
                            name_2_text . text = context ?. getString (R.string.full_name, user.self.firstName, user.self.lastName)
                            school_2_text.text = user.school.name
                            grade_2_text.text = context?.getString(R.string.grade_variable, user.self.grade)
                            profile_picture_2_image.setOnClickListener {
                                val action = FeedFragmentDirections.navProfile(
                                    user.self.id,
                                    1,
                                    user.self.firstName + " " + user.self.lastName
                                )
                                findNavController().navigate(action)
                            }
                        }
                    }

                    //show page
                    if (requestCompleted) {
                        loading_pb.visibility = View.GONE
                        blank_group.visibility = View.VISIBLE
                    }
                    else {
                        requestCompleted = true
                    }
                }
                else {
                    Snackbar.make(constraint_layout, R.string.unexpected_error,
                        Snackbar.LENGTH_LONG)
                        .show()
                    if (requestCompleted) {
                        loading_pb.visibility = View.GONE
                    }
                    else {
                        requestCompleted = true
                    }
                }
            }
        })
    }
}


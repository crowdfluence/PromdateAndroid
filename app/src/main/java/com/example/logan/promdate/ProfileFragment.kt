package com.example.logan.promdate

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.logan.promdate.data.User
import com.example.logan.promdate.data.UserResponse
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {

    private val profileFragmentArgs: ProfileFragmentArgs by navArgs()
    private lateinit var drawerInterface: DrawerInterface

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
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up toolbar at top of layout
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = toolbar as Toolbar
        if (profileFragmentArgs.userName != null) {
            toolbar.title = profileFragmentArgs.userName
        }
        else {
            toolbar.title = getString(R.string.your_profile)
        }
        appCompatActivity.setSupportActionBar(toolbar)

        //set up back arrow
        appCompatActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        //load user data
        val accessor = ApiAccessor()
        val sp: SharedPreferences =
            context?.getSharedPreferences("login", Context.MODE_PRIVATE) ?: throw BadTokenException()
        val token = sp.getString("token", null) ?: ""

        var userId: Int? = profileFragmentArgs.userId
        if (userId == -1) {
            userId = null
        }

        val call = accessor.apiService.getUser(token, userId)

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
                loading_pb.visibility = View.GONE
                //TODO: Proper no internet
            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.body()?.status == 200) {
                    val user: User = response.body()?.result?.user ?: User()
                    val partner: User? = response.body()?.result?.partner

                    loading_pb.visibility = View.GONE
                    blank_group.visibility = View.VISIBLE

                    //set up user profile with user's information
                    if (user.profilePictureUrl.isNotEmpty()) {
                        profile_picture_image.loadUrl(user.profilePictureUrl)
                    }
                    name_text.text = context?.getString(R.string.full_name, user.firstName, user.lastName)
                    school_text.text = "Placeholder High School, Matrix" //TODO: Schools API
                    grade_text.text = context?.getString(R.string.grade_variable, user.grade)
                    if (partner == null) {
                        relationship_text.text = context?.getString(R.string.single)
                    } else {
                        relationship_text.text = context?.getString(R.string.going_with, partner.firstName) //TODO: fix
                    }
                    bio_text.text = user.bio

                    if (user.snapchat != null) {
                        snapchat_image.visibility = View.VISIBLE
                        snapchat_text.text = user.snapchat
                    } else {
                        snapchat_image.visibility = View.GONE
                    }
                    if (user.twitter != null) {
                        twitter_image.visibility = View.VISIBLE
                        twitter_text.text = user.twitter
                    } else {
                        twitter_image.visibility = View.GONE
                    }
                    if (user.instagram != null) {
                        instagram_image.visibility = View.VISIBLE
                        instagram_text.text = user.instagram
                    } else {
                        instagram_image.visibility = View.GONE
                    }
                }
                else {
                    Snackbar.make(constraint_layout, R.string.unexpected_error,
                        Snackbar.LENGTH_LONG)
                        .show()
                    loading_pb.visibility = View.GONE
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        drawerInterface.unlockDrawer() //unlocks drawer upon exiting fragment
    }

    //sets image from url & converts it to a circle
    fun ImageView.loadUrl(url: String) {
        val fullUrl = "http://ec2-35-183-247-114.ca-central-1.compute.amazonaws.com${url.substring(2 until url.length)}"
        Picasso.get()
            .load(fullUrl)
            .transform(CircleTransformation(40, 1, ContextCompat.getColor(context, R.color.lightGray)))
            .resize(80, 80)
            .centerCrop()
            .placeholder(R.drawable.default_profile) //TODO: Change to loading animation
            .error(R.drawable.default_profile) //TODO: Change to actual error
            .into(this)
    }
}


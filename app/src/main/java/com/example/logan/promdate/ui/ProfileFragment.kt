package com.example.logan.promdate.ui

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.logan.promdate.*
import com.example.logan.promdate.data.DefaultResponse
import com.example.logan.promdate.data.User
import com.example.logan.promdate.data.UserResponse
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.content.ActivityNotFoundException
import android.net.Uri


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
                    bio_edit_wrapper.text = user.bio

                    if (user.snapchat != null && user.snapchat?.isNotEmpty() == true) {
                        snapchat_image.visibility = View.VISIBLE

                        snapchat_image.setOnClickListener {

                            try {
                                val snapUrl = "snapchat://add/${user.snapchat}"
                                val nativeAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse(snapUrl))
                                startActivity(nativeAppIntent)
                            }
                            catch (e: ActivityNotFoundException) {
                                val snapUrl = "https://snapchat.com/add/${user.snapchat}"
                                val websiteIntent = Intent(Intent.ACTION_VIEW, Uri.parse(snapUrl))
                                startActivity(websiteIntent)
                            }

                        }
                    } else {
                        snapchat_image.visibility = View.GONE
                    }
                    if (user.twitter != null && user.twitter?.isNotEmpty() == true) {
                        twitter_image.visibility = View.VISIBLE

                        twitter_image.setOnClickListener {
                            val uri = Uri.parse("http://twitter.com/${user.twitter}")
                            val twitterIntent = Intent(Intent.ACTION_VIEW, uri)

                            twitterIntent.setPackage("com.twitter.android")

                            try {
                                startActivity(twitterIntent)
                            } catch (e: ActivityNotFoundException) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://twitter.com/{${user.twitter}")
                                    )
                                )
                            }
                        }
                    } else {
                        twitter_image.visibility = View.GONE
                    }
                    if (user.instagram != null && user.instagram?.isNotEmpty() == true) {
                        instagram_image.visibility = View.VISIBLE

                        instagram_image.setOnClickListener {
                            val uri = Uri.parse("http://instagram.com/_u/${user.instagram}")
                            val instaIntent = Intent(Intent.ACTION_VIEW, uri)

                            instaIntent.setPackage("com.instagram.android")

                            try {
                                startActivity(instaIntent)
                            } catch (e: ActivityNotFoundException) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://instagram.com/{${user.instagram}")
                                    )
                                )
                            }
                        }

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
            .transform(
                CircleTransformation(
                    64,
                    1,
                    ContextCompat.getColor(context, R.color.lightGray)
                )
            )
            .resize(128, 128)
            .centerCrop()
            .placeholder(R.drawable.default_profile) //TODO: Change to loading animation
            .error(R.drawable.default_profile) //TODO: Change to actual error
            .into(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        when (profileFragmentArgs.userId) {
            -1 -> activity?.menuInflater?.inflate(R.menu.menu_self_profile, menu)
            else -> activity?.menuInflater?.inflate(R.menu.menu_other_profile, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    //controls what happens when button on toolbar is selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        //checks to make sure it was not the back button pressed
        if (item.itemId == R.id.action_match || item.itemId == R.id.action_edit)
            //switch depending on whether it is another user's profile or your own
            when (profileFragmentArgs.userId) {
                -1 -> {
                    findNavController().navigate(R.id.nav_settings)
                }
                else -> {
                    match()
                }
            }
        return super.onOptionsItemSelected(item)
    }

    private fun match() {

        //send match request
        val api = ApiAccessor().apiService
        val sp: SharedPreferences? = context?.getSharedPreferences("login", Context.MODE_PRIVATE)
        val token = sp?.getString("token", "") ?: ""

        //TODO: Change heart color while not currently matched with user

        api.matchUser(token, partnerId = profileFragmentArgs.userId)
            .enqueue(object : Callback<DefaultResponse> {

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Log.e(
                        "MatchUser",
                        "Failed to send match request!"
                    )
                }

                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.body()?.status ?: 0 != 200) {
                        //Match request failed
                        Log.e("MatchUser", "${response.body()?.status}: ${response.body()?.result}")
                        //TODO: Change heart back
                        Snackbar.make(
                            constraint_layout,
                            R.string.match_error,
                            Snackbar.LENGTH_SHORT
                        )
                    }
                }
            })
    }
}


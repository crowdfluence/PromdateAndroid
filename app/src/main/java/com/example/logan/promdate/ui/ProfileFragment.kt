package com.example.logan.promdate.ui

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
import com.example.logan.promdate.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.content.ActivityNotFoundException
import android.net.Uri
import com.example.logan.promdate.data.DefaultResponse
import com.example.logan.promdate.data.FullUser
import com.example.logan.promdate.data.UserResponse
import com.example.logan.promdate.util.ApiAccessor
import com.example.logan.promdate.util.LoadUrl
import android.graphics.Color
import androidx.core.graphics.drawable.DrawableCompat
import android.graphics.drawable.Drawable
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.ActionMenuView
import androidx.core.content.ContextCompat


class ProfileFragment : Fragment() {

    private val profileFragmentArgs: ProfileFragmentArgs by navArgs()
    private lateinit var drawerInterface: DrawerInterface
    private var isSelf = false
    private var isSelfMatched: Boolean? = null
    private var selfPartnerId: Int? = null

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

        //sets that there is no right icon if the user is already matched & it's not yourself
        val sp: SharedPreferences =
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: throw MissingSpException()
        val selfId = sp.getInt("userId", 0)
        isSelf = selfId == profileFragmentArgs.userId || profileFragmentArgs.userId == -1
        if (profileFragmentArgs.isMatched == 0 || isSelf) {
            setHasOptionsMenu(true)
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

        loadUser()
        if (!isSelf) {
            checkSelfMatched()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        drawerInterface.unlockDrawer() //unlocks drawer upon exiting fragment
    }

    private fun loadUser() {
        //load user data
        val accessor = ApiAccessor()
        val sp: SharedPreferences =
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: throw MissingSpException()
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
                val serverResponse = response.body()
                if (serverResponse != null && serverResponse.status == 200) {
                    val user: FullUser = serverResponse.result

                    loading_pb.visibility = View.GONE
                    blank_group.visibility = View.VISIBLE

                    //set up user profile with user's information
                    if (user.self.profilePictureUrl.isNotEmpty()) {
                        LoadUrl.loadUrl(context!!, profile_picture_image, user.self.profilePictureUrl)
                    }
                    name_text.text = context?.getString(R.string.full_name, user.self.firstName, user.self.lastName)
                    school_text.text = user.school.name
                    grade_text.text = context?.getString(R.string.grade_variable, user.self.grade)
                    if (user.partner == null) {
                        relationship_text.text = context?.getString(R.string.single)
                    } else {
                        relationship_text.text = context?.getString(R.string.going_with, user.partner?.firstName) //TODO: fix
                    }
                    bio_text.text = user.self.bio

                    if (user.self.snapchat != null && user.self.snapchat?.isNotEmpty() == true) {
                        snapchat_image.visibility = View.VISIBLE

                        snapchat_image.setOnClickListener {

                            try {
                                val snapUrl = "snapchat://add/${user.self.snapchat}"
                                val nativeAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse(snapUrl))
                                startActivity(nativeAppIntent)
                            }
                            catch (e: ActivityNotFoundException) {
                                val snapUrl = "https://snapchat.com/add/${user.self.snapchat}"
                                val websiteIntent = Intent(Intent.ACTION_VIEW, Uri.parse(snapUrl))
                                startActivity(websiteIntent)
                            }
                        }
                    } else {
                        snapchat_image.visibility = View.GONE
                    }
                    if (user.self.twitter != null && user.self.twitter?.isNotEmpty() == true) {
                        twitter_image.visibility = View.VISIBLE

                        twitter_image.setOnClickListener {
                            val uri = Uri.parse("http://twitter.com/${user.self.twitter}")
                            val twitterIntent = Intent(Intent.ACTION_VIEW, uri)

                            twitterIntent.setPackage("com.twitter.android")

                            try {
                                startActivity(twitterIntent)
                            } catch (e: ActivityNotFoundException) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://twitter.com/${user.self.twitter}")
                                    )
                                )
                            }
                        }
                    } else {
                        twitter_image.visibility = View.GONE
                    }
                    if (user.self.instagram != null && user.self.instagram?.isNotEmpty() == true) {
                        instagram_image.visibility = View.VISIBLE

                        instagram_image.setOnClickListener {
                            val uri = Uri.parse("http://instagram.com/_u/${user.self.instagram}")
                            val instaIntent = Intent(Intent.ACTION_VIEW, uri)

                            instaIntent.setPackage("com.instagram.android")

                            try {
                                startActivity(instaIntent)
                            } catch (e: ActivityNotFoundException) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://instagram.com/${user.self.instagram}")
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

    private fun checkSelfMatched() {
        //send match request
        val api = ApiAccessor().apiService
        val sp: SharedPreferences? =
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val token = sp?.getString("token", "") ?: ""

        api.getUser(token)
            .enqueue(object : Callback<UserResponse> {

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e(
                        "CheckSelfMatched",
                        "Failed to get data! ${t.javaClass.canonicalName}: ${t.message}"
                    )
                    isSelfMatched = false
                    selfPartnerId = -1
                }

                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.body()?.status != 200) {
                        //Match request failed
                        Log.e("CheckSelfMatched", "${response.body()?.status}: ${response.body()?.result}")
                        isSelfMatched = false
                        selfPartnerId = -1
                    }
                    else {
                        isSelfMatched = response.body()?.result?.self?.matched == 1
                        selfPartnerId = response.body()?.result?.self?.partnerId ?: -1

                        if (isSelfMatched == true && profileFragmentArgs.userId == selfPartnerId) {
                            changeHeart(Color.RED)
                        }
                        else if (profileFragmentArgs.userId == selfPartnerId) {
                            changeHeart(Color.WHITE)
                        }
                    }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        when (isSelf) {
            true -> activity?.menuInflater?.inflate(R.menu.menu_self_profile, menu)
            false -> activity?.menuInflater?.inflate(R.menu.menu_other_profile, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    //controls what happens when button on toolbar is selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //checks to make sure it was not the back button pressed
        if (item.itemId == R.id.action_match || item.itemId == R.id.action_edit)
            //switch depending on whether it is another user's profile or your own
            when (isSelf) {
                true -> findNavController().navigate(R.id.nav_settings)
                false -> match()
            }
        return super.onOptionsItemSelected(item)
    }

    private fun match() {

        var heartColour: Int? = null

        //ensures that user data has been received from server before proceeding
        if (selfPartnerId == null || isSelfMatched == null) {
            return
        }

        //Checks that the user is not currently matched
        val action: Int = if (selfPartnerId != -1) {
            if (selfPartnerId == profileFragmentArgs.userId) {
                //attempting to send request to someone already matched with; removes match instead
                //change heart to unfilled
                if (profileFragmentArgs.isMatched == 0) {
                    heartColour = null
                }
                1
            }
            else {
                if (isSelfMatched == true) {
                    //TODO: prompt "are you sure"
                }
                else {
                    //TODO: prompt "only one pending request"
                }
                heartColour = Color.WHITE
                0
            }
        }
        else {
            //change heart colour to white
            if (profileFragmentArgs.isMatched == 0) {
                heartColour = Color.WHITE
            }
            0
        }

        changeHeart(heartColour)

        //send match request
        val api = ApiAccessor().apiService
        val sp: SharedPreferences? =
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val token = sp?.getString("token", "") ?: ""

        api.matchUser(token, profileFragmentArgs.userId, action)
            .enqueue(object : Callback<DefaultResponse> {

                override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                    Log.e(
                        "MatchUser",
                        "Failed to send match request! ${t.javaClass.canonicalName}: ${t.message}"
                    )
                    Snackbar.make(
                        constraint_layout,
                        R.string.match_error,
                        Snackbar.LENGTH_LONG
                    ).show()

                    if (profileFragmentArgs.isMatched == 0) {
                        changeHeart(heartColour)
                    }
                }

                override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                    if (response.body()?.status != 200) { //something went wrong, but server received request
                        //Match request failed
                        Log.e("MatchUser", "${response.body()?.status}: ${response.body()?.result}")
                        Snackbar.make(
                            constraint_layout,
                            R.string.match_error,
                            Snackbar.LENGTH_LONG
                        ).show()

                        if (profileFragmentArgs.isMatched == 0) {
                            changeHeart(heartColour)
                        }
                    }
                    else { //success
                        //TODO: Make message more "human"
                        Snackbar.make(
                            constraint_layout,
                            response.body()?.result ?: "",
                            Snackbar.LENGTH_LONG
                        ).show()

                        isSelfMatched = false
                        selfPartnerId = if (action == 1) -1 else profileFragmentArgs.userId
                    }
                }
            })
    }

    private fun changeHeart(colour: Int?) {
        val heartItem: ActionMenuItemView = ((toolbar as Toolbar).getChildAt(2) as ActionMenuView).getChildAt(0) as ActionMenuItemView
        if (colour == null) {
            //not filled
            var drawable: Drawable? = ContextCompat.getDrawable(context!!, R.drawable.ic_heart_border)
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable)
                heartItem.setIcon(drawable)
            }
        }
        else {
            var drawable: Drawable? = ContextCompat.getDrawable(context!!, R.drawable.ic_heart)
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable!!.mutate(), colour)
                heartItem.setIcon(drawable)
            }
        }
    }
}


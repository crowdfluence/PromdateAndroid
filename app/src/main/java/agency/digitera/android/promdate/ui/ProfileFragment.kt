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
import agency.digitera.android.promdate.data.DefaultResponse
import agency.digitera.android.promdate.data.FullUser
import agency.digitera.android.promdate.data.UserResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.content.ActivityNotFoundException
import android.net.Uri
import agency.digitera.android.promdate.util.ApiAccessor
import agency.digitera.android.promdate.util.LoadUrl
import android.graphics.Color
import androidx.core.graphics.drawable.DrawableCompat
import android.graphics.drawable.Drawable
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.ActionMenuView
import androidx.core.content.ContextCompat
import agency.digitera.android.promdate.util.ConfirmationDialog
import agency.digitera.android.promdate.util.MissingSpException


class ProfileFragment : Fragment() {

    private val profileFragmentArgs: ProfileFragmentArgs by navArgs()
    private lateinit var drawerInterface: DrawerInterface
    private var isSelf = false
    private var selfId = -1
    private var isSelfMatched: Boolean? = null
    private var selfPartnerId: Int? = null
    private var requestCompleted = false
    private var hasPartner = false
    private var isFavourited = false
    private var hasGrade = false

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
        selfId = sp.getInt("userId", 0)
        isSelf = selfId == profileFragmentArgs.userId || profileFragmentArgs.userId == -1
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

        requestCompleted = isSelf //doesn't need to wait on checkSelf request if profile is own profile
        loadUser()

        if (!isSelf) {
            checkSelfMatched()
        }
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

                    val canSendMatch = !isSelf && profileFragmentArgs.isMatched == 0
                    if (canSendMatch) {
                        send_match_button.setOnClickListener {
                            match()
                        }
                    }

                    if (profileFragmentArgs.isMatched == 0 && user.self.partnerId == selfId) {
                        send_match_button.text = getString(R.string.accept_request)
                    }

                    //set up user profile with user's information
                    if (user.self.profilePictureUrl.isNotEmpty()) {
                        LoadUrl.loadProfilePicture(context!!, profile_picture_image, user.self.profilePictureUrl)
                    }
                    name_text.text = context?.getString(R.string.full_name, user.self.firstName, user.self.lastName)
                    school_text.text = user.school.name
                    grade_text.text = context?.getString(R.string.grade_variable, user.self.grade)
                    hasGrade = user.self.grade != null
                    if (user.partner == null) {
                        relationship_text.text = context?.getString(R.string.single)
                    } else {
                        hasPartner = true
                        relationship_text.text =
                            context?.getString(R.string.going_with, user.partner?.firstName) //TODO: fix
                        LoadUrl.loadProfilePicture(
                            context!!,
                            partner_picture_image,
                            user.partner?.profilePictureUrl ?: ""
                        )
                    }
                    if(user.self.gender != R.array.genders_array.toString(3)){
                        gender_text.text = user.self.gender
                        gender_text.visibility = View.VISIBLE
                    }
                    bio_text.text = user.self.bio

                    if (user.self.snapchat != null && user.self.snapchat?.isNotEmpty() == true) {
                        snapchat_image.visibility = View.VISIBLE

                        snapchat_image.setOnClickListener {

                            try {
                                val snapUrl = "snapchat://add/${user.self.snapchat}"
                                val nativeAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse(snapUrl))
                                startActivity(nativeAppIntent)
                            } catch (e: ActivityNotFoundException) {
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

                    if (!isSelf) {
                        isFavourited = user.self.isFavourited
                        changeHeart(if (isFavourited) Color.WHITE else null)
                    }

                    //show page if other request has completed
                    if (requestCompleted) {
                        loading_pb.visibility = View.GONE
                        send_match_button.visibility = if (canSendMatch) View.VISIBLE else View.GONE
                        partner_picture_image.visibility = if (hasPartner) View.VISIBLE else View.GONE
                        blank_group.visibility = View.VISIBLE
                        grade_text.visibility = if (hasGrade) View.VISIBLE else View.GONE
                        social_media_group.visibility = View.VISIBLE
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

                    if (requestCompleted) {
                        loading_pb.visibility = View.GONE
                    }
                    else {
                        requestCompleted = true
                    }
                }

                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.body()?.status != 200) {
                        //Match request failed
                        Log.e("CheckSelfMatched", "${response.body()?.status}: ${response.body()?.result}")
                        isSelfMatched = false
                        selfPartnerId = -1

                        if (requestCompleted) {
                            loading_pb.visibility = View.GONE
                        }
                        else {
                            requestCompleted = true
                        }
                    }
                    else {
                        isSelfMatched = response.body()?.result?.self?.matched == 1
                        selfPartnerId = response.body()?.result?.self?.partnerId ?: -1

                        //viewing partner's profile
                        if (isSelfMatched == true && profileFragmentArgs.userId == selfPartnerId) {
                            send_match_button.text = getString(R.string.unmatch)
                        }
                        //viewing pending partner's profile
                        else if (profileFragmentArgs.userId == selfPartnerId) {
                           send_match_button.text = getString(R.string.cancel_request)
                        }

                        if (requestCompleted) {
                            loading_pb.visibility = View.GONE
                            send_match_button.visibility = if (profileFragmentArgs.isMatched == 0) View.VISIBLE else View.GONE
                            partner_picture_image.visibility = if (hasPartner) View.VISIBLE else View.GONE
                            social_media_group.visibility = View.VISIBLE
                            blank_group.visibility = View.VISIBLE
                            grade_text.visibility = if (hasGrade) View.VISIBLE else View.GONE
                        }
                        else {
                            requestCompleted = true
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
        if (item.itemId == R.id.action_favourite || item.itemId == R.id.action_edit)
            //switch depending on whether it is another user's profile or your own
            when (isSelf) {
                true -> findNavController().navigate(R.id.nav_settings)
                false -> favourite()
            }
        return super.onOptionsItemSelected(item)
    }

    private fun favourite() {
        changeHeart(if (isFavourited) null else Color.WHITE)

        val accessor = ApiAccessor()
        val sp: SharedPreferences =
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: throw MissingSpException()
        val token = sp.getString("token", null) ?: ""

        val call = accessor.apiService.favourite(token, profileFragmentArgs.userId, if (isFavourited) 1 else 0)

        call.enqueue(object : Callback<DefaultResponse> {
            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Log.e(
                    "FavouriteUser",
                    "Failed to send match request! ${t.javaClass.canonicalName}: ${t.message}"
                )
                Snackbar.make(
                    constraint_layout,
                    R.string.favourite_error,
                    Snackbar.LENGTH_LONG
                ).show()

                //change colour back if fail
                changeHeart(if (isFavourited) Color.WHITE else null)
            }

            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.body()?.status != 200) { //something went wrong, but server received request
                    //Add to wishlist failed
                    Log.e("FavouriteUser", "${response.body()?.status}: ${response.body()?.result}")
                    Snackbar.make(
                        constraint_layout,
                        R.string.favourite_error,
                        Snackbar.LENGTH_LONG
                    ).show()

                    //change colour back if fail
                    changeHeart(if (isFavourited) Color.WHITE else null)

                } else { //success
                    //TODO: Make message more "human"
                    Snackbar.make(
                        constraint_layout,
                        response.body()?.result ?: "",
                        Snackbar.LENGTH_LONG
                    ).show()

                    isFavourited = !isFavourited
                }
            }
        })
    }

    private fun match() {

        //change text of send request button
        if (send_match_button.text == getString(R.string.send_match_request)) {
            send_match_button.text = getString(R.string.cancel_request)
        }
        else if (send_match_button.text == getString(R.string.cancel_request)) {
            send_match_button.text = getString(R.string.send_match_request)
        }

        //ensures that user data has been received from server before proceeding
        if (selfPartnerId == null || isSelfMatched == null) {
            Log.d("Match", "Data not loaded before attempting to send request")
            Snackbar.make(
                constraint_layout,
                R.string.unexpected_error,
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        //Checks that the user is not currently matched
        var waitForDialogInput = false
        val action: Int = if (selfPartnerId != -1) { //currently is matched or has sent a request that is pending
            if (selfPartnerId == profileFragmentArgs.userId) { //matched with person whose profile they're looking at
                //attempting to send request to someone already matched with; removes match instead
                1
            }
            else { //not matched with current person's profile
                waitForDialogInput = true

                //slightly different prompt message depending on if user is already matched
                val promptMsg: String = if (isSelfMatched == true) { //is matched with someone
                    getString(R.string.confirm_request_while_matched)
                }
                else { //has sent a pending request to someone
                    getString(R.string.confirm_no_multiple_requests)
                }
                //prompts user to confirm request
                ConfirmationDialog.newInstance(promptMsg).apply {
                    setPositiveClick { sendMatch(0) }
                }.also { dialog ->
                    dialog.show(
                        fragmentManager ?: throw Exception("Fragment manager not found"),
                        "confirm_request_dialog_fragment"
                    )
                }
                0
            }
        }
        else { //isn't currently matched with anyone and doesn't have any pending requests
            0
        }

        if (!waitForDialogInput) {
            sendMatch(action)
        }
    }

    private fun sendMatch(action: Int) {

        //send match request
        val api = ApiAccessor().apiService
        val sp: SharedPreferences? =
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val token = sp?.getString("token", null) ?: ""

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

                    //change text of send request button back if fail
                    if (send_match_button.text == getString(R.string.send_match_request)) {
                        send_match_button.text = getString(R.string.cancel_request)
                    }
                    else if (send_match_button.text == getString(R.string.cancel_request)) {
                        send_match_button.text = getString(R.string.send_match_request)
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

                        //change text of send request button back if fail
                        if (send_match_button.text == getString(R.string.send_match_request)) {
                            send_match_button.text = getString(R.string.cancel_request)
                        }
                        else if (send_match_button.text == getString(R.string.cancel_request)) {
                            send_match_button.text = getString(R.string.send_match_request)
                        }

                    } else { //success
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


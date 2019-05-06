package com.example.logan.promdate.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.logan.promdate.*
import com.example.logan.promdate.data.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NumberFormatException


class SettingsFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up toolbar at top of layout
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = toolbar as Toolbar
        toolbar.title = getString(R.string.edit_profile)
        appCompatActivity.setSupportActionBar(toolbar)

        //set up back arrow
        appCompatActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        //set up gender spinner with hint
        val genderOptions: Array<String> = resources.getStringArray(R.array.genders_array)
        val genderAdapter = HintAdapter(
            context!!,
            genderOptions,
            android.R.layout.simple_spinner_dropdown_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val genderSpinner: Spinner = gender_spinner
        genderSpinner.adapter = genderAdapter
        genderSpinner.setSelection(genderAdapter.count)

        //set up grade spinner with hint
        val gradeOptions: Array<String> = resources.getStringArray(R.array.grades_array)
        val gradeAdapter = HintAdapter(
            context!!,
            gradeOptions,
            android.R.layout.simple_spinner_dropdown_item
        )
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val gradeSpinner: Spinner = grade_spinner
        gradeSpinner.adapter = gradeAdapter
        gradeSpinner.setSelection(gradeAdapter.count)

        //set up save button
        save_button.setOnClickListener {
            updateUser()
        }

        //load data
        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        drawerInterface.unlockDrawer() //unlocks drawer upon exiting fragment

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val imm: InputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
            }
            catch (e: Exception) {
                Log.d("HideKeyboardFail", "${e.javaClass.canonicalName}: ${e.localizedMessage}")
            }
        }
    }

    private fun loadData() {
        val accessor = ApiAccessor()

        //get token
        val sp: SharedPreferences =
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: throw BadTokenException()
        val token = sp.getString("token", null) ?: ""

        //send request
        val call = accessor.apiService.getUser(token)

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
                        profile_picture_image.loadUrl(user.self.profilePictureUrl)
                    }
                    first_name_edit.setText(user.self.firstName)
                    last_name_edit.setText(user.self.lastName)
                    school_edit.setText(user.school.name)
                    //set grade
                    val gradeId = user.self.grade?.minus(9) ?: -1
                    if (gradeId in 0..3) {
                        grade_spinner.setSelection(gradeId)
                    }
                    else {
                        grade_spinner.setSelection(4)
                    }
                    bio_edit.setText(user.self.bio)

                    //set gender
                    when (user.self.gender?.toLowerCase()) {
                        "male" -> gender_spinner.setSelection(0)
                        "female" -> gender_spinner.setSelection(1)
                        "other" -> gender_spinner.setSelection(2)
                        else -> gender_spinner.setSelection(3)
                    }

                    instagram_edit.setText(user.self.instagram)
                    snapchat_edit.setText(user.self.snapchat)
                    twitter_edit.setText(user.self.twitter)
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

    private fun updateUser() {
        val firstName = first_name_edit.text.toString()
        val lastName = last_name_edit.text.toString()
        val bio = bio_edit.text.toString()
        val snapchat = snapchat_edit.text.toString()
        val instagram = instagram_edit.text.toString()
        val twitter = twitter_edit.text.toString()
        val schoolId = 1
        val grade: Int = try {
            grade_spinner.selectedItem.toString().toInt()
        }
        catch (e: NumberFormatException) {
            -1
        }
        val gender: String = gender_spinner.selectedItem.toString()

        //check that all required fields are there & valid
        var missingFields = false
        if (firstName.isEmpty()) {
            first_name_edit_wrapper.error = getString(R.string.required_field)
            missingFields = true
        } else {
            first_name_edit_wrapper.error = null
        }
        if (lastName.isEmpty()) {
            last_name_edit_wrapper.error = getString(R.string.required_field)
            missingFields = true
        } else {
            last_name_edit_wrapper.error = null
        }
        if (!isValidSchoolId(schoolId)) {
            missingFields = true
        } else {
            school_edit_wrapper.error = null
        }
        if (missingFields) {
            return
        }

        val apiAccessor = ApiAccessor()

        val sp: SharedPreferences =
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE) ?: throw BadTokenException()
        val token = sp.getString("token", null) ?: ""

        //create request
        val call: Call<UpdateResponse> = apiAccessor.apiService.update(
            token, instagram, snapchat, twitter, bio, firstName, lastName, schoolId, grade, gender
        )

        val loadingAnim = loading_pb
        loadingAnim.visibility = View.VISIBLE

        //send request
        call.enqueue(object : Callback<UpdateResponse> {
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                loadingAnim.visibility = View.GONE
                if (response.body()?.status != 200) {
                    Snackbar.make(
                        constraint_layout,
                        R.string.server_error,
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                Snackbar.make(
                    constraint_layout,
                    R.string.no_internet,
                    Snackbar.LENGTH_LONG
                ).show()
                loadingAnim.visibility = View.GONE
            }
        })
    }

    private fun isValidSchoolId(schoolId: Int): Boolean = schoolId > 0

    //sets image from url & converts it to a circle
    fun ImageView.loadUrl(url: String) {
        val fullUrl = "http://ec2-35-183-247-114.ca-central-1.compute.amazonaws.com${url.substring(2 until url.length)}"
        Picasso.get()
            .load(fullUrl)
            .transform(CircleTransformation(64, 1, ContextCompat.getColor(context, R.color.lightGray)))
            .resize(128, 128)
            .centerCrop()
            .placeholder(R.drawable.default_profile) //TODO: Change to loading animation
            .error(R.drawable.default_profile) //TODO: Change to actual error
            .into(this)
    }
}


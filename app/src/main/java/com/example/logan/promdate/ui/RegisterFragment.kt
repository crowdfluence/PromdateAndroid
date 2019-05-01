package com.example.logan.promdate.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.logan.promdate.*
import com.example.logan.promdate.data.DefaultResponse
import kotlinx.android.synthetic.main.fragment_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterFragment : Fragment() {

    private lateinit var drawerInterface: DrawerInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            drawerInterface = activity as DrawerInterface
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement DrawerInterface")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        drawerInterface.lockDrawer()
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set up toolbar at top of layout
        val appCompatActivity = activity as AppCompatActivity
        val toolbar: Toolbar = include as Toolbar
        toolbar.title = getString(R.string.register)
        appCompatActivity.setSupportActionBar(toolbar)

        //set blue text to return to login
        sign_in_text.setOnClickListener {
            signIn(it)
        }

        //set up register button
        register_button.setOnClickListener {
            register(it)
        }

        //set up gender adapter with hint
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

        //set up grade adapter with hint
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

        //set textChangedListener on all input fields to remove error upon typing (except confirm password)
        //also not set for optional fields
        email_edit.addTextChangedListener(InputTextWatcher(email_edit_wrapper))
        password_edit.addTextChangedListener(InputTextWatcher(password_edit_wrapper))
        first_name_edit.addTextChangedListener(InputTextWatcher(first_name_edit_wrapper))

        //validate that user's password matches as they are entering it
        confirm_password_edit.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isValidPasswordConfirmation(confirm_password_edit)
            }

            //don't need these but have to override as it is an interface
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
    }

    private fun register(view: View) {
        val emailEdit = email_edit
        val email = emailEdit.text.toString()
        val password = password_edit.text.toString()
        val checkPasswordEdit = confirm_password_edit
        val checkPassword = checkPasswordEdit.text.toString()
        val firstName = first_name_edit.text.toString()
        val lastName = last_name_edit.text.toString()
        var gender: String? = gender_spinner.selectedItem.toString()
        val grade = try {
            grade_spinner.selectedItem.toString().toInt()
        } catch (e: Exception) {
            -1
        }
        val schoolId = 1

        //check that all required fields are there & valid
        var missingFields = false
        if (!isValidEmail(emailEdit)) {
            missingFields = true
        } else if (email.isEmpty()) {
            email_edit_wrapper.error = getString(R.string.required_field)
            missingFields = true
        } else {
            email_edit_wrapper.error = null
        }
        if (password.isEmpty()) {
            password_edit_wrapper.error = getString(R.string.required_field)
            missingFields = true
        } else {
            password_edit_wrapper.error = null
        }
        if (!isValidPasswordConfirmation(checkPasswordEdit)) {
            missingFields = true
        } else if (checkPassword.isEmpty()) {
            confirm_password_edit_wrapper.error = getString(R.string.required_field)
            missingFields = true
        } else {
            confirm_password_edit_wrapper.error = null
        }
        if (firstName.isEmpty()) {
            first_name_edit_wrapper.error = getString(R.string.required_field)
            missingFields = true
        } else {
            first_name_edit_wrapper.error = null
        }
        if (lastName.isEmpty()) {
            last_name_edit_wrapper.error = getString(R.string.required_field)
            missingFields = true
        }
        else {
            last_name_edit_wrapper.error = null
        }
        if (gender == resources.getStringArray(R.array.genders_array)[3]) {
            //gender is optional, so doesn't put error if it is not entered
            gender = null
        }
        if (!isValidGrade(grade)) {
            missingFields = true
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

        //create request
        val call: Call<DefaultResponse> = apiAccessor.apiService.register(
                email, password, checkPassword, firstName,
                lastName, schoolId, grade
        )

        val loadingAnim = loading_pb
        loadingAnim.visibility = View.VISIBLE

        //send request
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    //successfully logged in
                    Snackbar.make(
                            constraint_layout,
                        R.string.register_success,
                            Snackbar.LENGTH_LONG
                    ).show()
                    loadingAnim.visibility = View.GONE
                } else {
                    Snackbar.make(
                            constraint_layout,
                        response.body()?.result ?: "",
                            Snackbar.LENGTH_LONG
                    ).show()
                    loadingAnim.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Snackbar.make(
                        constraint_layout,
                    R.string.no_internet,
                        Snackbar.LENGTH_LONG
                ).show()
                loadingAnim.visibility = View.GONE
            }
        })
    }

    //checks that email format is valid
    private fun isValidEmail(emailEdit: TextInputEditText): Boolean {
        val email = emailEdit.text.toString()
        val emailEditWrapper = email_edit_wrapper
        return if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditWrapper.error = getString(R.string.invalid_email)
            false
        } else {
            emailEditWrapper.error = null
            true
        }
    }

    private fun isValidGrade(grade: Int): Boolean = grade > 0

    private fun isValidSchoolId(schoolId: Int): Boolean = schoolId > 0

    private fun isValidPasswordConfirmation(confirmPassEdit: TextInputEditText): Boolean {
        val password = password_edit.text.toString()
        val confirmPassword = confirmPassEdit.text.toString()
        val confirmPassEditWrapper = confirm_password_edit_wrapper

        if (password.isEmpty()) {
            confirmPassEditWrapper.error = null
            return true //user entered confirm password before entering normal password
        }

        for (i in 0 until confirmPassword.length) {
            if (password[i] != confirmPassword[i]) { //passwords do not match
                confirmPassEditWrapper.error = getString(R.string.password_no_match)
                return false
            }
        }
        confirmPassEditWrapper.error = null
        return true
    }

    private fun signIn(view: View) {
        findNavController().navigate(R.id.nav_login)
    }

    //TODO: https://developer.android.com/guide/topics/search/search-dialog
}

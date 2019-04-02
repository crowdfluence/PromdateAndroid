package com.example.logan.promdate

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//TODO: (MAYBE) clear all errors after user starts typing

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //set up toolbar at top of layout
        val toolbar: Toolbar = findViewById(R.id.include)
        toolbar.title = getString(R.string.register)
        setSupportActionBar(toolbar)

        //make tapping on sign in return user to other activity
        val signInText = findViewById<TextView>(R.id.sign_in_text)
        signInText.setOnClickListener {
            signIn(it)
        }

        //set textChangedListener on all input fields to remove error upon typing (except confirm password)
        val emailEdit = findViewById<TextInputEditText>(R.id.email_edit)
        emailEdit.addTextChangedListener(InputTextWatcher(findViewById(R.id.email_edit_wrapper)))
        val passwordEdit = findViewById<TextInputEditText>(R.id.password_edit)
        passwordEdit.addTextChangedListener(InputTextWatcher(findViewById(R.id.password_edit_wrapper)))
        val firstNameEdit = findViewById<TextInputEditText>(R.id.first_name_edit)
        firstNameEdit.addTextChangedListener(InputTextWatcher(findViewById(R.id.first_name_edit_wrapper)))
        val lastNameEdit = findViewById<TextInputEditText>(R.id.last_name_edit)
        lastNameEdit.addTextChangedListener(InputTextWatcher(findViewById(R.id.last_name_edit_wrapper)))
        val genderEdit = findViewById<TextInputEditText>(R.id.gender_edit)
        genderEdit.addTextChangedListener(InputTextWatcher(findViewById(R.id.gender_edit_wrapper)))
        val gradeEdit = findViewById<TextInputEditText>(R.id.grade_edit)
        gradeEdit.addTextChangedListener(InputTextWatcher(findViewById(R.id.grade_edit_wrapper)))

        //validate that user's password matches as they are entering it
        val confirmPassEdit = findViewById<TextInputEditText>(R.id.confirm_password_edit)
        confirmPassEdit.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                isValidPasswordConfirmation(confirmPassEdit)
            }

            //don't need these but have to override as it is an interface
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
    }

    fun register(view: View) {
        val emailEdit = findViewById<TextInputEditText>(R.id.email_edit)
        val email = emailEdit.text.toString()
        val password = findViewById<TextInputEditText>(R.id.password_edit).text.toString()
        val checkPasswordEdit = findViewById<TextInputEditText>(R.id.confirm_password_edit)
        val checkPassword = checkPasswordEdit.text.toString()
        val firstName = findViewById<TextInputEditText>(R.id.first_name_edit).text.toString()
        val lastName = findViewById<TextInputEditText>(R.id.last_name_edit).text.toString()
        val gender = findViewById<TextInputEditText>(R.id.gender_edit).text.toString()
        val grade = findViewById<TextInputEditText>(R.id.grade_edit).text.toString().toIntOrNull() ?: -1
        val schoolId = 1

        var missingFields = false
        if (!isValidEmail(emailEdit)) {
            missingFields = true
        }
        else if (email.isEmpty()) {
            findViewById<TextInputLayout>(R.id.email_edit_wrapper).error = getString(R.string.required_field)
            missingFields = true
        }
        else {
            findViewById<TextInputLayout>(R.id.email_edit_wrapper).error = null
        }
        if (password.isEmpty()) {
            findViewById<TextInputLayout>(R.id.password_edit_wrapper).error = getString(R.string.required_field)
            missingFields = true
        }
        else {
            findViewById<TextInputLayout>(R.id.password_edit_wrapper).error = null
        }
        if (!isValidPasswordConfirmation(checkPasswordEdit)) {
            missingFields = true
        }
        else if (checkPassword.isEmpty()) {
            findViewById<TextInputLayout>(R.id.confirm_password_edit_wrapper).error = getString(R.string.required_field)
            missingFields = true
        }
        else {
            findViewById<TextInputLayout>(R.id.confirm_password_edit_wrapper).error = null
        }
        if (firstName.isEmpty()) {
            findViewById<TextInputLayout>(R.id.first_name_edit_wrapper).error = getString(R.string.required_field)
            missingFields = true
        }
        else {
            findViewById<TextInputLayout>(R.id.first_name_edit_wrapper).error = null
        }
        if (lastName.isEmpty()) {
            findViewById<TextInputLayout>(R.id.last_name_edit_wrapper).error = getString(R.string.required_field)
            missingFields = true
        }
        else {
            findViewById<TextInputLayout>(R.id.last_name_edit_wrapper).error = null
        }
        if (gender.isEmpty()) {
            findViewById<TextInputLayout>(R.id.gender_edit_wrapper).error = getString(R.string.required_field)
            missingFields = true
        }
        else {
            findViewById<TextInputLayout>(R.id.gender_edit_wrapper).error = null
        }
        if (!isValidGrade(grade)) {
            findViewById<TextInputLayout>(R.id.grade_edit_wrapper).error = getString(R.string.required_field)
            missingFields = true
        }
        else {
            findViewById<TextInputLayout>(R.id.grade_edit_wrapper).error = null
        }
        if (!isValidSchoolId(schoolId)) {
            missingFields = true
        }
        else {
            findViewById<TextInputLayout>(R.id.school_edit_wrapper).error = null
        }
        if (missingFields) {
            return
        }


        val apiAccessor = APIAccessor()

        val call: Call<DefaultResponse> = apiAccessor.apiService.register(email, password, checkPassword, firstName,
            lastName, schoolId, gender, grade)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    //successfully logged in
                    Snackbar.make(findViewById(R.id.constraint_layout), R.string.register_success,
                        Snackbar.LENGTH_LONG)
                        .show()
                }
                else {
                    Snackbar.make(findViewById(R.id.constraint_layout), response.body()?.result ?: "",
                        Snackbar.LENGTH_LONG)
                        .show()
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Snackbar.make(findViewById(R.id.constraint_layout), R.string.no_internet,
                    Snackbar.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun signIn(view: View) {
        finish()
    }

    private fun isValidEmail(emailEdit: TextInputEditText): Boolean {
        val email = emailEdit.text.toString()
        val emailEditWrapper = findViewById<TextInputLayout>(R.id.email_edit_wrapper)
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditWrapper.error = getString(R.string.invalid_email)
            return false
        }
        else {
            emailEditWrapper.error = null
            return true
        }
    }

    private fun isValidGrade(grade: Int): Boolean = grade > 0

    private fun isValidSchoolId(schoolId: Int): Boolean = schoolId > 0

    private fun isValidPasswordConfirmation(confirmPassEdit: TextInputEditText): Boolean {
        val password = findViewById<TextInputEditText>(R.id.password_edit).text.toString()
        val confirmPassword = confirmPassEdit.text.toString()
        val confirmPassEditWrapper = findViewById<TextInputLayout>(R.id.confirm_password_edit_wrapper)

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

    //TODO: https://developer.android.com/guide/topics/search/search-dialog
}

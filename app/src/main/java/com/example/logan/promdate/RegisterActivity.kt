package com.example.logan.promdate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import com.example.logan.promdate.data.DefaultResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
            finish()
        }

        //set up gender adapter with hint
        val genderOptions: Array<String> = resources.getStringArray(R.array.genders_array)
        val genderAdapter = HintAdapter(this, genderOptions, android.R.layout.simple_spinner_dropdown_item)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val genderSpinner: Spinner = findViewById(R.id.gender_spinner)
        genderSpinner.adapter = genderAdapter
        genderSpinner.setSelection(genderAdapter.count)

        //set up grade adapter with hint
        val gradeOptions: Array<String> = resources.getStringArray(R.array.grades_array)
        val gradeAdapter = HintAdapter(this, gradeOptions, android.R.layout.simple_spinner_dropdown_item)
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val gradeSpinner: Spinner = findViewById(R.id.grade_spinner)
        gradeSpinner.adapter = gradeAdapter
        gradeSpinner.setSelection(gradeAdapter.count)

        //set textChangedListener on all input fields to remove error upon typing (except confirm password)
        //also not set for optional fields
        val emailEdit = findViewById<TextInputEditText>(R.id.email_edit)
        emailEdit.addTextChangedListener(InputTextWatcher(findViewById(R.id.email_edit_wrapper)))
        val passwordEdit = findViewById<TextInputEditText>(R.id.password_edit)
        passwordEdit.addTextChangedListener(InputTextWatcher(findViewById(R.id.password_edit_wrapper)))
        val firstNameEdit = findViewById<TextInputEditText>(R.id.first_name_edit)
        firstNameEdit.addTextChangedListener(InputTextWatcher(findViewById(R.id.first_name_edit_wrapper)))

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

    fun onRegister(view: View) {
        val emailEdit = findViewById<TextInputEditText>(R.id.email_edit)
        val email = emailEdit.text.toString()
        val password = findViewById<TextInputEditText>(R.id.password_edit).text.toString()
        val checkPasswordEdit = findViewById<TextInputEditText>(R.id.confirm_password_edit)
        val checkPassword = checkPasswordEdit.text.toString()
        val firstName = findViewById<TextInputEditText>(R.id.first_name_edit).text.toString()
        val lastName = findViewById<TextInputEditText>(R.id.last_name_edit).text.toString()
        var gender: String? = findViewById<Spinner>(R.id.gender_spinner).selectedItem.toString()
        val grade = try {
            findViewById<Spinner>(R.id.grade_spinner).selectedItem.toString().toInt()
        } catch (e: Exception) {
            -1
        }
        val schoolId = 1

        //check that all required fields are there & valid
        var missingFields = false
        if (!isValidEmail(emailEdit)) {
            missingFields = true
        } else if (email.isEmpty()) {
            findViewById<TextInputLayout>(R.id.email_edit_wrapper).error = getString(R.string.required_field)
            missingFields = true
        } else {
            findViewById<TextInputLayout>(R.id.email_edit_wrapper).error = null
        }
        if (password.isEmpty()) {
            findViewById<TextInputLayout>(R.id.password_edit_wrapper).error = getString(R.string.required_field)
            missingFields = true
        } else {
            findViewById<TextInputLayout>(R.id.password_edit_wrapper).error = null
        }
        if (!isValidPasswordConfirmation(checkPasswordEdit)) {
            missingFields = true
        } else if (checkPassword.isEmpty()) {
            findViewById<TextInputLayout>(R.id.confirm_password_edit_wrapper).error = getString(R.string.required_field)
            missingFields = true
        } else {
            findViewById<TextInputLayout>(R.id.confirm_password_edit_wrapper).error = null
        }
        if (firstName.isEmpty()) {
            findViewById<TextInputLayout>(R.id.first_name_edit_wrapper).error = getString(R.string.required_field)
            missingFields = true
        } else {
            findViewById<TextInputLayout>(R.id.first_name_edit_wrapper).error = null
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
            findViewById<TextInputLayout>(R.id.school_edit_wrapper).error = null
        }
        if (missingFields) {
            return
        }

        val apiAccessor = ApiAccessor()

        //create request
        val call: Call<DefaultResponse> = apiAccessor.apiService.register(
                email, password, checkPassword, firstName,
                lastName, schoolId, gender, grade
        )

        val loadingAnim = findViewById<ProgressBar>(R.id.loading_pb)
        loadingAnim.visibility = View.VISIBLE

        //send request
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    //successfully logged in
                    Snackbar.make(
                            findViewById(R.id.constraint_layout), R.string.register_success,
                            Snackbar.LENGTH_LONG
                    ).show()
                    loadingAnim.visibility = View.GONE
                } else {
                    Snackbar.make(
                            findViewById(R.id.constraint_layout), response.body()?.result ?: "",
                            Snackbar.LENGTH_LONG
                    ).show()
                    loadingAnim.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<DefaultResponse>, t: Throwable) {
                Snackbar.make(
                        findViewById(R.id.constraint_layout), R.string.no_internet,
                        Snackbar.LENGTH_LONG
                ).show()
                loadingAnim.visibility = View.GONE
            }
        })
    }

    //checks that email format is valid
    private fun isValidEmail(emailEdit: TextInputEditText): Boolean {
        val email = emailEdit.text.toString()
        val emailEditWrapper = findViewById<TextInputLayout>(R.id.email_edit_wrapper)
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

package com.example.logan.promdate

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//TODO: Check that confirm password and password match as the user is typing

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: Toolbar = findViewById(R.id.include)
        toolbar.title = getString(R.string.register)
        setSupportActionBar(toolbar)

        val signInText = findViewById<TextView>(R.id.sign_in_text)
        signInText.setOnClickListener {
            signIn(it)
        }
    }

    fun register(view:View) {
        val email = findViewById<EditText>(R.id.email_edit).text.toString()
        val password = findViewById<EditText>(R.id.password_edit).text.toString()
        val checkPassword = findViewById<EditText>(R.id.confirm_password_edit).text.toString()
        val firstName = findViewById<EditText>(R.id.first_name_edit).text.toString()
        val lastName = findViewById<EditText>(R.id.last_name_edit).text.toString()
        val gender = findViewById<EditText>(R.id.gender_edit).text.toString()
        val grade = findViewById<EditText>(R.id.grade_edit).text.toString().toInt()
        val schoolId = 1

        val apiAccessor = APIAccessor()

        val call: Call<DefaultResponse> = apiAccessor.apiService.register(email, password, checkPassword, firstName,
            lastName, schoolId, gender, grade)
        call.enqueue(object : Callback<DefaultResponse> {
            override fun onResponse(call: Call<DefaultResponse>, response: Response<DefaultResponse>) {
                if (response.isSuccessful) {
                    //successfully logged in
                    Toast.makeText(this@RegisterActivity, "Registered account!", Toast.LENGTH_SHORT).show()
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

    //TODO: https://developer.android.com/guide/topics/search/search-dialog
}
